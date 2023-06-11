package com.example.ashaapp.adapters

import android.annotation.SuppressLint
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

class adapter_rv_na(c: Context) :
    RecyclerView.Adapter<adapter_rv_na.MyViewHolder>() {
    var list = ArrayList<IncentivesEntity>()
    private var c: Context
    private var fullList = ArrayList<IncentivesEntity>()

    init {
        this.c = c
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.approved_card, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model: IncentivesEntity = list[position]
        holder.scheme_name_rv.text = model.req_scheme_name
        holder.date_tv.text = Date(model.req_date).toString()
        if (model.internetState) {
            holder.scheme_name_rv.setTextColor(ContextCompat.getColor(c, R.color.yellow))
            holder.date_tv.setTextColor(ContextCompat.getColor(c, R.color.grey))
        } else {
            holder.scheme_name_rv.setTextColor(ContextCompat.getColor(c, R.color.red))
            holder.date_tv.setTextColor(ContextCompat.getColor(c, R.color.grey))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<IncentivesEntity>) {
        fullList.clear()
        fullList.addAll(newList)

        list.clear()
        list.addAll(fullList)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var scheme_name_rv: TextView
        var date_tv: TextView

        init {
            scheme_name_rv = itemView.findViewById(R.id.tv_scheme_name)
            date_tv = itemView.findViewById(R.id.tv_date)
        }
    }
}
