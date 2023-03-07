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
import com.example.ashaapp.room.notapprovedschemes.NotApprovedSchemesDAO
import com.example.ashaapp.room.notapprovedschemes.NotApprovedSchemesDB
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var notApprovedSchemesDAO: NotApprovedSchemesDAO
    private val uid = Firebase.auth.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)

        initDB()

        val calendar: Calendar = Calendar.getInstance()
        val yearDateFormat = SimpleDateFormat("yyyy", Locale.US)
        val currentYear = yearDateFormat.format(calendar.time)

        val monthDateFormat = SimpleDateFormat("MMM", Locale.US)
        val currentMonth = monthDateFormat.format(calendar.time)

        val offlineSchemes = notApprovedSchemesDAO.offlineSchemes()

        if (offlineSchemes != null) {
            if (offlineSchemes.isNotEmpty()){
                if (isNetworkAvailable()) {
                    binding.container.visibility = View.INVISIBLE
                    binding.mainProgressBar.visibility = View.VISIBLE
                    db.collection(currentYear).document(currentMonth).collection("users").document(uid!!)
                        .get().addOnSuccessListener {
                            Log.d("not_app", (it.data?.get("notApproved") as ArrayList<Map<String, Any>>).toString())
                            val notApproved =
                                it.data?.get("notApproved") as ArrayList<Map<String, Any>>
                            for (scheme in offlineSchemes){
                                notApproved.add(hashMapOf("name" to scheme.req_scheme_name, "time" to scheme.req_date, "value" to scheme.value_of_schemes))
                            }
                            db.collection(currentYear).document(currentMonth).collection("users").document(uid)
                                .update("notApproved", notApproved)
                                .addOnSuccessListener {
                                    Toast.makeText(view.context, "Offline Data Updated Successfully", Toast.LENGTH_LONG).show()
                                    notApprovedSchemesDAO.updatestate()
                                }
                                .addOnFailureListener { exception ->
                                    Log.d("Error", exception.toString())
                                }
                            binding.mainProgressBar.visibility = View.INVISIBLE
                            binding.container.visibility = View.VISIBLE
                        }
                        .addOnFailureListener {
                            Toast.makeText(view.context, "Offline Data Updated UnSuccessfully", Toast.LENGTH_LONG).show()
                            binding.mainProgressBar.visibility = View.INVISIBLE
                            binding.container.visibility = View.VISIBLE
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
                    loadFragmentWithBackStackEnabled(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        binding.addIncentivesButton.setOnClickListener {
            startActivity(Intent(this, AddIncentivesActivity::class.java))
        }
    }

    private fun initDB(){
        db = Firebase.firestore

        val notApprovedSchemesDB = NotApprovedSchemesDB.getDatabase(this)
        notApprovedSchemesDAO = notApprovedSchemesDB.dao()
    }

    private fun loadFragment(fragment: Fragment) {
        val fm: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        ft.replace(R.id.container, fragment, "fragment")
        ft.commit()
    }

    private fun loadFragmentWithBackStackEnabled(fragment: Fragment) {
        val fm: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        ft.replace(R.id.container, fragment, "fragment")
        ft.addToBackStack("fragment")
        ft.commit()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar,menu)
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