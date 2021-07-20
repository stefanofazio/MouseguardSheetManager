import android.provider.ContactsContract

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