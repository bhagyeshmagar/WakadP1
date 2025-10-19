package com.example.wakadp1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wakadp1.R
import com.example.wakadp1.data.ActivityEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EntryAdapter(private val entries: List<ActivityEntry>) :
    RecyclerView.Adapter<EntryAdapter.EntryViewHolder>() {

    inner class EntryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvType: TextView = view.findViewById(R.id.tvType)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvNotes: TextView = view.findViewById(R.id.tvNotes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entry, parent, false)
        return EntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry = entries[position]
        holder.tvType.text = entry.activityType

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val start = timeFormat.format(Date(entry.startTime))
        val end = timeFormat.format(Date(entry.endTime))
        holder.tvTime.text = "$start - $end"

        holder.tvNotes.text = entry.notes ?: ""
    }

    override fun getItemCount(): Int = entries.size
}