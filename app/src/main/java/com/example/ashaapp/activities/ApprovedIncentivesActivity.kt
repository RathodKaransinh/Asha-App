package com.example.ashaapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ashaapp.R
import com.example.ashaapp.adapters.adapter_rv
import com.example.ashaapp.room.approvedschemes.ApprovedSchemesDAO
import com.example.ashaapp.room.approvedschemes.ApprovedSchemesDB
import com.example.ashaapp.room.approvedschemes.ApprovedSchemesEntity

class ApprovedIncentivesActivity : AppCompatActivity() {

    private lateinit var approvedRecyclerView: RecyclerView
    var data: ArrayList<ApprovedSchemesEntity>? = null
    private lateinit var adapter: adapter_rv
    private lateinit var approvedSchemesDAO: ApprovedSchemesDAO

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approved_incentives)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.approvedToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val approvedSchemesDB = ApprovedSchemesDB.getDatabase(this)
        approvedSchemesDAO = approvedSchemesDB.dao()

        approvedRecyclerView = findViewById(R.id.approvedList)

        data = approvedSchemesDAO.getalldata() as ArrayList<ApprovedSchemesEntity>?
        if (data == null){
            Toast.makeText(this, "Nothing to show!", Toast.LENGTH_SHORT).show()
        } else{
            adapter = adapter_rv(data!!, this)
            approvedRecyclerView.layoutManager = LinearLayoutManager(this)
            approvedRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            onBackPressedDispatcher.onBackPressed()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}