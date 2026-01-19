package com.PULLSH.mymuseumadventure.zone

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.PULLSH.mymuseumadventure.themes.Theme

@Entity
data class Zone(
    @PrimaryKey val id: Int = 0,
    var positionX: Int, // Cambiato a Offset per supportare il movimento
    var positionY: Int,
    var height: Int,
    var width: Int,
    val theme: Int,
    var shown: Boolean,
    var score: Int, //deve essere tra 0 e 5
)
