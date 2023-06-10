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
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar


class BottomSheetFragment     // Required empty public constructor
    (private var isneton: Boolean) : BottomSheetDialogFragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var allSchemesDAO: AllSchemesDAO
    private lateinit var notApprovedSchemesDAO: NotApprovedSchemesDAO
    private lateinit var adapter_scheme_name: ArrayAdapter<*>
    private lateinit var scheme_code: AutoCompleteTextView
    private lateinit var scheme_name: AutoCompleteTextView
    private val uid = Firebase.auth.uid

    companion object {
        const val TAG = "ModalBottomSheet"
    }

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

        scheme_code = view.findViewById(R.id.scheme_code)
        scheme_name = view.findViewById(R.id.scheme)

        val codesList: ArrayList<String> = allSchemesDAO.getallschemescode() as ArrayList<String>
        val adapter_code: ArrayAdapter<*> =
            ArrayAdapter<Any?>(view.context, R.layout.drop_down, codesList as List<Any?>)
        scheme_code.setAdapter(adapter_code)

        scheme_code.setOnItemClickListener { _, _, position, _ ->
            // You can get the label or item that the user clicked:
            val value = adapter_code.getItem(position) ?: ""
            val schemesList = allSchemesDAO.filterSchemesWithCode(value.toString())
            adapter_scheme_name = ArrayAdapter<Any?>(
                view.context,
                R.layout.drop_down_scheme,
                schemesList as List<Any?>
            )
            scheme_name.setAdapter(adapter_scheme_name)
        }

        val btn: Button = view.findViewById(R.id.button)
        btn.setOnClickListener {
            val schemeName = scheme_name.text.toString()

            if (schemeName.isEmpty()) {
                Toast.makeText(context, "Select scheme name!", Toast.LENGTH_SHORT).show()
            } else {
                val value: Long = allSchemesDAO.getprize(schemeName)
                val calendar: Calendar = Calendar.getInstance()
                val date = calendar.time
                val dateTime = Timestamp(date)

                notApprovedSchemesDAO.insert(
                    NotApprovedSchemesEntity(
                        0,
                        schemeName,
                        date.time,
                        value,
                        isneton
                    )
                )
                if (isneton) {
                    db.collection("users")
                        .document(uid!!)
                        .get().addOnSuccessListener {
                            val notApproved =
                                it.data?.get("notApproved") as ArrayList<Map<String, Any>>
                            notApproved.add(
                                hashMapOf(
                                    "name" to schemeName,
                                    "time" to dateTime,
                                    "value" to value
                                )
                            )
                            db.collection("users")
                                .document(uid)
                                .update("notApproved", notApproved)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        view.context,
                                        "Data Added Successfully",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                .addOnFailureListener { exception ->
                                    Log.d("Error", exception.toString())
                                }
                        }
                } else {
                    Toast.makeText(view.context, "તમારુ નેટ બંધ છે", Toast.LENGTH_LONG).show()
                }
                onDestroyView()
            }
        }

        return view
    }
}
