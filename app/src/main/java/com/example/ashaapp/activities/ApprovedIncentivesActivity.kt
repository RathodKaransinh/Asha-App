package com.example.ashaapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ashaapp.adapters.adapter_rv
import com.example.ashaapp.databinding.ActivityApprovedIncentivesBinding
import com.example.ashaapp.room.user_incentives.DB
import com.example.ashaapp.room.user_incentives.IncentivesDao
import com.example.ashaapp.room.user_incentives.IncentivesEntity

class ApprovedIncentivesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApprovedIncentivesBinding
    private var data: ArrayList<IncentivesEntity>? = null
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

        data = userIncentivesDAO.approvedSchemes() as ArrayList<IncentivesEntity>?
        if (data == null) {
            Toast.makeText(this, "Nothing to show!", Toast.LENGTH_SHORT).show()
        } else {
            adapter = adapter_rv(data!!, this)
            binding.approvedList.layoutManager = LinearLayoutManager(this)
            binding.approvedList.adapter = adapter
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