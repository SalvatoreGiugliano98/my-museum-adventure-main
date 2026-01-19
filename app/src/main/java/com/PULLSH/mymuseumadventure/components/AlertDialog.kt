package com.PULLSH.mymuseumadventure.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    labelConfirm: String,
    labelDismiss: String = null.toString(),
    //icon: ImageVector,
) {
    AlertDialog(
        /*icon = {
            Icon(icon, contentDescription = "Example Icon")
        },*/
        title = {
            Text(
                text = dialogTitle,
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Text(
                text = dialogText,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(
                    text = labelConfirm
                )
            }
        },
        dismissButton = if (labelDismiss != "null") {
            {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text(
                        text = labelDismiss
                    )
                }
            }
        } else{
            null
        }
    )
}


