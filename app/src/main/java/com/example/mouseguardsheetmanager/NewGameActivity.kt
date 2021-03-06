package com.example.mouseguardsheetmanager

import GameClass
import Utils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.random.Random
import android.content.Intent
import android.renderscript.ScriptGroup
import android.text.InputType

class NewGameActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var uidEditText: EditText
    private lateinit var functionButton: Button
    private lateinit var newGameName: EditText
    private lateinit var numberOfPlayers: EditText
    private lateinit var shareButton: Button

    private lateinit var mDatabase: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    private var gameUID : String = ""
    private var playerNumber : Int = 0
    private var gameName : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_game)
        uidEditText = findViewById(R.id.gameUidEditText)
        functionButton = findViewById(R.id.newGameFunctionButton)
        newGameName = findViewById(R.id.newGameNameEditText)
        numberOfPlayers = findViewById(R.id.playerNumberEditText)
        shareButton = findViewById(R.id.shareGame)

        mDatabase = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        role = intent.getStringExtra("role").toString()

        if (role.equals("master")) {
            if (intent.hasExtra("gameUID")) {
                gameUID = intent.getStringExtra("gameUID").toString()
                playerNumber = intent.getIntExtra("playerNumber", 0).toInt()
                gameName = intent.getStringExtra("gameName").toString()
                functionButton.setText(getString(R.string.sheets))
                uidEditText.setText(gameUID)
                uidEditText.inputType = InputType.TYPE_NULL
                newGameName.setText(gameName)
                newGameName.inputType = InputType.TYPE_NULL
                numberOfPlayers.setText(playerNumber.toString())
                numberOfPlayers.inputType = InputType.TYPE_NULL
            }
            else {
                functionButton.setText(getString(R.string.createGame))
                shareButton.isVisible = false
            }
        }
        else {
            if (intent.hasExtra("gameUID")) {
                gameUID = intent.getStringExtra("gameUID").toString()
                playerNumber = intent.getIntExtra("playerNumber", 0).toInt()
                gameName = intent.getStringExtra("gameName").toString()
                functionButton.setText(getString(R.string.sheets))
                uidEditText.setText(gameUID)
                uidEditText.inputType = InputType.TYPE_NULL
                newGameName.setText(gameName)
                newGameName.inputType = InputType.TYPE_NULL
                numberOfPlayers.setText(playerNumber.toString())
                numberOfPlayers.inputType = InputType.TYPE_NULL

            } else {
                functionButton.setText(getString(R.string.join))
            }
            shareButton.isVisible = false
        }

    }

    fun newGameFunction(view:View?)
    {
        when(role)
        {
            "master" -> if (gameUID.isNullOrEmpty()) createGame() else openGame(gameUID, role)
            "player" -> if (gameUID.isNullOrEmpty()) joinGame() else openGame(gameUID, role)
        }
    }



    fun ShareGame(view:View?)
    {
        var whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.type = "text/plain"
        whatsappIntent.setPackage("com.whatsapp")
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "$gameName $gameUID")
        if (whatsappIntent.resolveActivity(packageManager) == null) {
            Toast.makeText(this,
                "Please install whatsapp first.",
                Toast.LENGTH_SHORT).show()
            return
        }

        // Starting Whatsapp
        startActivity(whatsappIntent)

    }

    private fun openGame(id : String, playerRole : String)
    {
        var sheetIntent = Intent(this, SheetActivity::class.java)
        sheetIntent.putExtra("gameID", id)
        sheetIntent.putExtra("role", playerRole)
        startActivity(sheetIntent)
    }

    private fun joinGame()
    {
        if (uidEditText.text.isNotEmpty())
        {
            val ownerEmail = mAuth.currentUser?.email.toString()
            val uid = uidEditText.text.toString().trim()
            val nameListener = object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val playerName = snapshot.getValue().toString()
                    //numberOfPlayers.setText(snapshot.getValue().toString())
                    InsertInGame(playerName, ownerEmail, uid)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("NewGame","loadOnName:cancelled", error.toException())
                }
            }
            mDatabase.child("Users").child(mAuth.currentUser?.uid.toString()).child("name").addValueEventListener(nameListener)

        }
    }

    private fun createGame()
    {
        //var ownerName = ""
        if (newGameName.text.isNotEmpty())
        {
            val ownerEmail = mAuth.currentUser?.email.toString()
            val newName = newGameName.text.toString()
            val nameListener = object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val ownerName = snapshot.getValue().toString()
                    numberOfPlayers.setText(snapshot.getValue().toString())
                    var newGame = GameClass(newName, ownerName, ownerEmail)
                    newGame.gameID = Utils.IDGeneration(newName.toString())
                    InsertNewGame(newGame)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("NewGame","loadOnName:cancelled", error.toException())
                }
            }
            mDatabase.child("Users").child(mAuth.currentUser?.uid.toString()).child("name").addValueEventListener(nameListener)

        }
    }



    private fun MD5(item: String) : String
    {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(item.toByteArray())).toString(16).padStart(10, '0')
    }

    private fun InsertInGame(name: String, email:String, uid:String)
    {
        mDatabase.child("Games").child(uid).child("players").child(name).setValue(email).addOnCompleteListener(this) { task2 ->
            if (task2.isSuccessful) {
                Log.w("succ","success")
                finish()
            }
            else {
                var t = Toast(this)
                t.setText(getString(R.string.failed_to_join))
                t.show()
                Log.w(
                    "SigninActivity",
                    "createUserWithEmail:Failure",
                    task2.exception
                )
            }
        }
    }

    private fun InsertNewGame(newGame:GameClass)
    {
        newGame.addPlayer("Beatrice", "beatrice@email.it")
        var gson = Gson()
        val jsonGame = gson.toJson(newGame)
        //val hash = gson.fromJson(jsonGame, new TypeToken<HashMap<String, Object>>() {}.getType)
        mDatabase.child("Games").child(newGame.gameID).setValue(newGame).addOnCompleteListener(this) { task2 ->
            if (task2.isSuccessful) {
                Log.w("succ","success")
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
}