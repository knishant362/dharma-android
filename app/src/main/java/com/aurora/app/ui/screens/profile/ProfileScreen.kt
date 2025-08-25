package com.aurora.app.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aurora.app.R
import com.aurora.app.ui.components.AuroraTopBar
import com.aurora.app.ui.components.button.AuroraButton
import com.aurora.app.ui.components.textField.AuroraTextField
import com.aurora.app.ui.navigation.ScreenTransition
import com.aurora.app.utils.showToast
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>(style = ScreenTransition::class)
@Composable
fun ProfileScreen(
    navigator: DestinationsNavigator,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val profile by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProfileEvent.ShowToast -> {
                    context.showToast(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AuroraTopBar(
                text = "Profile",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = { navigator.navigateUp() }
            )
        }
    ) { contentPadding ->

        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.bg_land),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)

            ) {

                AuroraTextField(
                    value = profile.name,
                    onValueChange = { viewModel.onFieldChange(profile.copy(name = it)) },
                    placeholder = "Name",
                    errorMessage = profile.errors["name"].orEmpty()
                )

                AuroraTextField(
                    value = profile.gender,
                    onValueChange = { viewModel.onFieldChange(profile.copy(gender = it)) },
                    placeholder = "Gender",
                    enabled = false,
                    errorMessage = profile.errors["gender"].orEmpty()
                )

                AuroraTextField(
                    value = profile.dateOfBirth,
                    onValueChange = { viewModel.onFieldChange(profile.copy(dateOfBirth = it)) },
                    placeholder = "Date of Birth",
                    enabled = false,
                    errorMessage = profile.errors["dob"].orEmpty()
                )

                AuroraTextField(
                    value = profile.relationshipStatus,
                    onValueChange = { viewModel.onFieldChange(profile.copy(relationshipStatus = it)) },
                    placeholder = "Relationship Status",
                    enabled = false,
                    errorMessage = profile.errors["relationship"].orEmpty()
                )
                AuroraTextField(
                    value = profile.occupation,
                    onValueChange = { viewModel.onFieldChange(profile.copy(occupation = it)) },
                    placeholder = "Occupation",
                    enabled = false,
                    errorMessage = profile.errors["occupation"].orEmpty()
                )

                AuroraButton(
                    text = "Save",
                    onClick = {
                        viewModel.saveProfile()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )

            }

        }

    }
}
