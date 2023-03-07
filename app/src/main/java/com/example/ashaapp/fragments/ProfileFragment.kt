package com.example.ashaapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ashaapp.R
import com.example.ashaapp.activities.LoginActivity
import com.example.ashaapp.models.UserProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment(), RefreshProfile {

    private lateinit var textViewName: TextView
    private lateinit var textViewImage: TextView
    private lateinit var textViewDistrict: TextView
    private lateinit var logoutButton: Button
    private lateinit var editButton: Button
    private lateinit var progressBar: ProgressBar
    private val auth = Firebase.auth
    private val uid = auth.uid!!
    private val db = Firebase.firestore
    private lateinit var userProfile: UserProfile

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)

        initUI(view)

        loadProfileFromFirebase()

        logoutButton.setOnClickListener{
            logout()
        }

        editButton.setOnClickListener {
            editProfile()
        }

        return view
    }

    private fun loadProfileFromFirebase() {
        progressBar.visibility = View.VISIBLE
        db.collection("user_profiles").document(uid)
            .get()
            .addOnSuccessListener { document ->
                userProfile = document.toObject<UserProfile>()!!
                textViewName.text = userProfile.name
                textViewDistrict.text = userProfile.district
                if(userProfile.name?.length==0) textViewImage.text = ""
                else textViewImage.text = userProfile.name?.substring(0,1)
                progressBar.visibility = View.INVISIBLE
            }
            .addOnFailureListener { exception ->
                Log.d("user", "Error getting documents: ", exception)
                progressBar.visibility = View.INVISIBLE
            }
    }

    private fun initUI(view: View){
        textViewName = view.findViewById(R.id.profileName)
        logoutButton = view.findViewById(R.id.profileLogOutButton)
        textViewImage = view.findViewById(R.id.profileImage)
        textViewDistrict = view.findViewById(R.id.profileDistrict)
        editButton = view.findViewById(R.id.profileEditButton)
        progressBar =view.findViewById(R.id.profileProgressBar)
    }

    private fun logout() {
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