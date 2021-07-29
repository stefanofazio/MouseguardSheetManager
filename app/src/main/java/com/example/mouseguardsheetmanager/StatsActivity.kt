package com.example.mouseguardsheetmanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class StatsActivity : AppCompatActivity() {

    private lateinit var receivedStats : HashMap<String, Int>
    private lateinit var editTextList : MutableList<Pair<EditText, EditText>>
    private var statsList : MutableList<Pair<String, Int>> = mutableListOf<Pair<String, Int>>()

    private lateinit var confirmStatsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        confirmStatsButton = findViewById(R.id.confirmStatsButton)
        getEditText()

        receivedStats = intent.getSerializableExtra("stats") as HashMap<String, Int>
        statsList = getListFromHashMap(receivedStats)
        setContent()
    }

    private fun setContent()
    {
        for (i in 0..Math.min(editTextList.count(), statsList.count()) - 1)
        {
            editTextList[i].first.setText(statsList[i].first)
            editTextList[i].second.setText(statsList[i].second.toString())
        }
    }

    private fun getEditText()
    {
        editTextList = mutableListOf<Pair<EditText, EditText>>()
        editTextList.add(Pair(findViewById(R.id.stat1NameEditText), findViewById(R.id.stat1ValueEditText)))
        editTextList.add(Pair(findViewById(R.id.stat2NameEditText), findViewById(R.id.stat2ValueEditText)))
        editTextList.add(Pair(findViewById(R.id.stat3NameEditText), findViewById(R.id.stat3ValueEditText)))
        editTextList.add(Pair(findViewById(R.id.stat4NameEditText), findViewById(R.id.stat4ValueEditText)))
        editTextList.add(Pair(findViewById(R.id.stat5NameEditText), findViewById(R.id.stat5ValueEditText)))
        editTextList.add(Pair(findViewById(R.id.stat6NameEditText), findViewById(R.id.stat6ValueEditText)))
        editTextList.add(Pair(findViewById(R.id.stat7NameEditText), findViewById(R.id.stat7ValueEditText)))
        editTextList.add(Pair(findViewById(R.id.stat8NameEditText), findViewById(R.id.stat8ValueEditText)))
        editTextList.add(Pair(findViewById(R.id.stat9NameEditText), findViewById(R.id.stat9ValueEditText)))
    }

    private fun getListFromHashMap(data : HashMap<String, Int>) : MutableList<Pair<String, Int>>
    {
        var returnValue = mutableListOf<Pair<String,Int>>()
        for (element in data)
        {
            var newPair = Pair<String, Int>(element.key, element.value.toInt())
            returnValue.add(newPair)
        }
        return returnValue
    }

    private fun getHashMapFromList(data : List<Pair<String, Int>>) : HashMap<String, Int>
    {
        var returnValue = HashMap<String, Int>()
        for (element in data)
        {
            if (!element.first.equals(""))
                returnValue[element.first] = element.second
        }
        return returnValue
    }

    private fun getValuesFromEditText() : MutableList<Pair<String, Int>>
    {
        var returnList = mutableListOf<Pair<String, Int>>()
        for (element in editTextList)
        {
            if (element.first.text.isNotEmpty() and isNumber(element.second.text.toString()))
            {
                returnList.add(Pair<String, Int>(element.first.text.toString(), element.second.text.toString().toInt()))
            }
        }
        return returnList
    }

    private fun isNumber(s : String) : Boolean
    {
        return if (s.isNullOrEmpty()) false else s.all { Character.isDigit(it) }
    }

    fun confirmStats(view:View?)
    {
        statsList = getValuesFromEditText()
        var newStats = getHashMapFromList(statsList)
        val returnIntent = Intent()
        returnIntent.putExtra("stats", newStats)
        setResult(RESULT_OK, returnIntent)
        finish()
    }

}