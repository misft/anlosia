package com.example.anlosia.ui.list.vacation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.anlosia.R
import com.example.anlosia.model.VacationResponse
import com.example.anlosia.util.Util
import org.w3c.dom.Text

class ListVacationAdapter(private val myDataset: List<VacationResponse>) : RecyclerView.Adapter<ListVacationAdapter.ListVacationViewHolder>() {
    class ListVacationViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListVacationAdapter.ListVacationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_vacation, parent, false)

        return ListVacationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListVacationViewHolder, position: Int) {
        holder.view.findViewById<TextView>(R.id.start_day).text = Util.dateYMDtoIndo(myDataset[position].start_day)
        holder.view.findViewById<TextView>(R.id.end_day).text = Util.dateYMDtoIndo(myDataset[position].end_day)
        holder.view.findViewById<TextView>(R.id.vacation_type).text = myDataset[position].vacation_type
        holder.view.findViewById<TextView>(R.id.vacation_status).text = myDataset[position].vacation_status
        holder.view.findViewById<TextView>(R.id.message).text = myDataset[position].message
    }

    override fun getItemCount() = myDataset.size
}