package database.config

import core.enums.LogColors
import core.logs.BasicLog
import database.annotations.Column
import database.annotations.Id
import database.annotations.Table
import database.enums.GeneratedBy
import java.io.FileInputStream
import java.lang.reflect.Field
import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * Basic ORM class to handle database operations.
 * @property url The database URL.
 * @property username The database username.
 * @property password The database password.
 * @property connection The database connection.
 * @property props The properties file for database configuration.
 * @param T The type of the entity.
 * @constructor Accepts a KClass of the entity type.
 */
abstract class BasicOrm<T : Any>(val entityClass: KClass<T>) {

    private var url: String
    private var username: String
    private var password: String
    var connection: Connection
    private val props: Properties = Properties()

    init {
        runCatching {
            FileInputStream("src/main/resources/server.properties")
        }.onSuccess {
            props.load(it)
        }.onFailure {
            BasicLog.getLogWithColorFor<(Any)>(
                LogColors.YELLOW,
                StringBuilder().append("Using Database Default Properties !\n")
                    .append("| Add server.properties on main properties folder |\n").append("| Options:\n")
                    .append("| database.url -> example: jdbc:mysql://localhost:3306/your_database\n")
                    .append("| database.username -> example: root\n").append("| database.password -> example: root\n")
                    .toString()
            )
        }

        url = props.getProperty("database.url") ?: "jdbc:mysql://127.0.0.1:3306/my_db"
        username = props.getProperty("database.username") ?: "root"
        password = props.getProperty("database.password") ?: "root"

        try {
            connection = DriverManager.getConnection(url, username, password)
        } catch (e: Exception) {
            throw RuntimeException("Failed to connect to the database", e)
        }
    }

    /**
     * Inserts a record into the database.
     * @param data The object to be inserted.
     * @throws IllegalArgumentException if the class is not annotated with @Table.
     */
    fun insert(data: T) {
        val query: StringBuilder = StringBuilder()

        try {
            val tableName: String? = data::class.findAnnotation<Table>()?.name

            if (tableName == null) {
                throw IllegalArgumentException("Class ${data::class.simpleName} is not annotated with @Table")
            }

            val fields: Array<Field> = data::class.java.declaredFields

            query.append("INSERT INTO $tableName (")

            fields.forEach { field ->
                val name = field.getAnnotation(Column::class.java)?.name

                field.getAnnotation(Id::class.java)?.let {
                    if (it.type == GeneratedBy.AUT0_INCREMENT) {
                        return@forEach
                    }
                }

                if (name != null) {
                    query.append(name)
                    if (field != fields.last()) {
                        query.append(", ")
                    }
                }
            }

            query.append(") VALUES (")

            fields.forEach { field ->
                field.getAnnotation(Id::class.java)?.let {
                    if (it.type == GeneratedBy.AUT0_INCREMENT) {
                        return@forEach
                    }
                }

                field.isAccessible = true
                val value = field.get(data)

                when (field.type) {
                    Int::class.java -> query.append(value)
                    String::class.java -> query.append("'$value'")
                    Boolean::class.java -> query.append(if (value as Boolean) 1 else 0)
                    else -> throw IllegalArgumentException("Unsupported data type: ${field.type}")
                }

                if (field != fields.last()) {
                    query.append(", ")
                }
            }
            query.append(");")

            println(query)

            val statement = connection.createStatement()

            statement.execute(query.toString())

            statement.close()
        } catch (e: Exception) {
            BasicLog.getLogWithColorFor<BasicOrm<T>>(
                LogColors.RED,
                StringBuilder().append("Error inserting data: ${e.message}\n").append("Query: $query").toString()
            )
        }
    }

    /**
     * Finds all records in the database for the given class.
     * @return A list of records.
     * @throws IllegalArgumentException if the class is not annotated with @Table.
     */
    fun findAll(): MutableList<T> {
        val query: StringBuilder = StringBuilder()

        try {
            val tableName: String? = entityClass.findAnnotation<Table>()?.name

            if (tableName == null) {
                throw IllegalArgumentException("Class ${entityClass.simpleName} is not annotated with @Table")
            }

            query.append("SELECT * FROM $tableName;")

            println(query)

            val statement = connection.createStatement()

            statement.executeQuery(query.toString())

            val resultSet = statement.resultSet

            val list: MutableList<T> = mutableListOf()

            while (resultSet.next()) {
                val obj = entityClass.java.getDeclaredConstructor().newInstance()
                val fields: Array<Field> = entityClass.java.declaredFields

                // Iterate over the fields and set their values from the result set
                fields.forEach { field ->
                    field.isAccessible = true
                    val name = field.getAnnotation(Column::class.java)?.name
                    if (name != null) {
                        val value = resultSet.getObject(name)
                        field.set(obj, value)
                    }
                }
                list.add(obj)
            }
            resultSet.close()
            statement.close()

            return list
        } catch (e: Exception) {
            BasicLog.getLogWithColorFor<BasicOrm<T>>(
                LogColors.RED,
                StringBuilder().append("Error finding all records: ${e.message}\n").append("Query: $query").toString()
            )
            return mutableListOf()
        }
    }
}