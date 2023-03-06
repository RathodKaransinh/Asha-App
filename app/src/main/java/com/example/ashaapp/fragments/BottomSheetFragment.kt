package com.example.ashaapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import com.example.ashaapp.R
import com.example.ashaapp.room.allschemes.AllSchemesDAO
import com.example.ashaapp.room.allschemes.AllSchemesDB
import com.example.ashaapp.room.notapprovedschemes.NotApprovedSchemesDAO
import com.example.ashaapp.room.notapprovedschemes.NotApprovedSchemesDB
import com.example.ashaapp.room.notapprovedschemes.NotApprovedSchemesEntity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class BottomSheetFragment     // Required empty public constructor
    (var isneton: Boolean) : BottomSheetDialogFragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var allSchemesDAO: AllSchemesDAO
    private lateinit var notApprovedSchemesDAO: NotApprovedSchemesDAO

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
        val allSchemesDB = context?.let { AllSchemesDB.getDatabase(it) }
        if (allSchemesDB != null) {
            allSchemesDAO = allSchemesDB.dao()
        }

        val notApprovedSchemesDB = context?.let { NotApprovedSchemesDB.getDatabase(it) }
        if (notApprovedSchemesDB != null) {
            notApprovedSchemesDAO = notApprovedSchemesDB.dao()
        }

        db = Firebase.firestore

        val scheme_name: AutoCompleteTextView = view.findViewById(R.id.scheme)
        val schemesList: ArrayList<String> = allSchemesDAO.getallschemes() as ArrayList<String>
        val adapter_scheme_name: ArrayAdapter<*> =
            ArrayAdapter<Any?>(view.context, R.layout.drop_down_scheme, schemesList as List<Any?>)
        scheme_name.setAdapter(adapter_scheme_name)

        val scheme_code: AutoCompleteTextView = view.findViewById(R.id.scheme_code)
        val codesList: ArrayList<String> = allSchemesDAO.getallschemescode() as ArrayList<String>
        val adapter_code: ArrayAdapter<*> =
            ArrayAdapter<Any?>(view.context, R.layout.drop_down, codesList as List<Any?>)
        scheme_code.setAdapter(adapter_code)

        val btn: Button = view.findViewById(R.id.button)
        btn.setOnClickListener {
            val schemeName = scheme_name.text.toString()
            val value: Long = allSchemesDAO.getprize(schemeName)
            val calendar: Calendar = Calendar.getInstance()
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.US)
            val dateTime = simpleDateFormat.format(calendar.time)

            notApprovedSchemesDAO.insert(
                NotApprovedSchemesEntity(
                    0,
                    schemeName,
                    dateTime,
                    value,
                    isneton
                )
            )
            if (isneton) {
                db.collection("user_incentives").document("1")
                    .get().addOnSuccessListener {
                        Log.d(
                            "not_app",
                            (it.data?.get("notapproved") as ArrayList<Map<String, Any>>).toString()
                        )
                        val notApproved =
                            it.data?.get("notapproved") as ArrayList<Map<String, Any>>
                        notApproved.add(
                            hashMapOf(
                                "name" to schemeName,
                                "time" to dateTime,
                                "value" to value
                            )
                        )
                        db.collection("user_incentives").document("1")
                            .update("notapproved", notApproved)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    view.context,
                                    "Data Added Successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            .addOnFailureListener {
                                Log.d("Error", it.toString())
                            }
                    }
            } else {
                Toast.makeText(view.context, "તમારુ નેટ બંધ છે", Toast.LENGTH_LONG).show()
            }
            onDestroyView()
        }

        return view
    }
}
