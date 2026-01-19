package com.PULLSH.mymuseumadventure.riddles

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.PULLSH.mymuseumadventure.R
import java.util.Locale

val medalResourceMap = mapOf(
    1 to R.drawable.medal_romantic,
    2 to R.drawable.medal_fantastic,
    3 to R.drawable.medal_horror,
    4 to R.drawable.medal_adventure,
    5 to R.drawable.medal_scifi,
    6 to R.drawable.medal_comic,
    // Aggiungi altre associazioni se necessario
)

@Entity(tableName = "riddles")
data class Riddle(
    @PrimaryKey val id:Int,
    val titleEn: String,
    val titleIt: String,
    val descriptionEn: String,
    val descriptionIt: String,
    val medal: Int,
    val artworkID: Int,
    var completed: Boolean = false,
) {
    companion object {
        fun fromArray(array: Array<Any>): Riddle? {
            if(array.isEmpty()){
                return null
            }
            // Validazione della dimensione e del tipo dei dati
            require(array.size == 8) { "Array must have exactly 8 elements (num elements found:"+ array.size +")" }
            require(array[0] is Int) { "First element must be an Int (id)" }
            require(array[1] is String) { "Second element must be a String (titleEn)" }
            require(array[2] is String) { "Second element must be a String (titleIt)" }
            require(array[3] is String) { "Third element must be a String (descriptionEn)" }
            require(array[4] is String) { "Fourth element must be a String (descriptionIt)"}
            require(array[5] is Int) { "Fourth element must be a String (medal)" }
            require(array[6] is Int) { "Fifth element must be a String (artworkID)" }
            require(array[7] is Boolean) { "Fifth element must be a Boolean (completed)" }

            // Creazione di un oggetto Riddle
            return Riddle(
                id = array[0] as Int,
                titleEn = array[1] as String,
                titleIt = array[2] as String,
                descriptionEn = array[3] as String,
                descriptionIt = array[4] as String,
                medal = array[5] as Int,
                artworkID = array[6] as Int,
                completed = array[7] as Boolean
            )
        }
    }
    fun toArray(): Array<Any> {
        return arrayOf(
            id,
            titleEn,
            titleIt,
            descriptionEn,
            descriptionIt,
            medal,
            artworkID,
            completed
        )
    }
    fun getMedalDrawable(medalId: Int): Int {
        return medalResourceMap[medalId] ?: R.drawable.ic_launcher_foreground // Fallback su un'immagine di default
    }
    fun getTitle(): String{
        val currentLocale = Locale.getDefault()
        val language = currentLocale.language
        return when (language) {
            "it" -> {
                titleIt
            }
            "en" -> {
                titleEn
            } else -> {
                titleEn
            }
        }
    }
}

