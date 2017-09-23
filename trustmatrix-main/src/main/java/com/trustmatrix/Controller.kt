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

import javafx.animation.AnimationTimer
import javafx.collections.FXCollections
import javafx.fxml.Initializable
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*


class Controller : Initializable {
    val log: Logger = LoggerFactory.getLogger(Controller::class.java)
    lateinit var img: Canvas
    lateinit var gc: GraphicsContext
    lateinit var grid: BorderPane
    lateinit var resetButton: Button
    lateinit var distortion: TextField
    lateinit var speed: TextField
    var trustMatrix = TrustMatrix(100, 100)
    val initialDistributionItems = FXCollections.observableArrayList(InitialDistribution.values().map { it.name })
    val defaultItems = FXCollections.observableArrayList(Strategy.defaults::class.members.filter { it.returnType == Strategy::class })
    lateinit var listBoxForInitialDistribution: ListView<String>


    private fun speed() = speed.text.toDoubleOrNull() ?: 1.0
    private fun drawTrustMatrix(matrix: TrustMatrix) {
        val positionRectXSize = img.width / matrix.xDimension
        val positionRectYSize = img.height / matrix.yDimension
        matrix.positionMatrix.forEach {
            it.forEach {
                gc.fill = it.color()
                gc.fillRect(positionRectXSize * it.j, positionRectYSize * it.i, positionRectXSize, positionRectYSize)
            }
        }
    }

    protected var at: AnimationTimer = object : AnimationTimer() {
        var generation = 1
        var y = 2.0
        var lastUpdate: Long = 0

        fun run() {
            log.info("tick: Generation ${trustMatrix.generation.number}")

            trustMatrix.generate()
            drawTrustMatrix(trustMatrix)

//            var x = img.width - 100
//            var y = img.height - 20
//            gc.fill = Color.WHITE
//            gc.fillRect(x, y, 100.0, 20.0)
//            gc.strokeText("gen ${generation}", x + 10.0, y + 10.0, 100.0)


        }

        override fun handle(now: Long) {
            if (now - lastUpdate >= 280_000_000 * (1.0 - speed())) {

                fillBackground()
                run()
                lastUpdate = now

            }


        }

        private fun fillBackground() {
            gc.fill = Color.GREEN
            gc.fillRect(0.0, 0.0, img.width, img.height)
        }
    }


    override fun initialize(location: URL?, resources: ResourceBundle?) {
        listBoxForInitialDistribution.items = initialDistributionItems
        listBoxForInitialDistribution.prefHeight = initialDistributionItems.size * 24 + 2.0
        resetButton.setOnMouseClicked {
            trustMatrix = buildTrustMatrix()
        }
        gc = img.getGraphicsContext2D();
        grid.layoutXProperty()
//        img.widthProperty().bind(grid.bottom.layoutBoundsProperty().)
//        img.heightProperty().bind(grid.center.layoutYProperty())
        gc.fillRect(50.0, 50.0, 100.0, 100.0)
        gc.stroke = Color.BLACK
        at.start()
    }

    private fun buildTrustMatrix() = TrustMatrix(100, 100,
            mutations = listOf(
                    SimpleStrongestNeighbourMutation(distortion = distortion.text.toDoubleOrNull() ?: 0.0),
                    SpawnMutationUniform(setOf(
                            Strategy.alwaysCheat
                            , Strategy.alwaysCooperate
                            , Strategy.anEyeForAnEye
                            , Strategy.smartOne

                    ))
            ),
            initialDistribution = InitialDistribution.valueOf(listBoxForInitialDistribution.focusModel.focusedItem ?: InitialDistribution.ALL_ALWAYS_CHEAT.name).player)
}
