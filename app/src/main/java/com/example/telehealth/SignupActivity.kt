package com.example.telehealth

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var usernameEt: TextInputEditText
    private lateinit var mobileEt: TextInputEditText
    private lateinit var emailEt: TextInputEditText
    private lateinit var passEt: TextInputEditText
    private lateinit var confirmPassEt: TextInputEditText
    private lateinit var signUpButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Link XML views to Kotlin variables
        usernameEt = findViewById(R.id.userNameEt)
        mobileEt = findViewById(R.id.MobileEt)
        emailEt = findViewById(R.id.emailEt)
        passEt = findViewById(R.id.passEt)
        confirmPassEt = findViewById(R.id.confirmpassEt)
        signUpButton = findViewById(R.id.button)

        // Set click listener for Sign Up button
        signUpButton.setOnClickListener {
            val username = usernameEt.text.toString().trim()
            val mobile = mobileEt.text.toString().trim()
            val email = emailEt.text.toString().trim()
            val password = passEt.text.toString().trim()
            val confirmPassword = confirmPassEt.text.toString().trim()

            // Validate user input
            if (username.isEmpty()) {
                usernameEt.error = "Please enter a valid username"
                usernameEt.requestFocus()
                return@setOnClickListener
            }

            if (mobile.isEmpty() || mobile.length != 10) {
                mobileEt.error = "Please enter a valid 10-digit mobile number"
                mobileEt.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEt.error = "Enter a valid email"
                emailEt.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 6) {
                passEt.error = "Password should be at least 6 characters"
                passEt.requestFocus()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                confirmPassEt.error = "Passwords do not match"
                confirmPassEt.requestFocus()
                return@setOnClickListener
            }

            // Register user with Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        createUserInFirestore(username, mobile, email)

                        // Show success message
                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        // Show failure message
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    // Function to create a user document in Firestore
    private fun createUserInFirestore(username: String, mobile: String, email: String) {
        val user = hashMapOf(
            "username" to username,
            "mobile" to mobile,
            "email" to email
        )

        // Add a new document with the username as the document ID
        firestore.collection("Users")
            .document(email)  // Document ID is the username
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "User data saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
