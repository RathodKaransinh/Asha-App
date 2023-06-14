package com.example.ashaapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.ashaapp.R
import com.example.ashaapp.room.user_incentives.IncentivesEntity
import com.google.android.material.appbar.AppBarLayout
import java.util.Date


class adapter_rv_na(c: Context, toolbar: AppBarLayout, listener: OnDeletePressed) :
    RecyclerView.Adapter<adapter_rv_na.MyViewHolder>() {
    private var list = ArrayList<IncentivesEntity>()
    private var isEnable = false
    var isSelectAll = false
    var selectList = ArrayList<IncentivesEntity>()
    private var c: Context
    private var fullList = ArrayList<IncentivesEntity>()
    private var toolbar: AppBarLayout
    private var listener: OnDeletePressed

    init {
        this.c = c
        this.toolbar = toolbar
        this.listener = listener
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

        holder.itemView.setOnLongClickListener {
            toolbar.visibility = View.GONE
            if (!isEnable) {
                it.startActionMode(ActionModeCallBack(holder, holder.card, holder.cardColor))
            } else {
                clickItem(holder, holder.card, holder.cardColor)
            }
            true
        }

        holder.itemView.setOnClickListener {
            if (isEnable) {
                clickItem(holder, holder.card, holder.cardColor)
            }
        }

        if (isEnable) {
            if (selectList.contains(model)) {
                holder.card.setCardBackgroundColor(Color.LTGRAY)
            } else {
                holder.card.setCardBackgroundColor(holder.cardColor)
            }
        } else {
            holder.card.setCardBackgroundColor(holder.cardColor)
        }
    }

    private fun clickItem(holder: ViewHolder, cardHolder: CardView, cardColor: ColorStateList) {
        val s: IncentivesEntity = fullList[holder.adapterPosition]

        if (!selectList.contains(s)) {
            cardHolder.setCardBackgroundColor(Color.LTGRAY)
            selectList.add(s)
        } else {
            cardHolder.setCardBackgroundColor(cardColor)
            selectList.remove(s)
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

    inner class MyViewHolder(itemView: View) : ViewHolder(itemView) {
        var scheme_name_rv: TextView
        var date_tv: TextView
        var card: CardView
        var cardColor: ColorStateList

        init {
            scheme_name_rv = itemView.findViewById(R.id.tv_scheme_name)
            date_tv = itemView.findViewById(R.id.tv_date)
            card = itemView.findViewById(R.id.rvCard)
            cardColor = card.cardBackgroundColor
        }
    }

    inner class ActionModeCallBack(
        private val holder: ViewHolder,
        private var card: CardView,
        private var cardColor: ColorStateList
    ) :
        ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val menuInflater: MenuInflater = mode?.menuInflater!!
            menuInflater.inflate(R.menu.recycler_view_actions, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            isEnable = true
            clickItem(holder, card, cardColor)
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item!!.itemId) {
                R.id.menu_delete -> {
                    if (selectList.isEmpty()) {
                        Toast.makeText(c, "Select Items to delete!", Toast.LENGTH_SHORT).show()
                    } else {
                        val builder = AlertDialog.Builder(c)
                        builder.setMessage("Do you want to delete this incentive permanently?")
                        builder.setTitle("Are you sure?")
                        builder.setCancelable(false)
                        builder.setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        builder.setPositiveButton("Yes") { dialog, _ ->
                            listener.deleteIncentives(selectList)
                            mode?.finish()
                            toolbar.visibility = View.VISIBLE
                            dialog.dismiss()
                        }
                        val alertDialog = builder.create()
                        alertDialog.show()
                    }
                }

                R.id.menu_select_all -> {
                    if (selectList.size == fullList.size) {
                        isSelectAll = false
                        selectList.clear()
                    } else {
                        isSelectAll = true
                        selectList.clear()
                        selectList.addAll(fullList)
                    }
                    notifyDataSetChanged()
                }
            }
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            isEnable = false
            isSelectAll = false
            selectList.clear()
            notifyDataSetChanged()
            toolbar.visibility = View.VISIBLE
        }
    }
}

interface OnDeletePressed {
    fun deleteIncentives(list: ArrayList<IncentivesEntity>)
}
