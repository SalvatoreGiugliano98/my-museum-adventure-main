package com.PULLSH.mymuseumadventure.themes

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.PULLSH.mymuseumadventure.R
import com.PULLSH.mymuseumadventure.database.DatabaseProvider
import kotlinx.coroutines.launch

class ThemeViewModel (application: Application) : AndroidViewModel(application) {
    private val themeDao = DatabaseProvider.getDatabase(application).themeDao()
    val themes = mutableStateListOf<Theme>()

    init {
        loadThemesFromDatabase()
    }

    private fun loadThemesFromDatabase() {
        viewModelScope.launch {
            //themeDao.deleteAllThemes()
            val savedThemes = themeDao.getAllThemes()
            if (savedThemes.isNotEmpty()) {
                themes.addAll(savedThemes)
            } else {
                val defaultThemes = listOf(
                    Theme(
                        id = 1,
                        title = R.string.horror,
                        description = "",
                        image = R.drawable.horror,
                        color = Color(
                            209,
                            233,
                            248,
                            255
                        ).toArgb()),
                    Theme(
                        id = 2,
                        title = R.string.adventure,
                        description = "",
                        image = R.drawable.avventura,
                        color = Color(
                            218,
                            255,
                            173,
                            255
                        ).toArgb()),
                    Theme(
                        id = 3,
                        title = R.string.romantic,
                        description = "",
                        image = R.drawable.romantico,
                        color = Color(
                            234,
                            135,
                            117,
                            255
                        ).toArgb()),
                    Theme(
                        id = 4,
                        title = R.string.science_fiction,
                        description = "",
                        image = R.drawable.fantascienza,
                        color = Color(
                            163,
                            208,
                            210,
                            255
                        ).toArgb()),
                    Theme(
                        id = 5,
                        title = R.string.comic,
                        description = "",
                        image = R.drawable.felice,
                        color = Color(
                            248,
                            201,
                            87,
                            255
                        ).toArgb()),
                    Theme(
                        id = 6,
                        title = R.string.fantasy,
                        description = "",
                        image = R.drawable.fantasy,
                        color = Color(
                            149,
                            245,
                            221,
                            255
                        ).toArgb())
                )
                themes.addAll(defaultThemes)
                saveThemesToDatabase(defaultThemes)
            }
        }
    }

    fun loadThemes() {
        loadThemesFromDatabase()
    }

    fun toggleSelection(index: Int) {
        val theme = themes[index]
        themes[index] = theme.copy(selected = !theme.selected)
        updateThemeInDatabase(themes[index])
    }

    fun setAllSelected(allSelected: Boolean) {
        themes.replaceAll { it.copy(selected = allSelected) }
        saveThemesToDatabase(themes)
    }

    fun getSelectedThemesTitle(): List<Int> {
        return themes
            .filter { it.selected }
            .map { it.title }
    }

    fun getSelectedThemes(): List<Theme>{
        return themes
            .filter { it.selected }
    }

    fun getTheme(themeId: Int): Theme {
        return themes.find { it.id == themeId } ?: themes[0]
    }

    fun deleteAllThemes() {
        viewModelScope.launch {
            setAllSelected(false)
        }
    }

    fun isThemeSelected(themeId: Int): Boolean{
        val theme = themes.find {
            it.id == themeId && it.selected
        }
        return theme != null
    }

    private fun saveThemesToDatabase(themes: List<Theme>) {
        viewModelScope.launch {
            themeDao.insertThemes(themes)
        }
    }

    private fun updateThemeInDatabase(theme: Theme) {
        viewModelScope.launch {
            themeDao.updateTheme(theme)
        }
    }
}

