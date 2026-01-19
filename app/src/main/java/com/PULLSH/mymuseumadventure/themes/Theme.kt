package com.PULLSH.mymuseumadventure.themes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "themes")
data class Theme(
    @PrimaryKey val id: Int,
    val title: Int,
    val description: String,
    val image: Int,
    val color: Int, // Usato come valore ARGB
    var selected: Boolean = false
)