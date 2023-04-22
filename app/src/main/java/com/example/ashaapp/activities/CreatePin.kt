package com.example.ashaapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ashaapp.databinding.ActivityCreatePinBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CreatePin : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePinBinding
    private val uid = Firebase.auth.uid
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePinBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.createUserProgressBar.visibility = View.VISIBLE
        binding.linearLayout.visibility = View.INVISIBLE

        val calendar: Calendar = Calendar.getInstance()
        val yearDateFormat = SimpleDateFormat("yyyy", Locale.US)
        val currentYear = yearDateFormat.format(calendar.time)

        val monthDateFormat = SimpleDateFormat("MMM", Locale.US)
        val currentMonth = monthDateFormat.format(calendar.time)

        val defaultData = hashMapOf(
            "approved" to ArrayList<HashMap<String, Any>>(),
            "notApproved" to ArrayList<HashMap<String, Any>>(),
            "areSchemesUpdated" to false,
            "areApprovedSchemesUpdated" to false
        )

        db.collection(currentYear).document(currentMonth).collection("users").document(uid!!)
            .addSnapshotListener { value, error ->
                if (value!!.exists()) {
                    db.collection(currentYear).document(currentMonth).collection("users")
                        .document(uid)
                        .update("areSchemesUpdated", false, "areApprovedSchemesUpdated", false)
                        .addOnSuccessListener {
                            binding.linearLayout.visibility = View.VISIBLE
                            binding.createUserProgressBar.visibility = View.INVISIBLE
                        }
                        .addOnFailureListener {
                            binding.linearLayout.visibility = View.VISIBLE
                            binding.createUserProgressBar.visibility = View.INVISIBLE
                            Log.d("error", it.toString())
                        }
                } else {
                    db.collection(currentYear).document(currentMonth).collection("users")
                        .document(uid)
                        .set(defaultData)
                        .addOnSuccessListener {
                            binding.linearLayout.visibility = View.VISIBLE
                            binding.createUserProgressBar.visibility = View.INVISIBLE
                        }
                        .addOnFailureListener {
                            binding.linearLayout.visibility = View.VISIBLE
                            binding.createUserProgressBar.visibility = View.INVISIBLE
                            Log.d("error", it.toString())
                        }
                }
                if (error != null) {
                    binding.linearLayout.visibility = View.VISIBLE
                    binding.createUserProgressBar.visibility = View.INVISIBLE
                    Log.d("error", error.toString())
                }
            }

        val sharedPreferences = getSharedPreferences("preference", MODE_PRIVATE)

        binding.createPinButton.setOnClickListener {
            val pin = binding.createPinEditText.text.toString()
            if (pin.length != 6) Toast.makeText(this, "Enter a 6 digit pin!", Toast.LENGTH_SHORT)
                .show()
            else {
                sharedPreferences.edit().apply {
                    putString("user_pin", pin)
                    apply()
                }
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}