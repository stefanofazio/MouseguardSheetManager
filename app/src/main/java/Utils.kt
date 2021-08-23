import android.content.Context
import kotlin.random.Random


class Utils {
    companion object {
        fun AdaptToJSON(startingJSON: String): String {
            var newJSON = startingJSON.removePrefix("DataSnapshot {")
            newJSON = newJSON.removeSuffix("}")
            newJSON = newJSON.removeRange(0, newJSON.indexOf("{"))
            return newJSON
        }


        fun IDGeneration(item : String) : String
        {
            var returnValue = ""
            for (i in 0..7)
            {
                var random = Random.nextInt(0, item.length)
                returnValue += item[random]
            }
            return returnValue
        }
    }
}