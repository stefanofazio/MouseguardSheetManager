package com.example.mouseguardsheetmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.content.Intent
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if (currentUser != null)
        {
            emailEditText.setText(currentUser.email.toString())
        }
    }

    fun userSignIn(view: View?)
    {
        val signinIntent = Intent(this, SigninActivity::class.java)
        startActivity(signinIntent)
    }

    fun login(view: View?)
    {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        if (!email.isEmpty() and !password.isEmpty())
        {
            loginUser(email, password)
        }


        //val intent = Intent(this, MainActivity::class.java)
        //startActivity(intent)
    }

    fun loginUser(email:String, password:String)
    {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {task ->
                if (task.isSuccessful)
                {
                    val mainIntent:Intent = Intent(this, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                }
                else
                {
                    val builder = AlertDialog.Builder(this)
                    with(builder)
                    {
                        setTitle(task.exception?.message)
                        setPositiveButton("OK", null)
                        show()
                    }
                }
            }
    }
}