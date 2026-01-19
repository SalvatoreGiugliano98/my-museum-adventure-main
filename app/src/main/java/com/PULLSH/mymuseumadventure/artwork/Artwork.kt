package com.PULLSH.mymuseumadventure.artwork

import com.PULLSH.mymuseumadventure.R
import java.util.Locale

val artworkResourceMap = mapOf(
    1 to R.drawable.gioconda,
    2 to R.drawable.nike,
    3 to R.drawable.zattera,
    4 to R.drawable.viandante,
    5 to R.drawable.grandprix,
    6 to R.drawable.mickey,
    7 to R.drawable.notte_stellata
    // Aggiungi altre associazioni se necessario
)


val artworkAudioResourceMap: Map<Int, Pair<Int,Int> > = mapOf(
    1 to Pair(R.raw.gioconda_eng,R.raw.gioconda_ita),
    2 to Pair(R.raw.nike_eng,R.raw.nike_ita),
    3 to Pair(R.raw.zattera_eng,R.raw.zattera_ita),
    4 to Pair(R.raw.viandante_eng,R.raw.viandante_ita),
    5 to Pair(R.raw.lunar_eng,R.raw.lunar_ita),
    6 to Pair(R.raw.mickey_mouse_eng,R.raw.mickey_mouse_ita),
    7 to Pair(R.raw.notte_stellata_eng,R.raw.notte_stellata_ita)
)


data class Artwork(
    val id: Int,
    val titleEn: String,
    val titleIt: String,
    val artist: String,
    val image: Int,
    val descriptionEn: String,
    val descriptionIt: String,
    val year: String,
    val themeID: Int,
    val titleSynonyms: List<String>  // Campo aggiunto per i sinonimi del titolo
) {
    fun getArtworkImage(): Int{
        return artworkResourceMap[image] ?: R.drawable.ic_launcher_foreground
    }
    fun getArtworkAudio(): Int {

        val currentLocale = Locale.getDefault()
        val language = currentLocale.language
        val audio = when (language) {
            "it" -> {
                 artworkAudioResourceMap[id]?.second ?: R.raw.prova
            }
            "en" -> {
                 artworkAudioResourceMap[id]?.first ?: R.raw.prova
            }

            else -> {
                 artworkAudioResourceMap[id]?.first ?: R.raw.prova
            }
        }
        return audio
    }
    fun getArtworkTitle(): String{
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