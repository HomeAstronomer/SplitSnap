package com.splitsnap.activity

import android.graphics.RuntimeShader
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.splitsnap.navigation.NavHostInitializer
import com.splitsnap.theme.SplitSnapTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import org.intellij.lang.annotations.Language

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {


            val navController = rememberNavController()
            val isLoggedIn= Firebase.auth.currentUser

            installSplashScreen()

            SplitSnapTheme {
                NavHostInitializer(navController, isLoggedIn)
            }

        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SplitSnapTheme  {
       val Coral = MaterialTheme.colorScheme.tertiary
        val LightYellow =MaterialTheme.colorScheme.tertiaryContainer
        @Language("AGSL")
        val CUSTOM_SHADER = """
    uniform float2 resolution;
    layout(color) uniform half4 color;
    layout(color) uniform half4 color2;

    half4 main(in float2 fragCoord) {
        float2 uv = fragCoord/resolution.xy;

        float mixValue = distance(uv, vec2(0, 1));
        return mix(color, color2, mixValue);
    }
""".trimIndent()
        Box(
            Modifier
                .fillMaxSize()
                .drawWithCache {
                val shader = RuntimeShader(CUSTOM_SHADER)
                val shaderBrush = ShaderBrush(shader)
                shader.setFloatUniform("resolution", size.width, size.height)
                onDrawBehind {
                    shader.setColorUniform(
                        "color",
                        android.graphics.Color.valueOf(
                            LightYellow.red, LightYellow.green,
                            LightYellow
                                .blue,
                            LightYellow.alpha
                        )
                    )
                    shader.setColorUniform(
                        "color2",
                        android.graphics.Color.valueOf(
                            Coral.red,
                            Coral.green,
                            Coral.blue,
                            Coral.alpha
                        )
                    )
                    drawRect(shaderBrush)
                }
            }
        )
    }
}