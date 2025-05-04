package com.aurora.app.ui.screens.cardDetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aurora.app.domain.model.TarotCard
import com.aurora.app.domain.model.spread.Property
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.utils.AssetImageLoader
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun CardDetailScreen(
    tarotCard: TarotCard,
    navigator: DestinationsNavigator,
    viewModel: CardDetailViewModel = hiltViewModel()) {

    LaunchedEffect(Unit) {
        viewModel.initialSetup(tarotCard)
    }

    val state = viewModel.uiState.value
    Scaffold(
        topBar = {
            AuroraTopBar(
                text = state.title,
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = { navigator.navigateUp() })
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CardDetailImage(imagePath = state.imagePath, title = state.title, modifier = Modifier.height(260.dp))
            Spacer(modifier = Modifier.height(16.dp))
            CardTagRow(tags = state.tags)
            Spacer(modifier = Modifier.height(16.dp))
            CardAffirmation(affirmation = state.affirmation)
            CardDescription(description = state.description)
            Spacer(modifier = Modifier.height(24.dp))
            CardPropertiesSection(properties = state.properties)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailTopBar(title: String, onBackClick: () -> Unit = {}) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
        navigationIcon = { Icons.AutoMirrored.Filled.ArrowBack },
        actions = { onBackClick()}
    )
}

@Composable
fun CardDetailImage(modifier: Modifier, imagePath: String, title: String) {
    val context = LocalContext.current
    val bitmap by remember(imagePath) {
        mutableStateOf(AssetImageLoader.loadBitmapFromAsset(context, imagePath))
    }

    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = "$title Tarot Card",
            modifier = modifier
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun CardTagRow(tags: List<String>) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        tags.forEach { CardTag(it) }
    }
}

@Composable
fun CardAffirmation(affirmation: String) {
    Text(
        text = "✦ AFFIRMATION ✦",
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 14.sp
    )
    Text(
        text = affirmation,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@Composable
fun CardDescription(description: String) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier

        )
    }

}

@Composable
fun CardTag(text: String) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.White
        )
    }
}

@Composable
fun CardPropertiesSection(properties: List<Property>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val grouped = properties.chunked(3)
        grouped.forEach { group ->
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items(group) { prop ->
                    PropertyCard(title = prop.title, value = prop.value)
                }
            }
        }
    }
}

@Composable
fun PropertyCard(modifier: Modifier = Modifier, title: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(text = title, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}