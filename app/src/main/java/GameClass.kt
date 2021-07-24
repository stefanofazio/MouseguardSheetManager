import android.provider.ContactsContract
import java.security.acl.Owner

class GameClass {
    var gameName:String = ""
    var gameOwnerName:String = ""
    var gameOwnerEmail:String = ""
    lateinit var players: HashMap<String, String>

    constructor()
    {

    }

    constructor(name:String, ownerName:String, ownerEmail:String){
        this.gameName = name
        this.gameOwnerName = ownerName
        this.gameOwnerEmail = ownerEmail
        players = HashMap()
        players["Serena"] = "serena@email.it"
        players["stefano"] = "stefano@email.it"
    }

    constructor(GameName:String, OwnerName:String, OwnerEmail:String, Players:HashMap<String, String>)
    {
        this.gameName = GameName
        this.gameOwnerName = OwnerName
        this.gameOwnerEmail = OwnerEmail
        this.players = Players
    }

    fun gameName() : String
    {
        return this.gameName
    }

    fun gameOwnerEmail():String
    {
        return this.gameOwnerEmail
    }

    fun gameOwnerName ():String
    {
        return this.gameOwnerName
    }

    fun addPlayer(name:String, email:String)
    {
        this.players[name] = email
    }

}