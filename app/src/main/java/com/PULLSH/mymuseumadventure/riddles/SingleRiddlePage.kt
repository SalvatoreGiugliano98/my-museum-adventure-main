package com.PULLSH.mymuseumadventure.riddles

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.PULLSH.mymuseumadventure.R
import com.PULLSH.mymuseumadventure.ui.theme.MyMuseumAdventureTheme
import java.util.Locale


@Composable
fun SingleRiddlePage (
    navController: NavController,
    selectedRiddle: Riddle?,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackHandler(true) {
            navController.popBackStack()
        }
        if(selectedRiddle == null){
            return
        }

        val model = if (!selectedRiddle.completed) {
            R.drawable.medal_blank
        } else {
            selectedRiddle.getMedalDrawable(selectedRiddle.medal)
        }
        // Usa Coil per caricare l'immagine dal URI
        val painter = rememberAsyncImagePainter(
            model = model,
            error = painterResource(R.drawable.ic_launcher_foreground)             // Un'immagine di fallback in caso di errore
        )

        val medalDesc = if (!selectedRiddle.completed) {
            "medalNotCompleted"
        } else {
            "medalCompleted"
        }

        Image(
            painter = painter,
            contentDescription = medalDesc,
            modifier = Modifier.height((LocalConfiguration.current.screenHeightDp/3).dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = selectedRiddle.getTitle(),
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        val currentLocale = Locale.getDefault()
        val language = currentLocale.language
        val description = when (language) {
            "it" -> {
                selectedRiddle.descriptionIt
            }
            "en" -> {
                selectedRiddle.descriptionEn
            }
            else -> {
                selectedRiddle.descriptionEn
            }
        }
        Text(
            text = description,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Left,
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun SingleRiddlePagePreview () {
    MyMuseumAdventureTheme {
        SingleRiddlePage(
            navController = rememberNavController(),
            selectedRiddle = Riddle(
                id = 0,
                titleEn = "Title",
                titleIt = "Titolo",
                descriptionEn = "Description",
                descriptionIt = "Descrizione",
                completed = false,
                artworkID = 0,
                medal = 0
            )
        )
    }
}