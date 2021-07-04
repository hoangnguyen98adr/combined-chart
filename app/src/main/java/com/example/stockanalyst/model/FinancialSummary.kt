package com.example.stockanalyst.model

data class FinancialSummary(
    val code: Int? = null,
    val currency: String? = null,
    val finalcialSummary: String? = null,
    val lastUpdated: String? = null,
    val msg: String? = null,
    val annualFinancialStatement: List<FinancialSummaryItemModel>? = null,
    val annualRatioAndOtherData: List<FinancialSummaryItemModel>? = null,
    val annualCashFlow: List<FinancialSummaryItemModel>? = null,
    val annualBalanceSheet: List<FinancialSummaryItemModel>? = null,
    val quarterCashFlow: List<FinancialSummaryItemModel>? = null,
    val quarterBalanceSheet: List<FinancialSummaryItemModel>? = null,
    val quarterFinancialStatement: List<FinancialSummaryItemModel>? = null,
    val quarterRatioAndOtherData: List<FinancialSummaryItemModel>? = null
)