package database.enums

/**
 * Enum class representing the type of relation between two entities.
 *
 * @property ManyToOne Represents a many-to-one relationship.
 * @property OneToMany Represents a one-to-many relationship.
 */
enum class Relation {
    ManyToOne,
    OneToMany
}
