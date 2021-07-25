package com.example.mouseguardsheetmanager

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import GameClass
import Utils
import android.graphics.Paint
import android.nfc.NfcAdapter
import android.util.Log
import android.widget.*
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.coroutines.selects.select


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
        SetListListeners()
        //SetListContentView()
    }

    fun gamesButton(view:View?)
    {
        if (role.equals("master"))
        {
            val intent = Intent(this, NewGameActivity::class.java)
            intent.putExtra("role", role)
            startActivity(intent)
        }
    }

    private fun setButtonText()
    {
        if (role == "master") { gamesButton.text = "Crea" }
        else { gamesButton.text = "Unisciti" }
    }

    private fun SetListListeners()
    {
        gamesList.onItemClickListener = AdapterView.OnItemClickListener{
            parent, view, position, id ->
            val selectedGame = games[position]
            doThingsWithGame(selectedGame)
        }
    }



    private fun SetEventListener()
    {
        val nameListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val presentGames = mutableListOf<GameClass>()
               for (i in snapshot.children)
                {
                    var tmpGame = GameClass()
                    val tmpJson = Utils.AdaptToJSON(i.toString())
                    tmpGame = gson.fromJson<GameClass>(tmpJson, GameClass::class.java)
                    presentGames.add(tmpGame)
                }
                games = presentGames
                SetListContentView()
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

    private fun doThingsWithGame(game : GameClass)
    {
        if (role.equals("master"))
        {
            OpenGameForMaster(game)
        }
        else
        {
            OpenGameForPlayer(game)
        }
    }

    private fun OpenGameForMaster(game : GameClass)
    {
        val intent = Intent(this, SheetActivity::class.java)
        intent.putExtra("gameID", game.gameID)
        intent.putExtra("role", "master")
        startActivity(intent)
    }

    private fun OpenGameForPlayer(game: GameClass)
    {
        val intent = Intent(this, JoinGameActivity::class.java)
        intent.putExtra("gameID", game.gameID)
        intent.putExtra("role", "player")
        startActivity(intent)
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
        val game = elements[Position]
        var gameName: TextView = rowView.findViewById(R.id.gameNameText)
        var masterName: TextView = rowView.findViewById(R.id.masterNameText)
        var playerNumber: TextView = rowView.findViewById(R.id.playerNumberText)
        gameName.text = java.lang.String.format(context.getString(R.string.gameName),game.gameName)
        masterName.text = java.lang.String.format(context.getString(R.string.masterName),game.gameOwnerName)
        playerNumber.text = java.lang.String.format(context.getString(R.string.playerNumber),game.players.count().toString())

        return rowView
    }
}