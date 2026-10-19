package logger

object Logger {

    fun info(text: String) {
        println("${ConsoleColor.CYAN}Game Engine Debugger ${ConsoleColor.RESET}(Info): $text")
    }

    fun warning(warn: String) {
        println("${ConsoleColor.CYAN}Game Engine Debugger ${ConsoleColor.YELLOW}(Warning)${ConsoleColor.RESET}: $warn")
    }

    fun error(e: String) {
        println("${ConsoleColor.CYAN}Game Engine Debugger ${ConsoleColor.RED}(Error)${ConsoleColor.RESET}: $e")
    }

    fun debug(s: String) {
        println("${ConsoleColor.CYAN}Game Engine Debugger ${ConsoleColor.GREEN}(Debug)${ConsoleColor.RESET}: $s")
    }
}