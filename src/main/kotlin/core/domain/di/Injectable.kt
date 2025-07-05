package core.domain.di

/**
 * This annotation is used to mark a class as injectable.
 * This means that the class can be injected into other classes.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Injectable