package com.example.piotrkurek_wt_14_00

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.widget.doOnTextChanged
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONArray

class ExchangeActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var localCurrency: EditText
    private lateinit var foreignCurrency: EditText
    private lateinit var exchangeSpinner: Spinner
    private lateinit var container: LinearLayout
    private lateinit var errorMessage: TextView
    private var selectedCurrency = 0
    private var changesBlocked = false

    var onLoad: (()->Unit)? = null
    var onLoadError: (()->Unit)? = null

    var dataSet = mutableListOf<CurrencyDetails>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exchange_activty)

        localCurrency = findViewById(R.id.exchange_top_value)
        foreignCurrency = findViewById(R.id.exchange_bottom_value)
        errorMessage = findViewById(R.id.error_message)
        exchangeSpinner = findViewById(R.id.exchange_spinner)
        container = findViewById(R.id.container)

        DataHolder.prepare(applicationContext)


        onLoadError = {
            showLoadingError()
        }

        onLoad = {
            loadCurrency()
            showContainer()

            localCurrency.doOnTextChanged { text, _, _, _ ->
                if (text?.isEmpty() == false && !changesBlocked) {
                    calculateForeignCurrency()
                }
            }

            foreignCurrency.doOnTextChanged { text, _, _, _ ->
                if (text?.isEmpty() == false && !changesBlocked) {
                    calculateLocalCurrency()
                }
            }
        }
        fetchRates()
    }

    fun fetchRates() {
        var requestsLeft = 2
        val rates = mutableListOf<CurrencyDetails>()

        fun makeRequest(table: String): JsonArrayRequest {

            fun loadData(response: JSONArray?, table: String) {
                response?.let {
                    var jsonRates = response.getJSONObject(0).getJSONArray("rates")
                    val lastRates = response.getJSONObject(1).getJSONArray("rates")
                    val ratesCount = jsonRates.length()
                    val tmpData = dataSet
                    for (i in 0 until ratesCount) {
                        val currencyCode = jsonRates.getJSONObject(i).getString("code")
                        val currencyRate = jsonRates.getJSONObject(i).getDouble("mid")
                        val lastRate = lastRates.getJSONObject(i).getDouble("mid")
                        val flag = DataHolder.getFlagCountry(currencyCode)
                        val currencyObject = CurrencyDetails(currencyCode, currencyRate, table, flag, currencyRate > lastRate)
                        tmpData.add(currencyObject)
                    }
                    rates.addAll(tmpData)
                }
            }

            val url = "http://api.nbp.pl/api/exchangerates/tables/${table}/last/2?format=json"
            val currenciesRatesRequest = JsonArrayRequest(Request.Method.GET, url,null,
                    { response ->
                        println("Success")
                        loadData(response, table)
                        requestsLeft--
                        if(requestsLeft == 0)
                            updateRates(rates)
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

        DataHolder.queue.add(tableA)
        DataHolder.queue.add(tableB)
    }

    fun updateRates(rates: List<CurrencyDetails>) {
        dataSet.clear()
        dataSet.addAll(
                rates.sortedBy { rate -> rate.currencyCode }
                        .distinctBy { rate -> rate.currencyCode }
        )
        onLoad?.invoke()
    }

    fun loadCurrency() {
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            dataSet.map { rate -> rate.currencyCode }
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            exchangeSpinner.adapter = adapter
            exchangeSpinner.onItemSelectedListener = this
            exchangeSpinner.setSelection(selectedCurrency)
        }
    }

    fun calculateLocalCurrency() {
        val rate = foreignCurrency.text.toString().toFloat() * dataSet[selectedCurrency].rate
        changesBlocked = true
        localCurrency.setText(rate.toString())
        changesBlocked = false
    }

    fun calculateForeignCurrency() {
        val rate = localCurrency.text.toString().toFloat() / dataSet[selectedCurrency].rate
        changesBlocked = true
        foreignCurrency.setText(rate.toString())
        changesBlocked = false
    }

    fun showLoadingError() {
        container.visibility = View.INVISIBLE
        errorMessage.visibility = View.VISIBLE
    }

    fun showContainer() {
        container.visibility = View.VISIBLE
        errorMessage.visibility = View.INVISIBLE
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        selectedCurrency = pos
        calculateLocalCurrency()
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Has to be there for some reason
    }
}