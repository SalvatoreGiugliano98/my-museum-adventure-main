package com.PULLSH.mymuseumadventure.zone

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.PULLSH.mymuseumadventure.R
import com.PULLSH.mymuseumadventure.components.AlertDialog
import com.PULLSH.mymuseumadventure.themes.ThemeViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZonePageModal(
    zoneViewModel: ZoneViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel(),
    showSheet: MutableState<Boolean>,
    setShowDialog: (Boolean) -> Unit = {},
    onDismiss: () -> Unit
){
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showSendAlert by remember { mutableStateOf(false) }
    val selectedZone = zoneViewModel.selectedZone
    if (selectedZone!= null){
        val sliderValue = remember { mutableFloatStateOf(selectedZone.score.toFloat()) }
        if (showSheet.value) {
            ModalBottomSheet(
                onDismissRequest = {
                    coroutineScope.launch {
                        sheetState.hide() // Nasconde con animazione
                        showSheet.value = false
                        onDismiss() // Chiude dopo l'animazione
                    }
                },
                sheetState = sheetState,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.zone) + " " + selectedZone.id.toString(),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Row (
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(
                                horizontal = 16.dp
                            )
                    ) {
                        Text(
                            text = stringResource(R.string.this_zone_is) + " " +  stringResource(themeViewModel.getTheme(selectedZone.theme).title),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row (
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(
                                horizontal = 16.dp
                            )
                    ) {
                        Text(
                            text = stringResource(R.string.which_score_do_you_want_to_give_to_this_zone),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp
                            )
                    ) {
                        Text(
                            text = "0",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "1",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "2",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "3",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "4",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "5",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Slider che va da 0 a 5, con valori interi
                    Slider(
                        value = sliderValue.value,
                        onValueChange = { newValue ->
                            sliderValue.value =
                                newValue // Limita tra 0 e 5
                        },
                        valueRange = 0f..5f, // Impostiamo il range da 0 a 5
                        steps = 4, // 4 step per avere 5 valori (0, 1, 2, 3, 4, 5)
                        onValueChangeFinished = {
                            // Convertiamo il valore del slider in un intero
                            //sliderValue.intValue
                        },
                        modifier = Modifier.padding(
                            horizontal = 16.dp
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            if(sliderValue.floatValue.toInt() < 5){
                                zoneViewModel.selectedZone!!.score = sliderValue.floatValue.toInt()
                                showSendAlert = true
                            } else {
                                zoneViewModel.selectedZone = null
                                coroutineScope.launch {
                                    sheetState.hide() // Chiudi con animazione
                                    showSheet.value = false
                                    onDismiss()
                                }
                            }

                        },
                    ) {
                        Text(stringResource(R.string.send_report))
                    }
                }
            }
        }
    }
    if (showSendAlert){
        AlertDialog(
            onDismissRequest = {
                showSendAlert = false
                zoneViewModel.selectedZone?.let {
                    it.score = 5
                }
                zoneViewModel.selectedZone = null
                coroutineScope.launch {
                    sheetState.hide() // Chiudi con animazione
                    showSheet.value = false
                    onDismiss()
                }
            },
            onConfirmation = {
                showSendAlert = false
                zoneViewModel.selectedZone?.let {
                    zoneViewModel.updateScore(
                        it.id,
                        it.score
                    )
                    zoneViewModel.removeZone(
                        it.id
                    )
                }
                coroutineScope.launch {
                    sheetState.hide() // Chiudi con animazione
                    showSheet.value = false
                    onDismiss()
                }
            },
            dialogTitle = stringResource(R.string.are_you_sure_you_want_to_report_this_zone),
            dialogText = stringResource(R.string.the_zone) + " " + zoneViewModel.selectedZone?.id + " " + stringResource(
                R.string.will_be_removed_from_the_map
            ),
            labelConfirm = stringResource(R.string.ok),
            labelDismiss = stringResource(R.string.cancel)
        )
    }

}