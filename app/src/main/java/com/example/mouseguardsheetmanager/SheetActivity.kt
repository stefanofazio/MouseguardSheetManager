package com.example.mouseguardsheetmanager

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import Sheet
import android.util.Log
import android.widget.ImageView
import android.widget.ListView
import com.google.firebase.database.*
import com.google.gson.Gson

class SheetActivity : AppCompatActivity() {

    private lateinit var role : String
    private lateinit var gameID : String
    private lateinit var mDatabase: DatabaseReference

    private lateinit var sheetListView: ListView
    private var sheets = mutableListOf<Sheet>()
    private var gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sheet)
        sheetListView = findViewById(R.id.sheetListView)

        role = intent.getStringExtra("role").toString()

        mDatabase = FirebaseDatabase.getInstance().reference

        if (role.equals("master")) {
            gameID = intent.getStringExtra("gameID").toString()
            writedummysheet()
            setGameSheetListener(gameID)
        }
        else {
            gameID = ""
        }



    }

    private fun writedummysheet()
    {
        val tmpSheet = Sheet("alex","Rufus",  "Red", "Beatrice", "Beatrice@email.it")
        tmpSheet.addToGame("eueeofBo")
        var gson = Gson()
        val jsonGame = gson.toJson(tmpSheet)
        mDatabase.child("Sheets").child(Utils.IDGeneration(tmpSheet.toString())).setValue(tmpSheet).addOnCompleteListener(this) { task2 ->
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

    private fun setGameSheetListener(id : String)
    {
        val sheetListener = object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allSheets = mutableListOf<Sheet>()
                for (i in snapshot.children) {
                    var tmpSheet = Sheet()
                    val tmpJson = Utils.AdaptToJSON(i.toString())
                    tmpSheet = gson.fromJson<Sheet>(tmpJson, Sheet::class.java)
                    if (tmpSheet.usedInGames.containsKey(id)) {
                        allSheets.add(tmpSheet)
                    }
                }
                sheets = allSheets
                SetListViewContent()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Sheet","loadOnName:cancelled", error.toException())
            }
        }

        mDatabase.child("Sheets").addValueEventListener(sheetListener)

    }

    private fun SetListViewContent()
    {
        var myAdapter = SheetAdapter(this, sheets)
        sheetListView.adapter = myAdapter
    }

    fun sheetButton(view : View?)
    {

    }
}

class SheetAdapter(private val context: Context, private val elements: List<Sheet>) : BaseAdapter()
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
        val rowView = LayoutInflater.from(context).inflate(R.layout.sheet_row, parent, false)
        val sheet = elements[Position]
        var characterName: TextView = rowView.findViewById(R.id.character_Name)
        var mantleColor: TextView = rowView.findViewById(R.id.mantle_Color)
        characterName.text = java.lang.String.format(context.getString(R.string.gameName),sheet.characterName)
        mantleColor.text = java.lang.String.format(context.getString(R.string.masterName),sheet.mantleColor)
        //playerNumber.text = java.lang.String.format(context.getString(R.string.playerNumber),game.players.count().toString())
        return rowView
    }
}