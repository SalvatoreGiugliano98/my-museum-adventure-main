package com.PULLSH.mymuseumadventure.jetpacknavigation

// una sealed class è una classe le cui sottoclassi sono conosciute a compile time
sealed class Page (val route: String){
    data object HomePage : Page("home_page")
    data object RiddlesPage : Page("riddles_page")
    data object SingleRiddlePage : Page("riddle_page")
    data object PreferencesPage : Page("preferences_page")
    data object CameraPage:Page("camera_page")
    data object ArtworkPage:Page("artwork_page")
    data object ZonePage:Page("zone_page")
    /*fun withArg(vararg args: Any): String{
        return buildString{
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }*/
}