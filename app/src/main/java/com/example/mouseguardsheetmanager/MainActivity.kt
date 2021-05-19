package com.example.mouseguardsheetmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import android.content.Intent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun getClick(view: View?)
    {
        val intent = Intent(this, ModeActivity::class.java)
        when(view?.id) {
            R.id.masterButton -> intent.putExtra("role", "master")
            R.id.playerButton -> intent.putExtra("role", "player")
        }
        startActivity(intent)
    }
}