package com.PULLSH.mymuseumadventure.mainView

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.PULLSH.mymuseumadventure.R
import com.PULLSH.mymuseumadventure.components.AlertDialog
import com.PULLSH.mymuseumadventure.themes.ThemeViewModel
import com.PULLSH.mymuseumadventure.ui.theme.MyMuseumAdventureTheme
import com.PULLSH.mymuseumadventure.zone.ZonePageModal
import com.PULLSH.mymuseumadventure.zone.ZoneViewModel
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun Map(
    modifier: Modifier = Modifier,
    pad: Dp = 0.dp,
    zoneViewModel: ZoneViewModel = viewModel(),
    tourStarted: Boolean = false,
    themeViewModel: ThemeViewModel = viewModel(),
    navController: NavController,
    setShowZoneDialog: (Boolean) -> Unit = {},
) {
    var showDialogAlertButton by rememberSaveable { mutableStateOf(false) }
    val setShowDialogAlertButton: (Boolean) -> Unit = { state -> showDialogAlertButton = state }
    val offset by remember { mutableStateOf(Offset.Zero) }
    val scale by remember { mutableFloatStateOf(1f) }
    var imageSize by remember { mutableStateOf(IntSize(2209, 2165)) }
    val showSheet = remember { mutableStateOf(false) }
    val onLongClickZone: (Boolean) -> Unit = { state ->
        showSheet.value = state
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(pad),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val newScale = (scale * zoom).coerceIn(1f, 4f)
                        val newOffset = offset + pan
                        // Calculate movement limits
                        val maxOffsetX = max(0f, (imageSize.width * newScale - imageSize.width) / 2)
                        val maxOffsetY =
                            max(0f, (imageSize.height * newScale - imageSize.height) / 2)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Map draw
            Image(
                painter = painterResource(id = R.drawable.floor1),
                contentDescription = "Map Image",
                modifier = Modifier
                    .onSizeChanged { size ->
                        imageSize = size // Ottieni la dimensione della mappa
                    }
                    .graphicsLayer(
                        translationX = -offset.x,
                        translationY = -offset.y -100,
                        scaleX = scale,
                        scaleY = scale,
                    )
                    .width(with(LocalDensity.current) { imageSize.width.toDp() })  // Imposta la larghezza originale
                    .height(with(LocalDensity.current) { imageSize.height.toDp() }) // Imposta l'altezza originale
            )
            //Zone draw
            if (tourStarted) {
                zoneViewModel.zones.forEach { zone ->
                    if(zone.shown && themeViewModel.isThemeSelected(zone.theme)){
                        Box(
                            modifier = Modifier
                                .size((zone.width).dp, (zone.height).dp) // Applichiamo la scala anche alla dimensione
                                .offset(
                                    x = (zone.positionX).dp,
                                    y = (zone.positionY).dp
                                )
                                .align(Alignment.TopStart)
                                .graphicsLayer(
                                    translationX = - offset.x,
                                    translationY =  - offset.y,
                                    scaleX = scale,
                                    scaleY = scale,
                                )
                                .combinedClickable(
                                    enabled = true,
                                    onClick = {},
                                    onLongClick = {
                                        zoneViewModel.selectedZone = zone
                                        onLongClickZone(true)

                                    }
                                ),
                        ) {
                            val theme = themeViewModel.getTheme(zone.theme)
                            Surface(
                                shape = RectangleShape,
                                color = Color(theme.color).copy(alpha = 0.8f),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .align(Alignment.Center)
                                ) {
                                val fontSize = min(zone.width, zone.height)/5
                                Text(
                                    text = stringResource(R.string.zone) + " " + zone.id.toString(),
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = fontSize.sp),
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .fillMaxWidth()
                                        .padding((fontSize/2).dp),
                                )
                            }
                        }
                    }
                }
            }

        }
        // Button to show alert report
        IconButton(
            onClick = {
                setShowDialogAlertButton(true)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(90.dp)
                .padding(20.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.alert_triangle),
                    contentDescription = "Report Classification Icon",
                    modifier = Modifier.padding(10.dp),
                )
            }
        }

        // Alert Report
        if (showDialogAlertButton) {
            ReportAlert(dismiss = setShowDialogAlertButton)
        }
    }
    if (showSheet.value){
        ZonePageModal(zoneViewModel,themeViewModel,showSheet,setShowZoneDialog){}
    }
    
}

@Composable
fun ReportAlert(dismiss: (Boolean) -> Unit = {}) {
    AlertDialog(
        onDismissRequest = { },
        onConfirmation = { dismiss(false) },
        dialogTitle = stringResource(R.string.AlertReportClassificationTitle),
        dialogText = stringResource(R.string.AlertReportClassificationDescription),
        labelConfirm = stringResource(R.string.understood),
    )
}

@Composable
fun MainView(
    tourStarted:Boolean,
    themeViewModel: ThemeViewModel = viewModel(),
    zoneViewModel: ZoneViewModel = viewModel(),
    navController: NavController,
    setShowZoneDialog: (Boolean) -> Unit = {}
) {
    Map(
        zoneViewModel = zoneViewModel,
        tourStarted = tourStarted,
        themeViewModel = themeViewModel,
        navController = navController,
        setShowZoneDialog = setShowZoneDialog
    )
}

@Preview
@Composable
fun ReportAlertPreview() {
    MyMuseumAdventureTheme {
        ReportAlert()
    }
}

