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
import GameClass
import android.util.Log
import com.google.firebase.database.*
import com.google.gson.Gson


class GameActivity : AppCompatActivity() {
    private lateinit var gamesList:ListView
    private lateinit var gamesButton: Button
    private lateinit var mDatabase:DatabaseReference
    private var role: String = ""
    private var gson = Gson()

    private var games : List<GameClass> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)
        gamesList = findViewById(R.id.gamesListView)
        gamesButton = findViewById(R.id.gamesButton)
        role = intent.getStringExtra("role").toString()
        mDatabase = FirebaseDatabase.getInstance().reference
        setButtonText()
        SetEventListener()
        //SetListContentView()
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

    private fun AdaptToJSON(startingJSON : String) : String
    {
        var newJSON = startingJSON.removePrefix("DataSnapshot {")
        newJSON = newJSON.removeSuffix("}")
        newJSON = newJSON.removeRange(0, newJSON.indexOf("{"))
        return newJSON
    }

    private fun setButtonText()
    {
        if (role == "master") { gamesButton.text = "Crea" }
        else { gamesButton.text = "Unisciti" }
    }

    private fun SetEventListener()
    {
        val nameListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val presentGames = mutableListOf<GameClass>()
                /*var tmpGameClass = GameClass()
                var tmpString = AdaptToJSON(snapshot.child("Ahah").toString())
                tmpGameClass = gson.fromJson<GameClass>(tmpString, GameClass::class.java)
                //snapshot.child("Prima")
                presentGames.add(tmpGameClass)*/
               for (i in snapshot.children)
                {
                    var tmpGame = GameClass()
                    tmpGame = gson.fromJson<GameClass>(AdaptToJSON((i).toString()), GameClass::class.java)
                    presentGames.add(tmpGame)
                }
                games = presentGames
                SetListContentView()
                //var myAdapter : GameAdapter = GameAdapter(parent.baseContext, presentGames)
                //gamesList.adapter = myAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("NewGame","loadOnName:cancelled", error.toException())
            }
        }
        mDatabase.child("Games").addValueEventListener(nameListener)

        //var myAdapter = GameAdapter(this, games)
    }

    private fun SetListContentView()
    {
        var myAdapter = GameAdapter(this, games)
        gamesList.adapter = myAdapter
    }
}

class GameAdapter(private val context: Context, private val elements: List<GameClass>) : BaseAdapter()
{
    override fun getCount(): Int {
        return elements.count()
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(Position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = LayoutInflater.from(context).inflate(R.layout.game_row, parent, false)
        val game = elements.get(Position)
        var gameName: TextView = rowView.findViewById(R.id.gameNameText)
        var masterName: TextView = rowView.findViewById(R.id.masterNameText)
        var playerNumber: TextView = rowView.findViewById(R.id.playerNumberText)
        gameName.text = java.lang.String.format(context.getString(R.string.gameName),game.gameName)
        masterName.text = java.lang.String.format(context.getString(R.string.masterName),game.gameOwnerName)
        playerNumber.text = java.lang.String.format(context.getString(R.string.playerNumber),game.players.count().toString())

        return rowView
    }
}