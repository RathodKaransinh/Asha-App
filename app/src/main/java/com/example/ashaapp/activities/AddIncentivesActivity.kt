package com.example.ashaapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ashaapp.adapters.adapter_rv_na
import com.example.ashaapp.databinding.ActivityAddIncentivesBinding
import com.example.ashaapp.fragments.BottomSheetFragment
import com.example.ashaapp.room.notapprovedschemes.NotApprovedSchemesDAO
import com.example.ashaapp.room.notapprovedschemes.NotApprovedSchemesDB
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class AddIncentivesActivity : AppCompatActivity(){
    private lateinit var adapter_rv_na: adapter_rv_na
    private lateinit var binding: ActivityAddIncentivesBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var notApprovedSchemesDAO: NotApprovedSchemesDAO

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddIncentivesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initDB()

        binding.notApprovedList.setHasFixedSize(true)
        binding.notApprovedList.layoutManager=LinearLayoutManager(this)
        adapter_rv_na = adapter_rv_na(this)
        binding.notApprovedList.adapter = adapter_rv_na

        notApprovedSchemesDAO.getalldata().observe(this) {
            it?.let {
                adapter_rv_na.updateList(it)
            }
        }

        binding.addService.setOnClickListener{
            val dialog = BottomSheetFragment(isNetworkAvailable())
            dialog.show(supportFragmentManager, dialog.tag)
        }
    }

    private fun initDB(){
        db = Firebase.firestore

        val notApprovedSchemesDB = NotApprovedSchemesDB.getDatabase(this)
        notApprovedSchemesDAO = notApprovedSchemesDB.dao()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}