package com.example.mouseguardsheetmanager

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase

class GameActivity : AppCompatActivity() {
    private lateinit var gamesList:ListView
    private lateinit var gamesButton: Button
    private lateinit var mDatabase:FirebaseDatabase
    private var role: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        gamesList = findViewById(R.id.gamesListView)
        gamesButton = findViewById(R.id.gamesButton)
        role = intent.getStringExtra("role").toString()
        mDatabase = FirebaseDatabase.getInstance()
        setButtonText()
        //setListViewContent()
    }

    fun gamesButton(view:View?)
    {
        if (role.equals("master"))
        {
            val intent = Intent(this, NewGameActivity::class.java)
            intent.putExtra("role", role)
            startActivity(intent)
            finish()
        }
    }

    private fun setButtonText()
    {
        if (role == "master") { gamesButton.text = "Crea" }
        else { gamesButton.text = "Unisciti" }
    }

    private fun setListViewContent()
    {
        var myAdapter: GameAdapter = GameAdapter(this, 18)
        gamesList.adapter = myAdapter
    }
}

class GameAdapter(private val context: Context, private val elements : Int) : BaseAdapter()
{
    override fun getCount(): Int {
        return elements
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = LayoutInflater.from(context).inflate(R.layout.game_row, parent, false)
        var gameName: TextView = rowView.findViewById(R.id.gameNameText)
        var masterName: TextView = rowView.findViewById(R.id.masterNameText)
        var playerNumber: TextView = rowView.findViewById(R.id.playerNumberText)
        gameName.text = "Partita numero " + position.toString()
        masterName.text = "Master numero " + position.toString()
        playerNumber.text = "Numero giocatori: " + (1..15).random().toString()
        return rowView
    }
}