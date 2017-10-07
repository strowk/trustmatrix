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
            Random.DEFAULT = JsRandom()
        }

    }

    override fun random(): Random = JsRandom()
}

class JsRandom() : Random {
    override fun nextDouble(): Double = Math.random()
    override fun nextInt(max: Int): Int = Math.round(Math.random() * max)
    override fun nextInt(): Int = nextInt(Int.MAX_VALUE - 1)
}

enum class LogLevels {
    TRACE, DEBUG, INFO, ERROR
}

class JsConsoleLogger(val clazz: KClass<out Any>) : Logger {
    val level = LogLevels.DEBUG
    val leveledLog: (message: String, logWith: LogLevels, logFun: (msg: String) -> Unit) -> Unit = { message, logWith, logFun ->
        if (level.ordinal <= logWith.ordinal) logFun(message) else Unit
    }

    override fun info(message: String) = leveledLog(message, LogLevels.INFO, { console.log(it) })
    override fun debug(message: String) = leveledLog(message, LogLevels.DEBUG, { console.log("DEBUG:" + it) })
    override fun trace(message: String) = leveledLog(message, LogLevels.TRACE, { console.log("TRACE:" + it) })
    override fun error(message: String) = leveledLog(message, LogLevels.ERROR, { console.error(it) })
}

class JsLoggerFactory(val clazz: KClass<out Any>) : LoggerFactory {
    override fun getLogger(): Logger = JsConsoleLogger(clazz)
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