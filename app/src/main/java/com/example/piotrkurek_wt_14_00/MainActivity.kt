package com.example.piotrkurek_wt_14_00

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View


//Piotr Kurek
//Jest raczej wszystko ale nie jakoś bardzo dobrze, np. strzałki są w góre i w dół, ale kolor się coś nie wyświetla

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startCurrencyRatesActivity(view: View) {
        val intent = Intent(this, CurrencyRatesActivity::class.java)
        startActivity(intent)
    }

    fun startGoldActivity(view: View) {
        val intent = Intent(this, GoldActivity::class.java)
        startActivity(intent)
    }

    fun startExchangeActivity(view: View) {
        val intent = Intent(this, ExchangeActivity::class.java)
        startActivity(intent)
    }
}