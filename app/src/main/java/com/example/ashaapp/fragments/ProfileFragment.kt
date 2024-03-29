package com.example.ashaapp.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.ashaapp.R
import com.example.ashaapp.activities.LoginActivity
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class ProfileFragment(private var isNetworkAvailable: Boolean) : Fragment(), RefreshProfile {

    private lateinit var textViewName: TextView
    private lateinit var profileImage: ShapeableImageView
    private lateinit var textViewDistrict: TextView
    private lateinit var logoutButton: Button
    private lateinit var editButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var sharedPreferences: SharedPreferences
    private val auth = Firebase.auth
    private val uid = auth.uid!!
    private val db = Firebase.firestore
    private val profileImageReference = Firebase.storage.reference.child("profileImages/$uid")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)

        initUI(view)

        loadProfileFromFirebase()

        logoutButton.setOnClickListener {
            if (isNetworkAvailable) logout()
            else Toast.makeText(view.context, "તમારુ નેટ બંધ છે", Toast.LENGTH_LONG).show()
        }

        editButton.setOnClickListener {
            if (isNetworkAvailable) editProfile()
            else Toast.makeText(view.context, "તમારુ નેટ બંધ છે", Toast.LENGTH_LONG).show()
        }

        return view
    }

    private fun loadProfileFromFirebase() {
        if (!isNetworkAvailable) return

        progressBar.visibility = View.VISIBLE

        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            if (auth.currentUser!!.displayName != null) {
                textViewName.text = auth.currentUser!!.displayName
            }
            textViewDistrict.text = doc.get("district") as String
            profileImageReference.downloadUrl.addOnSuccessListener {
                context?.let { it1 ->
                    Glide.with(it1).load(it).centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(profileImage)
                }
                progressBar.visibility = View.INVISIBLE
            }.addOnFailureListener {
                context?.let { it1 ->
                    Glide.with(it1).load(R.drawable.baseline_person_24)
                        .fitCenter().into(profileImage)
                }
                progressBar.visibility = View.INVISIBLE
            }
        }
    }

    private fun initUI(view: View) {
        textViewName = view.findViewById(R.id.profileName)
        logoutButton = view.findViewById(R.id.profileLogOutButton)
        profileImage = view.findViewById(R.id.profileImage)
        textViewDistrict = view.findViewById(R.id.profileDistrict)
        editButton = view.findViewById(R.id.profileEditButton)
        progressBar = view.findViewById(R.id.profileProgressBar)
        sharedPreferences =
            requireActivity().getSharedPreferences("preference", Context.MODE_PRIVATE)
    }

    private fun logout() {
        sharedPreferences.edit().apply {
            putString("password", null)
            putString("user_pin", null)
            apply()
        }
        auth.signOut()
        startActivity(Intent(context, LoginActivity::class.java))
        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }

    private fun editProfile() {
        val dialogFragment = DialogFragment(this)
        dialogFragment.show(parentFragmentManager, "Dialog Fragment")
    }

    override fun updateProfileFragment() {
        loadProfileFromFirebase()
    }
}