package database.interfaces

/**
 * Interface for basic ORM operations.
 * @param T The type of the entity.
 */
interface IBasicOrm<T : Any> {
    fun insert(data: T)
    fun findAll(): MutableList<T>
}