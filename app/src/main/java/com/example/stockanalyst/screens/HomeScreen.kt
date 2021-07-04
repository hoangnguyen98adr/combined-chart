package com.example.stockanalyst.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.stockanalyst.R
import com.example.stockanalyst.extentions.safeSublist
import com.example.stockanalyst.model.FinancialSummary
import com.example.stockanalyst.model.FinancialSummaryItemModel
import com.example.stockanalyst.viewmodel.StockAnalystViewModel
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.android.synthetic.main.layout_financial_chart.*

class HomeScreen : Fragment(), View.OnClickListener {
    private lateinit var mStockAnalystViewModel: StockAnalystViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStockAnalystViewModel = StockAnalystViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDefaultChartMode()
        initObserver()
        initListener()
        cbFinancialChart.initChart()
        cbBalanceSheetChart.initChart()
    }

    private fun CombinedChart.initChart() {
        this.description.isEnabled = false
        this.description.position
        this.setTouchEnabled(false)
        this.setScaleEnabled(false)
        this.setDrawGridBackground(false)
        this.extraBottomOffset = 0F
        this.setPinchZoom(false)
        this.setExtraOffsets(0F, 0F, 0F, 10F)
        this.legend.apply {
            isEnabled = true
            isWordWrapEnabled = true
            context?.let { textColor = ContextCompat.getColor(it, R.color.white) }
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            orientation = Legend.LegendOrientation.HORIZONTAL
        }
        this.xAxis.apply {
            setCenterAxisLabels(true)
            position = XAxis.XAxisPosition.BOTTOM
            axisMinimum = 0f
            axisMaximum = 4F
            granularity = 1F
            spaceMin = 10F
            setDrawGridLines(false)
            context?.let { textColor = ContextCompat.getColor(it, R.color.white) }
        }
        this.axisLeft.apply {
            axisMinimum = 0F
            isEnabled = false
        }
        this.axisRight.apply {
            axisMinimum = 0f
            setDrawAxisLine(false)
            context?.let { textColor = ContextCompat.getColor(it, R.color.white) }
        }
    }

    private fun initListener() {
        tvAnnualFinancial.setOnClickListener(this)
        tvQuarterly.setOnClickListener(this)
        tvAnnualBalanceSheet.setOnClickListener(this)
        tvQuarterlyBalanceSheet.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            tvAnnualFinancial -> incomeChartMode(tvAnnualFinancial)
            tvQuarterly -> incomeChartMode(tvQuarterly)
            tvAnnualBalanceSheet -> balanceChartMode(tvAnnualBalanceSheet)
            tvQuarterlyBalanceSheet -> balanceChartMode(tvQuarterlyBalanceSheet)
        }
    }

    private fun setDefaultChartMode() {
        tvAnnualFinancial.isSelected = true
        tvAnnualBalanceSheet.isSelected = true
    }

    private fun incomeChartMode(view: TextView?) {
        tvAnnualFinancial.isSelected = tvAnnualFinancial == view
        tvQuarterly.isSelected = tvQuarterly == view
        updateChartModel(cbFinancialChart, tvAnnualFinancial.isSelected, FINANCIAL_STATEMENT, TOTAL_REVENUE, NET_INCOME)
    }

    private fun balanceChartMode(view: TextView?) {
        tvAnnualBalanceSheet.isSelected = tvAnnualBalanceSheet == view
        tvQuarterlyBalanceSheet.isSelected = tvQuarterlyBalanceSheet == view
        updateChartModel(cbBalanceSheetChart, tvAnnualBalanceSheet.isSelected, BALANCE_SHEET, BALANCE_1, BALANCE_2)
    }

    private fun updateChartModel(combinedChart: CombinedChart, isAnnual: Boolean, queryLabel: String, queryFirst: String, queryLast: String) {
        val data = mStockAnalystViewModel.mFinancialSummaryLiveData.value?.let {
            when(queryLabel) {
                FINANCIAL_STATEMENT -> if (isAnnual) it.annualFinancialStatement else it.quarterFinancialStatement
                BALANCE_SHEET -> if (isAnnual) it.annualBalanceSheet else it.quarterBalanceSheet
                else -> it.quarterRatioAndOtherData
            }
        }
        updateDataForChart(combinedChart, getIncomeChart(data, queryFirst, queryLast), getValueForChart(data, queryLabel))
    }

    private fun initObserver() {
        mStockAnalystViewModel.mFinancialSummaryLiveData.observe(viewLifecycleOwner) {
            setupViews(it)
        }
    }

    private fun setupViews(model: FinancialSummary) {
        updateDataForChart(cbFinancialChart,
            getIncomeChart(model.annualFinancialStatement, TOTAL_REVENUE, NET_INCOME),
            getValueForChart(model.annualFinancialStatement, FINANCIAL_STATEMENT)
        )
        updateDataForChart(cbBalanceSheetChart,
            getBalanceChart(model.annualBalanceSheet, BALANCE_1, BALANCE_2),
            getValueForChart(model.annualFinancialStatement, BALANCE_SHEET))
    }

    private fun updateDataForChart(combinedChart: CombinedChart, combinedData: CombinedData, labelChart: List<String>) {
        combinedChart.apply {
            this.xAxis.valueFormatter = IndexAxisValueFormatter(labelChart)
            this.data = combinedData
            invalidate()
        }
    }

    private fun getValueForChart(
        list: List<FinancialSummaryItemModel>?,
        queryFirst: String
    ): ArrayList<String> {
        val model = list?.find { it.description == queryFirst }
        return if (model != null) model.values.safeSublist(0, MAX_YEAR)
            .map { it.replace(FISCAL_YEAR, "") } as ArrayList<String> else ArrayList()
    }

    private fun getBarEntry(list: List<FinancialSummaryItemModel>?, query: String): List<BarEntry> {
        return getValueForChart(list, query).mapIndexed { index, s ->
            BarEntry(index.toFloat(), s.toFloat())
        }
    }

    private fun calculateLineEntry(total: BarDataSet, normal: BarDataSet): List<Entry> {
        return total.values.mapIndexed { index, totalEntry ->
            Entry(index.toFloat() + SPACE_X_AXIS, normal.values[index].y / totalEntry.y * 100)
        }
    }

    private fun configBarChartUI(barChart: BarDataSet, color: Int) {
        barChart.apply {
            activity?.let { this.color = ContextCompat.getColor(it, color) }
            setDrawValues(false)
            axisDependency = YAxis.AxisDependency.LEFT
        }
    }

    private fun configLineChartUI(lineChart: LineDataSet, color: Int) {
        lineChart.apply {
            mode = LineDataSet.Mode.LINEAR
            setDrawValues(false)
            activity?.let { this.color = ContextCompat.getColor(it, color) }
            setDrawCircles(false)
            lineWidth = 2F
        }
    }

    private fun configGroupBarChartUI(groupBar: BarData) {
        groupBar.apply {
            barWidth = 0.2F
            groupBars(0F, 0.5F, 0.05F)
        }
    }

    private fun getIncomeChart(
        list: List<FinancialSummaryItemModel>?,
        queryFirst: String,
        queryLast: String
    ): CombinedData {
        val combinedData = CombinedData()
        val totalDataSet = BarDataSet(getBarEntry(list, queryFirst), "ABC").apply { configBarChartUI(this, R.color.blue2685DB) }
        val normalDataSet = BarDataSet(getBarEntry(list, queryLast), "FSF").apply { configBarChartUI(this, R.color.green2DD0D0) }
        val lineDataSet = LineDataSet(calculateLineEntry(totalDataSet, normalDataSet), "Line Chart").apply { configLineChartUI(this, R.color.gray2B3248) }
        combinedData.setData(BarData(totalDataSet, normalDataSet).apply { configGroupBarChartUI(this) })
        combinedData.setData(LineData(lineDataSet))
        return combinedData
    }

    private fun getBalanceChart(
        list: List<FinancialSummaryItemModel>?,
        queryFirst: String,
        queryLast: String
    ): CombinedData {
        val combinedData = CombinedData()
        val totalDataSet = BarDataSet(getBarEntry(list, queryFirst), "fdsf").apply { configBarChartUI(this, R.color.blue2685DB) }
        val normalDataSet = BarDataSet(getBarEntry(list, queryLast), "fds").apply { configBarChartUI(this, R.color.green2DD0D0) }
        val lineDataSet = LineDataSet(calculateLineEntry(totalDataSet, normalDataSet), "Line Chart2").apply { configLineChartUI(this, R.color.gray2B3248) }
        combinedData.setData(BarData(totalDataSet, normalDataSet).apply { configGroupBarChartUI(this) })
        combinedData.setData(LineData(lineDataSet))
        return combinedData
    }

    companion object {
        private const val FINANCIAL_STATEMENT = "Financial Statement"
        private const val TOTAL_REVENUE = "Total Revenue"
        private const val NET_INCOME = "Net Income"
        private const val FISCAL_YEAR = "FY"
        private const val SPACE_X_AXIS = 0.5F
        private const val MAX_YEAR = 4
        private const val INCOME_CHART = "INCOME_CHART"
        private const val BALANCE_SHEET = "Balance Sheet"
        private const val BALANCE_1 = "Banlance1"
        private const val BALANCE_2 = "Banlance2"

        @JvmStatic
        fun newInstance() = HomeScreen()
    }
}