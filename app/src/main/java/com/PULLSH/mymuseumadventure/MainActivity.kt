package com.PULLSH.mymuseumadventure

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.PULLSH.mymuseumadventure.artwork.Artwork
import com.PULLSH.mymuseumadventure.artwork.ArtworkViewModel
import com.PULLSH.mymuseumadventure.components.AlertDialog
import com.PULLSH.mymuseumadventure.jetpacknavigation.Navigation
import com.PULLSH.mymuseumadventure.jetpacknavigation.Page
import com.PULLSH.mymuseumadventure.riddles.Riddle
import com.PULLSH.mymuseumadventure.riddles.RiddleViewModel
import com.PULLSH.mymuseumadventure.themes.ThemeViewModel
import com.PULLSH.mymuseumadventure.ui.theme.MyMuseumAdventureTheme
import com.PULLSH.mymuseumadventure.zone.ZoneViewModel

const val TAG = "BasePage"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel = ViewModelProvider(this)[ThemeViewModel::class.java]
            val artworkViewModel = ViewModelProvider(this)[ArtworkViewModel::class.java]
            val riddleViewModel = ViewModelProvider(this)[RiddleViewModel::class.java]
            val zoneViewModel = ViewModelProvider(this)[ZoneViewModel::class.java]
            MyMuseumAdventureTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    BasePage(
                        themeViewModel = themeViewModel,
                        artworkViewModel = artworkViewModel,
                        riddleViewModel = riddleViewModel,
                        zoneViewModel = zoneViewModel
                    )
                }
            }
        }
    }

}

/**
 * Composable for the basePage.
 * **Contains [TopAppBar] e [BottomAppBar].**
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasePage(
    themeViewModel: ThemeViewModel = viewModel(),
    artworkViewModel: ArtworkViewModel = viewModel(),
    riddleViewModel: RiddleViewModel = viewModel(),
    zoneViewModel: ZoneViewModel = viewModel()
) {
    val navController = rememberNavController()
    var leftButtonState by rememberSaveable { mutableStateOf(true) }
    var rightButtonState by rememberSaveable { mutableStateOf(true) }
    var centerButtonState by rememberSaveable { mutableStateOf(true) }
    var showBottomBar by rememberSaveable { mutableStateOf(true) }
    var tourStarted by rememberSaveable { mutableStateOf(false) }
    var showDialogBackButton by rememberSaveable { mutableStateOf(false) }
    var showDialogStartTour by rememberSaveable { mutableStateOf(false) }
    var showDialogStopTour: Boolean by rememberSaveable { mutableStateOf(false) }
    var singleRiddle by rememberSaveable {  mutableStateOf(mutableListOf<Any>()) }
    var showBottomBarBackGround by rememberSaveable { mutableStateOf(true) }
    var audioPlaying by rememberSaveable { mutableStateOf(false) }
    var audioIsStarted by rememberSaveable { mutableStateOf(false) }
    var showDialogRiddleSolved by rememberSaveable { mutableStateOf(false) }
    var fromRiddleToArtwork by rememberSaveable { mutableStateOf(false) }
    var showSendAlert by remember { mutableStateOf(false) }

    val setFromRiddleToArtwork: (Boolean) -> Unit = {state -> fromRiddleToArtwork = state}
    val getFromRiddleToArtwork: () -> Boolean = {fromRiddleToArtwork}
    val setShowDialogRiddleSolved: (Boolean) -> Unit = { state -> showDialogRiddleSolved = state }
    val setAudioStart: (Boolean) -> Unit = { state ->
        audioIsStarted = state
    }
    val setAudioPlaying: (Boolean) -> Unit = { state -> audioPlaying = state }
    val setSelectedArtwork: (Int) -> Unit = artworkViewModel::setSelectedArtwork
    val getSelectedArtwork: () -> Artwork? = artworkViewModel::getSelectedArtwork
    val setShowBottomBarBackground: (Boolean) -> Unit = { state -> showBottomBarBackGround = state }
    val setSingleRiddle: (Riddle) -> Unit = { riddle -> singleRiddle = riddle.toArray().toMutableList() }
    val getSingleRiddle: () -> Riddle? = {
        Riddle.fromArray(singleRiddle.toTypedArray())

    }
    val setShowDialogBackButton: (Boolean) -> Unit = { state -> showDialogBackButton = state }
    val setTourStarted: (Boolean) -> Unit = { state -> tourStarted = state }
    val setShowBottomBar: (Boolean) -> Unit = { state -> showBottomBar = state }
    val setLeftButtonState: (Boolean) -> Unit = { state -> leftButtonState = state }
    val setRightButtonState: (Boolean) -> Unit = { state -> rightButtonState = state }
    val setCenterButtonState: (Boolean) -> Unit = { state -> centerButtonState = state }
    val setShowDialogStopTour: (Boolean) -> Unit = { state -> showDialogStopTour = state }
    val setShowDialogStartTour: (Boolean) -> Unit = { state -> showDialogStartTour = state }
    val setShowZoneDialog: (Boolean) -> Unit = { state -> showSendAlert = state }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val context = LocalContext.current
    val player = remember {ExoPlayer.Builder(context).build()}
    val audioProgress = remember { mutableFloatStateOf(0f) }
    val handler = Handler(Looper.getMainLooper())
    lateinit var updateProgressRunnable: Runnable

    fun updateProgress() {
        updateProgressRunnable = Runnable {
            val currentPosition = player.currentPosition
            val duration = player.duration

            if (duration != C.TIME_UNSET) { // Evita la divisione per zero
                audioProgress.value = currentPosition.toFloat() / duration // Aggiorna lo stato del progresso
            }

            handler.postDelayed(updateProgressRunnable, 500) // Aggiorna ogni 500ms (puoi regolare questo valore)
        }

        handler.post(updateProgressRunnable) // Avvia l'aggiornamento iniziale
    }
    player.addListener(object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY || playbackState == Player.STATE_BUFFERING) {
                updateProgress() // Aggiorna il progresso
            } else {
                handler.removeCallbacks(updateProgressRunnable) // Interrompi gli aggiornamenti
            }

            if (playbackState == Player.STATE_ENDED) {
                Log.i(TAG, "END Audio")
                player.stop()
                setAudioPlaying(false)
                setAudioStart(false)
                setRightButtonState(false)
                player.seekTo(0)
                audioProgress.floatValue = 0f
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            // Puoi anche gestire qui cambiamenti di isPlaying, se necessario.
            // Ad esempio, per aggiornare lo stato dell'icona di riproduzione/pausa.
        }
    })

    /**
     * La callback della LaunchedEffect viene eseguita ogni volta che una delle sue chiavi cambia
     * in questo caso la chiave è la route corrente
     */
    LaunchedEffect(navBackStackEntry?.destination?.route) {
        Log.i(TAG, "current route: ${navBackStackEntry?.destination?.route}")
        if (Page.HomePage.route == navBackStackEntry?.destination?.route) {
            setLeftButtonState(true)
            setRightButtonState(true)
            setCenterButtonState(true)
        }
        if (Page.PreferencesPage.route == navBackStackEntry?.destination?.route) {
            setLeftButtonState(false)
            setRightButtonState(false)
            setCenterButtonState(true)
        }
        if(Page.CameraPage.route == navBackStackEntry?.destination?.route){
            setCenterButtonState(false)
            setLeftButtonState(false)
            setRightButtonState(false)
        }
    }

    Scaffold(
        modifier = Modifier.background(Color.Transparent),
        topBar = {
            if(Page.CameraPage.route != navBackStackEntry?.destination?.route){
                CenterAlignedTopAppBar(
                    title = {
                        val text = when (navBackStackEntry?.destination?.route) {
                            Page.HomePage.route -> stringResource(R.string.app_name)
                            Page.PreferencesPage.route -> stringResource(R.string.title_page_themes)
                            Page.RiddlesPage.route -> stringResource(R.string.riddles)
                            Page.SingleRiddlePage.route -> stringResource(R.string.riddle)
                            Page.ArtworkPage.route -> artworkViewModel.getSelectedArtwork()?.getArtworkTitle()
                            else -> stringResource(R.string.app_name)
                        }

                        if (text != null) {
                            Box{
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.align(Alignment.Center),
                                    textAlign = TextAlign.Left,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    },
                    modifier = Modifier.background(Color.Transparent),
                    navigationIcon = {
                        // Resto delle icone di navigazione
                        if (Page.PreferencesPage.route == navBackStackEntry?.destination?.route) {
                            IconButton(
                                onClick = { setShowDialogBackButton(true) },
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                            }
                        }
                        if (Page.SingleRiddlePage.route == navBackStackEntry?.destination?.route) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                            }
                        }
                        if (Page.CameraPage.route == navBackStackEntry?.destination?.route) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                            }
                        }
                        if (Page.ArtworkPage.route == navBackStackEntry?.destination?.route) {
                            IconButton(
                                onClick = {
                                    setAudioPlaying(false)
                                    setAudioStart(false)
                                    if (fromRiddleToArtwork) {
                                        navController.navigate(Page.SingleRiddlePage.route){
                                            popUpTo(Page.SingleRiddlePage.route) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                            setFromRiddleToArtwork(false)
                                        }
                                    } else {
                                        navController.navigate(Page.HomePage.route) {
                                            popUpTo(navController.graph.startDestinationRoute ?: Page.HomePage.route) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                            setFromRiddleToArtwork(false)
                                        }
                                    }
                                    player.stop()
                                },
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                            }
                        }
                        if (Page.RiddlesPage.route == navBackStackEntry?.destination?.route) {
                            IconButton(
                                onClick = {
                                    navController.popBackStack()
                                },
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    )
                )
            }
        },
        bottomBar = {
            if (navBackStackEntry?.destination?.route != Page.CameraPage.route && navBackStackEntry?.destination?.route != Page.RiddlesPage.route) {
            BottomAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    )
                    .background(
                        brush = if (showBottomBarBackGround) {
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x33FFFFFF),
                                    Color(0xFF9E9E9E),
                                )
                            )
                        } else {
                            // Aggiungi un valore di default nel ramo else se necessario
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Transparent,
                                )
                            )
                        }
                    ),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                if (showBottomBar) {
                    BottomBar(
                        navController = navController,
                        tourStarted = tourStarted,
                        leftButtonState = leftButtonState,
                        rightButtonState = rightButtonState,
                        centerTourButtonState = centerButtonState,
                        audioPlaying = audioPlaying,
                        audioIsStarted = audioIsStarted,
                        setFromRiddleToArtwork = setFromRiddleToArtwork,
                        setShowDialogRiddleSolved = setShowDialogRiddleSolved,
                        setRightoButtonState = setRightButtonState,
                        setAudioStarted = setAudioStart,
                        setAudioPlaying = setAudioPlaying,
                        setShowDialogStartTour = setShowDialogStartTour,
                        setShowDialogStopTour = setShowDialogStopTour,
                        selectedRiddle = singleRiddle,
                        setSelectedArtwork = setSelectedArtwork,
                        player = player,
                        themeViewModel = themeViewModel,
                        audioProgress = audioProgress
                    )
                }
                if (showDialogStopTour) {
                    AlertDialog(
                        onDismissRequest = {
                            setShowDialogStopTour(false)
                        },
                        onConfirmation = {
                            setShowDialogStopTour(false)
                            setTourStarted(false)
                            themeViewModel.deleteAllThemes()
                            zoneViewModel.resetAllZones()
                        },
                        dialogTitle = stringResource(R.string.do_you_want_to_stop_the_tour),
                        dialogText = stringResource(R.string.all_the_progresses_medals_and_achievements_will_be_lost),
                        labelConfirm = stringResource(R.string.stop),
                        labelDismiss = stringResource(R.string.no)
                    )
                }
                if (showDialogRiddleSolved) {
                    //setFromRiddleToArtwork(false)
                    val riddle = riddleViewModel.riddles.find { it.id == artworkViewModel.getSelectedArtwork()?.id }
                    if (riddle != null) {
                        if(riddle.artworkID == artworkViewModel.getSelectedArtwork()?.id){
                            if (!riddle.completed){
                                riddleViewModel.setCompleted(riddle.id)
                                AlertDialog(
                                    onDismissRequest = {
                                        setShowDialogRiddleSolved(false)
                                        if(player.playbackState == Player.STATE_READY){
                                            setAudioPlaying(true)
                                            player.play()
                                        }
                                    },
                                    onConfirmation = {
                                        setSingleRiddle(riddle)
                                        setShowDialogRiddleSolved(false)
                                        player.stop()
                                        setAudioStart(false)
                                        setAudioPlaying(false)
                                        if(fromRiddleToArtwork){
                                            navController.navigate(Page.SingleRiddlePage.route){
                                                popUpTo(Page.SingleRiddlePage.route) {
                                                    inclusive = true
                                                }
                                                launchSingleTop = true
                                                setFromRiddleToArtwork(false)
                                            }
                                        } else {
                                            navController.navigate(Page.SingleRiddlePage.route)
                                        }
                                    },
                                    dialogTitle = stringResource(R.string.awesome) + "!",
                                    dialogText = stringResource(R.string.you_solved_the_riddle) + ": " + riddle.getTitle(),
                                    labelConfirm = stringResource(R.string.go_to_riddle),
                                    labelDismiss = stringResource(R.string.ok)
                                )
                            }
                            else {
                                AlertDialog(
                                    onDismissRequest = {
                                        setShowDialogRiddleSolved(false)
                                        if(player.playbackState == Player.STATE_READY){
                                            setAudioPlaying(true)
                                            player.play()
                                        }

                                    },
                                    onConfirmation = {
                                        setSingleRiddle(riddle)
                                        setShowDialogRiddleSolved(false)
                                        player.stop()
                                        setAudioStart(false)
                                        setAudioPlaying(false)
                                        if(fromRiddleToArtwork){
                                            navController.navigate(Page.SingleRiddlePage.route){
                                                popUpTo(Page.SingleRiddlePage.route) {
                                                    inclusive = true
                                                }
                                                launchSingleTop = true
                                                setFromRiddleToArtwork(false)
                                            }
                                        } else {
                                            navController.navigate(Page.SingleRiddlePage.route)
                                        }
                                    },
                                    dialogTitle = stringResource(R.string.riddle_already_solved),
                                    dialogText = stringResource(R.string.you_have_already_solved_this_riddle) + ": " + riddle.getTitle(),
                                    labelConfirm = stringResource(R.string.go_to_riddle),
                                    labelDismiss = stringResource(R.string.ok)
                                )
                            }
                        }
                    }
                    else {
                        AlertDialog(
                            onDismissRequest = {
                                setShowDialogRiddleSolved(false)
                            },
                            onConfirmation = {
                                setShowDialogRiddleSolved(false)
                            },
                            dialogTitle = stringResource(R.string.sorry),
                            dialogText = stringResource(R.string.this_is_not_a_riddle_solution),
                            labelConfirm = stringResource(R.string.ok)
                        )
                    }
                }
            }
        }
        },
        content = { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                Navigation(
                    modifier = Modifier
                        .fillMaxSize() // Makes the navigation extend over the whole screen
                        .padding(
                            PaddingValues(
                                top = innerPadding.calculateTopPadding(),
                                bottom = 0.dp // Ignore bottom padding to extend under BottomBar
                            )
                        ),
                    navController = navController,
                    tourStarted = tourStarted,
                    showDialogBackButton = showDialogBackButton,
                    showDialogStartTour = showDialogStartTour,

                    setTourStarted = setTourStarted,
                    setShowBottomBar = setShowBottomBar,
                    setShowDialogBackButton = setShowDialogBackButton,
                    setShowDialogStartTour = setShowDialogStartTour,
                    setSingleRiddle = setSingleRiddle,
                    setLeftButtonState = setLeftButtonState,
                    setRightButtonState = setRightButtonState,
                    setCenterButtonState = setCenterButtonState,
                    setShowBottomBarBackground = setShowBottomBarBackground,
                    setShowDialogRiddleSolved = setShowDialogRiddleSolved,
                    setFromRiddleToArtwork = setFromRiddleToArtwork,
                    setShowZoneDialog = setShowZoneDialog,

                    getSingleRiddle = getSingleRiddle,
                    getSelectedArtwork = getSelectedArtwork,
                    getFromRiddleToArtwork = getFromRiddleToArtwork,

                    themeViewModel = themeViewModel,
                    riddleViewModel = riddleViewModel,
                    artworkViewModel = artworkViewModel,
                    zoneViewModel = zoneViewModel,
                    player = player
                )
            }
        }
    )
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun BottomBar(
    navController: NavController,
    leftButtonState: Boolean,
    rightButtonState: Boolean,
    centerTourButtonState: Boolean = true,
    tourStarted: Boolean,
    selectedRiddle: MutableList<Any> = mutableListOf(),
    audioPlaying: Boolean = false,
    audioIsStarted: Boolean = false,

    setFromRiddleToArtwork: (Boolean) -> Unit = {},
    setShowDialogRiddleSolved: (Boolean) -> Unit = {},
    setRightoButtonState: (Boolean) -> Unit = {},
    setAudioStarted: (Boolean) -> Unit = {},
    setAudioPlaying: (Boolean) -> Unit = {},
    setSelectedArtwork: (Int) -> Unit = {},
    setShowDialogStartTour: (Boolean) -> Unit = {},
    setShowDialogStopTour: (Boolean) -> Unit = {},
    player: ExoPlayer,
    themeViewModel: ThemeViewModel = viewModel(),
    audioProgress : MutableFloatState,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    Row(
        modifier = Modifier
            .background(Color.Transparent)
            .fillMaxWidth()
            .height(160.dp),
        verticalAlignment = Alignment.CenterVertically,
    )
    {
        val size = 60.dp
        val offset = 15.dp
        //Left Button
        Box (
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ){
            if (leftButtonState) {
                IconButton (
                    modifier = Modifier.size(size),
                    onClick = {
                        if (Page.HomePage.route == navBackStackEntry?.destination?.route) {
                            navController.navigate(Page.CameraPage.route)
                        }
                        else if (Page.SingleRiddlePage.route == navBackStackEntry?.destination?.route) {
                            val riddle = Riddle.fromArray(selectedRiddle.toTypedArray())

                            if (riddle != null) {
                                if (riddle.completed) {
                                    setSelectedArtwork(riddle.artworkID)
                                    setFromRiddleToArtwork(true)
                                    navController.navigate(Page.ArtworkPage.route)
                                } else {
                                    setFromRiddleToArtwork(true)
                                    navController.navigate(Page.CameraPage.route)
                                }
                            }
                        }
                        else if (Page.ArtworkPage.route == navBackStackEntry?.destination?.route) {
                            setShowDialogRiddleSolved(true)
                            setAudioPlaying(false)
                            player.pause()
                        }
                    },
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        if (Page.SingleRiddlePage.route == navBackStackEntry?.destination?.route) {
                            val riddle = Riddle.fromArray(selectedRiddle.toTypedArray())
                            if (riddle != null) {
                                if (riddle.completed) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.show_artwork),
                                        contentDescription = "Scan Button Icon",
                                        modifier = Modifier.padding(offset)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Scan Button Icon",
                                        modifier = Modifier.padding(offset)
                                    )
                                }
                            }
                        }
                        else if (Page.ArtworkPage.route == navBackStackEntry?.destination?.route){
                            Icon(
                                painter = painterResource(R.drawable.chackriddlefromartwork),
                                contentDescription = "Check Riddle",
                                modifier = Modifier.padding(offset)
                            )
                        }
                        else {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera Button Icon",
                                modifier = Modifier.padding(offset)
                            )
                        }
                    }
                }
            }
            else {
                Box { }
            }
        }

        //Center Button
        Box (modifier = Modifier
            .weight(1.2f)
            .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ){
            if (centerTourButtonState) {
                val shape = if (Page.ArtworkPage.route == navBackStackEntry?.destination?.route) {
                    CircleShape
                } else {
                    MaterialTheme.shapes.extraLarge
                }
                if (Page.HomePage.route == navBackStackEntry?.destination?.route ) {
                    Button(
                        onClick = {
                            if (!tourStarted) {
                                if (Page.HomePage.route == navBackStackEntry?.destination?.route) {
                                    navController.navigate(Page.PreferencesPage.route)
                                }
                                if (navBackStackEntry?.destination?.route == Page.PreferencesPage.route) {
                                    setShowDialogStartTour(true)
                                }
                            } else {
                                setShowDialogStopTour(true)
                            }
                        },
                        modifier = Modifier.height(size),
                        shape = shape,
                    ) {
                        if (navBackStackEntry?.destination?.route == Page.HomePage.route
                        ) {
                            if (!tourStarted) {
                                val text = stringResource(R.string.start_tour)
                                val fontSize = if (text.length > 9) 14.sp else 20.sp // Adatta la dimensione
                                Text(
                                    text = stringResource(R.string.start_tour),
                                    style = MaterialTheme.typography.titleLarge.copy(fontSize = fontSize),
                                    textAlign = TextAlign.Center,
                                )
                            } else
                            {
                                val text = stringResource(R.string.stop_tour)
                                val fontSize = if (text.length > 9) 14.sp else 20.sp // Adatta la dimensione
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.titleLarge.copy(fontSize = fontSize),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
                else if (Page.PreferencesPage.route == navBackStackEntry?.destination?.route) {
                    Button(
                        onClick = {
                            if (!tourStarted) {
                                if (Page.HomePage.route == navBackStackEntry?.destination?.route) {
                                    navController.navigate(Page.PreferencesPage.route)
                                }
                                if (navBackStackEntry?.destination?.route == Page.PreferencesPage.route) {
                                    setShowDialogStartTour(true)
                                }
                            } else {
                                setShowDialogStopTour(true)
                            }
                        },
                        modifier = Modifier.height(size),
                        shape = shape,
                        enabled = themeViewModel.getSelectedThemesTitle().isNotEmpty()
                    ) {
                        if (navBackStackEntry?.destination?.route == Page.PreferencesPage.route
                        ) {
                            if (!tourStarted) {
                                val text = stringResource(R.string.start_tour)
                                val fontSize = if (text.length > 9) 14.sp else 20.sp // Adatta la dimensione
                                Text(
                                    text = stringResource(R.string.start_tour),
                                    style = MaterialTheme.typography.titleLarge.copy(fontSize = fontSize),
                                    textAlign = TextAlign.Center,
                                )
                            }
                            else
                            {
                                val text = stringResource(R.string.stop_tour)
                                val fontSize = if (text.length > 9) 14.sp else 20.sp // Adatta la dimensione
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.titleLarge.copy(fontSize = fontSize),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
                else if (Page.ArtworkPage.route == navBackStackEntry?.destination?.route) {
                    IconButton(
                        modifier = Modifier.size(size),
                        onClick = {
                            if (!audioIsStarted) {
                                Log.i(TAG, "Starting Audio")
                                player.prepare()
                                player.play()
                                setAudioPlaying(true)
                                setAudioStarted(true)
                                setRightoButtonState(true)
                            } else {
                                if (audioPlaying) {
                                    player.pause()
                                    setAudioPlaying(false)
                                } else {
                                    setAudioPlaying(true)
                                    player.play()
                                }
                            }
                        },
                    ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    painter = if (audioIsStarted && audioPlaying) {
                                        painterResource(R.drawable.pause)
                                    } else if ( !audioPlaying && audioIsStarted ) {
                                        painterResource(R.drawable.play)
                                    }else {
                                        painterResource(R.drawable.start_audio)
                                    },
                                    contentDescription = "Riddle Page Icon",
                                    modifier = Modifier.padding(offset)
                                )
                            }
                    }
                    if(audioIsStarted){
                        CircularProgressIndicator(
                            progress = {
                                audioProgress.floatValue // Valore di progresso (da 0 a 1)
                            },
                            modifier = Modifier.size(size),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 5.dp,
                            trackColor = MaterialTheme.colorScheme.primary
                        )
                    }

                }
            }
            else {
                Box { }
            }
        }

        //Right Button
        Box (modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ){
            if (rightButtonState) {
                IconButton(
                    modifier = Modifier.size(size),
                    enabled = if (Page.HomePage.route == navBackStackEntry?.destination?.route){
                        tourStarted
                    } else {
                        true
                    },
                    onClick = {
                        if (Page.HomePage.route == navBackStackEntry?.destination?.route) {
                            navController.navigate(Page.RiddlesPage.route)
                        }
                        if (Page.ArtworkPage.route == navBackStackEntry?.destination?.route) {
                            setRightoButtonState(false)
                            setAudioPlaying(false)
                            setAudioStarted(false)
                            audioProgress.floatValue = 0f
                            player.stop()
                            player.seekTo(0)
                        }
                    },
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (Page.HomePage.route == navBackStackEntry?.destination?.route){
                            if (tourStarted){
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.LightGray
                            }
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        contentColor =  if (Page.HomePage.route == navBackStackEntry?.destination?.route){
                            if (tourStarted){
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                Color.Gray
                            }
                        } else {
                            MaterialTheme.colorScheme.onPrimary
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            painter =
                            if (Page.HomePage.route == navBackStackEntry?.destination?.route) {
                                painterResource(id = R.drawable.riddlepagebutton)
                            } else if (Page.ArtworkPage.route == navBackStackEntry?.destination?.route) {
                                painterResource(id = R.drawable.stop)
                            } else {
                                painterResource(id = R.drawable.riddlepagebutton)
                            },
                            contentDescription = "Riddle Page Icon",
                            modifier = Modifier.padding(offset)
                        )
                    }
                }
            }
            else {
                Box { }
            }
        }
    }

}

/*@Preview(showBackground = true, showSystemUi = false)
@Composable
fun BasePagePreview() {
    MyMuseumAdventureTheme {
        BasePage(
            viewModel(),
            player = player
        )
    }
}

*/