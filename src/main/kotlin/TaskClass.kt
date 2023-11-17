
import java.util.*


data class TaskClass(
    val name : String?,
    val description : String?,
    val date : Date = Date(),
    val priority : Int = 1,
    val status : Boolean = false,
)
