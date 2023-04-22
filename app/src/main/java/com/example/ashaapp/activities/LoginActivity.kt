package com.example.ashaapp.activities

import android.animation.Animator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.ashaapp.R
import com.example.ashaapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        object : CountDownTimer(3000, 1000) {
            override fun onFinish() {
                binding.bookITextView.visibility = View.GONE
                binding.loadingProgressBar.visibility = View.GONE
                binding.bookIconImageView.setImageResource(R.mipmap.asha_launcher)
                startAnimation()
            }

            override fun onTick(p0: Long) {}
        }.start()

        sharedPreferences = getSharedPreferences("preference", MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()
        val currentuser = auth.currentUser
        if (currentuser != null && sharedPreferences.getString("user_pin", null) != null) {
            val intent = Intent(this, EnterPinActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.loginTextEmailAddress.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    binding.loginTextPassword.requestFocus()
                    true
                }

                else -> false
            }
        }

        binding.loginTextPassword.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    loginProcess()
                    true
                }

                else -> false
            }
        }

        binding.loginbtn.setOnClickListener {
            loginProcess()
        }
    }

    private fun loginProcess() {
        if (binding.loginTextEmailAddress.text.toString()
                .isEmpty() || binding.loginTextPassword.text.toString().isEmpty()
        ) {
            Toast.makeText(
                this,
                "Email and Password fields can't be empty!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            binding.bookIconImageView.visibility = View.GONE
            binding.afterAnimationView.visibility = View.GONE
            binding.loginWave1.visibility = View.GONE
            binding.loginProgressBar.visibility = View.VISIBLE
            auth.signInWithEmailAndPassword(
                binding.loginTextEmailAddress.text.toString(),
                binding.loginTextPassword.text.toString()
            )
                .addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        binding.loginProgressBar.visibility = View.GONE
                        Toast.makeText(
                            this, "Logged in successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                        sharedPreferences.edit().apply {
                            putString("password", binding.loginTextPassword.text.toString())
                            apply()
                        }
                        val intent = Intent(this, CreatePin::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        binding.loginProgressBar.visibility = View.GONE
                        binding.bookIconImageView.visibility = View.VISIBLE
                        binding.afterAnimationView.visibility = View.VISIBLE
                        binding.loginWave1.visibility = View.VISIBLE
                        Toast.makeText(
                            baseContext, it.exception?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun startAnimation() {
        binding.bookIconImageView.animate().apply {
            x(50f)
            y(100f)
            duration = 1000
        }.setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                binding.afterAnimationView.visibility = View.VISIBLE
                binding.loginWave1.visibility = View.VISIBLE
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
    }
}