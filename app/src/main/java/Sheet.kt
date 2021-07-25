import android.provider.ContactsContract
import java.security.acl.Owner

class Sheet {
    var characterName:String = ""
    var characterSecondName:String = ""
    var mantleColor:String = ""
    var ownerEmail:String = ""
    var ownerName:String = ""
    lateinit var usedInGames : HashMap<String, String>
    lateinit var stats : HashMap<String, Int>

    constructor()
    {

    }

    constructor(name : String, secondName:String, mantle : String, owner : String, email : String)
    {
        this.characterName = name
        this.characterSecondName = secondName
        this.mantleColor = mantle
        this.ownerName = owner
        this.ownerEmail = email
        usedInGames = HashMap()
        stats = HashMap()
    }

    constructor(name : String, secondName:String, mantle : String, owner : String, email : String, games : HashMap<String, String>, skills : HashMap<String, Int>)
    {
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