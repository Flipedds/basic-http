package core.domain.request

/**
 * Annotation to map a function to a route
 * @param path the path of the route
 * @param method the method of the route
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Mapping(
    val path: String,
    val method: RequestMethod,
)