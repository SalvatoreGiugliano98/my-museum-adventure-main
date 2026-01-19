package com.PULLSH.mymuseumadventure.riddles

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonColors
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.PULLSH.mymuseumadventure.R
import com.PULLSH.mymuseumadventure.jetpacknavigation.Page
import com.PULLSH.mymuseumadventure.themes.ThemeViewModel
import com.PULLSH.mymuseumadventure.ui.theme.MyMuseumAdventureTheme
import com.PULLSH.mymuseumadventure.ui.theme.segmentedButtonColors
import java.util.Locale

@Composable
fun RiddlesPage (
    viewModel: RiddleViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel(),
    navController: NavController,
    setSingleRiddle: (Riddle) -> Unit = {}
) {
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    val changeSelectedIndex: (Int) -> Unit = {
            index -> selectedIndex = index
    }

    Column (modifier = Modifier
        .fillMaxSize()
        .padding(16.dp))
    {

        SegmentedButtonSingleSelect(
            selectedIndex,
            changeSelectedIndex
        )

        Spacer(modifier = Modifier.height(12.dp))

        RiddleLazyVerticalGrid(index = selectedIndex,
            riddleViewModel = viewModel,
            themeViewModel = themeViewModel,
            navController = navController,
            setRiddle = setSingleRiddle
        )
    }
}

@Composable
fun RiddleLazyVerticalGrid (
    index: Int,
    riddleViewModel: RiddleViewModel,
    themeViewModel: ThemeViewModel,
    setRiddle: (Riddle) -> Unit = {},
    navController: NavController
) {
    val riddleList = when (index) {
        0 -> {
            riddleViewModel.riddles
        }
        1 -> {
            riddleViewModel.getCompletedRiddles()
        }
        else -> {
            riddleViewModel.getNotCompletedRiddles()
        }
    }
    if (riddleList.isNotEmpty()){
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {

            val currentLocale = Locale.getDefault()
            val language = currentLocale.language
            items(riddleList){ riddle ->
                val description = when (language) {
                    "it" -> {
                        riddle.descriptionIt
                    }
                    "en" -> {
                        riddle.descriptionEn
                    }
                    else -> {
                        riddle.descriptionEn
                    }
                }
                RiddleCard(
                    title = riddle.getTitle(),
                    description = description,
                    completed = riddle.completed,
                    onClick = {
                        setRiddle(riddle)
                        navController.navigate(Page.SingleRiddlePage.route)
                    }
                )
            }
        }
    }
    else {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.no_riddles_found),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RiddleCard (
    title: String,
    description: String,
    completed: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f)
            .clickable(onClick = onClick)
            .border(width = 0.dp, color = Color.Transparent, shape = RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .background(color = if (!completed) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            val color = if (!completed) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSecondary
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Left,
                color = color
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Left,
                color = color,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun SegmentedButtonSingleSelect(
    selectedIndex: Int,
    changeSelectedIndex: (index:Int) -> Unit = {}
) {
    val options = listOf(
        stringResource(R.string.all),
        stringResource(R.string.completed),
        stringResource(R.string.not_completed)
    )
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = {changeSelectedIndex(index)},
                selected = index == selectedIndex,
                colors = segmentedButtonColors
            ) {
                val fontSize = if (label.length >10) 12.sp else 14.sp
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize= fontSize),
                    modifier = Modifier,
                    textAlign = TextAlign.Left,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RiddlesPagePreview () {
    MyMuseumAdventureTheme {
        RiddlesPage(
            navController = rememberNavController(),
        )
    }
}

