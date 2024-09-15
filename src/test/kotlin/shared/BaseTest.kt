package shared

import org.junit.jupiter.api.Assertions.assertEquals

abstract class BaseTest {
    protected infix fun Any.eq(expected: Any?) = assertEquals(expected, this)
}