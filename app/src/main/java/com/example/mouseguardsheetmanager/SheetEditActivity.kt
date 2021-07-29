package com.example.mouseguardsheetmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import Sheet
import com.google.gson.Gson
import Utils
import android.app.Activity
import android.content.Intent
import android.widget.*
import com.google.firebase.database.*

class SheetEditActivity : AppCompatActivity() {

    private lateinit var mDatabase : DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    private var gson = Gson()

    private var currentSheet = Sheet()
    private var currentUserName : String = ""

    //private var mantleColors : ArrayList<String> = arrayListOf(getString(R.string.red), getString(R.string.yellow), getString(R.string.black), getString(R.string.green))
    private var mantleColors : ArrayList<String> = arrayListOf()

    private lateinit var characterPicImageView : ImageView
    private lateinit var charNameEditText: EditText
    private lateinit var charSecondNameEditText : EditText
    private lateinit var mantleColorSpinner : Spinner
    private lateinit var statsButton: Button
    private lateinit var confirmSheetButton : Button
    private lateinit var characterPicButton: Button

    private var sheetID : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sheet_edit)

        characterPicImageView = findViewById(R.id.characterPicImageView)
        charNameEditText = findViewById(R.id.charNameEditText)
        charSecondNameEditText = findViewById(R.id.charSecondNameEditText)
        mantleColorSpinner = findViewById(R.id.mantleColorSpinner)
        statsButton = findViewById(R.id.statsButton)
        confirmSheetButton = findViewById(R.id.confirmSheetButton)
        characterPicButton = findViewById(R.id.characterPicButton)

        mDatabase = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        mantleColors = arrayListOf(getString(R.string.red),getString(R.string.yellow),getString(R.string.black),getString(R.string.green))

        populateSpinner()

        setUserNameListener()

        sheetID = intent.getStringExtra("sheetID").toString()
        if (!sheetID.isNullOrEmpty())
            RetrieveSheetData(sheetID)

    }

    private fun populateSpinner()
    {
        var arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mantleColors)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mantleColorSpinner.adapter = arrayAdapter
    }

    private fun RetrieveSheetData(id : String)
    {
        val sheetListener = object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                var tmpSheet = Sheet()
                val tmpJson = Utils.AdaptToJSON(snapshot.toString())
                tmpSheet = gson.fromJson<Sheet>(tmpJson, Sheet::class.java)
                currentSheet = tmpSheet
                setContent(tmpSheet)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Sheet","loadOnName:cancelled", error.toException())
            }
        }
        mDatabase.child("Sheets").child(id).addValueEventListener(sheetListener)
    }

    private fun setUserNameListener()
    {
        val userNameListener = object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUserName = snapshot.value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Sheet","loadOnName:cancelled", error.toException())
            }
        }
        mDatabase.child("Users").child(mAuth.currentUser?.uid.toString()).child("name").addValueEventListener(userNameListener)
    }

    private fun setContent(sheet : Sheet)
    {
        charNameEditText.setText(sheet.characterName)
        charSecondNameEditText.setText(sheet.characterSecondName)
        mantleColorSpinner.setSelection(sheet.mantleColor)
    }

    private fun putSheetOnDatabase(sheet: Sheet, sheetID : String)
    {
        var gson = Gson()
        mDatabase.child("Sheets").child(sheet.sheetID).setValue(sheet).addOnCompleteListener(this) { task2 ->
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

    fun addCharacterPic(view:View?)
    {

    }

    fun confirmSheet(view:View?)
    {
        if (!charNameEditText.text.isNullOrEmpty() and
            !charSecondNameEditText.text.isNullOrEmpty() and
            !mantleColorSpinner.selectedItem.toString().isNullOrEmpty())
        {
            val name = charNameEditText.text.replace("\\s".toRegex(), "")
            val secondName = charSecondNameEditText.text.replace("\\s".toRegex(), "")
            val mantleColor = mantleColorSpinner.selectedItemPosition
            currentSheet.characterName = name
            currentSheet.characterSecondName = secondName
            currentSheet.mantleColor = mantleColor
            currentSheet.ownerEmail = mAuth.currentUser?.email.toString()
            currentSheet.ownerName = currentUserName
            if (sheetID.isNullOrEmpty())
                currentSheet.sheetID = Utils.IDGeneration(currentSheet.toString())

            putSheetOnDatabase(currentSheet, currentSheet.sheetID)
        }
    }

    fun openStats(view:View?)
    {
        var intent = Intent(this, StatsActivity::class.java)
        if (currentSheet.stats == null)
            intent.putExtra("stats", HashMap<String, Int>())
        else
            intent.putExtra("stats", currentSheet.stats)

        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                var newStats : HashMap<String, Int> = data?.getSerializableExtra("stats") as HashMap<String, Int>
                currentSheet.stats = newStats
            }
        }
    }
}