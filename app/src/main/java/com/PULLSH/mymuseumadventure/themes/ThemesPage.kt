package com.PULLSH.mymuseumadventure.themes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.PULLSH.mymuseumadventure.R
import com.PULLSH.mymuseumadventure.artwork.ArtworkViewModel
import com.PULLSH.mymuseumadventure.components.AlertDialog
import com.PULLSH.mymuseumadventure.jetpacknavigation.Page
import com.PULLSH.mymuseumadventure.riddles.RiddleViewModel
import com.PULLSH.mymuseumadventure.ui.theme.MyMuseumAdventureTheme
import com.PULLSH.mymuseumadventure.ui.theme.segmentedButtonColors

@Composable
fun ThemesPage(
    viewModel: ThemeViewModel = viewModel(),
    riddleViewModel: RiddleViewModel = viewModel(),
    artworkViewModel: ArtworkViewModel = viewModel(),
    navController: NavController,
    showDialogBackButton: Boolean = false,
    showDialogStartTour: Boolean = false,
    setShowDialogBackButton: (Boolean) -> Unit = {},
    setShowDialogStartTour: (Boolean) -> Unit = {},
    setTourStarted: (Boolean) -> Unit = {}
) {
    if (viewModel.themes.isEmpty()){
        viewModel.loadThemes()
    }
    val themes = viewModel.themes
    val context = LocalContext.current
    var selectedIndex by remember { mutableIntStateOf(0) } // indica l'indice del pulsante selezionato: 0 -> Interactive, 1 -> Non-interactive
    val changeSelectedIndex: (Int) -> Unit = { index -> selectedIndex = index }
    val selectedAll = themes.all { it.selected }

    if (showDialogBackButton) {
        AlertDialog(
            // "Confirm" --> torno alla home page
            // dismiss --> rimango nella pagina delle themes
            onDismissRequest = {
                setShowDialogBackButton(false)
            },
            onConfirmation = {
                setShowDialogBackButton(false)
                navController.navigate(Page.HomePage.route)
                viewModel.setAllSelected(false)
            },

            dialogTitle = stringResource(R.string.warning),
            dialogText = stringResource(R.string.backFromPreferencePage),
            labelConfirm = stringResource(R.string.ok),
            labelDismiss = stringResource(R.string.cancel)
        )

    }
    if (showDialogStartTour) {
        AlertDialog(
            // si --> vado alla pagina successiva
            // no --> rimango nella pagina delle themes
            onDismissRequest = {
                setShowDialogStartTour(false)
            },
            onConfirmation = {
                setShowDialogStartTour(false)
                navController.navigate(Page.HomePage.route)
                riddleViewModel.setRiddlesFromThemes(viewModel.getSelectedThemes(), artworkViewModel.artworks)
                setTourStarted(true)
            },
            dialogTitle = stringResource(R.string.start_tour_title),
            dialogText = (if (viewModel.getSelectedThemesTitle().size == 1)
                stringResource(R.string.start_tour_text_1) + "\n"
            else
                stringResource(R.string.start_tour_text_2))
                    + "\n" +
                    viewModel.getSelectedThemesTitle().joinToString(separator = "\n") {
                        context.resources.getString(it)
                    },
            labelConfirm = stringResource(R.string.start_tour_confirmation),
            labelDismiss = stringResource(R.string.start_tour_dismiss)
        )

    }

    BackHandler(enabled = !showDialogBackButton && !showDialogStartTour ) {
        setShowDialogBackButton(true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SegmentedButtonSingleSelect(selectedIndex, changeSelectedIndex)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.setAllSelected(!selectedAll) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedAll) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer,
                contentColor = if (selectedAll) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
            )
        ) {
            val string = if (!selectedAll) stringResource(R.string.Select_All)
            else stringResource(R.string.deselect_all_selected)
            Text(
                text = string,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        //Spacer(modifier = Modifier.height(16.dp))
        PreferencesLazyVerticalGrid(themes, viewModel::toggleSelection)
    }

}

@Composable
fun PreferencesLazyVerticalGrid(themes: List<Theme>, onChangeSelectedPreferences: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(themes.size) { index ->
            val theme = themes[index]
            PreferenceCard(
                title = theme.title,
                image = theme.image,
                color = Color(theme.color),
                isSelected = theme.selected,
                onClick = { onChangeSelectedPreferences(index) }
            )
        }
        // Aggiungi uno Spacer dopo l'ultimo elemento
        item { Spacer(modifier = Modifier.height(160.dp)) }
    }
}


@Composable
fun PreferenceCard(title: Int, image: Int, color: Color, isSelected: Boolean, onClick: () -> Unit) {
    val colorBackground = if (isSelected) color else Color.Transparent
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick)
            .border(width = 0.dp, color = Color.Transparent, shape = RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = colorBackground)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(15.dp)
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = stringResource(id = title),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SegmentedButtonSingleSelect(
    selectedIndex: Int,
    changeSelectedIndex: (index: Int) -> Unit = {}
) {

    val options = listOf(
        stringResource(R.string.interactive),
        stringResource(R.string.non_interactive)
    )
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = { changeSelectedIndex(index) },
                selected = index == selectedIndex,
                colors = segmentedButtonColors
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreferencesPagePreview() {
    MyMuseumAdventureTheme {
        ThemesPage(
            navController = rememberNavController(),
            showDialogBackButton = false
        )
    }
}