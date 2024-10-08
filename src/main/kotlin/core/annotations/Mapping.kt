package core.annotations

import core.enums.RequestMethod

@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Mapping(
    val path: String,
    val method: RequestMethod,
)