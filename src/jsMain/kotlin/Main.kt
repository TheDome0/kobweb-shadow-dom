import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.KobwebComposeStyles
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.zIndex
import com.varabyte.kobweb.silk.SilkFoundationStyles
import com.varabyte.kobweb.silk.components.forms.TextInput
import com.varabyte.kobweb.silk.defer.DeferringHost
import com.varabyte.kobweb.silk.init.SilkWidgetVariables
import kotlinx.browser.document
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.OPEN
import org.w3c.dom.ShadowRootInit
import org.w3c.dom.ShadowRootMode

fun main() {
    val host = document.createElement("div").apply {
        id = "shadow-host"
    }
    document.body?.append(host)
    val shadow = host.attachShadow(ShadowRootInit(ShadowRootMode.OPEN))
    document.createElement("span").apply {
        textContent = "Hello, shadow!"
        shadow.append(this)
    }

    renderComposable(document.createElement("div").apply {
        id = "ext-root"
        shadow.append(this)
    }) {
        KobwebComposeStyles()
        SilkFoundationStyles(
            initSilk = { ctx ->
                com.varabyte.kobweb.silk.init.initSilkWidgets(ctx)
            }
        )
        SilkWidgetVariables("ext-root") // replace "ext-root" with "shadow-host": no crash but
        DeferringHost {
            var input by remember { mutableStateOf("") }
            Column(
                Modifier.position(Position.Absolute).zIndex(999999),
            ) {
                TextInput(
                    input,
                    { input = it }
                )
            }
        }
    }
}