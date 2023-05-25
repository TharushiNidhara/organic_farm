package com.example.itemsazanew

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class login : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var submitBtn: Button
    private lateinit var fireBaseAuth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        submitBtn = findViewById(R.id.submit)

        setTitle("User Login")

        submitBtn.setOnClickListener {
            loginUser(email.text.toString(), password.text.toString())
        }
    }

    private fun loginUser(emailText: String, passwordText: String) {

        var errorCount: Int = 0

        if (emailText.isEmpty()) {
            errorCount++
            email.error = "please enter email"
        }

        if (passwordText.isEmpty()) {
            errorCount++
            password.error = "Please enter your password"
        }

        if (emailText.isNotEmpty()) {
            val pattern = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
            )
            val matcher = pattern.matcher(emailText)

            if (!matcher.matches()) {
                errorCount++
                email.error = "Enter valid email"
            }
        }

        if (errorCount == 0) {
            //initialize to the database//
            fireBaseAuth = FirebaseAuth.getInstance()

            //user validation database connection//
            fireBaseAuth.signInWithEmailAndPassword(emailText, passwordText).addOnCompleteListener {
                if (it.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Your user credentials are wrong!", Toast.LENGTH_LONG).show()
            }
        }
    }
}