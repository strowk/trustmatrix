import com.trustmatrix.TrustMatrix
import com.trustmatrix.platform.*
import kotlinx.html.canvas
import kotlinx.html.div
import kotlinx.html.dom.create
import kotlinx.html.p
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Date
import kotlin.js.Math


fun main(args: Array<String>) {
    document.title = "Trust Matrix Evolution"
    val platformTools = JsPlatformTools() //should be first one
    JsLoggerFactory.appender.level = LogLevels.INFO
    val log = LoggerBuilder.factory(document::class).getLogger()
    val canvas = document.create.canvas { } as HTMLCanvasElement

    canvas.height = Math.min(window.innerHeight, window.innerWidth)
    canvas.width = Math.min(window.innerHeight, window.innerWidth)
    val div = document.create.div("div") {
        p { +"test2" }
    }
    val body = document.body
    body!!.appendChild(canvas)
    body.appendChild(div)
    val render = canvas.getContext("2d") as CanvasRenderingContext2D

    val trustMatrix = TrustMatrix(50, 50, platformTools = platformTools)
    val start = Date().getTime()
    val canvasWidth = canvas.width * 1.0
    val canvasHeight = canvas.height * 1.0
    val ySize = canvasHeight / trustMatrix.yDimension
    val xSize = canvasWidth / trustMatrix.xDimension
//    val timerToDraw = window.setInterval({
//
//    }, 25)

    val timerToGenerate = window.setInterval({
        trustMatrix.generate()
        drawMatrix(render, canvasWidth, canvasHeight, trustMatrix, log, xSize, ySize)
    }, 200)
}

private fun drawMatrix(render: CanvasRenderingContext2D, canvasWidth: Double, canvasHeight: Double, trustMatrix: TrustMatrix, log: Logger, xSize: Double, ySize: Double) {
    render.fillStyle = "white"
    render.fillRect(0.0, 0.0, canvasWidth, canvasHeight)
    trustMatrix.positionMatrix.forEach {
        it.forEach {
            val fillcolor = (it.color() as JsColors).name.toLowerCase()
            log.debug("fillcolor: ${fillcolor}")
            render.fillStyle = fillcolor
            render.fillRect(it.i * xSize, it.j * ySize, xSize, ySize)
        }
    }
}