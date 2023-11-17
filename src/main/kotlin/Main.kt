import java.util.*


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
        val taskName: String = readln()
        println("Describe the task")
            val taskDescription = readln()
        println("What is the priority of the task")
            val taskPriority = readln().toInt()


        val newTask = TaskClass(
            name = taskName,
            description = taskDescription,
            date = Date(),
            priority = taskPriority,
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
        println("What is the name of the task you want to edit")
        val taskName: String = readln()

        do {
            println("What you want to edit?")
            val reason: String = readln()

            when (reason) {
                "name" -> db.editTask(taskName)
//                "description" -> newTask.deleteTask()
//                "priority" -> newTask.editTask()
//                "status" -> println(newTask.getTasks())
                else -> {
                    println("Incorrect input. Please try again.")
                }
            }

        } while (reason !in listOf("name", "description", "priority", "status"))

        db.editTask(taskName)
    }
}

fun main() {
    val newTask = Engine()

    var action: String?

// https://stackoverflow.com/questions/76232682/how-kotlin-pattern-matching-work-as-scala


    do {
        println("Enter your action: ")
        action = readLine()

        when (action) {
            "add" -> newTask.addTask()
            "delete" -> newTask.deleteTask()
            "edit" -> newTask.editTask()
            "show" -> println(newTask.getTasks())
            "end" -> break
            else -> {
                println("Incorrect input. Please try again.")
            }
        }
    } while (action !in listOf("end"))


}

