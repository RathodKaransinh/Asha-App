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
import com.example.ashaapp.room.user_incentives.DB
import com.example.ashaapp.room.user_incentives.IncentivesDao
import com.example.ashaapp.room.user_incentives.IncentivesEntity
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
    private lateinit var userIncentivesDao: IncentivesDao
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

        val userIncentivesDB = context?.let { DB.getDatabase(it) }
        if (userIncentivesDB != null) {
            userIncentivesDao = userIncentivesDB.dao()
        }

        db = Firebase.firestore

        scheme_code = view.findViewById(R.id.scheme_code)
        scheme_name = view.findViewById(R.id.scheme)

        val codesList: ArrayList<String> = allSchemesDAO.getallschemescode() as ArrayList<String>
        var set = HashSet<String>()
        set.addAll(codesList)
        codesList.clear()
        codesList.addAll(set)
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

        scheme_name.setOnClickListener {
            if (scheme_code.text.toString().isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please select a scheme code first!",
                    Toast.LENGTH_SHORT
                ).show()
            }
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

                userIncentivesDao.insert(
                    IncentivesEntity(
                        0,
                        schemeName,
                        date.time,
                        value,
                        false,
                        isneton
                    )
                )
                if (isneton) {
                    db.collection("users")
                        .document(uid!!)
                        .get().addOnSuccessListener {
                            val incentives =
                                it.data?.get("incentives") as ArrayList<Map<String, Any>>
                            incentives.add(
                                hashMapOf(
                                    "name" to schemeName,
                                    "time" to dateTime,
                                    "value" to value,
                                    "isApproved" to false,
                                )
                            )
                            db.collection("users")
                                .document(uid)
                                .update("incentives", incentives)
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
