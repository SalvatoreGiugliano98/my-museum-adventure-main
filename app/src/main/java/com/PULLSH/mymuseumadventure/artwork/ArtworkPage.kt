package com.PULLSH.mymuseumadventure.artwork

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.PULLSH.mymuseumadventure.R
import com.PULLSH.mymuseumadventure.riddles.Riddle
import com.PULLSH.mymuseumadventure.riddles.RiddleViewModel
import com.PULLSH.mymuseumadventure.themes.ThemeViewModel
import java.util.Locale

@Composable
fun ArtworkPage (
    themeViewModel: ThemeViewModel = viewModel(),
    selectedRiddle: Riddle? = null,
    artwork: Artwork,
    getFromRiddleToArtwork: () -> Boolean = {false},
    setShowDialogRiddleSolved: (Boolean) -> Unit = {},
    player: ExoPlayer
) {

    val rawResourceUri = "android.resource://com.PULLSH.mymuseumadventure/${artwork.getArtworkAudio()}"
    val mediaItem = MediaItem.fromUri(rawResourceUri)
    player.setMediaItem(mediaItem)

    if (getFromRiddleToArtwork()) {
        if (selectedRiddle != null) {
            if (!selectedRiddle.completed) {
                setShowDialogRiddleSolved(true)
            }
        }
    }
    Column (
        modifier = Modifier
            .background(Color.Transparent)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 2.dp,
                    vertical = 10.dp
                ),
            verticalAlignment = Alignment.CenterVertically // Allinea immagine e testo al centro verticalmente
        ) {
            Image(
                painter = painterResource(id = artwork.getArtworkImage()), // Sostituisci con la tua immagine
                contentDescription = artwork.titleEn,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp)) // Angoli arrotondati
                    .border(3.dp, Color.Black, RoundedCornerShape(8.dp))
                    .weight(1f)
            )

            Spacer(modifier = Modifier.width(10.dp)) // Spaziatura tra immagine e testo

            Column (
                modifier = Modifier.weight(1f)
            )
            {

                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top, // Allinea il testo in alto
                    horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = stringResource(R.string.artist) +": ",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.width(80.dp) // Mantiene allineata la colonna dei titoli
                    )
                    Text(
                        text = artwork.artist,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = stringResource(R.string.year) + ": ",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.width(80.dp)
                    )
                    Text(
                        text = artwork.year,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = stringResource(R.string.title_page_themes) +": ",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.width(80.dp)
                    )
                    val theme = themeViewModel.getTheme(artwork.themeID)
                    Text(
                        text = stringResource(theme.title),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = stringResource(R.string.description),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            val currentLocale = Locale.getDefault()
            val language = currentLocale.language
            val description = when (language) {
                "it" -> {
                    artwork.descriptionIt
                }
                "en" -> {
                    artwork.descriptionEn
                }
                else -> {
                    artwork.descriptionEn
                }
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(160.dp))
        }

    }

}