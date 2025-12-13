package core.domain.request

import kotlin.reflect.KParameter

/**
 * This class is responsible for containing the data of a parameter of a method.
 *
 * @param name: The name of the parameter.
 * @param typeName: The type name of the parameter.
 * @param parameter: The parameter.
 * @param nameIsNull: Whether the parameter is null.
 * @param hasQuery: Whether the parameter is a query parameter.
 * @param hasBody: Whether the parameter is a body parameter.
 * @param hasPathParam: Whether the parameter is a path parameter.
 */
data class ParameterData(
        val name: String?,
        val typeName: String,
        val parameter: KParameter,
        val nameIsNull: Boolean,
        val hasQuery: Boolean,
        val hasBody: Boolean,
        val hasPathParam: Boolean
)
