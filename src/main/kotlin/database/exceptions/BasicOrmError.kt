package database.exceptions

/**
 * Custom exception class for Basic ORM errors.
 * @param msg The error message.
 */
class BasicOrmError(msg: String) : RuntimeException(msg) {
}
