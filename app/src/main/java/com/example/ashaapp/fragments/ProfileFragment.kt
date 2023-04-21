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
import com.example.ashaapp.models.UserProfile
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class ProfileFragment : Fragment(), RefreshProfile {

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
    private lateinit var userProfile: UserProfile

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)

        initUI(view)

        loadProfileFromFirebase()

        logoutButton.setOnClickListener {
            logout()
        }

        editButton.setOnClickListener {
            editProfile()
        }

        return view
    }

    private fun loadProfileFromFirebase() {
        progressBar.visibility = View.VISIBLE
        val defaultData = hashMapOf("name" to "name", "district" to "district", "taluka" to "taluka", "village" to "village")
        db.collection("user_profiles").document(uid)
            .addSnapshotListener { value, error ->
                if (value!!.exists()) {
                    userProfile = value.toObject<UserProfile>()!!
                    textViewName.text = userProfile.name
                    textViewDistrict.text = userProfile.district
                    profileImageReference.downloadUrl
                        .addOnSuccessListener{
                            Glide.with(requireContext())
                                .load(it)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(profileImage)
                            progressBar.visibility = View.INVISIBLE
                        }
                        .addOnFailureListener{
                            Glide.with(requireContext())
                                .load(R.drawable.baseline_person_24)
                                .fitCenter()
                                .into(profileImage)
                            progressBar.visibility = View.INVISIBLE
                        }
                } else {
                    db.collection("user_profiles").document(uid)
                        .set(defaultData)
                        .addOnSuccessListener {
                            progressBar.visibility = View.INVISIBLE
                        }
                }
                if (error != null){
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