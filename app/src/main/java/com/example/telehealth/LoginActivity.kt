package com.example.telehealth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.telehealth.databinding.ActivityLoginBinding
import com.example.telehealth.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflate the layout
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Navigate to SignUpActivity when "Sign Up" text is clicked
        binding.signUpText.setOnClickListener {
            val signupIntent = Intent(this, SignupActivity::class.java)
            startActivity(signupIntent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passEt.text.toString()

            // Check if the fields are not empty
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                // Authenticate the user with Firebase
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // If sign-in is successful, store the user's email in SharedPreferences
                        val sharedPreferences = getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("USER_NAME", email)
                        editor.apply()

                        // Navigate to the home screen (MainActivity)
                        val mainIntent = Intent(this, MainActivity::class.java)
                        startActivity(mainIntent)
                        finish()

                    } else {
                        // Display error message if sign-in fails
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Notify the user to fill in the fields
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
