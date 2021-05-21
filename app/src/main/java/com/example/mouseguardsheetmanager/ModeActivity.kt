package com.example.mouseguardsheetmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.content.Intent

class ModeActivity : AppCompatActivity() {

    private var role: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode)
        role = intent.getStringExtra("role").toString()
    }

    fun getClick(view: View?)
    {
        val gameIntent: Intent
        if (view?.id == R.id.gameButton)
        {
            gameIntent = Intent(this, GameActivity::class.java)
            gameIntent.putExtra("role",role)
        }
        else
        {
            gameIntent = Intent(this, CharactersActivity::class.java)
            gameIntent.putExtra("role", role)
        }
        startActivity(gameIntent)
    }
}