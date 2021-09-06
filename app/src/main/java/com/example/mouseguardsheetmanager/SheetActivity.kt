package com.example.mouseguardsheetmanager

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import Sheet
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.createBitmap
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.*
import com.google.firebase.storage.ktx.storage

class SheetActivity : AppCompatActivity() {

    private lateinit var role : String
    private lateinit var gameID : String
    private var isForResult : Boolean = false

    private lateinit var mDatabase: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mStorage: StorageReference

    private lateinit var sheetListView: ListView
    val images = mutableListOf<Bitmap>()
    val sheetsWithPic = mutableListOf<String>()
    private var sheets = mutableListOf<Sheet>()
    private var gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sheet)
        sheetListView = findViewById(R.id.sheetListView)

        role = intent.getStringExtra("role").toString()

        mDatabase = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        mStorage = Firebase.storage.reference

        if (intent.hasExtra("isForResult"))
        {
            isForResult = true
        }

        if (role.equals("master")) {
            gameID = intent.getStringExtra("gameID").toString()
            setGameSheetListener(gameID, "")
        }
        else {
            if (intent.hasExtra("gameID")) {
                gameID = intent.getStringExtra("gameID").toString()
                setGameSheetListener(gameID, mAuth.currentUser?.email)
            }
            else {
                gameID = ""
                setGameSheetListener(gameID, mAuth.currentUser?.email)
            }
        }

        SetListListeners()

    }

    /*private fun writedummysheet()
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
    }*/

    private fun SetListListeners()
    {
        sheetListView.onItemClickListener = AdapterView.OnItemClickListener{
                parent, view, position, id ->
            val selectedSheet = sheets[position]
            if (isForResult)
            {
                val returnIntent = Intent()
                returnIntent.putExtra("sheetID", selectedSheet.sheetID.toString())
                setResult(RESULT_OK, returnIntent)
                finish()
            }
            else {
                openSheet(selectedSheet)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                var sheetID = data?.getStringExtra("sheetID").toString()
                insertSheetInGame(gameID, sheetID)
            }
        }
    }

    private fun openSheet(sheet : Sheet)
    {
        val intent = Intent(this, SheetEditActivity::class.java)
        intent.putExtra("sheetID", sheet.sheetID)
        intent.putExtra("role", role)
        startActivity(intent)
    }

    private fun setGameSheetListener(id : String, email : String?)
    {
        val sheetListener = object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allSheets = mutableListOf<Sheet>()
                for (i in snapshot.children) {
                    var tmpSheet = Sheet()
                    val tmpJson = Utils.AdaptToJSON(i.toString())
                    tmpSheet = gson.fromJson<Sheet>(tmpJson, Sheet::class.java)
                    if (!email.isNullOrEmpty() && id.isNotEmpty())
                    {
                        if (tmpSheet.ownerEmail.equals(email) && tmpSheet.usedInGames.containsKey(id))
                        {
                            allSheets.add(tmpSheet)
                        }
                    }
                    else {
                        if (!email.isNullOrEmpty()) {
                            if (tmpSheet.ownerEmail.equals(email))
                                allSheets.add(tmpSheet)
                        } else {
                            if (tmpSheet.usedInGames.containsKey(id))
                                allSheets.add(tmpSheet)
                        }
                    }
                }
                sheets = allSheets
                GetCharPics(sheets)
                //SetListViewContent()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Sheet","loadOnName:cancelled", error.toException())
            }
        }

        mDatabase.child("Sheets").addValueEventListener(sheetListener)

    }

    private fun GetCharPics(sheets : List<Sheet>)
    {
        for (element in sheets)
        {
            val sheetID = element.sheetID
            mStorage.child("proPics").child(sheetID).getBytes(1024 * 1024).addOnSuccessListener {
                images.add(BitmapFactory.decodeByteArray(it, 0, it.size) )
                sheetsWithPic.add(sheetID)
                SetListViewContent()
            }.addOnFailureListener{

            }
        }


    }

    private fun SetListViewContent()
    {
        var myAdapter = SheetAdapter(this, sheets, images, sheetsWithPic)
        sheetListView.adapter = myAdapter
    }

    fun sheetButton(view : View?)
    {
        if (role.equals("player") && gameID.isNotEmpty())
        {
            val addSheetToGameIntent = Intent (this, SheetActivity::class.java)
            addSheetToGameIntent.putExtra("role", role)
            addSheetToGameIntent.putExtra("isForResult", true)
            startActivityForResult(addSheetToGameIntent, 10)
        }
        else {
            val sheetIntent = Intent(this, SheetEditActivity::class.java)
            sheetIntent.putExtra("sheetID", "")
            startActivity(sheetIntent)
        }
    }

    private fun insertSheetInGame(game_id : String, sheet_id : String)
    {
        var needToUpdate = true
        val singleSheetListener = object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (needToUpdate) {
                    for (i in snapshot.children) {
                        var tmpSheet = Sheet()
                        var tmpJson = Utils.AdaptToJSON(i.toString())
                        tmpSheet = gson.fromJson<Sheet>(tmpJson, Sheet::class.java)
                        if (tmpSheet.sheetID.equals(sheet_id))
                        {
                            tmpSheet.addToGame(game_id)
                            updateSheet(tmpSheet)
                        }
                    }
                }
                needToUpdate = false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Sheet","loadOnName:cancelled", error.toException())
            }
        }

        mDatabase.child("Sheets").addValueEventListener(singleSheetListener)
    }

    private fun updateSheet(new_sheet : Sheet)
    {
        mDatabase.child("Sheets").child(new_sheet.sheetID).setValue(new_sheet).addOnCompleteListener(this) { task2 ->
            if (task2.isSuccessful) {
                Log.w("succ", "success")
            } else {
                Log.w(
                    "SigninActivity",
                    "createUserWithEmail:Failure",
                    task2.exception
                )
            }
        }
    }
}

class SheetAdapter(private val context: Context, private val elements: List<Sheet>, private val images : List<Bitmap>, private val sheetsWithPic : List<String>) : BaseAdapter()
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
        var mantleColors : ArrayList<String> = arrayListOf()
        mantleColors = arrayListOf(context.getString(R.string.red),context.getString(R.string.yellow),context.getString(R.string.black),context.getString(R.string.green))
        val rowView = LayoutInflater.from(context).inflate(R.layout.sheet_row, parent, false)
        val sheet = elements[Position]
        var characterName: TextView = rowView.findViewById(R.id.character_Name)
        var mantleColor: TextView = rowView.findViewById(R.id.mantle_Color)
        var charPic : ImageView = rowView.findViewById(R.id.characterPic)
        characterName.text = java.lang.String.format(context.getString(R.string.characterName),sheet.characterName)
        mantleColor.text = java.lang.String.format(context.getString(R.string.mantle_color),mantleColors[sheet.mantleColor])
        if (sheetsWithPic.contains(sheet.sheetID))
        {
            charPic.setImageBitmap(images[sheetsWithPic.indexOf(sheet.sheetID)])
        }
        return rowView
    }
}