package com.example.ashaapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ashaapp.adapters.adapter_rv_na
import com.example.ashaapp.databinding.ActivityAddIncentivesBinding
import com.example.ashaapp.fragments.BottomSheetFragment
import com.example.ashaapp.room.notapprovedschemes.NotApprovedSchemesDAO
import com.example.ashaapp.room.notapprovedschemes.NotApprovedSchemesDB
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AddIncentivesActivity : AppCompatActivity() {
    private lateinit var adapter: adapter_rv_na
    private lateinit var binding: ActivityAddIncentivesBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var notApprovedSchemesDAO: NotApprovedSchemesDAO
    private val uid = Firebase.auth.uid
    private lateinit var currentYear: String
    private lateinit var currentMonth: String

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddIncentivesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.notApprovedToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initDB()

        binding.notApprovedList.setHasFixedSize(true)
        binding.notApprovedList.layoutManager = LinearLayoutManager(this)
        adapter = adapter_rv_na(this)
        binding.notApprovedList.adapter = adapter

        notApprovedSchemesDAO.getalldata().observe(this) {
            it?.let {
                adapter.updateList(it)
            }
        }

        binding.addService.setOnClickListener {
            val dialog = BottomSheetFragment(isNetworkAvailable())
            dialog.show(supportFragmentManager, BottomSheetFragment.TAG)
        }
    }

    private fun initDB() {
        val calendar: Calendar = Calendar.getInstance()
        val yearDateFormat = SimpleDateFormat("yyyy", Locale.US)
        currentYear = yearDateFormat.format(calendar.time)

        val monthDateFormat = SimpleDateFormat("MMM", Locale.US)
        currentMonth = monthDateFormat.format(calendar.time)

        db = Firebase.firestore

        val notApprovedSchemesDB = NotApprovedSchemesDB.getDatabase(this)
        notApprovedSchemesDAO = notApprovedSchemesDB.dao()

        if (isNetworkAvailable()) {
            val offlineSchemes = notApprovedSchemesDAO.offlineSchemes()

            if (!offlineSchemes.isNullOrEmpty()) {
                binding.notApprovedList.visibility = View.INVISIBLE
                binding.addService.visibility = View.INVISIBLE
                binding.addIncentivesProgressBar.visibility = View.VISIBLE
                db.collection(currentYear).document(currentMonth).collection("users")
                    .document(uid!!).get().addOnSuccessListener {
                        val notApproved = it.data?.get("notApproved") as ArrayList<Map<String, Any>>
                        for (scheme in offlineSchemes) {
                            notApproved.add(
                                hashMapOf(
                                    "name" to scheme.req_scheme_name,
                                    "time" to scheme.req_date,
                                    "value" to scheme.value_of_schemes
                                )
                            )
                        }
                        db.collection(currentYear).document(currentMonth).collection("users")
                            .document(uid).update("notApproved", notApproved).addOnSuccessListener {
                                Toast.makeText(
                                    this, "Offline Data Updated Successfully", Toast.LENGTH_LONG
                                ).show()
                                notApprovedSchemesDAO.updatestate()
                                binding.notApprovedList.visibility = View.VISIBLE
                                binding.addService.visibility = View.VISIBLE
                                binding.addIncentivesProgressBar.visibility = View.INVISIBLE
                            }.addOnFailureListener { exception ->
                                Log.d("Error", exception.toString())
                                binding.notApprovedList.visibility = View.VISIBLE
                                binding.addService.visibility = View.VISIBLE
                                binding.addIncentivesProgressBar.visibility = View.INVISIBLE
                            }
                    }.addOnFailureListener {
                        Toast.makeText(
                            this, "Offline Data Updated UnSuccessfully", Toast.LENGTH_LONG
                        ).show()
                        binding.notApprovedList.visibility = View.VISIBLE
                        binding.addService.visibility = View.VISIBLE
                        binding.addIncentivesProgressBar.visibility = View.INVISIBLE
                    }
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
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