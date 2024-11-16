package com.example.aisplitwise

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.aisplitwise.ui.theme.AISplitwiseTheme
import com.example.aisplitwise.utils.ifNullOrEmpty
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.vertexai.type.HarmBlockThreshold
import com.google.firebase.vertexai.type.HarmCategory
import com.google.firebase.vertexai.type.SafetySetting
import com.google.firebase.vertexai.type.Schema
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.type.generationConfig
import com.google.firebase.vertexai.vertexAI
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
val transactionSchema = Schema.obj(
    mapOf(
        "sender" to Schema.obj(
            mapOf(
                "name" to Schema.string(),
                "upiId" to Schema.string()
            )
        ),
        "receiver" to Schema.obj(
            mapOf(
                "name" to Schema.string(),
                "upiId" to Schema.string()
            )
        ),
        "amount" to Schema.double(),
        "time" to Schema.string("Iso Time"), // ISO 8601 format
        "platform" to Schema.enumeration(listOf("GooglePay", "PhonePe", "Paytm", "Other"))
    )
)

private val dangerousContent = SafetySetting(HarmCategory.DANGEROUS_CONTENT, HarmBlockThreshold.NONE)
private val sexuallyExplicit = SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, HarmBlockThreshold.NONE)
private val hateSpeech = SafetySetting(HarmCategory.HATE_SPEECH, HarmBlockThreshold.NONE)
private val harassment = SafetySetting(HarmCategory.HARASSMENT, HarmBlockThreshold.NONE)

val generativeModel = Firebase.vertexAI.generativeModel("gemini-1.5-flash",generationConfig = generationConfig {
    responseMimeType = "application/json"
    responseSchema = transactionSchema
},
    safetySettings = listOf(dangerousContent, sexuallyExplicit, hateSpeech, harassment)
)

class AISplitwiseImageAcceptorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val imageUri =  intent.extras?.get(Intent.EXTRA_STREAM).toString()



        setContent {
            AISplitwiseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White.copy(alpha = 0.9f)
                ) {

                     ImageAcceptorScreen(imageUri)
//                    LoadImageAndRecognizeText(imageUri){}
                }
            }
        }
    }
}

@Composable
fun ImageAcceptorScreen(imageUri:String) {
    val context = LocalContext.current
    val loader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(imageUri)
        .build()

    val painter = rememberAsyncImagePainter(
        model =request
    )
    var recognizedText by remember { mutableStateOf("") }

    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val resultLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val uri = result.data!!.data
            uri?.let {
                // Save the recognized text to the selected location
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write(recognizedText.toByteArray())
                }
            }
        }
    }
    LaunchedEffect(request) {
        val result = (loader.execute(request) as? SuccessResult)?.drawable
        imageBitmap = drawableToBitmap(result)
        imageBitmap?.let { bitmap ->

            val prompt = content {
                image(bitmap)
                text("Extract Sender Name,Sender UPI Id ,Reciever Name ,Reciever UPI Id,Amount ,Time in iso")
            }
            recognizedText=generativeModel.generateContent(prompt).text?:""
//            processImage(bitmap,onTextRecognized= { text ->
//                recognizedText = text
//                saveTextToScopedStorage(context,recognizedText,resultLauncher)
//                saveRecognizedTextToFile(context,text)
//            },
//                error = {})
        }

    }

    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val targetOffset = with(LocalDensity.current) {
        5000.dp.toPx()
    }
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = targetOffset,
        animationSpec = infiniteRepeatable(
            tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )
    val brushColors = listOf(Color(0xff7057f5).copy(0.4f),Color(0xff86f7fa).copy(alpha = 0.4f))


    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Box() {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxHeight(0.6f)
                )
                if(recognizedText.isEmpty()) {
                    Box(Modifier.matchParentSize().blur(50.dp)
                        .drawWithCache {
                            val brushSize = 400f
                            val brush = Brush.linearGradient(
                                colors = brushColors,
                                start = Offset(offset, offset),
                                end = Offset(offset + brushSize, offset + brushSize),
                                tileMode = TileMode.Mirror
                            )
                            onDrawBehind {
                                drawRect(brush)
                            }

                        })
                }
            }
            if(recognizedText.isEmpty()){
                val rememberCount = remember { mutableStateOf(0) }
                val dotCount= animateIntAsState(
                    targetValue = rememberCount.value,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 500, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ))
                // Animate dot count (0 to 3)
                rememberCount.value=3
                // Generate the text with dots
                val dots = when (dotCount.value) {
                    1 -> "."
                    2 -> ".."
                    3 -> "..."
                    else -> ""
                }

                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Recognizing Text$dots",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }else{
                Text(
                    text = "Recognized Text \n $recognizedText",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        }
    }
}

private fun processImage(bitmap: Bitmap, onTextRecognized: (String) -> Unit,error:(Exception)->Unit) {
    val image = InputImage.fromBitmap(bitmap, 0)

    // Initialize Text Recognizer
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    // Process the image
    recognizer.process(image)
        .addOnSuccessListener { visionText ->
            val recognizedText = visionText.text.replace("\n", " ").trim()
            Log.d("OCR", "Text Recognized Original: \n ${visionText.text}")
            Log.d("OCR", "Text Recognized : \n $recognizedText")
            onTextRecognized(recognizedText)
        }
        .addOnFailureListener { e ->
            Log.e("OCR", "Text recognition failed", e)
            error.invoke(e)
        }
}

@Composable
fun LoadImageAndRecognizeText(imageUri: String, onTextRecognized: (String) -> Unit) {
    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var recognizedText by remember { mutableStateOf("") }

    // Load the image using Coil's ImageLoader
    LaunchedEffect(imageUri) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageUri)
            .build()

        val result = (loader.execute(request) as? SuccessResult)?.drawable
        imageBitmap = drawableToBitmap(result)
    }

    // Process the image once the bitmap is available
    imageBitmap?.let { bitmap ->
        processImage(bitmap,onTextRecognized= { text ->
            recognizedText = text
            onTextRecognized(text)
        },
            error = {})
    }
}

// Function to convert Drawable to Bitmap
private fun drawableToBitmap(drawable: Drawable?): Bitmap? {
    return if (drawable is BitmapDrawable) {
        drawable.bitmap
    } else {
        null
    }
}

fun saveRecognizedTextToFile(context: Context, recognizedText: String) {
    // Define the folder path where the files will be saved
    val folderName = "OCRResults"
    val storageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), folderName)

    // Create the directory if it doesn't exist
    if (!storageDir.exists()) {
        storageDir.mkdirs()
    }

    // Find the next available filename with incrementing numbers (OCR-1.txt, OCR-2.txt, etc.)
    var fileNumber = 1
    var textFile = File(storageDir, "OCR-$fileNumber.txt")

    // Check if the file already exists, increment the number until we find an unused filename
    while (textFile.exists()) {
        fileNumber++
        textFile = File(storageDir, "OCR-$fileNumber.txt")
    }

    // Write the recognized text to the file
    try {
        FileOutputStream(textFile).use { outputStream ->
            outputStream.write(recognizedText.toByteArray())
            outputStream.flush()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

@Composable
fun requestPermissions() {
    val context= LocalContext.current
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true &&
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
            // Permissions granted
        } else {
            // Permission denied, show a message
            Toast.makeText(context, "Storage permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    requestPermissionLauncher.launch(
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    )
}


fun saveRecognizedTextToPublicDocuments(context: Context, recognizedText: String) {
    // Define the folder path where the files will be saved (in the public Documents directory)
    val folderName = "OCRResults"
    val storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), folderName)

    // Create the directory if it doesn't exist
    if (!storageDir.exists()) {
        storageDir.mkdirs()
    }

    // Find the next available filename with incrementing numbers (OCR-1.txt, OCR-2.txt, etc.)
    var fileNumber = 1
    var textFile = File(storageDir, "OCR-$fileNumber.txt")

    // Check if the file already exists, increment the number until we find an unused filename
    while (textFile.exists()) {
        fileNumber++
        textFile = File(storageDir, "OCR-$fileNumber.txt")
    }

    // Write the recognized text to the file
    try {
        FileOutputStream(textFile).use { outputStream ->
            outputStream.write(recognizedText.toByteArray())
            outputStream.flush()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun saveTextToScopedStorage(context: Context, recognizedText: String, resultLauncher: ActivityResultLauncher<Intent>) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "OCR-1.txt")
        }
        resultLauncher.launch(intent)
    } else {
        // For Android versions below Q, you can use the old method
        saveRecognizedTextToPublicDocuments(context, recognizedText)
    }
}

@Preview(device = Devices.NEXUS_5, showBackground = true)
@Composable
fun GradientBackgroundPreview(){
    AISplitwiseTheme  {
        val infiniteTransition = rememberInfiniteTransition()

        val red by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        val green by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        val blue by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 5000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

//        Box(
//            Modifier
//                .fillMaxSize()
//                .background(gradientBrush)
//        )
    }
}



