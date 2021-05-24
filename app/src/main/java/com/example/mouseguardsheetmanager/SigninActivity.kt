package com.example.mouseguardsheetmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import android.view.View
import android.widget.EditText
import com.google.firebase.database.FirebaseDatabase
import android.content.Intent
import android.util.Log
import android.widget.Toast
import java.lang.Exception


class SigninActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var userNameEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        emailEditText = findViewById(R.id.emailEditTextSign)
        passwordEditText = findViewById(R.id.passwordEditTextSign)
        userNameEditText = findViewById(R.id.usernameEditText)

        mAuth = FirebaseAuth.getInstance()
    }

    fun signIn(view:View?)
    {
        var email = emailEditText.text.toString().trim()
        var password = passwordEditText.text.toString().trim()
        var userName = userNameEditText.text.toString().trim()
        if (userName.isEmpty())
        {
            userNameEditText.setError("Enter username!")
            return
        }
        if (email.isEmpty())
        {
            emailEditText.setError("Enter email!")
            return
        }

        if (password.isEmpty())
        {
            passwordEditText.setError("Enter password")
            return
        }

        createUser(userName, email, password)
    }

    private fun createUser(userName:String, email:String, password:String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val currentUser = mAuth.currentUser
                    val uid = currentUser!!.uid
                    val userMap = HashMap<String, String>()
                    userMap["name"] = userName
                    userMap["email"] = email
                    val database = FirebaseDatabase.getInstance().getReference("Users").child(uid)
                    database.setValue(userMap).addOnCompleteListener(this) { task2 ->
                        if (task2.isSuccessful) {
                            val mainIntent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(mainIntent)
                            finish()
                        }
                        else {
                            Log.w(
                                "SigninActivity",
                                "createUserWithEmail:Failure",
                                task2.exception
                            )
                        }
                    }
                }
                else {
                    Log.w("SigninActivity","createUserWithEmail:Failure",task.exception)
                    Toast.makeText(baseContext, R.string.authentication_failed, Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun userLogin(view:View?)
    {
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }
}