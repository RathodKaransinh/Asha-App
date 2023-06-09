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
import com.example.ashaapp.fragments.ProfileFragment
import com.example.ashaapp.room.allschemes.AllSchemesDAO
import com.example.ashaapp.room.allschemes.AllSchemesDB
import com.example.ashaapp.room.allschemes.AllSchemesEntity
import com.example.ashaapp.room.approvedschemes.ApprovedSchemesDAO
import com.example.ashaapp.room.approvedschemes.ApprovedSchemesDB
import com.example.ashaapp.room.approvedschemes.ApprovedSchemesEntity
import com.example.ashaapp.room.notapprovedschemes.NotApprovedSchemesDAO
import com.example.ashaapp.room.notapprovedschemes.NotApprovedSchemesDB
import com.example.ashaapp.room.notapprovedschemes.NotApprovedSchemesEntity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var allSchemesDAO: AllSchemesDAO
    private lateinit var approvedSchemesDAO: ApprovedSchemesDAO
    private lateinit var notApprovedSchemesDAO: NotApprovedSchemesDAO
    private var areSchemesUpdated = false
    private var areApprovedSchemesUpdated = false
    private val uid = Firebase.auth.uid
    private lateinit var currentYear: String
    private lateinit var currentMonth: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.mainTopAppBar)

        initDB()

        val calendar: Calendar = Calendar.getInstance()
        val yearDateFormat = SimpleDateFormat("yyyy", Locale.US)
        currentYear = yearDateFormat.format(calendar.time)

        val monthDateFormat = SimpleDateFormat("MMM", Locale.US)
        currentMonth = monthDateFormat.format(calendar.time)

        if (isNetworkAvailable()) {
            binding.container.visibility = View.INVISIBLE
            binding.mainProgressBar.visibility = View.VISIBLE
            db.collection(currentYear).document(currentMonth).collection("users").document(uid!!)
                .get().addOnSuccessListener {
                    areSchemesUpdated = it.get("areSchemesUpdated") as Boolean
                    areApprovedSchemesUpdated = it.get("areApprovedSchemesUpdated") as Boolean

                    if (!areSchemesUpdated || !areApprovedSchemesUpdated) {
                        if (!areSchemesUpdated) {
                            allSchemesDAO.truncate()
                            db.collection("services").get().addOnSuccessListener { codes ->
                                for (code in codes) {
                                    val schemesList =
                                        code.data["schemes"] as ArrayList<Map<String, Any>>
                                    for (scheme in schemesList) {
                                        allSchemesDAO.insert(
                                            AllSchemesEntity(
                                                0,
                                                code.id,
                                                scheme["name"] as String,
                                                scheme["value"] as Long
                                            )
                                        )
                                    }
                                }
                                db.collection(currentYear).document(currentMonth)
                                    .collection("users")
                                    .document(uid).update("areSchemesUpdated", true)
                                areSchemesUpdated = true
                                binding.mainProgressBar.visibility = View.INVISIBLE
                                binding.container.visibility = View.VISIBLE
                            }
                        }

                        if (!areApprovedSchemesUpdated) {
                            notApprovedSchemesDAO.deleteOnlineSchemes()
                            approvedSchemesDAO.truncate()
                            db.collection(currentYear).document(currentMonth).collection("users")
                                .document(uid).get().addOnSuccessListener { doc ->
                                    val approved =
                                        doc.data?.get("approved") as ArrayList<Map<String, Any>>?
                                    val notApproved =
                                        doc.data?.get("notApproved") as ArrayList<Map<String, Any>>?
                                    if (approved != null) {
                                        for (scheme in approved) {
                                            approvedSchemesDAO.insert(
                                                ApprovedSchemesEntity(
                                                    0,
                                                    scheme["name"] as String,
                                                    scheme["time"] as String,
                                                    scheme["value"] as Long
                                                )
                                            )
                                        }
                                    }
                                    if (notApproved != null) {
                                        for (scheme in notApproved) {
                                            notApprovedSchemesDAO.insert(
                                                NotApprovedSchemesEntity(
                                                    0,
                                                    scheme["name"] as String,
                                                    scheme["time"] as String,
                                                    scheme["value"] as Long,
                                                    true
                                                )
                                            )
                                        }
                                    }
                                    db.collection(currentYear).document(currentMonth)
                                        .collection("users")
                                        .document(uid).update("areApprovedSchemesUpdated", true)
                                    areApprovedSchemesUpdated = true
                                    Toast.makeText(
                                        this, "Schemes Updated Successfully", Toast.LENGTH_SHORT
                                    ).show()
                                    binding.mainProgressBar.visibility = View.INVISIBLE
                                    binding.container.visibility = View.VISIBLE
                                }
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

        notApprovedSchemesDAO.getalldata().observe(this) { data ->
            data?.let { _ ->
                val offlineSchemes = notApprovedSchemesDAO.offlineSchemes()

                if (!offlineSchemes.isNullOrEmpty()) {
                    if (isNetworkAvailable()) {
                        binding.container.visibility = View.INVISIBLE
                        binding.mainProgressBar.visibility = View.VISIBLE
                        db.collection(currentYear).document(currentMonth).collection("users")
                            .document(uid!!).get().addOnSuccessListener {
                                val notApproved =
                                    it.data?.get("notApproved") as ArrayList<Map<String, Any>>
                                for (scheme in offlineSchemes) {
                                    notApproved.add(
                                        hashMapOf(
                                            "name" to scheme.req_scheme_name,
                                            "time" to scheme.req_date,
                                            "value" to scheme.value_of_schemes
                                        )
                                    )
                                }
                                db.collection(currentYear).document(currentMonth)
                                    .collection("users")
                                    .document(uid).update("notApproved", notApproved)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            view.context,
                                            "Offline Data Updated Successfully",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        notApprovedSchemesDAO.updatestate()
                                        binding.mainProgressBar.visibility = View.INVISIBLE
                                        binding.container.visibility = View.VISIBLE
                                    }.addOnFailureListener { exception ->
                                        Log.d("Error", exception.toString())
                                        binding.mainProgressBar.visibility = View.INVISIBLE
                                        binding.container.visibility = View.VISIBLE
                                    }
                            }.addOnFailureListener {
                                Toast.makeText(
                                    view.context,
                                    "Offline Data Updated UnSuccessfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                binding.mainProgressBar.visibility = View.INVISIBLE
                                binding.container.visibility = View.VISIBLE
                            }
                    }
                }
            }
        }

        if (savedInstanceState == null) loadFragment(AnalyticsCard())

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_page -> {
                    loadFragment(AnalyticsCard())
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

    private fun initDB() {
        db = Firebase.firestore

        val allSchemesDB = AllSchemesDB.getDatabase(this)
        allSchemesDAO = allSchemesDB.dao()

        val approvedSchemesDB = ApprovedSchemesDB.getDatabase(this)
        approvedSchemesDAO = approvedSchemesDB.dao()

        val notApprovedSchemesDB = NotApprovedSchemesDB.getDatabase(this)
        notApprovedSchemesDAO = notApprovedSchemesDB.dao()
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
}