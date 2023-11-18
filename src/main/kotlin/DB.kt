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

            // Check if the "Tasks" table already exists
            val tableExistsSQL = "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'Tasks');"
            val resultSet: ResultSet = statement.executeQuery(tableExistsSQL)
            resultSet.next()
            val tableExists = resultSet.getBoolean(1)

            if (!tableExists) {
                // SQL statement to create the "Tasks" table
                val createTableSQL = """
                CREATE TABLE Tasks (
                    name TEXT,
                    description TEXT,
                    date TIMESTAMP,
                    priority INT,
                    status BOOLEAN
                );
            """.trimIndent()

                // Execute the SQL statement to create the table
                statement.execute(createTableSQL)

                println("Table 'Tasks' created successfully.")
            }
        } catch (e: SQLException) {
            println("Table 'Tasks' already exists.")
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

    fun deleteTask(taskName: String) {
        val sql = "DELETE FROM Tasks WHERE name = ?"
        val preparedStatement = connection.prepareStatement(sql)

        preparedStatement.setString(1, taskName)

        preparedStatement.executeUpdate()

        preparedStatement.close()
    }

    fun editTask(taskName: String, updatedTask: TaskClass) {
        val sql = "UPDATE Tasks SET name = ?, description = ?, date = ?, priority = ?, status = ? WHERE name = ?"
        val preparedStatement = connection.prepareStatement(sql)

        preparedStatement.setString(1, updatedTask.name)

        // Set description only if updatedDescription is not null
        if (updatedTask.description != null) {
            preparedStatement.setString(2, updatedTask.description)
        } else {
            preparedStatement.setNull(2, Types.VARCHAR)
        }

        // Set date only if updatedDate is not null
        preparedStatement.setTimestamp(3, Timestamp(updatedTask.date.time))

        // Set priority only if updatedPriority is not null
        if (updatedTask.priority != -1) {
            preparedStatement.setInt(4, updatedTask.priority)
        } else {
            preparedStatement.setNull(4, Types.INTEGER)
        }

        // Set status only if updatedStatus is not null
        if (updatedTask.status) {
            preparedStatement.setBoolean(5, true)
        } else {
            preparedStatement.setBoolean(5, false)
        }

        preparedStatement.setString(6, taskName)

        preparedStatement.executeUpdate()

        preparedStatement.close()
    }




    fun addTask(newTask: TaskClass) {
        val sql = "INSERT INTO Tasks (name, description, date, priority, status) VALUES (?, ?, ?, ?, ?)"
        val preparedStatement = connection.prepareStatement(sql)

        preparedStatement.setString(1, newTask.name)
        preparedStatement.setString(2, newTask.description)
        preparedStatement.setTimestamp(3, Timestamp(newTask.date.time))
        preparedStatement.setInt(4, newTask.priority)
        preparedStatement.setBoolean(5, newTask.status)

        preparedStatement.executeUpdate()

        preparedStatement.close()
    }

}
//
//
//case class Register(username: String, password: String)
//case class RegistrationResult(username: String, registered: Boolean, message: String)
//case class Login(username: String, password: String)
//case class LoginResults(username: String, savedGame : String, message: String)
//case class LoadGame(username: String)
//
//case class LoadGameResults(username: String, RetrievedData : String)
//
//
//case class SaveGame(username: String, charactersJSON: String)
//
//trait Database {
//    def playerExists(username: String, password: String): Boolean
//
//    def IncorrectInput(username: String, password: String): String
//
//    def createPlayer(username: String, password: String): Unit
//    def saveGameState(username: String, gameState: String): Unit
//    def loadGameState(username: String): String
//}
//
//    var connection: Connection? = null
//
//    fun createTable() = {
//
//        val statement = Connection.createStatement()
//        println("Postgres connector")
//
//        statement.execute("CREATE TABLE IF NOT EXISTS players ( username TEXT primary key,  password TEXT, GameData TEXT);")
//
//
//        class DB extends Actor with Database {
//
//        classOf[org.postgresql.Driver]
//
////   val url = "jdbc:postgresql://localhost:5432/Demo3"
////   val username = "postgres" // change
////   val password = "password"
////   val connection = DriverManager.getConnection(url, username, password)
//
//        def createTable(): Unit = {
//        val statement = connection.createStatement()
//        println("Postgres connector")
//
//        statement.execute("CREATE TABLE IF NOT EXISTS players ( username TEXT primary key,  password TEXT, GameData TEXT);")
//
//    }
//
//        createTable()
//
//        var users: List[String] = List()
//
//        override def receive: Receive = {
//
//        case party: Register =>
//        val PlayerAlreadyExists: Boolean = playerExists(party.username, party.password)
//        println(PlayerAlreadyExists)
//        if (PlayerAlreadyExists) {
//            sender() ! RegistrationResult(party.username, registered = false, "Player already exists")
//        } else {
//            //        println("got there")
//
//            val passwordEncripted = BCrypt.withDefaults.hashToString(12, party.password.toCharArray)
//            createPlayer(party.username, passwordEncripted)
//            sender() ! RegistrationResult(party.username, registered = true, "Registration Successful")
//        }
//
//
//        case party: Login =>
//        val PlayerAlreadyExists: Boolean = playerExists(party.username, party.password)
//        if (PlayerAlreadyExists) {
//            val gameState = loadGameState(party.username)
//            sender() ! LoginResults(party.username, gameState, "Success")
//        } else {
//            val WrongCredential = IncorrectInput(party.username, party.password)
//            sender() ! LoginResults(party.username, "", WrongCredential)
//        }
//
//
//        case party: SaveGame =>
//        saveGameState(party.username, party.charactersJSON)
//
////    case party: LoadGame =>
////      var DBData = loadGameState(party.username)
////      sender() ! LoadGameResults(party.username, DBData)
//    }
//
//
//        override def playerExists(playerUsername: String, Playerpasword: String): Boolean = {
//        val statement = connection.createStatement()
//        val result: ResultSet = statement.executeQuery("SELECT * FROM players")
//
//
//        while (result.next()) {
//            val username = result.getString("username")
//            val password = result.getString("password")
//
//            val decriptingPassword = BCrypt.verifyer.verify(Playerpasword.toCharArray, password)
//            if (username == playerUsername && decriptingPassword.verified) {
//                return true
//            }
//        }
//        return false
//    }
//
//        override def createPlayer(username: String, password: String): Unit = {
//        val statement = connection.prepareStatement("INSERT INTO players Values (?, ?, ?)")
//
//        statement.setString(1, username)
//        statement.setString(2, password)
//        statement.setString(3, "")
//
//
//        statement.execute()
//    }
//
//        override def saveGameState(username: String, gameState: String): Unit = {
//        val updateQuery = s"UPDATE players SET GameData = '$gameState' WHERE username = '$username'"
//
//        // Update the row with the new game_state value
//        val updateStatement = connection.prepareStatement(updateQuery)
////        println(gameState)
//        updateStatement.executeUpdate()
////    val statement = connection.prepareStatement(s"SELECT GameData FROM players WHERE username = '$username'")
//        //    println(data)
//    }
//
//
//        override def loadGameState(username: String): String = {
//        val statement = connection.prepareStatement(s"SELECT GameData FROM players WHERE username = '$username'")
//
//        val resultSet = statement.executeQuery()
//
//        if (resultSet.next()) {
//            val gameData = resultSet.getString("GameData")
////      println("DB data" + gameData)
//            gameData
//        } else {
//            throw new NoSuchElementException(s"User '$username' not found.")
//        }
//    }
//
//        override def IncorrectInput(Playerusername: String, Playerpassword: String): String = {
//        val statement = connection.createStatement()
//        val result: ResultSet = statement.executeQuery("SELECT * FROM players")
//
//
//        while (result.next()) {
//            val username = result.getString("username")
//            val password = result.getString("password")
//
//            val decriptingPassword = BCrypt.verifyer.verify(Playerpassword.toCharArray, password)
//            if (username != Playerusername && decriptingPassword.verified) {
//                return "Player Username is incorrect"
//
//            } else if (username == Playerusername && !decriptingPassword.verified) {
//
//                return "Player password is incorrect"
//            }
//        }
//
//        return "Password and Username is incorrect"
//    }
//    }
//    }
//
//
