package com.example.wakadp1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wakadp1.R
import com.example.wakadp1.data.WeekSummary

class WeeklyAdapter(private val weeks: List<WeekSummary>) :
    RecyclerView.Adapter<WeeklyAdapter.WeekVH>() {

    inner class WeekVH(view: View) : RecyclerView.ViewHolder(view) {
        val tvWeekRange: TextView = view.findViewById(R.id.tvWeekRange)
        val recyclerDays: RecyclerView = view.findViewById(R.id.recyclerDays)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_weekly, parent, false)
        return WeekVH(v)
    }

    override fun onBindViewHolder(holder: WeekVH, position: Int) {
        val week = weeks[position]
        holder.tvWeekRange.text = week.rangeText
        holder.recyclerDays.adapter = DayAdapter(week.days)
    }

    override fun getItemCount(): Int = weeks.size
}
