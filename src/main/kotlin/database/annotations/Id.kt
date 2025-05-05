package database.annotations

import database.enums.GeneratedBy

/**
 * Annotation to mark a column as an identifier in a database table.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Id(
    val type: GeneratedBy
)
