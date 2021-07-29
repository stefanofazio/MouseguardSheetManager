import android.provider.ContactsContract
import java.security.acl.Owner

class Sheet {
    var sheetID : String = "0"
    var characterName:String = ""
    var characterSecondName:String = ""
    var mantleColor:Int = 0
    var ownerEmail:String = ""
    var ownerName:String = ""
    lateinit var usedInGames : HashMap<String, String>
    lateinit var stats : HashMap<String, Int>

    constructor()
    {
        stats = HashMap()
        usedInGames = HashMap()
    }

    constructor(id:String, name : String, secondName:String, mantle : Int, owner : String, email : String)
    {
        this.sheetID = id
        this.characterName = name
        this.characterSecondName = secondName
        this.mantleColor = mantle
        this.ownerName = owner
        this.ownerEmail = email
        usedInGames = HashMap()
        stats = HashMap()
    }

    constructor(id:String, name : String, secondName:String, mantle : Int, owner : String, email : String, games : HashMap<String, String>, skills : HashMap<String, Int>)
    {
        this.sheetID = id
        this.characterName = name
        this.characterSecondName = secondName
        this.mantleColor = mantle
        this.ownerName = owner
        this.ownerEmail = email
        this.usedInGames = games
        this.stats = skills
    }




    fun addToGame(id : String)
    {
        this.usedInGames[id] = "si"
    }
}