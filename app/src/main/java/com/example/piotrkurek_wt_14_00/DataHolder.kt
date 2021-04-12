package com.example.piotrkurek_wt_14_00

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley.newRequestQueue
import com.blongho.country_data.Country
import com.blongho.country_data.World

object DataHolder {
    lateinit var queue: RequestQueue
    private lateinit var countriesData: List<Country>

    fun prepare(contex: Context){
        queue = newRequestQueue(contex)
        World.init(contex)
        countriesData = World.getAllCountries().distinctBy{it.currency.code}

    }

    fun getFlagCountry(countryCode: String): Int {
        if(countryCode == "USD") {
            return R.drawable.us
        } else if (countryCode == "EUR") {
            return R.drawable.eu
        } else if (countryCode == "GBP") {
            return R.drawable.gb
        } else if (countryCode == "CHF") {
            return R.drawable.ch
        } else if (countryCode == "NKD") {
            return R.drawable.hk
        } else {
            return countriesData.find { it.currency.code == countryCode }?.flagResource ?: World.getWorldFlag()
        }
    }
}