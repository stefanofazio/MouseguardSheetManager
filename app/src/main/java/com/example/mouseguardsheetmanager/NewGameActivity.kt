package com.example.mouseguardsheetmanager

import GameClass
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class NewGameActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var uidEditText: EditText
    private lateinit var functionButton: Button
    private lateinit var newGameName: EditText
    private lateinit var numberOfPlayers: EditText

    private lateinit var mDatabase: DatabaseReference
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_game)
        uidEditText = findViewById(R.id.gameUidEditText)
        functionButton = findViewById(R.id.newGameFunctionButton)
        newGameName = findViewById(R.id.newGameNameEditText)
        numberOfPlayers = findViewById(R.id.playerNumberEditText)

        mDatabase = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        SetListeners()

        role = intent.getStringExtra("role").toString()

    }

    private fun SetListeners()
    {
        uidEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (uidEditText.text.isNotEmpty())
                {
                    when(role) {
                        "master" -> functionButton.text = getString(R.string.sheets)
                        "player" -> functionButton.text = getString(R.string.join)
                    }
                }
                else{
                    when(role) {
                        "master" -> functionButton.text = getString(R.string.createGame)
                        "player" -> functionButton.text = ""
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                return
            }
        })
    }

    fun newGameFunction(view:View?)
    {
        if (uidEditText.text.isEmpty())
        {
            when(role)
            {
                "master" -> createGame()
            }
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
                    InsertNewGame(newGame)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("NewGame","loadOnName:cancelled", error.toException())
                }
            }
            mDatabase.child("Users").child(mAuth.currentUser?.uid.toString()).child("name").addValueEventListener(nameListener)

        }
    }

    private fun InsertNewGame(newGame:GameClass)
    {
        newGame.addPlayer("Beatrice", "beatrice@email.it")
        mDatabase.child("Games").child(newGame.gameName()).setValue(newGame).addOnCompleteListener(this) { task2 ->
            if (task2.isSuccessful) {
                Log.w("succ","success")
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