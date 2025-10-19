package com.example.wakadp1.adapters
import com.example.wakadp1.data.WeekSummary
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wakadp1.R

class WeeklyAdapter(private val weeks: List<WeekSummary>) :
    RecyclerView.Adapter<WeeklyAdapter.WeekViewHolder>() {

    inner class WeekViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvWeekRange: TextView = view.findViewById(R.id.tvWeekRange)
        val recyclerDays: RecyclerView = view.findViewById(R.id.recyclerDays)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weekly, parent, false)
        return WeekViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        val week = weeks[position]
        holder.tvWeekRange.text = week.weekRange   // ✅ FIXED

        holder.recyclerDays.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.recyclerDays.adapter = DayAdapter(week.days)
    }

    override fun getItemCount(): Int = weeks.size
}