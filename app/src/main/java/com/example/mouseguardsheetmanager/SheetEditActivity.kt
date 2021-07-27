package com.example.mouseguardsheetmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import Sheet
import com.google.gson.Gson
import Utils
import android.app.Activity
import android.content.Intent

class SheetEditActivity : AppCompatActivity() {

    private lateinit var mDatabase : DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    private var gson = Gson()

    private var currentSheet = Sheet()

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

        sheetID = intent.getStringExtra("sheetID").toString()
        if (!sheetID.isNullOrEmpty())
            RetrieveSheetData(sheetID)

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

    private fun setContent(sheet : Sheet)
    {
        charNameEditText.setText(sheet.characterName)
        charSecondNameEditText.setText(sheet.characterSecondName)
        //do things
    }

    private fun putSheetOnDatabase(sheet: Sheet, sheetID : String)
    {

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
            val mantleColor = mantleColorSpinner.selectedItem.toString()
            if (sheetID.isNullOrEmpty())
            {
                putSheetOnDatabase(currentSheet, sheetID)
            }

        }
    }



    fun openStats(view:View?)
    {
        var intent = Intent(this, StatsActivity::class.java)
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