package com.example.ashaapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ashaapp.R
import com.example.ashaapp.room.user_incentives.IncentivesEntity
import java.util.Date

class adapter_rv(data: ArrayList<IncentivesEntity>, context: Context) :
    RecyclerView.Adapter<adapter_rv.MyViewHolder>() {
    private var data: ArrayList<IncentivesEntity>
    var context: Context

    init {
        this.data = data
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.approved_card, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model: IncentivesEntity = data[position]
        holder.scheme_name_rv.text = model.req_scheme_name
        holder.date_rv.text = Date(model.req_date).toString()
        holder.scheme_name_rv.setTextColor(ContextCompat.getColor(context, R.color.green))
        holder.date_rv.setTextColor(ContextCompat.getColor(context, R.color.grey))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var scheme_name_rv: TextView
        var date_rv: TextView

        init {
            scheme_name_rv = itemView.findViewById(R.id.tv_scheme_name)
            date_rv = itemView.findViewById(R.id.tv_date)
        }
    }
}
