package com.example.ashaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ashaapp.databinding.ActivityEnterPinBinding

class EnterPinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnterPinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterPinBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val sharedPreferences = getSharedPreferences("preference", MODE_PRIVATE)

        binding.enterPinButton.setOnClickListener {
            val pin = binding.enterPinEditText.text.toString()

            if (pin.length != 4) Toast.makeText(this, "Enter a 4 digit pin", Toast.LENGTH_SHORT)
                .show()
            else {
                if (pin == sharedPreferences.getString("user_pin", null)) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Wrong pin entered", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}