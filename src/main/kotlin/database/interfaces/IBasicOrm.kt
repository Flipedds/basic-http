package database.interfaces

/**
 * Interface for basic ORM operations.
 * @param T The type of the entity.
 */
interface IBasicOrm<T : Any> {
    fun insert(entity: T)
    fun findAll(): MutableList<T>
    fun updateOne(entity: T)
    fun deleteOne(entity: T)
    fun closeConnection()
}