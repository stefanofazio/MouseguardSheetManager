package com.example.mouseguardsheetmanager

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class CharactersActivity : AppCompatActivity() {
    private lateinit var charsList: ListView
    private lateinit var charsButton: Button
    private var role: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_characters)
        charsList = findViewById(R.id.charsListView)
        charsButton = findViewById(R.id.charsButton)
        role = intent.getStringExtra("role").toString()
        setListViewContent()
    }

    fun setListViewContent()
    {
        var myAdapter: charAdapter = charAdapter(this, 22)
        charsList.adapter = myAdapter

    }

    class charAdapter(private val context: Context, private val elements: Int) : BaseAdapter()
    {
        var colors = arrayOf<String>("Red", "Black", "Blue")

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
            val rowView = LayoutInflater.from(context).inflate(R.layout.sheet_row, parent, false)
            var charName: TextView = rowView.findViewById(R.id.character_Name)
            var mantleColor: TextView = rowView.findViewById(R.id.mantle_Color)
            var charPic: ImageView = rowView.findViewById(R.id.characterPic)
            charName.text = "Personaggio numero " + position.toString()
            mantleColor.text = colors[(0..2).random()]

            return rowView
        }
    }
}