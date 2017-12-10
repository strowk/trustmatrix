package com.trustmatrix.platform

import kotlin.reflect.KClass

class JavaPlatformTools : PlatformTools {
    companion object {
        init {
            LoggerBuilder.factory = { Slf4jLoggerFactory(it) }
            val log = LoggerBuilder.factory(JavaPlatformTools::class).getLogger()
            log.info("Tools initialization")
            log.info("Initialize Logger: ${Slf4jLogger::class}")
            log.info("Initialize Color: ${JavaFxColor::class}")
            JavaFxColor(javafx.scene.paint.Color.WHITE).toPlatform() //would cause JavaFxColor::init to call
            log.info("Initialize Random: ${JavaRandom::class}")
            Random.RANDOM_SOURCE = JavaRandom(java.util.Random())
        }

    }

    override fun random(): Random = Random.RANDOM_SOURCE
}

class JavaRandom(val random: java.util.Random) : Random {

    override fun nextDouble(): Double = random.nextDouble()
    override fun nextInt(): Int = random.nextInt()
    override fun nextInt(max: Int): Int = random.nextInt(max)
}

class Slf4jLogger(val logger: org.slf4j.Logger) : Logger {
    override fun info(message: String) = logger.info(message)
    override fun debug(message: String) = logger.debug(message)
    override fun trace(message: String) = logger.trace(message)
    override fun error(message: String) = logger.error(message)
}

class Slf4jLoggerFactory(val clazz: KClass<out Any>) : LoggerFactory {
    override fun getLogger(): Logger = Slf4jLogger(org.slf4j.LoggerFactory.getLogger(clazz.java))
}

class JavaFxColor(val color: javafx.scene.paint.Color) : Color {
    override fun toPlatform(): Any = color

    companion object {
        init {
            Color.BLACK = JavaFxColor(javafx.scene.paint.Color.BLACK)
            Color.WHITE = JavaFxColor(javafx.scene.paint.Color.WHITE)
            Color.GREEN = JavaFxColor(javafx.scene.paint.Color.GREEN)
            Color.YELLOW = JavaFxColor(javafx.scene.paint.Color.YELLOW)
            Color.BLUE = JavaFxColor(javafx.scene.paint.Color.BLUE)
        }
    }
}