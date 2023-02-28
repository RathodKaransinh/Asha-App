package com.example.ashaapp.activities

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ashaapp.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var loginbtn: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var email: EditText
    private lateinit var pass: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPreferences = getSharedPreferences("preference", MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()
        val currentuser = auth.currentUser
        if (currentuser != null && sharedPreferences.getString("user_pin", null) != null) {
            val intent = Intent(this, EnterPinActivity::class.java)
            startActivity(intent)
            finish()
        }

        loginbtn = findViewById(R.id.loginbtn)
        email = findViewById(R.id.loginTextEmailAddress)
        pass = findViewById(R.id.loginTextPassword)
        loginbtn.setOnClickListener {
            if (email.text.toString().isEmpty() || pass.text.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    "Email and Password fields can't be empty!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Authenticating...")
                progressDialog.setMessage("Logging in, please wait")
                progressDialog.show()
                auth.signInWithEmailAndPassword(email.text.toString(), pass.text.toString())
                    .addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            progressDialog.dismiss()
                            Toast.makeText(
                                this, "Logged in successfully.",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this, CreatePin::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            progressDialog.dismiss()
                            Log.w(ContentValues.TAG, "signInWithEmail:failure", it.exception)
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }
}