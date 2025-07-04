package core.domain.authentication

/**
 * Annotation to indicate that the endpoint requires authentication.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class UseAuthentication
