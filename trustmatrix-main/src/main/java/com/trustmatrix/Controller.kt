package com.trustmatrix

import javafx.animation.AnimationTimer
import javafx.fxml.Initializable
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
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
    val trustMatrix = TrustMatrix(100, 100)

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
            log.debug("gen ${generation++}")

            trustMatrix.generate()
            drawTrustMatrix(trustMatrix)

//            var x = img.width - 100
//            var y = img.height - 20
//            gc.fill = Color.WHITE
//            gc.fillRect(x, y, 100.0, 20.0)
//            gc.strokeText("gen ${generation++}", x + 10.0, y + 10.0, 100.0)


        }

        override fun handle(now: Long) {
            if (now - lastUpdate >= 280_000_000) {
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
        gc = img.getGraphicsContext2D();
        grid.layoutXProperty()
//        img.widthProperty().bind(grid.bottom.layoutBoundsProperty().)
//        img.heightProperty().bind(grid.center.layoutYProperty())
        gc.fillRect(50.0, 50.0, 100.0, 100.0)
        gc.stroke = Color.BLACK
        at.start()
    }
}
