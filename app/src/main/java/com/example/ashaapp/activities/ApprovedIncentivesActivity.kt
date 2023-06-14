package com.example.ashaapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ashaapp.adapters.adapter_rv
import com.example.ashaapp.databinding.ActivityApprovedIncentivesBinding
import com.example.ashaapp.room.user_incentives.DB
import com.example.ashaapp.room.user_incentives.IncentivesDao

class ApprovedIncentivesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApprovedIncentivesBinding
    private lateinit var adapter: adapter_rv
    private lateinit var userIncentivesDAO: IncentivesDao

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApprovedIncentivesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.approvedToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val approvedSchemesDB = DB.getDatabase(this)
        userIncentivesDAO = approvedSchemesDB.dao()

        adapter = adapter_rv(this)
        binding.approvedList.layoutManager = LinearLayoutManager(this)
        binding.approvedList.adapter = adapter

        userIncentivesDAO.approvedSchemes().observe(this) {
            it?.let {
                adapter.updateList(it)
            }
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