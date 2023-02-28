package com.example.ashaapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.ashaapp.R
import com.example.ashaapp.databinding.ActivityMainBinding
import com.example.ashaapp.fragments.AddIncentivesFragment
import com.example.ashaapp.fragments.AnalyticsCard
import com.example.ashaapp.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)

        val first: Fragment = AnalyticsCard()
        val second: Fragment = AddIncentivesFragment()
        val third: Fragment = ProfileFragment()

        if (savedInstanceState == null) loadFragment(first)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    loadFragment(first)
                    true
                }
                R.id.page_2 -> {
                    loadFragment(second)
                    true
                }
                R.id.page_3 -> {
                    loadFragment(third)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val fm: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        ft.replace(R.id.container, fragment)
        ft.commit()
    }
}