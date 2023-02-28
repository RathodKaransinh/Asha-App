package com.example.ashaapp.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ashaapp.R
import com.example.ashaapp.models.UserProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    private lateinit var textView: TextView
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private lateinit var userProfile: UserProfile

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)
        textView = view.findViewById(R.id.profileText)
        val uid = auth.uid!!

        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Fetching Details...")
        progressDialog.setMessage("Updating profile, please wait")
        progressDialog.show()

        db.collection("user_profiles").document(uid)
            .get()
            .addOnSuccessListener { document ->
                Log.d("user", "${document.id} => ${document.data}")
                userProfile = document.toObject<UserProfile>()!!
                val str = "Name = ${userProfile.name}\nDistrict = ${userProfile.district}"
                textView.text = str
                if (textView.text != "")
                    progressDialog.dismiss()
            }
            .addOnFailureListener { exception ->
                Log.d("user", "Error getting documents: ", exception)
            }

        return view
    }
}