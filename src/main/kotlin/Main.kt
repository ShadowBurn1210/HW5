
class Engine {
    val db = DB()


    init {
        db.createTable()
    }

    fun getTasks(): List<String> {
        return db.loadTasks()
    }

    fun addTask() {

        println("What is the name of the task")
        var taskName: String = readln()

        if (taskName == ""){
            taskName = "Not mentioned"
        }

        println("Describe the task")
        var taskDescription = readln()

        if (taskDescription == ""){
            taskDescription = "Not described"
        }
        println("What is the priority of the task")
        var taskPriority = readln()

        if (taskPriority == ""){
            taskPriority = "0"
        }


        println("How many days left till the deadline?")
        var taskDueDate = readln()

        if (taskDueDate == ""){
            taskDueDate = "0"

        }

        println("How long will it take to complete the task?")
        var timeToComplete = readln()

        if (timeToComplete == ""){
            timeToComplete = "99"

        }


        val newTask = TaskClass(
            name = taskName,
            description = taskDescription,
            daysLeft = taskDueDate.toInt(),
            priority = taskPriority.toInt(),
            timeToComplete = timeToComplete.toDouble(),
            status = false
        )
        db.addTask(newTask)
    }

    fun deleteTask() {
        println("What is the name of the task you want to delete")
        val taskName: String = readln()
        db.deleteTask(taskName)
    }

    fun editTask() {
        println("What is the name of the task you want to edit?")
        val taskName: String = readlnOrNull() ?: return

        println("Enter the new name of the task:")
        val updatedName: String = readlnOrNull() ?: taskName

        println("Enter the new description of the task:")
        val updatedDescription: String = readlnOrNull() ?: ""

        println("How many days left till the deadline?")
        val taskDueDate = readlnOrNull()?.toInt() ?: 1

        println("Enter the new priority of the task:")
        val updatedPriority: Int = readlnOrNull()?.toInt() ?: -1

        println("How long will it take to complete the task?")
        val timeToComplete: Double = readlnOrNull()?.toDouble() ?: -1.0

        println("Has the task been completes(false/true):")
        val updatedStatus: Boolean = readlnOrNull().toBoolean()

        val updatedTask = TaskClass(
            name = updatedName,
            description = updatedDescription,
            daysLeft = taskDueDate,
            priority = updatedPriority,
            timeToComplete = timeToComplete,
            status = updatedStatus
        )

        db.editTask(taskName, updatedTask)
    }

    fun orderData() {

        var time: Double?

        val tasks = db.getData()
        var totalTime = 0.0
        val taksToComplete = mutableListOf<String>()
        val tasksByPriority = tasks.sortedByDescending { it.priority }

        do {
            println("How many hours do you plan to study/work: ")
            time = readlnOrNull()?.toDouble()

            //https://chat.openai.com/share/56416b28-e5ed-4587-90b8-e5ade84fad72
            // This helped with sorting and getting the right data from the database

            if (time != null) {

                if (tasksByPriority.isNotEmpty()) {
                        // Go through all the tasks
                    for (task in tasksByPriority) {

                            // If the task to complete takes less than time hours
                        if (task.timeToComplete < time && task.timeToComplete + totalTime < time) {
                            totalTime += task.timeToComplete
                            task.name?.let { taksToComplete.add(it) }
                        }
                    }

                        // Show what tasks he can complete in under time hours
                    if (taksToComplete.isNotEmpty()) {
                            println("You can complete the following tasks in under $time hours: ")
                            println(taksToComplete)
                    }
                    else{
                            println("There are no tasks that you can complete in under $time hours")
                    }
                }

            } else {
                println("Invalid input. Please enter a valid number.")
            }

        } while (time == null || time < 0.0)
    }


}

fun main() {
    val newTask = Engine()

    var action: String?

// https://stackoverflow.com/questions/76232682/how-kotlin-pattern-matching-work-as-scala
    // This helped with the pattern matching
    do {
        println("Enter your action: ")
        action = readlnOrNull()

        when (action) {
            "add" -> newTask.addTask()
            "delete" -> newTask.deleteTask()
            "edit" -> newTask.editTask()
            "show" -> println(newTask.getTasks())
            "task" -> println(newTask.getTasks().random())
            "help" -> println("add, delete, edit, show, task, prioritize, end")
            "prioritize" ->  newTask.orderData()
                else -> {
                    println("Incorrect input. Please try again.")
                }
            }
    } while (action !in listOf("end"))

}

