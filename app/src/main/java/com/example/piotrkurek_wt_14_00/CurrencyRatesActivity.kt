package com.example.piotrkurek_wt_14_00

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.example.piotrkurek_wt_14_00.DataHolder.queue
import org.json.JSONArray

class CurrencyRatesActivity : AppCompatActivity() {

    internal lateinit var recycler: RecyclerView
    internal lateinit var adapter: CurrenciesListAdapter
    internal lateinit var dataSet:MutableList<CurrencyDetails>
    internal lateinit var titleTextView:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency_rates)

        titleTextView = findViewById(R.id.text_view_rates)
        recycler = findViewById(R.id.recyler_rates)
        recycler.layoutManager = LinearLayoutManager(applicationContext)

        DataHolder.prepare(applicationContext)

        fetchRates()
    }

    fun fetchRates() {
        dataSet = mutableListOf()
        adapter = CurrenciesListAdapter(emptyArray(), this)
        recycler.adapter = adapter
        var requestsLeft = 2

        fun makeRequest(table: String): JsonArrayRequest {
            val url = "http://api.nbp.pl/api/exchangerates/tables/${table}/last/2?format=json"
            val currenciesRatesRequest = JsonArrayRequest(Request.Method.GET, url,null,
                    { response ->
                        println("Success")
                        loadData(response, table)
                        adapter.dataSet = dataSet.toTypedArray()
                        requestsLeft--
                        if(requestsLeft == 0)
                            adapter.notifyDataSetChanged()
                    },
                    { error ->
                        showLoadingError()
                        Log.d("rates:Error", error.toString())
                    }
            )
            return currenciesRatesRequest
        }

        val tableA = makeRequest("a")
        val tableB = makeRequest("b")

        queue.add(tableA)
        queue.add(tableB)
    }

    fun showLoadingError() {
        titleTextView.text = getText(R.string.currency_fetching_error)
    }

    private fun loadData(response: JSONArray?, table: String) {
        response?.let {
            val rates = response.getJSONObject(0).getJSONArray("rates")
            val lastRates = response.getJSONObject(1).getJSONArray("rates")
            val ratesCount = rates.length()
            val tmpData = dataSet
            for (i in 0 until ratesCount) {
                val currencyCode = rates.getJSONObject(i).getString("code")
                val currencyRate = rates.getJSONObject(i).getDouble("mid")
                val lastRate = lastRates.getJSONObject(i).getDouble("mid")
                val flag = DataHolder.getFlagCountry(currencyCode)
                val currencyObject = CurrencyDetails(currencyCode, currencyRate,table,flag, currencyRate > lastRate)
                tmpData.add(currencyObject)
            }

            this.dataSet = tmpData
        }
    }
}