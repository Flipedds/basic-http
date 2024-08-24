package core.di.injectables

import core.annotations.Injectable

@Injectable
class TestClass {
    fun testMethod(): String {
        return "testMethod"
    }
}