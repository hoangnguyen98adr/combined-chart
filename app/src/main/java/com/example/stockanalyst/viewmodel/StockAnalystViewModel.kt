package com.example.stockanalyst.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stockanalyst.model.FinancialSummary
import com.example.stockanalyst.model.FinancialSummaryItemModel

class StockAnalystViewModel : ViewModel() {
    val mFinancialSummaryLiveData: MutableLiveData<FinancialSummary> = MutableLiveData()

    init {
        initData()
    }

    private fun initData() {
        val dummyData = FinancialSummary(
            annualFinancialStatement =
            arrayListOf(
                FinancialSummaryItemModel(
                    "abc", "Financial Statement",
                    arrayListOf("FY2017", "FY2018", "FY2019", "FY2020", "FY2021")
                ),
                FinancialSummaryItemModel(
                    "abc", "Total Revenue",
                    arrayListOf("20", "10", "25", "60", "35")
                ),
                FinancialSummaryItemModel(
                    "abc", "Net Income",
                    arrayListOf("10", "5", "7", "15", "9")
                )
            ),
            quarterFinancialStatement =
            arrayListOf(
                FinancialSummaryItemModel(
                    "abc", "Financial Statement",
                    arrayListOf("FY2017 Q1", "FY2018 Q2", "FY2019 Q3", "FY2020 Q4", "FY2021 Q5")
                ),
                FinancialSummaryItemModel(
                    "abc", "Total Revenue",
                    arrayListOf("124", "10", "55", "25", "10")
                ),
                FinancialSummaryItemModel(
                    "abc", "Net Income",
                    arrayListOf("50", "4", "7", "15", "5")
                )
            ),
            annualBalanceSheet =
            arrayListOf(
                FinancialSummaryItemModel(
                    "abc", "Balance Sheet",
                    arrayListOf("FY2017", "FY2018", "FY2019", "FY2020", "FY2021")
                ),
                FinancialSummaryItemModel(
                    "abc", "Banlance1",
                    arrayListOf("22", "55", "77", "57", "74")
                ),
                FinancialSummaryItemModel(
                    "abc", "Banlance2",
                    arrayListOf("12", "25", "38", "28", "37")
                )
            ),
            quarterBalanceSheet =
            arrayListOf(
                FinancialSummaryItemModel(
                    "abc", "Balance Sheet",
                    arrayListOf("FY2017 Q1", "FY2018 Q2", "FY2019 Q3", "FY2020 Q4", "FY2021 Q4")
                ),
                FinancialSummaryItemModel(
                    "abc", "Banlance1",
                    arrayListOf("33", "30", "44", "66", "71")
                ),
                FinancialSummaryItemModel(
                    "abc", "Banlance2",
                    arrayListOf("12", "25", "38", "28", "37")
                )
            )
        )
        mFinancialSummaryLiveData.value = dummyData
    }
}