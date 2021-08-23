package com.example.mouseguardsheetmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import Sheet
import com.google.gson.Gson
import Utils
import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.*
import com.google.firebase.database.*
import android.provider.MediaStore
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.*
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class SheetEditActivity : AppCompatActivity() {

    private lateinit var mDatabase : DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mStorage: StorageReference

    private var gson = Gson()

    private lateinit var charPic : Bitmap
    private var isPicPresent : Boolean = false

    private var currentSheet = Sheet()
    private var currentUserName : String = ""

    //private var mantleColors : ArrayList<String> = arrayListOf(getString(R.string.red), getString(R.string.yellow), getString(R.string.black), getString(R.string.green))
    public var mantleColors : ArrayList<String> = arrayListOf()

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
        mStorage = Firebase.storage.reference

        mantleColors = arrayListOf(getString(R.string.red),getString(R.string.yellow),getString(R.string.black),getString(R.string.green))

        populateSpinner()

        setUserNameListener()

        sheetID = intent.getStringExtra("sheetID").toString()
        if (!sheetID.isNullOrEmpty())
            RetrieveSheetData(sheetID)
            //charPic = RetrieveCharPic(sheetID)
    }

    private fun populateSpinner()
    {
        var arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mantleColors)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mantleColorSpinner.adapter = arrayAdapter
    }

    private fun RetrieveCharPic(id : String)
    {
        if (false)
            isPicPresent = false
        else
            isPicPresent = true

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
                if (isPicPresent) {
                    val stream = ByteArrayOutputStream()
                    charPic.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val bytes = stream.toByteArray()
                    var uploadTask = mStorage.child("proPics").child(sheetID).putBytes(bytes)
                    uploadTask.addOnFailureListener {
                        // Handle unsuccessful uploads
                    }.addOnSuccessListener { taskSnapshot ->
                        finish()
                    }
                }
                else finish()
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
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (askForPermissions()) {
            startActivityForResult(cameraIntent, 200)
        }
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
        else if (requestCode == 200)
        {
            if (resultCode == Activity.RESULT_OK && data != null) {
                characterPicImageView.setImageBitmap(data.extras?.get("data") as Bitmap)
                charPic = data.extras?.get("data") as Bitmap
                isPicPresent = true
            }
        }
    }

    fun isPermissionsAllowed(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            false
        } else true
    }

    fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this as Activity,Manifest.permission.CAMERA)) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(this as Activity,arrayOf(Manifest.permission.CAMERA),200)
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String>,grantResults: IntArray) {
        when (requestCode) {
            200 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission is granted, you can perform your operation here
                } else {
                    // permission is denied, you can ask for permission again, if you want
                    //  askForPermissions()
                }
                return
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton("App Settings",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    // send to app settings if permission is denied permanently
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", getPackageName(), null)
                    intent.data = uri
                    startActivity(intent)
                })
            .setNegativeButton("Cancel",null)
            .show()
    }
}