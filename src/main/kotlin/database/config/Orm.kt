package database.config

import database.annotations.Column
import database.annotations.Table
import java.lang.reflect.Field
import java.sql.Connection
import java.sql.DriverManager
import kotlin.reflect.full.findAnnotation

class Orm(private val host: String, private val username: String, private val password: String) {
    lateinit var connection: Connection

    init {
        getJdbcConnection()
    }

    fun getJdbcConnection(): Connection {
        try {
            connection = DriverManager.getConnection(host, username, password)
        } catch (e: Exception) {
            throw RuntimeException("Failed to connect to the database", e)
        }
        return connection
    }

    fun writeToDatabase(data: Any) {

        val tableName: String? = data::class.findAnnotation<Table>()?.name

        if (tableName == null) {
            throw IllegalArgumentException("Class ${data::class.simpleName} is not annotated with @Table")
        }

        val fields: Array<Field> = data::class.java.declaredFields

        fields.forEach { field ->
            val name = field.getAnnotation(Column::class.java)?.name
            println(name)
        }

        fields.map { field ->
            field.isAccessible = true
            println(field.get(data))
        }

        val query: StringBuilder = StringBuilder()

        query.append("INSERT INTO $tableName (")

        fields.forEach { field ->
            val name = field.getAnnotation(Column::class.java)?.name

            if (name != null) {
                query.append(name)
                if (field != fields.last()) {
                    query.append(", ")
                }
            }
        }

        query.append(") VALUES (")

        fields.forEach { field ->
            field.isAccessible = true
            val value = field.get(data)

            if (value is String) {
                query.append("'$value'")
            } else {
                query.append(value)
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
    }
}