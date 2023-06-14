package com.example.ashaapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ashaapp.R
import com.example.ashaapp.room.user_incentives.DB
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class AnalyticsCard : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_analytics_card, container, false)

        val currentYear = SimpleDateFormat("yyyy", Locale.US).format(Calendar.getInstance().time)
        val currentMonth = SimpleDateFormat("MMM", Locale.US).format(Calendar.getInstance().time)
        val userIncentivesDao = DB.getDatabase(requireContext()).dao()

        userIncentivesDao.approvedSchemes().observe(requireActivity()) {
            if (it != null) {
                var currentMonthAmount: Long = 0
                var currentMonthIncentives = 0
                var currentYearAmount: Long = 0
                var currentYearIncentives = 0
                for (incentive in it) {
                    val date = Date(incentive.req_date)
                    val year = SimpleDateFormat("yyyy", Locale.US).format(date)
                    val month = SimpleDateFormat("MMM", Locale.US).format(date)

                    if (currentYear.equals(year)) {
                        currentYearIncentives++
                        currentYearAmount += incentive.value_of_schemes
                        if (currentMonth.equals(month)) {
                            currentMonthIncentives++
                            currentMonthAmount += incentive.value_of_schemes
                        }
                    }
                }
                view.findViewById<TextView>(R.id.amount_month).text = currentMonthAmount.toString()
                view.findViewById<TextView>(R.id.number_month).text =
                    currentMonthIncentives.toString()
                view.findViewById<TextView>(R.id.number_year).text =
                    currentYearIncentives.toString()
                view.findViewById<TextView>(R.id.amount_year).text = currentYearAmount.toString()
            }
        }


        return view
    }
}