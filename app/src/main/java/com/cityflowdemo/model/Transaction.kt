package com.cityflowdemo.model

data class Transaction(
    var hash: String? = "",
    var amount: Double? = null,
    var value: Long? = 0,
    var time: Long? = null,
    var timeInDisplay: String? = ""
) {
}