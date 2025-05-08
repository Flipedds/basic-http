package database.annotations

import database.enums.Relation

/**
 * Annotation to specify the join column in a database entity.
 * @property name The name of the join column.
 * @property type The type of relation (e.g., OneToMany, ManyToOne).
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class JoinColumn(
    val name: String = "",
    val type: Relation
)
