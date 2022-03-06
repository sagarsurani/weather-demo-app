package com.weather.android.app.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weather.android.app.R
import com.weather.android.app.data.models.WeatherReportData
import kotlinx.android.synthetic.main.item_weather_report.view.*

class WeatherReportAdapter :
    RecyclerView.Adapter<WeatherReportAdapter.WeatherReportViewHolder>() {
    private var weatherReportList: ArrayList<WeatherReportData>? = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_weather_report,
            parent,
            false
        )
        return WeatherReportViewHolder(view)
    }


    override fun onBindViewHolder(holder: WeatherReportViewHolder, position: Int) {
        holder.bind(weatherReportList!![position])
    }

    override fun getItemCount(): Int {
        return weatherReportList!!.size
    }

    fun setList(daily: ArrayList<WeatherReportData>) {
        weatherReportList = daily
        notifyDataSetChanged()
    }

    class WeatherReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(weatherReport: WeatherReportData) {
            itemView.tvDay.text = weatherReport.day
            itemView.tvDate.text = weatherReport.date
            itemView.tvTemperature.text = weatherReport.temp
            itemView.tvDesc.text = weatherReport.message
            itemView.tvSubDesc.text = itemView.context.getString(
                R.string.weather_desc,
                weatherReport.maxTemp,
                weatherReport.minTemp
            )
        }
    }
}