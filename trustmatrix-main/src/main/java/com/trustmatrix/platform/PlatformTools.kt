/*-
 * #%L
 * trustmatrix-main
 * %%
 * Copyright (C) 2017 Timur Sultanaev
 * %%
 * Licensed under the MIT license.
 * See LICENSE file in the root of project for details.
 * #L%
 */
package com.trustmatrix.platform

import kotlin.reflect.KClass

interface PlatformTools {
    fun random(): Random
}

interface Random {
    companion object {
        lateinit var DEFAULT: Random
    }

    fun nextDouble(): Double
    fun nextInt(): Int
    fun nextInt(max: Int): Int
}

interface Logger {
    fun error(message: String)
    fun info(message: String)
    fun debug(message: String)
    fun trace(message: String)
}

interface LoggerFactory {
    fun getLogger(): Logger
}

class LoggerBuilder {
    companion object {
        lateinit var factory: (KClass<out Any>) -> LoggerFactory
    }
}

interface Color {
    fun toPlatform(): Any

    companion object {
        lateinit var BLACK: Color
        lateinit var WHITE: Color
        lateinit var YELLOW: Color
        lateinit var GREEN: Color
        lateinit var BLUE: Color
    }
}

