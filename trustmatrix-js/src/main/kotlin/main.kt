import com.trustmatrix.TrustMatrix
import com.trustmatrix.platform.JsPlatformTools
import org.w3c.dom.HTMLInputElement
import kotlin.browser.document

fun main(args: Array<String>) {
    val email = document.getElementById("email") as HTMLInputElement
    email.value = "test"
    val trustMatrix = TrustMatrix(10, 10, platformTools = JsPlatformTools())
    trustMatrix.generate()
    trustMatrix.generate()
}