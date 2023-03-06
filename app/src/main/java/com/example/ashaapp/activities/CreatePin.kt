package com.example.ashaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.ashaapp.databinding.ActivityCreatePinBinding

class CreatePin : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePinBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val sharedPreferences = getSharedPreferences("preference", MODE_PRIVATE)

        binding.createPinButton.setOnClickListener {
            val pin = binding.createPinEditText.text.toString()
            if (pin.length != 4) Toast.makeText(this, "Enter a 4 digit pin!", Toast.LENGTH_SHORT)
                .show()
            else {
                sharedPreferences.edit().apply() {
                    putString("user_pin", pin)
                    apply()
                }
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}