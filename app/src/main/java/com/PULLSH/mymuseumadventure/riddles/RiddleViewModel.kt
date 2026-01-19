package com.PULLSH.mymuseumadventure.riddles

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.PULLSH.mymuseumadventure.R
import com.PULLSH.mymuseumadventure.artwork.Artwork
import com.PULLSH.mymuseumadventure.database.DatabaseProvider
import com.PULLSH.mymuseumadventure.themes.Theme
import com.google.gson.Gson
import kotlinx.coroutines.launch

data class RiddlesWrapper(
    val riddles: List<Riddle>
)

class RiddleViewModel (application: Application) : AndroidViewModel(application){
    private val riddleDao = DatabaseProvider.getDatabase(application).riddleDao()
    var riddles = mutableStateListOf<Riddle>()

    init {
        loadRiddlesFromDatabase()
    }

    private fun loadRiddlesFromDatabase() {
        viewModelScope.launch {
            val savedRiddles = riddleDao.getAllRiddles()
            if (savedRiddles.isNotEmpty()) {
                riddles.addAll(savedRiddles)
            } else {
                val defaultRiddles = loadRiddlesFromJson()
                riddles.addAll(defaultRiddles)
                saveRiddlesToDatabase(defaultRiddles)
            }
        }
    }
    fun loadRiddle(){
        loadRiddlesFromDatabase()
    }

    private fun loadRiddlesFromJson(): List<Riddle> {
        // Ottieni il file JSON da res/raw
        val resources = getApplication<Application>().resources
        val inputStream = resources.openRawResource(R.raw.default_riddles)
        val json = inputStream.bufferedReader().use { it.readText() }

        // Usa Gson per deserializzare il JSON
        val gson = Gson()
        val riddlesWrapper = gson.fromJson(json, RiddlesWrapper::class.java)
        require(riddlesWrapper.riddles.isNotEmpty()) {"Error in loading riddles from JSON"}
        return riddlesWrapper.riddles
    }


    private fun saveRiddlesToDatabase(riddles: List<Riddle>) {
        viewModelScope.launch {
            riddleDao.insertRiddles(riddles)
        }
    }

    private fun updateRiddleInDatabase(riddle: Riddle) {
        viewModelScope.launch {
            riddleDao.updateRiddle(riddle)
        }
    }

    private fun updateRiddlesInDatabase(riddles: List<Riddle>) {
        viewModelScope.launch {
            riddleDao.updateRiddles(riddles)
        }
    }

    fun getRiddleFromId(id: Int): Riddle? {
        val riddle = riddles.find { it.id == id }
        return riddle
    }

    fun setAllCompleted(allCompleted: Boolean) {
        riddles.forEach(){ riddle ->
            riddle.completed = true
        }
        updateRiddlesInDatabase(riddles)
    }

    fun setCompleted(id: Int) {
        riddles.forEach() { riddle ->
            if (riddle.id == id){
                riddle.completed = true
                updateRiddleInDatabase(riddle)
            }
        }
    }

    fun getCompletedRiddles(): List<Riddle> {
        return riddles.filter{ it.completed }
    }

    fun getNotCompletedRiddles(): List<Riddle> {
        return riddles.filter{ !it.completed }
    }

    fun setRiddlesFromThemes(selectedThemes: List<Theme>, artworks: List<Artwork>){
        val newRiddles = riddles.filter { riddle ->
            val theme = artworks.find { it.id == riddle.artworkID }?.themeID
            selectedThemes.find { it.id == theme } != null
        }
        riddles.clear()
        riddles.addAll(newRiddles)
    }

}