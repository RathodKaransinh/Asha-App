package com.example.ashaapp.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.ashaapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class DialogFragment(val listener: RefreshProfile) : DialogFragment() {

    private lateinit var name: EditText
    private lateinit var district: EditText
    private lateinit var update: Button
    private lateinit var progressDialog: ProgressDialog
    private val auth = Firebase.auth
    private val uid = auth.uid!!
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_fragment, container, false)

        initUI(view)

        update.setOnClickListener {
            updateProfile()
        }

        return view
    }

    private fun updateProfile() {
        val userName = name.text.toString()
        val userDistrict = district.text.toString()

        showProgressDialog()

        db.collection("user_profiles").document(uid)
            .update("name", userName, "district", userDistrict)
            .addOnSuccessListener{
                progressDialog.dismiss()
                onDestroyView()
                Toast.makeText(context, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                listener.updateProfileFragment()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                onDestroyView()
                Toast.makeText(context, "Error in Updating Profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showProgressDialog() {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Fetching Details...")
        progressDialog.setMessage("Updating profile, please wait")
        progressDialog.show()
    }

    private fun initUI(view: View) {
        name = view.findViewById(R.id.dialogTextName)
        district = view.findViewById(R.id.dialogTextDistrict)
        update = view.findViewById(R.id.dialogUpdateButton)
    }
}

interface RefreshProfile{
    fun updateProfileFragment()
}