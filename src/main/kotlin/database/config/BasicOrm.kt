package database.config

import core.enums.LogColors
import core.logs.BasicLog
import database.annotations.Column
import database.annotations.Id
import database.annotations.JoinColumn
import database.annotations.Table
import database.enums.GeneratedBy
import database.exceptions.BasicOrmError
import java.io.FileInputStream
import java.lang.reflect.Field
import java.sql.Connection
import java.sql.Date
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
            BasicLog.getLogWithColorFor<BasicOrm<T>>(
                LogColors.GREEN,
                StringBuilder().append("Connected to the database successfully.").toString()
            )
        } catch (e: Exception) {
            throw RuntimeException("Failed to connect to the database", e)
        }
    }

    /**
     * Inserts a record into the database.
     * @param entity The object to be inserted.
     * @throws IllegalArgumentException if the class is not annotated with @Table.
     * @throws BasicOrmError if there is an error executing the query.
     */
    @Throws(IllegalArgumentException::class, BasicOrmError::class)
    fun insert(entity: T) {
        val query: StringBuilder = StringBuilder()

        try {
            val tableName: String? = entity::class.findAnnotation<Table>()?.name

            if (tableName == null) {
                throw IllegalArgumentException("Class ${entity::class.simpleName} is not annotated with @Table")
            }

            val fields: Array<Field> = entity::class.java.declaredFields

            query.append("INSERT INTO $tableName (")

            fields.forEach { field ->
                field.getAnnotation(Id::class.java)?.let {
                    if (it.type == GeneratedBy.AUT0_INCREMENT) {
                        return@forEach
                    }
                }

                val entityColumnName = field.getAnnotation(Column::class.java)?.name

                if (entityColumnName != null) {
                    query.append(entityColumnName)
                    if (field != fields.last()) {
                        query.append(", ")
                    }
                    return@forEach
                }

                val entityJoinColumnName = field.getAnnotation(JoinColumn::class.java)?.name
                if (entityJoinColumnName != null) {
                    query.append(entityJoinColumnName)
                    if (field != fields.last()) {
                        query.append(", ")
                    }
                    return@forEach
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
                val value = field.get(entity)

                when (field.type) {
                    Int::class.java -> query.append(value)
                    String::class.java -> query.append("'$value'")
                    Boolean::class.java -> query.append(if (value as Boolean) 1 else 0)
                    Enum::class.java -> query.append("'${(value as Enum<*>).name}'")
                    Date::class.java -> query.append("'${(value as Date).time}'")
                    else -> if (field.getAnnotation(JoinColumn::class.java) != null) {
                        val joinEntity = field.type

                        joinEntity.declaredFields.firstOrNull { it.getAnnotation(Id::class.java) != null }
                            ?.let { joinEntityIdField ->
                                joinEntityIdField.isAccessible = true
                                when (joinEntityIdField.type) {
                                    Int::class.java -> query.append(joinEntityIdField.get(value))
                                    String::class.java -> query.append("'${joinEntityIdField.get(value)}'")
                                    Boolean::class.java -> query.append(if (joinEntityIdField.get(value) as Boolean) 1 else 0)
                                    Enum::class.java -> query.append("'${(joinEntityIdField.get(value) as Enum<*>).name}'")
                                    Date::class.java -> query.append("'${(joinEntityIdField.get(value) as Date).time}'")
                                    else -> throw IllegalArgumentException("Unsupported data type for join column: ${joinEntityIdField.type}")
                                }
                            }
                    } else {
                        throw IllegalArgumentException("Unsupported data type for insert: ${field.type}")
                    }
                }

                if (field != fields.last()) {
                    query.append(", ")
                }
            }
            query.append(");")

            BasicLog.getLogWithColorFor<BasicOrm<T>>(
                LogColors.GREEN,
                StringBuilder().append(query).toString()
            )

            val statement = connection.createStatement()

            statement.execute(query.toString())

            statement.close()
        } catch (e: Exception) {
            val msg = StringBuilder()
                .append("Error inserting data: ${e.message}\n")
                .append("Query: $query")
                .toString()
            BasicLog.getLogWithColorFor<BasicOrm<T>>(
                LogColors.RED,
                msg
            )
            throw BasicOrmError(msg)
        }
    }

    /**
     * Finds all records in the database for the given class.
     * @return A list of records.
     * @throws IllegalArgumentException if the class is not annotated with @Table.
     * @throws BasicOrmError if there is an error executing the query.
     */
    @Throws(IllegalArgumentException::class, BasicOrmError::class)
    fun findAll(): MutableList<T> {
        val query: StringBuilder = StringBuilder()

        try {
            val tableName: String? = entityClass.findAnnotation<Table>()?.name

            if (tableName == null) {
                throw IllegalArgumentException("Class ${entityClass.simpleName} is not annotated with @Table")
            }

            query.append("SELECT * FROM $tableName;")

            BasicLog.getLogWithColorFor<BasicOrm<T>>(
                LogColors.GREEN,
                StringBuilder().append(query).toString()
            )

            val statement = connection.createStatement()

            statement.executeQuery(query.toString())

            val resultSet = statement.resultSet

            val list: MutableList<T> = mutableListOf()

            while (resultSet.next()) {
                val entityObj = entityClass.java.getDeclaredConstructor().newInstance()
                val entityFields: Array<Field> = entityClass.java.declaredFields

                // Iterate over the fields and set their values from the result set
                entityFields.forEach { entityField ->
                    entityField.isAccessible = true
                    if (entityField.getAnnotation(Column::class.java) != null) {
                        val entityFieldName = entityField.getAnnotation(Column::class.java)?.name
                        if (entityFieldName != null) {
                            val value = resultSet.getObject(entityFieldName)
                            entityField.set(entityObj, value)
                        }
                    } else if (entityField.getAnnotation(JoinColumn::class.java) != null) {
                        val joinFieldName = entityField.getAnnotation(JoinColumn::class.java)?.name
                        val joinEntity = entityField.type

                        joinEntity.declaredFields.firstOrNull { it.getAnnotation(Id::class.java) != null }
                            ?.let { joinEntityIdField ->
                                joinEntity.getAnnotation(Table::class.java)?.let { joinEntityTable ->
                                    val joinEntityTableName = joinEntityTable.name
                                    val joinEntityIdFieldName =
                                        joinEntityIdField.getAnnotation(Column::class.java)?.name
                                    if (joinEntityIdFieldName != null) {
                                        query.clear()
                                        query.append(
                                            "SELECT * FROM $joinEntityTableName WHERE $joinEntityIdFieldName = ${
                                                resultSet.getObject(
                                                    joinFieldName
                                                )
                                            };"
                                        )
                                        BasicLog.getLogWithColorFor<BasicOrm<T>>(
                                            LogColors.GREEN,
                                            StringBuilder().append(query).toString()
                                        )

                                        val statement = connection.createStatement()

                                        statement.executeQuery(query.toString())

                                        val resultSet = statement.resultSet

                                        if (resultSet.next()) {
                                            val joinEntityObj = joinEntity.getDeclaredConstructor().newInstance()
                                            val joinEntityFields: Array<Field> = joinEntity.declaredFields

                                            joinEntityFields.forEach { joinColumnField ->
                                                joinColumnField.isAccessible = true
                                                if (joinColumnField.getAnnotation(Column::class.java) != null) {
                                                    val joinColumnFieldName =
                                                        joinColumnField.getAnnotation(Column::class.java)?.name
                                                    if (joinColumnFieldName != null) {
                                                        val value = resultSet.getObject(joinColumnFieldName)
                                                        joinColumnField.set(joinEntityObj, value)
                                                    }
                                                }
                                            }
                                            entityField.set(entityObj, joinEntityObj)
                                        } else {
                                            entityField.set(entityObj, null)
                                        }
                                    }
                                }
                            }
                    }
                }
                list.add(entityObj)
            }
            resultSet.close()
            statement.close()

            return list
        } catch (e: Exception) {
            val msg = StringBuilder()
                .append("Error finding all records: ${e.message}\n")
                .append("Query: $query")
                .toString()
            BasicLog.getLogWithColorFor<BasicOrm<T>>(
                LogColors.RED,
                msg
            )
            throw BasicOrmError(msg)
        }
    }

    /**
     * Delete a record from the database.
     * @param entity The object to be deleted.
     * @throws IllegalArgumentException if the class is not annotated with @Table.
     * @throws BasicOrmError if there is an error executing the query.
     */
    @Throws(IllegalArgumentException::class, BasicOrmError::class)
    fun deleteOne(entity: T) {
        val query: StringBuilder = StringBuilder()

        try {
            val tableName: String? = entity::class.findAnnotation<Table>()?.name

            if (tableName == null) {
                throw IllegalArgumentException("Class ${entity::class.simpleName} is not annotated with @Table")
            }

            val fields: Array<Field> = entity::class.java.declaredFields

            query.append("DELETE FROM $tableName WHERE ")

            val idColumn = fields.firstOrNull { it.getAnnotation(Id::class.java) != null }
                ?: throw IllegalArgumentException("Class ${entity::class.simpleName} does not have an identifier field")

            val fieldName = idColumn.getAnnotation(Column::class.java)?.name
                ?: throw IllegalArgumentException("Identifier field ${idColumn.name} does not have a @Column annotation or not has name attribute")

            idColumn.isAccessible = true

            val id = when (idColumn.get(entity)) {
                is Int -> idColumn.get(entity) as Int
                is String -> "'${idColumn.get(entity)}'"
                else -> throw IllegalArgumentException("Unsupported data type for identifier column: ${idColumn.type}")
            }

            query.append("$fieldName = $id LIMIT 1;")

            BasicLog.getLogWithColorFor<BasicOrm<T>>(
                LogColors.GREEN,
                StringBuilder().append(query).toString()
            )

            val statement = connection.createStatement()

            statement.execute(query.toString())

            statement.close()
        } catch (e: Exception) {
            val msg = StringBuilder()
                .append("Error deleting data: ${e.message}\n")
                .append("Query: $query")
                .toString()
            BasicLog.getLogWithColorFor<BasicOrm<T>>(
                LogColors.RED,
                msg
            )
            throw BasicOrmError(msg)
        }
    }

    /**
     * Updates a record in the database.
     * @param entity The object to be updated.
     * @throws IllegalArgumentException if the class is not annotated with @Table.
     * @throws BasicOrmError if there is an error executing the query.
     */
    fun updateOne(entity: T) {
        val query: StringBuilder = StringBuilder()

        try {
            val tableName: String? = entity::class.findAnnotation<Table>()?.name

            if (tableName == null) {
                throw IllegalArgumentException("Class ${entity::class.simpleName} is not annotated with @Table")
            }

            val fields: Array<Field> = entity::class.java.declaredFields

            query.append("UPDATE $tableName SET ")

            fields.forEach { field ->
                field.getAnnotation(Id::class.java)?.let {
                    return@forEach
                }

                field.isAccessible = true

                val name = field.getAnnotation(Column::class.java)?.name

                if (name != null) {
                    val value = field.get(entity)

                    when (field.type) {
                        Int::class.java -> query.append("$name = $value")
                        String::class.java -> query.append("$name = '$value'")
                        Boolean::class.java -> query.append("$name = ${if (value as Boolean) 1 else 0}")
                        Enum::class.java -> query.append("'${(value as Enum<*>).name}'")
                        Date::class.java -> query.append("'${(value as Date).time}'")
                        else -> throw IllegalArgumentException("Unsupported data type for field column: ${field.type}")
                    }
                    if (field != fields.last()) {
                        query.append(", ")
                    }
                    return@forEach
                }

                val joinColumnName = field.getAnnotation(JoinColumn::class.java)?.name
                field.isAccessible = true

                if (joinColumnName != null) {
                    val value = field.get(entity)

                    when (field.type) {
                        Int::class.java -> query.append("$joinColumnName = $value")
                        String::class.java -> query.append("$joinColumnName = '$value'")
                        Boolean::class.java -> query.append("$joinColumnName = ${if (value as Boolean) 1 else 0}")
                        Enum::class.java -> query.append("'${(value as Enum<*>).name}'")
                        Date::class.java -> query.append("'${(value as Date).time}'")
                        else -> if (field.getAnnotation(JoinColumn::class.java) != null) {
                            val joinEntity = field.type

                            joinEntity.declaredFields.firstOrNull { it.getAnnotation(Id::class.java) != null }
                                ?.let { joinEntityIdField ->
                                    joinEntityIdField.isAccessible = true
                                    when (joinEntityIdField.type) {
                                        Int::class.java -> query.append("$joinColumnName = ${joinEntityIdField.get(value)}")
                                        String::class.java -> query.append(
                                            "$joinColumnName = '${
                                                joinEntityIdField.get(
                                                    value
                                                )
                                            }'"
                                        )

                                        Boolean::class.java -> query.append(
                                            "$joinColumnName = ${
                                                if (joinEntityIdField.get(
                                                        value
                                                    ) as Boolean
                                                ) 1 else 0
                                            }"
                                        )
                                        else -> throw IllegalArgumentException("Unsupported data type for join column: ${joinEntityIdField.type}")
                                    }
                                }
                        } else {
                            throw IllegalArgumentException("Unsupported data type for insert: ${field.type}")
                        }
                    }
                    if (field != fields.last()) {
                        query.append(", ")
                    }
                    return@forEach
                }
            }

            val idColumn = fields.firstOrNull { it.getAnnotation(Id::class.java) != null }
                ?: throw IllegalArgumentException("Class ${entity::class.simpleName} does not have an identifier field")

            val fieldName = idColumn.getAnnotation(Column::class.java)?.name
                ?: throw IllegalArgumentException("Identifier field ${idColumn.name} does not have a @Column annotation or not has name attribute")

            idColumn.isAccessible = true

            val id = when (idColumn.get(entity)) {
                is Int -> idColumn.get(entity) as Int
                is String -> "'${idColumn.get(entity)}'"
                else -> throw IllegalArgumentException("Unsupported data type for identifier column: ${idColumn.type}")
            }

            query.append(" WHERE $fieldName = $id LIMIT 1;")

            BasicLog.getLogWithColorFor<BasicOrm<T>>(
                LogColors.GREEN,
                StringBuilder().append(query).toString()
            )

            val statement = connection.createStatement()

            statement.execute(query.toString())

            statement.close()
        } catch (e: Exception) {
            val msg = StringBuilder()
                .append("Error updating data: ${e.message}\n")
                .append("Query: $query")
                .toString()
            BasicLog.getLogWithColorFor<BasicOrm<T>>(
                LogColors.RED,
                msg
            )
            throw BasicOrmError(msg)
        }
    }

    /**
     * Closes the database connection.
     */
    fun closeConnection() {
        try {
            connection.close()
            BasicLog.getLogWithColorFor<BasicOrm<T>>(
                LogColors.GREEN,
                StringBuilder().append("Connection closed successfully.").toString()
            )
        } catch (e: Exception) {
            BasicLog.getLogWithColorFor<BasicOrm<T>>(
                LogColors.RED, StringBuilder().append("Error closing connection: ${e.message}").toString()
            )
        }
    }
}