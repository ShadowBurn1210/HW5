
import java.util.*


data class TaskClass(
    val name : String?,
    val description : String?,
    val daysLeft : Int = 1,
    val priority : Int = 1,
    val timeToComplete : Double = 4.0,
    val status : Boolean = false,
)
