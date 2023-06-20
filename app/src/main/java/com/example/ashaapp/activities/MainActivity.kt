package com.example.ashaapp.activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.ashaapp.R
import com.example.ashaapp.databinding.ActivityMainBinding
import com.example.ashaapp.fragments.AnalyticsCard
import com.example.ashaapp.fragments.OnRefresh
import com.example.ashaapp.fragments.ProfileFragment
import com.example.ashaapp.room.allschemes.AllSchemesDAO
import com.example.ashaapp.room.allschemes.AllSchemesDB
import com.example.ashaapp.room.allschemes.AllSchemesEntity
import com.example.ashaapp.room.user_incentives.DB
import com.example.ashaapp.room.user_incentives.IncentivesDao
import com.example.ashaapp.room.user_incentives.IncentivesEntity
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

class MainActivity : AppCompatActivity(), OnRefresh {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var allSchemesDAO: AllSchemesDAO
    private lateinit var userIncentivesDAO: IncentivesDao
    private var areSchemesUpdated = true
    private var areApprovedSchemesUpdated = true
    private val uid = Firebase.auth.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.mainTopAppBar)

        initDB()

        updateIncentives()

//        offline incentives observer
        userIncentivesDAO.notApprovedSchemes().observe(this) { data ->
            data?.let {
                updateOfflineSchemes()
            }
        }

        if (savedInstanceState == null) loadFragment(AnalyticsCard(this))

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_page -> {
                    loadFragment(AnalyticsCard(this))
                    true
                }

                R.id.profile_page -> {
                    loadFragment(ProfileFragment(isNetworkAvailable()))
                    true
                }

                else -> false
            }
        }

        binding.addIncentivesButton.setOnClickListener {
            startActivity(Intent(this, AddIncentivesActivity::class.java))
        }
    }

    private fun updateIncentives() {
        if (isNetworkAvailable()) {
            binding.container.visibility = View.INVISIBLE
            binding.mainProgressBar.visibility = View.VISIBLE
            db.collection("users").document(uid!!).get().addOnSuccessListener {
                areSchemesUpdated = it.get("areSchemesUpdated") as Boolean
                areApprovedSchemesUpdated = it.get("areApprovedSchemesUpdated") as Boolean

                if (areSchemesUpdated || areApprovedSchemesUpdated) {
                    if (areSchemesUpdated) {
                        updateServices()
                    }

                    if (areApprovedSchemesUpdated) {
                        updateApprovedIncentives()
                    }
                } else {
                    binding.mainProgressBar.visibility = View.INVISIBLE
                    binding.container.visibility = View.VISIBLE
                }

            }.addOnFailureListener {
                Toast.makeText(this, "Failed to update flags", Toast.LENGTH_SHORT).show()
                binding.mainProgressBar.visibility = View.INVISIBLE
                binding.container.visibility = View.VISIBLE
            }
        }
    }

    private fun updateApprovedIncentives() {
        userIncentivesDAO.deleteOnlineSchemes()
        db.collection("users").document(uid!!).get().addOnSuccessListener { doc ->
            val incentives = doc.data?.get("incentives") as ArrayList<Map<String, Any>>?
            if (incentives != null) {
                for (incentive in incentives) {
                    userIncentivesDAO.insert(
                        IncentivesEntity(
                            0,
                            incentive["name"] as String,
                            (incentive["time"] as Timestamp).toDate().time,
                            incentive["value"] as Long,
                            incentive["isApproved"] as Boolean,
                            true,
                        )
                    )
                }
            }
            db.collection("users").document(uid).update("areApprovedSchemesUpdated", false)
            areApprovedSchemesUpdated = false
            Toast.makeText(
                this, "Schemes Updated Successfully", Toast.LENGTH_SHORT
            ).show()
            binding.mainProgressBar.visibility = View.INVISIBLE
            binding.container.visibility = View.VISIBLE
        }
    }

    private fun updateServices() {
        allSchemesDAO.truncate()
        db.collection("incentives").document("incentives").get().addOnSuccessListener { doc ->
            val map = doc.data!! as Map<String, ArrayList<Map<String, Any>>>
            map.forEach { entry ->
                val list = entry.value
                for (element in list) {
                    allSchemesDAO.insert(
                        AllSchemesEntity(
                            0, entry.key, element["name"] as String, element["value"] as Long
                        )
                    )
                }
            }
            db.collection("users").document(uid!!).update("areSchemesUpdated", false)
                .addOnSuccessListener {
                    binding.mainProgressBar.visibility = View.INVISIBLE
                    binding.container.visibility = View.VISIBLE
                }
                .addOnFailureListener {
                    binding.mainProgressBar.visibility = View.INVISIBLE
                    binding.container.visibility = View.VISIBLE
                }
            areSchemesUpdated = false
        }
    }

    private fun updateOfflineSchemes() {
        val offlineSchemes = userIncentivesDAO.offlineSchemes()

        if (!offlineSchemes.isNullOrEmpty()) {
            if (isNetworkAvailable()) {
                binding.container.visibility = View.INVISIBLE
                binding.mainProgressBar.visibility = View.VISIBLE
                db.collection("users").document(uid!!).get().addOnSuccessListener {
                    val incentives = it.data?.get("incentives") as ArrayList<Map<String, Any>>
                    for (scheme in offlineSchemes) {
                        incentives.add(
                            hashMapOf(
                                "name" to scheme.req_scheme_name,
                                "time" to Timestamp(Date(scheme.req_date)),
                                "value" to scheme.value_of_schemes,
                                "isApproved" to false,
                            )
                        )
                    }
                    db.collection("users").document(uid).update("incentives", incentives)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@MainActivity,
                                "Offline Data Updated Successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            userIncentivesDAO.updateState()
                            binding.mainProgressBar.visibility = View.INVISIBLE
                            binding.container.visibility = View.VISIBLE
                        }.addOnFailureListener { exception ->
                            Log.d("Error", exception.toString())
                            binding.mainProgressBar.visibility = View.INVISIBLE
                            binding.container.visibility = View.VISIBLE
                        }
                }.addOnFailureListener {
                    Toast.makeText(
                        this@MainActivity,
                        "Offline Data Updated UnSuccessfully",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.mainProgressBar.visibility = View.INVISIBLE
                    binding.container.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initDB() {
        db = Firebase.firestore

        val allSchemesDB = AllSchemesDB.getDatabase(this)
        allSchemesDAO = allSchemesDB.dao()

        val userIncentivesDB = DB.getDatabase(this)
        userIncentivesDAO = userIncentivesDB.dao()
    }

    private fun loadFragment(fragment: Fragment) {
        val fm: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        ft.replace(R.id.container, fragment, "fragment")
        ft.commit()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.approved -> {
            startActivity(Intent(this, ApprovedIncentivesActivity::class.java))
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onRefresh() {
        updateIncentives()
    }
}