package com.trustmatrix.platform

import kotlin.js.Math
import kotlin.reflect.KClass

class JsPlatformTools : PlatformTools {
    companion object {
        init {
            LoggerBuilder.factory = { JsLoggerFactory(it) }
            val log = LoggerBuilder.factory(JsPlatformTools::class).getLogger()
            log.info("Tools initialization")
            log.info("Initialize Logger: ${JsConsoleLogger::class}")
            log.info("Initialize Color: ${JsColor::class}")
            JsColor(JsColors.WHITE).toPlatform() //would cause JavaFxColor::init to call
            log.info("Initialize Random: ${JsRandom::class}")
            Random.RANDOM_SOURCE = JsRandom()
        }

    }

    override fun random(): Random = Random.RANDOM_SOURCE
}

class JsRandom() : Random {
    override fun nextDouble(): Double = Math.random()
    override fun nextInt(max: Int): Int = (Math.random() * (max - 0) + 0).toInt()
    override fun nextInt(): Int = nextInt(Int.MAX_VALUE - 1)
}

enum class LogLevels {
    TRACE, DEBUG, INFO, ERROR
}

class ConsoleAppender {
    var level = LogLevels.DEBUG
    val leveledLog: (message: String, logWith: LogLevels, logFun: (msg: String) -> Unit) -> Unit = { message, logWith, logFun ->
        if (level.ordinal <= logWith.ordinal) logFun(message) else Unit
    }
}

class JsConsoleLogger(val clazz: KClass<out Any>, val appender: ConsoleAppender) : Logger {
    override fun info(message: String) = appender.leveledLog(message, LogLevels.INFO, { console.log(it) })
    override fun debug(message: String) = appender.leveledLog(message, LogLevels.DEBUG, { console.log("DEBUG:" + it) })
    override fun trace(message: String) = appender.leveledLog(message, LogLevels.TRACE, { console.log("TRACE:" + it) })
    override fun error(message: String) = appender.leveledLog(message, LogLevels.ERROR, { console.error(it) })
}

class JsLoggerFactory(val clazz: KClass<out Any>) : LoggerFactory {
    companion object {
        val appender = ConsoleAppender()
    }

    override fun getLogger(): Logger = JsConsoleLogger(clazz, appender)
}

enum class JsColors {
    BLACK, WHITE, GREEN, YELLOW, BLUE
}

class JsColor(val color: JsColors) : Color {
    override fun toPlatform(): Any = color

    companion object {
        init {
            Color.BLACK = JsColor(JsColors.BLACK)
            Color.WHITE = JsColor(JsColors.WHITE)
            Color.GREEN = JsColor(JsColors.GREEN)
            Color.YELLOW = JsColor(JsColors.YELLOW)
            Color.BLUE = JsColor(JsColors.BLUE)
        }
    }
}