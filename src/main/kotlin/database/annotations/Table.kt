package database.annotations

/**
 * Annotation to specify the name of a database table.
 *
 * @property name The name of the table in the database.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Table(
    val name: String = ""
)