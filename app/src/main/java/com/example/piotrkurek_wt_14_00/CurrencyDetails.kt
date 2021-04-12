package com.example.piotrkurek_wt_14_00

class CurrencyDetails(var currencyCode:String, var rate:Double, var table:String, var flag:Int = 0, val wentUp: Boolean) {

    private var historicRates: List<Pair<String,Double>>? = null
}
