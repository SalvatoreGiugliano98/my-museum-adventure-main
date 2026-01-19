package com.PULLSH.mymuseumadventure.camera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.*
import androidx.camera.view.PreviewView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import androidx.camera.core.Preview as CameraPreview
import androidx.camera.core.CameraSelector
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.PULLSH.mymuseumadventure.R
import com.PULLSH.mymuseumadventure.artwork.ArtworkViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.PULLSH.mymuseumadventure.artwork.Artwork
import com.PULLSH.mymuseumadventure.jetpacknavigation.Page

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPage(navController: NavController,
               artworkViewModel: ArtworkViewModel = viewModel(),
               setFromRiddleToArtwork: (Boolean) -> Unit)
{
    val artworks=artworkViewModel.artworks
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    var permissionDeniedCount by remember {
        mutableIntStateOf(context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            .getInt("permissionDeniedCount", 0))
    }

    val shouldShowRationale = cameraPermissionState.status.shouldShowRationale
    var capturedImage by remember { mutableStateOf<ByteArray?>(null) } // Variabile per immagine catturata
    var showDialog by remember { mutableStateOf(false) } // Stato per mostrare il dialogo
    var dialogMessage by remember { mutableStateOf("") } // Messaggio del dialogo

    // CoroutineScope dedicato per la ricerca
    val searchScope = rememberCoroutineScope()
    var searchStart by remember { mutableStateOf(false) }

    LaunchedEffect(permissionDeniedCount) {
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            .edit()
            .putInt("permissionDeniedCount", permissionDeniedCount)
            .apply()
    }
    Box (
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                cameraPermissionState.status.isGranted -> {
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(modifier = Modifier.fillMaxSize()) {
                        // Anteprima della fotocamera e cattura immagine
                        CameraXPreview(searchStart) { imageBytes ->
                            capturedImage = imageBytes // Memorizza l'immagine scattata
                            searchScope.launch {
                                searchStart=true
                                captureAndRecognizeImage(context, imageBytes, artworks) { result ->
                                    searchStart=false
                                    capturedImage = null
                                    Log.d("CameraPage", "Risultato della cattura: $result")
                                    if (result == -1) {
                                        dialogMessage =
                                            context.getString(R.string.try_to_do_a_better_search) // Imposta il messaggio del dialogo
                                        showDialog = true      // Mostra il dialogo
                                    } else {
                                        artworkViewModel.setSelectedArtwork(
                                            (result as? Int) ?: (result as? String)?.toIntOrNull()
                                            ?: 0
                                        )
                                        navController.navigate(Page.ArtworkPage.route)
                                    }
                                }
                            }
                        }

                        // Mostra l'immagine catturata mentre è in elaborazione
                        capturedImage?.let { imageBytes ->
                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            // Applica una rotazione a 90 gradi (modifica la rotazione se necessario)
                            val rotatedBitmap = rotateBitmap(bitmap, 90f) // Rotazione a 90 gradi
                            //val newbitmap=Bitmap.createScaledBitmap(rotatedBitmap, 1200, rotatedBitmap.height*2, true)
                            Image(
                                bitmap = rotatedBitmap.asImageBitmap(), // Converte il Bitmap in ImageBitmap
                                contentDescription = "Captured Image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(30.dp)) // Mantieni angoli normali o personalizzati
                                    .graphicsLayer {
                                        scaleX = 1.55f
                                        scaleY = 1.55f
                                    }
                            )

                            // Mostra un'animazione di caricamento sopra l'immagine
                            AnimatedScannerLine() // Linea di scansione sopra l'immagine
                        }
                    }
                }
                shouldShowRationale -> {
                    Text(stringResource(R.string.the_app_needs_permission_to_access_to_the_camera_please_grant_it))
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        cameraPermissionState.launchPermissionRequest()
                        permissionDeniedCount++
                    }) {
                        Text(text = stringResource(R.string.grant_permission),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
                else -> {
                    if (permissionDeniedCount >= 1) {
                        Text(stringResource(R.string.to_use_the_camera_go_into_the_settings_to_enable_access))
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = Uri.parse("package:${context.packageName}")
                            context.startActivity(intent)
                        }) {
                            Text(text = stringResource(R.string.go_to_settings),
                                style = MaterialTheme.typography.titleLarge)
                        }
                    } else {
                        Text(text = stringResource(R.string.a_camera_permit_is_required))
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            cameraPermissionState.launchPermissionRequest()
                        }) {
                            Text(text = stringResource(R.string.request_permission)
                                , style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
            // AlertDialog per messaggi di errore
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        Button(onClick = { showDialog = false }) {
                            Text(stringResource(R.string.try_again))
                        }
                    },
                    title = { Text(stringResource(R.string.nothing_found)) },
                    text = { Text(dialogMessage) }
                )
            }
        }

        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(20.dp),
            onClick = {
                searchScope.cancel() // Interrompe la ricerca in corso
                navController.popBackStack()
                setFromRiddleToArtwork(false)
            },
        ) {
            Icon(
                imageVector = Icons.Filled.Cancel,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .shadow(
                        elevation = 8.dp, // L'elevazione definisce l'intensità dell'ombra
                        shape = CircleShape // Puoi personalizzare la forma dell'ombra
                    ),
                tint = Color.White
                )
        }
    }


}

// Funzione per ruotare il Bitmap
fun rotateBitmap(bitmap: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle) // Applica la rotazione al Bitmap
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

@Composable
fun CameraXPreview(searchStart:Boolean,onImageCaptured: (ByteArray) -> Unit) {
    val context = LocalContext.current
    val imageCapture = remember { ImageCapture.Builder().build() }
    val executor = ContextCompat.getMainExecutor(context)

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = CameraPreview.Builder().build().also { previewInstance ->
                            previewInstance.surfaceProvider = surfaceProvider
                        }
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                context as LifecycleOwner,
                                cameraSelector,
                                preview,
                                imageCapture
                            )
                        } catch (e: Exception) {
                            Log.e("CameraX", "Error binding camera", e)
                        }
                    }, executor)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (searchStart) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)) // Oscura leggermente lo sfondo
            )
        }

        // Overlay del frame come immagine
        Image(
            painter = painterResource(id = R.drawable.frame_overlay),
            contentDescription = "Camera Frame",
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize() // Regola le dimensioni relative allo schermo
                .padding(
                    bottom = 60.dp,
                    top = 20.dp,
                    start = 20.dp,
                    end = 20.dp
                )
                //.offset(y = (-50).dp) // Sposta l'immagine di 50 dp verso l'alto
        )

        Spacer(modifier = Modifier.height(8.dp))

        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        // Button
        Button(
            onClick = {
                // Attiva la vibrazione quando si preme il pulsante
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator?.vibrate(100) // Deprecated ma ancora compatibile con versioni più vecchie
                }

                imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(imageProxy: ImageProxy) {
                        val buffer = imageProxy.planes[0].buffer
                        val bytes = ByteArray(buffer.remaining())
                        buffer.get(bytes)
                        imageProxy.close()

                        onImageCaptured(bytes)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CameraX", "Image capture failed", exception)
                    }
                })
            },
            modifier = Modifier
                .align(Alignment.BottomCenter) // Posizione più in alto
                .padding(bottom = 20.dp) // Aggiungi un padding per distanziare il bottone dalla parte inferiore
                //.width(250.dp) // Larghezza maggiore
                .height(70.dp) // Altezza più piccola
                .clip(RoundedCornerShape(16.dp)),
            enabled = !searchStart
        ) {
            Text(text = stringResource(R.string.scan_artwork),
                style = MaterialTheme.typography.titleLarge
            ) // Aumenta la dimensione del testo
        }
    }
}

@Composable
fun AnimatedScannerLine() {
    // Utilizziamo una Box per rappresentare la linea scanner
    val transition = rememberInfiniteTransition()

    val yOffset by transition.animateValue(
        initialValue = 0f,
        targetValue = 1f,
        typeConverter = Float.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000 // Ridotto a 1000 millisecondi per una scansione più veloce
                0f at 0 using LinearOutSlowInEasing
                1f at 1000 using LinearOutSlowInEasing // Mettiamo il picco a metà
                0f at 2000 using LinearOutSlowInEasing
            },
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    ) {
        // Linea che si muove verticalmente sopra l'anteprima della fotocamera
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter) // Allinea la linea in alto
                .offset(y = (yOffset * 850).dp) // Muove la linea su e giù
                .fillMaxWidth()
                .height(5.dp)
                .background(MaterialTheme.colorScheme.primary) // Colore della linea (può essere personalizzato)
        )
    }
}

fun captureAndRecognizeImage(context: Context, imageBytes: ByteArray, artworks: SnapshotStateList<Artwork>, onResult: (Any) -> Unit) {
    val apiKey = "DA AGGIUNGERE"  // Aggiungi la tua API key di SerpApi  //SOLO 100 RICHIESTE CONSENTITE
    val imgurClientId = "DA AGGIUNGERE"  // Aggiungi il tuo Client ID di Imgur

    CoroutineScope(Dispatchers.IO).launch {
        val imgurUrl = uploadImageToImgur(imageBytes, imgurClientId)
        //val imgurUrl = "https://i.imgur.com/d9dKxQV.png" //FOTO PROVA DELLA GIOCONDA
        if (imgurUrl != null) {
            val recognizedObject = recognizeImageWithSerpApi(imgurUrl, apiKey, artworks)
            withContext(Dispatchers.Main) {
                onResult(recognizedObject)
            }
        } else {
            withContext(Dispatchers.Main) {
                onResult("Error uploading image to Imgur.")
            }
        }
    }
}

fun uploadImageToImgur(imageBytes: ByteArray, imgurClientId: String): String? {
    val client = OkHttpClient()
    val imageRequestBody = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("image", "image.jpg", imageRequestBody)
        .build()

    val request = Request.Builder()
        .url("https://api.imgur.com/3/upload")
        .addHeader("Authorization", "Client-ID $imgurClientId")
        .post(requestBody)
        .build()

    try {
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()

        // Aggiungi il log per stampare l'intera risposta JSON
        Log.d("ImgurUpload", "Risposta JSON: $responseBody")

        val json = JSONObject(responseBody ?: "")
        val imageUrl = json.getJSONObject("data").optString("link")

        // Log per il link dell'immagine
        Log.d("ImgurUpload", "Uploaded image link: $imageUrl")

        return if (imageUrl.isNotEmpty()) imageUrl else null
    } catch (e: Exception) {
        Log.e("CameraPage", "Error uploading to Imgur", e)
        return null
    }
}

fun recognizeImageWithSerpApi(imageUrl: String, serpApiKey: String, artworks: SnapshotStateList<Artwork>): Any {
    val client = OkHttpClient()

    val request = Request.Builder()
        .url("https://serpapi.com/search.json?engine=google_lens&api_key=$serpApiKey&country=it&url=$imageUrl")
        .build()

    try {
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()

        Log.d("CameraPage", "Risposta JSON: $responseBody")

        val json = JSONObject(responseBody ?: "")
        val visualMatches = json.optJSONArray("visual_matches")

        // Controlla ogni titolo all'interno di visual_matches
        if (visualMatches != null) {
            for (i in 0 until visualMatches.length()) {
                val match = visualMatches.getJSONObject(i)
                val title = match.optString("title", "")

                //val title="Gioconda" //titolo di test
                // Verifica se il titolo di visualMatch contiene uno dei sinonimi del titolo negli artworks
                for (artwork in artworks) {
                    // Controlla se il titolo contiene uno dei titleSynonyms
                    if (artwork.titleSynonyms.any { title.contains(it, ignoreCase = true) }) {
                        return artwork.id  // Restituisce l'ID dell'Artwork
                    }
                }
            }
        }
        // Se nessun titolo corrisponde, restituisce un messaggio predefinito
        return -1

    } catch (e: Exception) {
        Log.e("CameraPage", "Error during recognition", e)
        return "Errore: ${e.message}"
    }
}
/*
@Preview(showBackground = true)
@Composable
fun CameraPagePreview() {
    val navController = rememberNavController()
    CameraPage(navController)
}*/