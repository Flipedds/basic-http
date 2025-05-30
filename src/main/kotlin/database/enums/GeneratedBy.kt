package database.enums

/**
 * Enum class to represent the source of ID generation.
 * @property AUT0_INCREMENT Indicates that the ID is auto-incremented by the database.
 * @property APPLICATION Indicates that the ID is generated by the application.
 */
enum class GeneratedBy {
    AUT0_INCREMENT,
    APPLICATION
}