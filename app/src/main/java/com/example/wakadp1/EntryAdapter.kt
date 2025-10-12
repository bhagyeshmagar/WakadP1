package com.example.wakadp1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wakadp1.data.ActivityEntry
import java.text.SimpleDateFormat
import java.util.*
import com.example.wakadp1.R

class EntryAdapter : RecyclerView.Adapter<EntryAdapter.VH>() {

    private val items = ArrayList<ActivityEntry>()

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvType: TextView = view.findViewById(R.id.tvEntryType)
        val tvTime: TextView = view.findViewById(R.id.tvEntryTime)
        val tvNotes: TextView = view.findViewById(R.id.tvEntryNotes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entry, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val it = items[position]
        holder.tvType.text = it.activityType

        // if startTime and endTime are stored as Long (millis), format them; otherwise adapt
        try {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val start = Date(it.startTime)
            val end = Date(it.endTime)
            holder.tvTime.text = "${sdf.format(start)} - ${sdf.format(end)}"
        } catch (e: Exception) {
            // fallback: show raw strings if any problem
            holder.tvTime.text = "${it.startTime} - ${it.endTime}"
        }

        holder.tvNotes.text = it.notes ?: ""
    }

    override fun getItemCount(): Int = items.size

    fun submitList(list: List<ActivityEntry>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}
