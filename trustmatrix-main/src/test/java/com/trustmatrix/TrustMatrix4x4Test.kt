package com.trustmatrix

import org.hamcrest.collection.IsCollectionWithSize
import org.hamcrest.core.IsCollectionContaining
import org.junit.Assert
import org.junit.Test


internal class TrustMatrix4x4Test {
    val matrix = TrustMatrix(4, 4, TrustMatrix.ALL_ALWAYS_COOPERATE_DISTR, 10, TrustMatrix.DEFAULT_DILEMMA_GAME)
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