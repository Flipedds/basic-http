package core.enums

enum class LogColors(val ansiCode: String) {
    RED("\u001B[31m"),
     GREEN("\u001B[32m"),
     YELLOW("\u001B[33m"),
     BLUE("\u001B[34m"),
     RESET("\u001B[0m")
}