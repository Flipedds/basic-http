package core.annotations

/**
 * Annotation to be used in the parameters of the methods that are going to be used as query parameters.
 * @param key The key of the query parameter.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class QueryParam(
    val key: String
)
