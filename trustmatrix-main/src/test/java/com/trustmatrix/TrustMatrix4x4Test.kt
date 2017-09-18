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

import org.hamcrest.collection.IsCollectionWithSize
import org.hamcrest.core.IsCollectionContaining
import org.junit.Assert
import org.junit.Test


internal class TrustMatrix4x4Test {
    val matrix = TrustMatrix(4, 4, TrustMatrix.ALL_ALWAYS_COOPERATE_DISTR, 10, TrustMatrix.DEFAULT_DILEMMA_GAME,
            listOf(SimpleStrongestNeighbourMutation()))

    @Test
    fun testMutation() {
        //we place cheat spawn
        matrix.positionMatrix[1][1].placePlayer(Player(Strategy.alwaysCheat))
        //it growth
        matrix.generate()
        Assert.assertEquals(null, matrix.positionMatrix[1][1].nextPlayer)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][0].nextPlayer?.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][1].nextPlayer?.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[1][0].nextPlayer?.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][2].nextPlayer?.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[1][2].nextPlayer?.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[2][2].nextPlayer?.strategy)
        Assert.assertEquals(null, matrix.positionMatrix[0][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[2][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][1].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][0].nextPlayer)
        //and overtake the world
        matrix.generate()
        Assert.assertEquals(null, matrix.positionMatrix[1][1].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][0].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][1].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][0].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[2][2].nextPlayer)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][3].nextPlayer?.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[1][3].nextPlayer?.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[2][3].nextPlayer?.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[3][3].nextPlayer?.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[3][2].nextPlayer?.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[3][1].nextPlayer?.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[3][0].nextPlayer?.strategy)
        //now they all the same
        matrix.generate()
        //and therefore...
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][1].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[1][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][2].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[1][2].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[2][2].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][3].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[1][3].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[2][3].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[3][3].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[3][2].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[3][1].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[3][0].player.strategy)
        //they are not going to change anymore
        Assert.assertEquals(null, matrix.positionMatrix[1][1].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][0].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][1].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][0].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[2][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[2][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][1].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][0].nextPlayer)
        matrix.generate()
        //and over generations we would see only cheaters at all world
        matrix.generate()
        matrix.positionMatrix.forEach { it.forEach { Assert.assertEquals(Strategy.alwaysCheat, it.player.strategy) } }
        matrix.generate()
        matrix.positionMatrix.forEach { it.forEach { Assert.assertEquals(Strategy.alwaysCheat, it.player.strategy) } }
        matrix.generate()
        matrix.positionMatrix.forEach { it.forEach { Assert.assertEquals(Strategy.alwaysCheat, it.player.strategy) } }
        matrix.generate()
        matrix.positionMatrix.forEach { it.forEach { Assert.assertEquals(Strategy.alwaysCheat, it.player.strategy) } }

        //and even spawned some good
        matrix.positionMatrix[1][1].placePlayer(Player(Strategy.alwaysCooperate))
        matrix.generate()
        //would be long gone next time
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[1][1].nextPlayer?.strategy)
        Assert.assertEquals(null, matrix.positionMatrix[0][0].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][1].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][0].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[2][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[2][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][1].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][0].nextPlayer)

        matrix.generate()
        //and all would return to the same stagnation
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][1].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[1][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][2].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[1][2].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[2][2].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][3].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[1][3].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[2][3].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[3][3].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[3][2].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[3][1].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[3][0].player.strategy)
        Assert.assertEquals(null, matrix.positionMatrix[1][1].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][0].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][1].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][0].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[2][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[2][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][1].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][0].nextPlayer)

        //even eye for an eye
        matrix.positionMatrix[1][1].placePlayer(Player(Strategy.anEyeForAnEye))
        matrix.generate()
        //cannot handle this alone
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[1][1].nextPlayer?.strategy)
        Assert.assertEquals(null, matrix.positionMatrix[0][0].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][1].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][0].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[2][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[2][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][1].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][0].nextPlayer)
        matrix.generate()
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][1].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[1][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][2].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[1][2].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[2][2].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][3].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[1][3].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[2][3].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[3][3].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[3][2].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[3][1].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[3][0].player.strategy)
        Assert.assertEquals(null, matrix.positionMatrix[1][1].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][0].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][1].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][0].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[2][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[2][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][1].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][0].nextPlayer)

        //two of good guys
        matrix.positionMatrix[1][1].placePlayer(Player(Strategy.alwaysCooperate))
        matrix.positionMatrix[2][1].placePlayer(Player(Strategy.alwaysCooperate))
        matrix.generate()
        matrix.generate()
        //still would face inevitable
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][0].player.strategy)
        Assert.assertEquals(Strategy.alwaysCheat, matrix.positionMatrix[0][0].player.strategy)
        Assert.assertEquals(null, matrix.positionMatrix[1][1].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][0].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][1].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][0].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[2][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[0][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[1][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[2][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][3].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][2].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][1].nextPlayer)
        Assert.assertEquals(null, matrix.positionMatrix[3][0].nextPlayer)

        //but given right support
        matrix.positionMatrix[1][1].placePlayer(Player(Strategy.anEyeForAnEye))
        matrix.positionMatrix[2][1].placePlayer(Player(Strategy.anEyeForAnEye))
        matrix.generate()
        //they would grow themselves
        Assert.assertEquals(Strategy.anEyeForAnEye, matrix.positionMatrix[0][0].nextPlayer?.strategy)
        Assert.assertEquals(Strategy.anEyeForAnEye, matrix.positionMatrix[0][1].nextPlayer?.strategy)
        Assert.assertEquals(Strategy.anEyeForAnEye, matrix.positionMatrix[0][2].nextPlayer?.strategy)
        Assert.assertEquals(Strategy.anEyeForAnEye, matrix.positionMatrix[1][0].nextPlayer?.strategy)
        Assert.assertEquals(Strategy.anEyeForAnEye, matrix.positionMatrix[1][2].nextPlayer?.strategy)
        Assert.assertEquals(Strategy.anEyeForAnEye, matrix.positionMatrix[2][0].nextPlayer?.strategy)
        Assert.assertEquals(Strategy.anEyeForAnEye, matrix.positionMatrix[2][2].nextPlayer?.strategy)
        Assert.assertEquals(Strategy.anEyeForAnEye, matrix.positionMatrix[3][0].nextPlayer?.strategy)
        Assert.assertEquals(Strategy.anEyeForAnEye, matrix.positionMatrix[3][1].nextPlayer?.strategy)
        Assert.assertEquals(Strategy.anEyeForAnEye, matrix.positionMatrix[3][2].nextPlayer?.strategy)
        matrix.generate()
        matrix.generate()
        matrix.generate()
        //and overtake the world just the same way as cheaters once did
        matrix.positionMatrix.forEach { it.forEach { Assert.assertEquals(Strategy.anEyeForAnEye, it.player.strategy) } }
    }


    @Test
    fun testNeighbourhoodSize() {
        Assert.assertThat(matrix.neighbourhood, IsCollectionWithSize.hasSize(4 * matrix.xDimension * matrix.yDimension))
    }

    @Test
    fun testNeighbourhoodConnectionsSize() {
        matrix.positionMatrix.forEach { it.forEach { Assert.assertThat(it.neighborPositions, IsCollectionWithSize.hasSize(8)) } }
    }

    @Test
    fun testNeighbourhoodTorCorners() {
        Assert.assertThat(matrix.positionMatrix[0][0].neighborPositions,
                IsCollectionContaining.hasItems(
                        matrix.positionMatrix[0][1],
                        matrix.positionMatrix[1][0],
                        matrix.positionMatrix[1][1],
                        matrix.positionMatrix[1][1],
                        matrix.positionMatrix[3][0],
                        matrix.positionMatrix[3][1],
                        matrix.positionMatrix[0][3],
                        matrix.positionMatrix[3][3]
                ))
        Assert.assertThat(matrix.positionMatrix[0][3].neighborPositions,
                IsCollectionContaining.hasItems(
                        matrix.positionMatrix[0][0],
                        matrix.positionMatrix[0][2],
                        matrix.positionMatrix[1][0],
                        matrix.positionMatrix[1][2],
                        matrix.positionMatrix[1][3],
                        matrix.positionMatrix[3][0],
                        matrix.positionMatrix[3][2],
                        matrix.positionMatrix[3][3]
                ))
        Assert.assertThat(matrix.positionMatrix[3][0].neighborPositions,
                IsCollectionContaining.hasItems(
                        matrix.positionMatrix[2][0],
                        matrix.positionMatrix[2][1],
                        matrix.positionMatrix[3][1],
                        matrix.positionMatrix[0][0],
                        matrix.positionMatrix[0][1],
                        matrix.positionMatrix[2][3],
                        matrix.positionMatrix[3][3],
                        matrix.positionMatrix[0][3]
                ))
        Assert.assertThat(matrix.positionMatrix[3][3].neighborPositions,
                IsCollectionContaining.hasItems(
                        matrix.positionMatrix[2][2],
                        matrix.positionMatrix[2][3],
                        matrix.positionMatrix[3][2],
                        matrix.positionMatrix[0][0],
                        matrix.positionMatrix[0][3],
                        matrix.positionMatrix[0][2],
                        matrix.positionMatrix[2][0],
                        matrix.positionMatrix[0][0]
                ))
    }

}
