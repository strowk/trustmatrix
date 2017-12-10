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
package com.trustmatrix

import com.trustmatrix.platform.JavaPlatformTools
import org.junit.Assert
import org.junit.Test


internal class StrategiesTest {
    companion object {
        //have to initialize all statics first
        val platform = JavaPlatformTools()
    }

    private val gen = Generation(listOf(SimpleStrongestNeighbourMutation(TrustMatrix4x4Test.platform.random(), distortion = 0.0)))

    val alwaysCheat1_1: GamePosition = GamePosition(0, 1, Player(Strategy.defaults.alwaysCheat, gen), arrayListOf({ alwaysCheat1_2 }))
    val alwaysCheat1_2: GamePosition = GamePosition(1, 1, Player(Strategy.defaults.alwaysCheat, gen), arrayListOf({ alwaysCheat1_1 }))

    @Test
    fun testTwoAlwaysCheat() {
        val neighbourhood = Neighbourhood(alwaysCheat1_1, alwaysCheat1_2)
        neighbourhood.playGame(10, TrustMatrix.DEFAULT_DILEMMA_GAME)
        Assert.assertEquals(0, alwaysCheat1_1.player.generationIncome)
        Assert.assertEquals(0, alwaysCheat1_2.player.generationIncome)
    }

    val alwaysCheat2_1: GamePosition = GamePosition(0, 1, Player(Strategy.defaults.alwaysCheat, gen), arrayListOf({ alwaysCooperate2_2 }))
    val alwaysCooperate2_2: GamePosition = GamePosition(1, 1, Player(Strategy.defaults.alwaysCooperate, gen), arrayListOf({ alwaysCheat2_1 }))

    @Test
    fun testAlwaysCooperateAndAlwaysCheat() {
        val neighbourhood = Neighbourhood(alwaysCheat2_1, alwaysCooperate2_2)
        neighbourhood.playGame(10, TrustMatrix.DEFAULT_DILEMMA_GAME)
        Assert.assertEquals(30, alwaysCheat2_1.player.generationIncome)
        Assert.assertEquals(-10, alwaysCooperate2_2.player.generationIncome)
    }

    val alwaysCheat3_1: GamePosition = GamePosition(0, 1, Player(Strategy.defaults.alwaysCheat, gen), arrayListOf({ eyeForAnEye3_2 }))
    val eyeForAnEye3_2: GamePosition = GamePosition(1, 1, Player(Strategy.defaults.anEyeForAnEye, gen), arrayListOf({ alwaysCheat3_1 }))

    @Test
    fun testEyeForAnEyeAndAlwaysCheat() {
        val neighbourhood = Neighbourhood(alwaysCheat3_1, eyeForAnEye3_2)
        neighbourhood.playGame(10, TrustMatrix.DEFAULT_DILEMMA_GAME)
        Assert.assertEquals(3, alwaysCheat3_1.player.generationIncome)
        Assert.assertEquals(-1, eyeForAnEye3_2.player.generationIncome)
    }
}
