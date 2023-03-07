package com.example.ashaapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ashaapp.adapters.adapter_rv_na
import com.example.ashaapp.databinding.ActivityAddIncentivesBinding
import com.example.ashaapp.fragments.BottomSheetFragment
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
import java.util.*

class AddIncentivesActivity : AppCompatActivity(){
    private lateinit var adapter_rv_na: adapter_rv_na
    private lateinit var binding: ActivityAddIncentivesBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var allSchemesDAO: AllSchemesDAO
    private lateinit var notApprovedSchemesDAO: NotApprovedSchemesDAO
    private lateinit var approvedSchemesDAO: ApprovedSchemesDAO
    private var areSchemesUpdated = false
    private var areApprovedSchemesUpdated = false
    private val uid = Firebase.auth.uid

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddIncentivesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initDB()

        val calendar: Calendar = Calendar.getInstance()
        val yearDateFormat = SimpleDateFormat("yyyy", Locale.US)
        val currentYear = yearDateFormat.format(calendar.time)

        val monthDateFormat = SimpleDateFormat("MMM", Locale.US)
        val currentMonth = monthDateFormat.format(calendar.time)

        if (isNetworkAvailable()) {
            binding.notApprovedList.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
            db.collection(currentYear).document(currentMonth).collection("users").document(uid!!)
                .get()
                .addOnSuccessListener{
                    areSchemesUpdated = it.get("areSchemesUpdated") as Boolean
                    areApprovedSchemesUpdated = it.get("areApprovedSchemesUpdated") as Boolean

                    if (!areSchemesUpdated){
                        allSchemesDAO.truncate()
                        db.collection("services")
                            .get().addOnSuccessListener{ codes ->
                                for (code in codes){
                                    val schemesList = code.data["schemes"] as ArrayList<Map<String, Any>>
                                    for (scheme in schemesList){
                                        allSchemesDAO.insert(AllSchemesEntity(0, code.id, scheme["name"] as String, scheme["value"] as Long))
                                    }
                                }
                            }
                        db.collection(currentYear).document(currentMonth).collection("users").document(uid).update("areSchemesUpdated", true)
                        areSchemesUpdated = true
                    }

                    if (!areApprovedSchemesUpdated){
                        notApprovedSchemesDAO.deleteOnlineSchemes()
                        approvedSchemesDAO.truncate()
                        db.collection(currentYear).document(currentMonth).collection("users").document(uid)
                            .get().addOnSuccessListener{ doc ->
                                val approved =
                                    doc.data?.get("approved") as ArrayList<Map<String, Any>>?
                                val notApproved =
                                    doc.data?.get("notApproved") as ArrayList<Map<String, Any>>?
                                if (approved!=null){
                                    for(scheme in approved){
                                        approvedSchemesDAO.insert(ApprovedSchemesEntity(0, scheme["name"] as String, scheme["time"] as String, scheme["value"] as Long))
                                    }
                                }
                                if (notApproved!=null){
                                    for(scheme in notApproved){
                                        notApprovedSchemesDAO.insert(NotApprovedSchemesEntity(0, scheme["name"] as String, scheme["time"] as String, scheme["value"] as Long, true))
                                    }
                                }
                            }
                        db.collection(currentYear).document(currentMonth).collection("users").document(uid).update("areApprovedSchemesUpdated", true)
                    }
                    binding.progressBar.visibility =View.INVISIBLE
                    binding.notApprovedList.visibility = View.VISIBLE
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update flags", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility =View.INVISIBLE
                    binding.notApprovedList.visibility = View.VISIBLE
                }
        }

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

        val allSchemesDB = AllSchemesDB.getDatabase(this)
        allSchemesDAO = allSchemesDB.dao()

        val approvedSchemesDB = ApprovedSchemesDB.getDatabase(this)
        approvedSchemesDAO = approvedSchemesDB.dao()

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