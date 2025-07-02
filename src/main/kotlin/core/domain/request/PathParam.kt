package core.domain.request

/**
 * Annotation to indicate that a parameter is a path {} parameter.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class PathParam()
