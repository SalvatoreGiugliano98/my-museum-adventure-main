package com.PULLSH.mymuseumadventure.artwork

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import com.PULLSH.mymuseumadventure.R
import com.PULLSH.mymuseumadventure.themes.ThemeViewModel
import com.PULLSH.mymuseumadventure.themes.Theme
import com.google.gson.Gson

data class ArtworkWrapper(
    val artworks: List<Artwork>
)

class ArtworkViewModel (application: Application) : AndroidViewModel(application){
    var artworks = mutableStateListOf<Artwork>()

    init {
        val loaded = loadArtworkFromJson()
        artworks.addAll(loaded)
    }
    // Stato dell'artwork selezionato
    var selectedArtworkId by mutableStateOf(0)
        private set

    // Funzione per impostare l'artwork selezionato
    fun setSelectedArtwork(id: Int) {
        Log.d("ArtworkViewModel", "Setting selected artwork to $id")
        selectedArtworkId = id
    }

    fun getSelectedArtwork(): Artwork? {
        return artworks.find { it.id == selectedArtworkId }
    }

    private fun loadArtworkFromJson(): List<Artwork> {
        // Ottieni il file JSON da res/raw
        val resources = getApplication<Application>().resources
        val inputStream = resources.openRawResource(R.raw.artworks)
        val json = inputStream.bufferedReader().use { it.readText() }

        // Usa Gson per deserializzare il JSON
        val gson = Gson()
        val artworkWrapper = gson.fromJson(json, ArtworkWrapper::class.java)
        require(artworkWrapper.artworks.isNotEmpty()) {"Error in loading artworks from JSON"}
        return artworkWrapper.artworks
    }

    fun getTheme(themeId: Int): Theme {
        val themeModel = ThemeViewModel(getApplication())
        val theme = themeModel.getTheme(themeId)
        return theme
    }
}