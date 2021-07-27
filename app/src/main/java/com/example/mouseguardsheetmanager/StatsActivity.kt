package com.example.mouseguardsheetmanager

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*


class StatsActivity : AppCompatActivity() {

    private lateinit var receivedStats : HashMap<String, Int>
    private var statsList : MutableList<Pair<String, Int>> = mutableListOf<Pair<String, Int>>()

    private lateinit var statsListView: ListView
    private lateinit var addStatButton: Button
    private lateinit var confirmStatsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        statsListView = findViewById(R.id.statsListView)
        addStatButton = findViewById(R.id.addStatButton)
        confirmStatsButton = findViewById(R.id.confirmStatsButton)

        receivedStats = intent.getSerializableExtra("stats") as HashMap<String, Int>
        statsList = getListFromHashMap(receivedStats)

        RefreshList()

    }

    private fun RefreshList()
    {
        var myAdapter = statsAdapter(this, statsList)
        statsListView.adapter = myAdapter

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

    fun addStat(view:View?)
    {
        statsList.add(Pair<String, Int>("", 0))
    }

    fun confirmStats(view:View?)
    {
        var newStats = getHashMapFromList(statsList)

    }

    class statsAdapter(private val context: Context, private val elements: List<Pair<String, Int>>) : BaseAdapter()
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
            val rowView = LayoutInflater.from(context).inflate(R.layout.stats_row, parent, false)
            val stat = elements[Position]
            var statName: EditText = rowView.findViewById(R.id.statNameEditText)
            var statValue: EditText = rowView.findViewById(R.id.statValueEditText)
            statName.setText(stat.first)
            statValue.setText(stat.second.toString())
            return rowView
        }
    }
}