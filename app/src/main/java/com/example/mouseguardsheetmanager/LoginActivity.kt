package com.example.mouseguardsheetmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.content.Intent

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun login(view: View?)
    {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}