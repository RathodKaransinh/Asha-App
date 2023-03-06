package com.example.ashaapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room.databaseBuilder
import com.example.ashaapp.R
import com.example.ashaapp.adapters.adapter_rv
import com.example.ashaapp.room.approvedschemes.ApprovedSchemesDAO
import com.example.ashaapp.room.approvedschemes.ApprovedSchemesDB
import com.example.ashaapp.room.approvedschemes.ApprovedSchemesEntity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList


class ApprovedIncentivesActivity : AppCompatActivity() {
    private lateinit var approvedRecyclerView: RecyclerView
    var data: ArrayList<ApprovedSchemesEntity>? = null
    private lateinit var adapter: adapter_rv
    private lateinit var db: FirebaseFirestore
    private lateinit var approvedSchemesDAO: ApprovedSchemesDAO


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approved_incentives)

        val approvedSchemesDB = ApprovedSchemesDB.getDatabase(this)
        approvedSchemesDAO = approvedSchemesDB.dao()

        db = Firebase.firestore

        approvedRecyclerView = findViewById(R.id.approved_rv)

        data = approvedSchemesDAO.getalldata() as ArrayList<ApprovedSchemesEntity>?
        if (data == null){
            Toast.makeText(this, "Nothing to show!", Toast.LENGTH_SHORT).show()
        } else{
            adapter = adapter_rv(data!!, applicationContext)
            approvedRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
            approvedRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
}