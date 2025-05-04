package database.annotations

/**
 * Annotation to specify the name of a database column.
 *
 * @property name The name of the column in the database.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Column(
    val name: String = ""
)