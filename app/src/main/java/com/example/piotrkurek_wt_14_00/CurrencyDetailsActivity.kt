package com.example.piotrkurek_wt_14_00

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.json.JSONObject

class CurrencyDetailsActivity : AppCompatActivity() {
    internal lateinit var todayRate:TextView
    internal lateinit var yesterdayRate:TextView
    internal lateinit var monthlyLineChart:LineChart
    internal lateinit var weeklyLineChart:LineChart
    internal lateinit var currencyCode: String
    internal lateinit var currencyTable: String
    internal lateinit var monthlyData: Array<Pair<String,Double>>
    internal lateinit var weeklyData: Array<Pair<String,Double>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency_details)
        currencyCode = intent.getStringExtra("currencyCode")!!
        currencyTable = intent.getStringExtra("currencyTable")!!
        monthlyLineChart = findViewById(R.id.currency_monthly_rates)
        weeklyLineChart = findViewById(R.id.currency_weekly_rates)
        todayRate = findViewById(R.id.currency_today_rate)
        yesterdayRate = findViewById(R.id.currency_yesterday_rate)

        weeklyLineChart.xAxis.labelRotationAngle = -90f
        weeklyLineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        weeklyLineChart.extraBottomOffset = 45f
        weeklyLineChart.description.isEnabled = false
        weeklyLineChart.legend.isEnabled = false

        monthlyLineChart.xAxis.labelRotationAngle = -90f
        monthlyLineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        monthlyLineChart.extraBottomOffset = 45f
        monthlyLineChart.description.isEnabled = false
        monthlyLineChart.legend.isEnabled = false

        getData()
    }

    fun getData() {
        val queue = DataHolder.queue

        val url = "http://api.nbp.pl/api/exchangerates/rates/%s/%s/last/30?format=json".format(currencyTable,currencyCode)

        val historicRateRequest = JsonObjectRequest (
            Request.Method.GET, url, null,
                { response ->
                    println("Sucess")
                    loadDetails(response)
                    setData()
                },  { error ->
            showLoadingError()
            Log.d("details:Error", error.toString())
        }
        )

        queue.add(historicRateRequest)
    }

    fun showLoadingError() {
        todayRate.text = getText(R.string.currency_fetching_error)
    }

    private fun setData() {

        todayRate.text = getString(R.string.currency_today_rate, monthlyData.last().second)
        yesterdayRate.text = getString(R.string.currency_yesterday_rate, monthlyData[monthlyData.size-2].second)

        setLineChart(monthlyData, monthlyLineChart, 30)
        setLineChart(weeklyData, weeklyLineChart, 7)
    }

    private fun setLineChart(data: Array<Pair<String,Double>>, lineChart: LineChart, days:Int) {
        var entries = ArrayList<Entry>()
        for ((index, element) in data.withIndex()) {
            entries.add(Entry(index.toFloat(), element.second.toFloat()))
        }
        var entriesDataSet = LineDataSet(entries, "Kurs")
        val lineData = LineData(entriesDataSet)
        lineChart.data = lineData

        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(data.map { it.first }.toTypedArray())

        lineChart.invalidate()
    }

    private fun loadDetails(response: JSONObject?){
        response?.let {
            val rates = response.getJSONArray("rates")
            val ratesCount = rates.length()
            val tmpData = arrayOfNulls<Pair<String,Double>>(ratesCount)
            for(i in 0 until ratesCount) {
                val date = rates.getJSONObject(i).getString("effectiveDate")
                val currencyRate = rates.getJSONObject(i).getDouble("mid")
                tmpData[i] = Pair(date, currencyRate)
            }

            monthlyData = tmpData as Array<Pair<String, Double>>
            weeklyData = monthlyData.copyOfRange(0,7)
        }
    }
}