package org.freedu.realtimeb5

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import org.freedu.realtimeb5.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
// 🔐 If user is already signed in, skip to MainActivity

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.btnSignUp.setOnClickListener {
            signUpUser()
        }
        binding.tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@dipti\\.com\\.bd$")
        return emailRegex.matches(email)
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordRegex = Regex(
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>/?]).{8,}$"
        )
        return passwordRegex.matches(password)
    }


    private fun signUpUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 8) {
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (!isValidEmail(email)) {
            Toast.makeText(
                this,
                "Email must be in valid format and end with @dipti.com.bd",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (!isValidPassword(password)) {
            Toast.makeText(
                this,
                "Password must contain at least one uppercase letter, one lowercase letter, one number, and be at least 8 characters long",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this, "Account created successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this, SignInActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this, task.exception?.message ?: "Sign up failed", Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

}
