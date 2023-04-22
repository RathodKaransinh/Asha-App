package com.example.ashaapp.fragments

import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.ashaapp.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class DialogFragment(private val listener: RefreshProfile) : DialogFragment() {

    private lateinit var name: EditText
    private lateinit var district: EditText
    private lateinit var update: Button
    private lateinit var image: ShapeableImageView
    private lateinit var progressDialog: ProgressDialog
    private val auth = Firebase.auth
    private val uid = auth.uid!!
    private var uri: Uri? = null
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
            .update("name", userName, "district", userDistrict).addOnSuccessListener {
                if (uri != null) {
                    storage.reference.child("profileImages/$uid").putFile(uri!!).addOnSuccessListener {
                        progressDialog.dismiss()
                        onDestroyView()
                        Toast.makeText(context, "Profile Updated Successfully", Toast.LENGTH_SHORT)
                            .show()
                        listener.updateProfileFragment()
                    }.addOnFailureListener {
                        progressDialog.dismiss()
                        onDestroyView()
                        Toast.makeText(context, "Error in Updating Image", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    progressDialog.dismiss()
                    onDestroyView()
                    Toast.makeText(context, "Profile Updated Successfully", Toast.LENGTH_SHORT)
                        .show()
                    listener.updateProfileFragment()
                }
            }.addOnFailureListener {
                progressDialog.dismiss()
                onDestroyView()
                Toast.makeText(context, "Error in Updating Profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showProgressDialog() {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Updating Details...")
        progressDialog.setMessage("Updating profile, please wait")
        progressDialog.show()
    }

    private fun initUI(view: View) {
        name = view.findViewById(R.id.dialogTextName)
        district = view.findViewById(R.id.dialogTextDistrict)
        update = view.findViewById(R.id.dialogUpdateButton)
        image = view.findViewById(R.id.profileImage1)

        val pickPhoto = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            if (it != null) {
                image.setImageURI(it)
                uri = it
            }
        }

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) {
                pickPhoto.launch("image/*")
            }
        }

        image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                pickPhoto.launch("image/*")
            } else {
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
}

interface RefreshProfile {
    fun updateProfileFragment()
}