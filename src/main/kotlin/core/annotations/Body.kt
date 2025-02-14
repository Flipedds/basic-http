package core.annotations

/**
 * Annotation to indicate that the parameter is a body of the request.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Body
