package com.example.ashaapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ashaapp.fragments.AddIncentivesFragment
import com.example.ashaapp.fragments.AnalyticsCard
import com.example.ashaapp.fragments.ProfileFragment
import com.example.ashaapp.R
import com.example.ashaapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) loadFragment(AnalyticsCard())

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    loadFragment(AnalyticsCard())
                    true
                }
                R.id.page_2 -> {
                    loadFragment(AddIncentivesFragment())
                    true
                }
                R.id.page_3 -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }
}