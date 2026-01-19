package com.PULLSH.mymuseumadventure.zone

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.PULLSH.mymuseumadventure.R
import com.PULLSH.mymuseumadventure.database.DatabaseProvider
import com.google.gson.Gson
import kotlinx.coroutines.launch


data class ZoneWrapper(
    val zones: List<Zone>
)

class ZoneViewModel(application: Application) : AndroidViewModel(application) {
    private val zoneDao = DatabaseProvider.getDatabase(application).zoneDao()
    var zones = mutableStateListOf<Zone>()
    var selectedZone: Zone? = null
    init {
        loadFromDatabase()
    }

    private fun loadFromDatabase() {
        viewModelScope.launch {
            zoneDao.deleteAllZones()
            val savedZones = zoneDao.getAllZones()
            if (savedZones.isNotEmpty()) {
                zones.addAll(savedZones)
            } else {
                val defaultZones = loadZoneFromJson()
                zones.addAll(defaultZones)
                saveZoneToDatabase(defaultZones)
            }
        }
    }

    private fun loadZoneFromJson(): List<Zone> {
        // Ottieni il file JSON da res/raw
        val resources = getApplication<Application>().resources
        val inputStream = resources.openRawResource(R.raw.zone)
        val json = inputStream.bufferedReader().use { it.readText() }

        // Usa Gson per deserializzare il JSON
        val gson = Gson()
        val zonesWrapper = gson.fromJson(json, ZoneWrapper::class.java)
        require(zonesWrapper.zones.isNotEmpty()) {"Error in loading zones from JSON"}
        return zonesWrapper.zones
    }

    private fun saveZoneToDatabase(zones: List<Zone>) {
        viewModelScope.launch {
            zoneDao.insertZones(zones)
        }
    }

    private fun updateZoneInDatabase(zone: Zone) {
        viewModelScope.launch {
            zoneDao.updateZone(zone)
        }
    }

    private fun deleteZoneFromDatabase(zone: Zone) {
        viewModelScope.launch {
            zoneDao.deleteZone(zone)
        }
    }

    fun updateScore(zoneId: Int, newScore: Int) {
        zones.forEach() {
            if (it.id == zoneId) {
                it.score = newScore
                updateZoneInDatabase(it)
            }
        }
    }

    fun removeZone(zoneId: Int) {
        zones.forEach(){
            if (it.id == zoneId){
                it.shown = false
                updateZoneInDatabase(it)
            }
        }
    }

    fun resetAllZones(){
        zones.forEach{
            it.shown = true
            it.score = 5
            updateZoneInDatabase(it)
        }
    }
}