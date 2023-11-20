import java.io.FileInputStream
import java.sql.*
import java.util.*


// https://chat.openai.com/share/d3fea2f3-6b0a-4e7e-a71c-526f0561ce32
// My prompts with ChatGPT3.5 to help me connect DB

class DB {

    private val properties = Properties()

    init {
        // Load properties from the file
        val filePath = "C:\\Users\\Admin\\IdeaProjects\\HW5\\src\\main\\kotlin\\config.properties"
        val fileInputStream = FileInputStream(filePath)
        properties.load(fileInputStream)
        fileInputStream.close()

    }
    private val url: String = properties.getProperty("db.url")
    private val username: String = properties.getProperty("db.username")
    private val password: String = properties.getProperty("db.password")

    private val connection: Connection = DriverManager.getConnection(url, username, password)


    fun createTable() {
        try {
            val statement: Statement = connection.createStatement()

            // SQL statement to create the "Tasks" table
            val createTableSQL = """
            CREATE TABLE IF NOT EXISTS Tasks (
                name TEXT,
                description TEXT,
                daysLeft INT,
                priority INT,
                timeToComplete DOUBLE PRECISION,
                status BOOLEAN
            );
        """.trimIndent()

            // Execute the SQL statement to create the table
            statement.execute(createTableSQL)

            println("Table 'Tasks' created or already exists.")

        } catch (e: SQLException) {
            println("Error creating table 'Tasks': ${e.message}")
        }
    }


    fun loadTasks(): List<String> {
        val statement = connection.prepareStatement("SELECT name FROM tasks")
        val resultSet = statement.executeQuery()

        val taskNames = mutableListOf<String>()

        while (resultSet.next()) {
            val name = resultSet.getString("name")
            taskNames.add(name)
        }

        resultSet.close()
        statement.close()

        return taskNames
    }


    fun getData(): List<TaskClass> {
        val statement = connection.prepareStatement("SELECT name, daysLeft, priority, timeToComplete FROM tasks")
        val resultSet = statement.executeQuery()

        val tasks = mutableListOf<TaskClass>()

        while (resultSet.next()) {
            val name = resultSet.getString("name")
            val daysLeft = resultSet.getInt("daysLeft")
            val priority = resultSet.getInt("priority")
            val timeToComplete = resultSet.getDouble("timeToComplete")

            val task = TaskClass(name, null, daysLeft, priority, timeToComplete, false)
            tasks.add(task)
        }

        resultSet.close()
        statement.close()

        return tasks
    }


    fun deleteTask(taskName: String) {
        val sql = "DELETE FROM Tasks WHERE name = ?"
        val preparedStatement = connection.prepareStatement(sql)

        preparedStatement.setString(1, taskName)

        preparedStatement.executeUpdate()

        preparedStatement.close()
    }

    fun editTask(taskName: String, updatedTask: TaskClass) {
        val sql = "UPDATE Tasks SET name = ?, description = ?, daysLeft = ?, priority = ?, timeToComplete = ?, status = ? WHERE name = ?"
        val preparedStatement = connection.prepareStatement(sql)

        preparedStatement.setString(1, updatedTask.name)

        // Set description only if updatedDescription is not null
        if (updatedTask.description != null) {
            preparedStatement.setString(2, updatedTask.description)
        } else {
            preparedStatement.setNull(2, Types.VARCHAR)
        }

        // Set date only if updatedDate is not null
        preparedStatement.setInt(3, updatedTask.daysLeft)

        // Set priority only if updatedPriority is not null
        if (updatedTask.priority != -1) {
            preparedStatement.setInt(4, updatedTask.priority)
        } else {
            preparedStatement.setNull(4, Types.INTEGER)
        }

        if (updatedTask.timeToComplete != -1.0) {
            preparedStatement.setDouble(5, updatedTask.timeToComplete)
        } else {
            preparedStatement.setNull(5, Types.INTEGER)
        }
        // Set status only if updatedStatus is not null
        if (updatedTask.status) {
            preparedStatement.setBoolean(6, true)
        } else {
            preparedStatement.setBoolean(6, false)
        }

        preparedStatement.setString(6, taskName)

        preparedStatement.executeUpdate()

        preparedStatement.close()
    }




    fun addTask(newTask: TaskClass) {
        val sql = "INSERT INTO Tasks (name, description, daysLeft, priority, timeToComplete, status) VALUES (?, ?, ?, ?, ?, ?)"
        val preparedStatement = connection.prepareStatement(sql)

        preparedStatement.setString(1, newTask.name)
        preparedStatement.setString(2, newTask.description)
        preparedStatement.setInt(3, newTask.daysLeft)
        preparedStatement.setInt(4, newTask.priority)
        preparedStatement.setDouble(5, newTask.timeToComplete)
        preparedStatement.setBoolean(6, newTask.status)

        preparedStatement.executeUpdate()

        preparedStatement.close()
    }

}