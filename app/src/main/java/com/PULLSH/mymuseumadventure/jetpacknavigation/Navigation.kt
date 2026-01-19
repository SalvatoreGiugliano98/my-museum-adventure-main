package com.PULLSH.mymuseumadventure.jetpacknavigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.PULLSH.mymuseumadventure.artwork.Artwork
import com.PULLSH.mymuseumadventure.artwork.ArtworkPage
import com.PULLSH.mymuseumadventure.artwork.ArtworkViewModel
import com.PULLSH.mymuseumadventure.camera.CameraPage
import com.PULLSH.mymuseumadventure.mainView.MainView
import com.PULLSH.mymuseumadventure.riddles.Riddle
import com.PULLSH.mymuseumadventure.riddles.RiddleViewModel
import com.PULLSH.mymuseumadventure.riddles.RiddlesPage
import com.PULLSH.mymuseumadventure.riddles.SingleRiddlePage
import com.PULLSH.mymuseumadventure.themes.ThemeViewModel
import com.PULLSH.mymuseumadventure.themes.ThemesPage
import com.PULLSH.mymuseumadventure.zone.ZoneViewModel


@Composable
fun Navigation(
    modifier: Modifier=Modifier,
    navController: NavHostController,
    tourStarted: Boolean,
    showDialogBackButton: Boolean,
    showDialogStartTour: Boolean,

    setTourStarted:(Boolean) -> Unit = {},
    setShowBottomBar: (Boolean) -> Unit = {},
    setShowDialogBackButton: (Boolean) -> Unit = {},
    setShowDialogStartTour: (Boolean) -> Unit = {},
    setShowDialogRiddleSolved: (Boolean)->Unit = {},
    setSingleRiddle: (Riddle) -> Unit = {},
    setLeftButtonState: (Boolean) -> Unit = {},
    setShowBottomBarBackground: (Boolean) -> Unit = {},
    setCenterButtonState: (Boolean) -> Unit = {},
    setRightButtonState: (Boolean) -> Unit = {},
    setFromRiddleToArtwork: (Boolean) -> Unit = {},
    setShowZoneDialog:(Boolean) -> Unit = {},
    getSelectedArtwork: () -> Artwork? = {
        Artwork(
            id = 0,
            titleEn = "",
            titleIt = "",
            descriptionEn = "",
            descriptionIt = "",
            image = 0,
            artist = "",
            year = "",
            themeID = 0,
            titleSynonyms = listOf()
        )
    },
    getSingleRiddle: () -> Riddle? = {
        Riddle(
            id = 0,
            titleEn = "",
            titleIt = "",
            descriptionEn = "",
            descriptionIt = "",
            medal = 0,
            artworkID = 0,
            completed = false
        )
    },
    getFromRiddleToArtwork: () -> Boolean = {false},

    themeViewModel: ThemeViewModel = viewModel(),
    riddleViewModel: RiddleViewModel = viewModel(),
    artworkViewModel: ArtworkViewModel = viewModel(),
    zoneViewModel: ZoneViewModel = viewModel(),
    player: ExoPlayer
) {
    NavHost(navController = navController, startDestination = Page.HomePage.route, modifier = modifier){

        composable(route = Page.HomePage.route
        ){
            setShowBottomBar(true)
            setShowBottomBarBackground(true)
            setCenterButtonState(true)
            setLeftButtonState(true)
            setRightButtonState(true)
            MainView(
                tourStarted = tourStarted,
                themeViewModel = themeViewModel,
                zoneViewModel = zoneViewModel,
                navController = navController,
                setShowZoneDialog = setShowZoneDialog
            )
        }
        composable(
            route = Page.PreferencesPage.route,
        ){
            setShowBottomBar(true)
            setShowBottomBarBackground(true)
            setLeftButtonState(false)
            setRightButtonState(false)
            ThemesPage(
                viewModel = themeViewModel,
                navController = navController,
                showDialogBackButton = showDialogBackButton,
                showDialogStartTour = showDialogStartTour,
                setShowDialogBackButton = setShowDialogBackButton,
                setShowDialogStartTour = setShowDialogStartTour,
                setTourStarted = setTourStarted,
                riddleViewModel = riddleViewModel,
                artworkViewModel = artworkViewModel
            )
        }
        composable(
            route = Page.RiddlesPage.route,
        ){
            setShowBottomBar(false)
            setShowBottomBarBackground(false)
            RiddlesPage(
                viewModel = riddleViewModel,
                navController = navController,
                setSingleRiddle = setSingleRiddle,
                themeViewModel = themeViewModel
            )
        }
        composable(
            route = Page.SingleRiddlePage.route
        ) {
            setShowBottomBar(true)
            setLeftButtonState(true)
            setCenterButtonState(false)
            setRightButtonState(false)
            setShowBottomBarBackground(false)
            val riddle = getSingleRiddle()
            SingleRiddlePage(
                selectedRiddle = riddle,
                navController = navController,
            )
        }
        composable(
            route = Page.CameraPage.route
        ){
            setShowBottomBar(false)
            setShowBottomBarBackground(false)
            CameraPage(
                navController=navController,
                artworkViewModel = artworkViewModel,
                setFromRiddleToArtwork = setFromRiddleToArtwork
            )
        }
        composable(
            route = Page.ArtworkPage.route
        ) {
            setShowBottomBar(true)
            setShowBottomBarBackground(true)
            setCenterButtonState(true)
            if (navController.previousBackStackEntry?.destination?.route == Page.SingleRiddlePage.route) {
                setLeftButtonState(false)
            } else {
                setLeftButtonState(true)
            }
            setRightButtonState(false)
            getSelectedArtwork()?.let { it1 ->
                ArtworkPage(
                    themeViewModel = themeViewModel,
                    artwork = it1,
                    setShowDialogRiddleSolved = setShowDialogRiddleSolved,
                    getFromRiddleToArtwork = getFromRiddleToArtwork,
                    selectedRiddle = getSingleRiddle(),
                    player = player
                )
            }
        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun NavigationPreview() {
    MyMuseumAdventureTheme {
        Navigation(
            navController = rememberNavController(),
            showDialogBackButton = false,
            showDialogStartTour = false,
            tourStarted = false,
        )
    }
}*/