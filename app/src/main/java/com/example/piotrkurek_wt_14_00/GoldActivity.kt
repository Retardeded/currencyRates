package com.example.piotrkurek_wt_14_00

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

import com.example.piotrkurek_wt_14_00.objects.GoldData
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class GoldActivity : AppCompatActivity() {
    private lateinit var todayRate: TextView
    private lateinit var monthlyLineChart: LineChart

    private var monthlyData = mutableListOf<GoldData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gold)

        todayRate = findViewById(R.id.currency_current_rate)
        monthlyLineChart = findViewById(R.id.gold_monthly_rates)

        monthlyLineChart.legend.isEnabled = false
        monthlyLineChart.xAxis.labelRotationAngle = -90f
        monthlyLineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        monthlyLineChart.extraBottomOffset = 45f

        fetchRates()
    }

    fun fetchRates() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.nbp.pl/api/cenyzlota/last/30?format=json"
        val req = JsonArrayRequest(
                Request.Method.GET, url, null,
                { response ->
                    val collectionType: Type =
                            object : TypeToken<Collection<GoldData?>?>() {}.type
                    val data: Collection<GoldData> =
                            Gson().fromJson(response.toString(), collectionType)
                    monthlyData = data as MutableList<GoldData>
                    setData()
                },
                { error ->
                    showLoadingError()
                    Log.d("gold:Error", error.toString())
                }
        )
        queue.add(req)
    }

    fun showLoadingError() {
        todayRate.text = getText(R.string.currency_fetching_error)
    }

    private fun setData() {
        todayRate.text = "${monthlyData[0].cena} PLN"
        setLineChart(monthlyData, monthlyLineChart, 30)
    }

    private fun setLineChart(data: MutableList<GoldData>, lineChart: LineChart, days:Int) {
        var entries = ArrayList<Entry>()

        for ((index, element) in data.withIndex()) {
            entries.add(Entry(index.toFloat(), element.cena))
        }

        var entriesDataSet = LineDataSet(entries, "Kurs")
        val lineData = LineData(entriesDataSet)
        lineChart.data = lineData

        val description = Description()
        description.text = "Kurs złota z ostatnich %s dni".format(days)
        lineChart.description = description

        lineChart.invalidate()
    }

}