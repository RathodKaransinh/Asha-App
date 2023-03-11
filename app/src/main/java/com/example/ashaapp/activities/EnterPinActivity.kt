package com.example.ashaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
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

        binding.textViewForgotPin.setOnClickListener {
            binding.textViewEnterPin.text = "Enter your Password"
            binding.enterPinEditText.hint = "Enter Password"
            binding.textViewForgotPin.text = "Enter using pin?"
            binding.enterPinEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.enterPinEditText.text?.clear()

            binding.textViewForgotPin.setOnClickListener {
                startActivity(Intent(this, EnterPinActivity::class.java))
                finish()
            }

            binding.enterPinButton.setOnClickListener {
                val password = binding.enterPinEditText.text.toString()

                if (password.isEmpty()) Toast.makeText(this, "Password can't be Empty", Toast.LENGTH_SHORT)
                    .show()
                else {
                    if (password == sharedPreferences.getString("password", null)) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Wrong password entered", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}