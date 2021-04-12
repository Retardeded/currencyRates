package com.example.piotrkurek_wt_14_00

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

class CurrenciesListAdapter(var dataSet: Array<CurrencyDetails>, var contex: Context) : RecyclerView.Adapter<CurrenciesListAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val currencyCodeTextView: TextView
        val rateTextView: TextView
        val flagView: ImageView
        val currency_status: ImageView

        init {
            currencyCodeTextView = view.findViewById(R.id.currency_code)
            rateTextView = view.findViewById(R.id.currency_today_rate)
            flagView = view.findViewById(R.id.currency_flag)
            currency_status = view.findViewById(R.id.currency_arrow)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.currency_row, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val currency = dataSet[position]

        val arrowImage = when(currency.wentUp) {
            true -> R.drawable.green_arrow
            false -> R.drawable.red_arrow
        }

        viewHolder.currencyCodeTextView.text = currency.currencyCode
        viewHolder.rateTextView.text = currency.rate.toString()
        viewHolder.flagView.setImageResource(currency.flag)
        viewHolder.currency_status.setImageResource(arrowImage)

        viewHolder.itemView.setOnClickListener{ goToDetails(currency.currencyCode, currency.table)}

    }

    private fun goToDetails(currencyCode: String, currencyTable:String) {
        val intent = Intent(contex, CurrencyDetailsActivity::class.java).apply{
            putExtra("currencyCode", currencyCode)
            putExtra("currencyTable", currencyTable)
        }
        contex.startActivity(intent)
    }

    override fun getItemCount() = dataSet.size
}