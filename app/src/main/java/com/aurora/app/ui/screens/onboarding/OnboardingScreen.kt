package com.aurora.app.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aurora.app.R
import com.aurora.app.designsystem.theme.calistogaFontFamily
import com.aurora.app.ui.components.DatePickerSheet
import com.aurora.app.ui.components.StepProgressBar
import com.aurora.app.ui.components.button.AuroraButton
import com.aurora.app.ui.components.button.AuroraOutlinedButton
import com.aurora.app.ui.components.textField.AuroraTextField
import com.aurora.app.ui.components.utils.rememberNotificationPermissionRequester
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DashboardScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.time.LocalDate

@Destination<RootGraph>
@Composable
fun OnboardingScreen(
    navigator: DestinationsNavigator,
    viewModel: OnboardingViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigateToDashboard.collectLatest {
            navigator.popBackStack()
            navigator.navigate(DashboardScreenDestination)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TopSection(
                currentStep = uiState.currentStep,
                totalSteps = uiState.totalSteps,
            )

            RegisterSection(
                sectionIndex = uiState.currentStep,
                genderOptions = uiState.genderOptions,
                onNameSubmit = { viewModel.onNameEntered(it) },
                onDateOfBirthSubmit = { viewModel.onDateOfBirthEntered(it) },
                onGenderSubmit = { viewModel.onGenderEntered(it) },
                onNotificationSubmit = { viewModel.onNotificationPermissionGranted() },
                onFinish = { viewModel.onFinish() }
            )
        }
    }
}


@Composable
fun TopSection(
    modifier: Modifier = Modifier,
    currentStep: Int,
    totalSteps: Int
) {

    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {

        Image(
            modifier = Modifier.matchParentSize(),
            painter = painterResource(id = R.drawable.bg_gradient),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(
                modifier = Modifier
                    .windowInsetsTopHeight(WindowInsets.statusBars)
            )
            Text(
                text = "TAR🌓T",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    letterSpacing = 6.sp
                ),
                fontFamily = calistogaFontFamily()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "LET US MAKE YOUR\nPREDICTIONS",
                textAlign = TextAlign.Center,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(24.dp))

            StepProgressBar(
                totalSteps = totalSteps,
                currentStep = currentStep,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(36.dp))
        }


        Spacer(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(24.dp)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
        )

    }
}

@Composable
fun RegisterSection(
    sectionIndex: Int,
    genderOptions: List<String>,
    onNameSubmit: (String) -> Unit,
    onDateOfBirthSubmit: (String) -> Unit,
    onGenderSubmit: (String) -> Unit,
    onNotificationSubmit: () -> Unit,
    onFinish: () -> Unit
) {
    when (sectionIndex) {
        0 -> NameSection(onSubmitClick = onNameSubmit)
        1 -> DateOfBirthSection(onSubmitClick = onDateOfBirthSubmit)
        2 -> GenderSection(genderOptions = genderOptions, onSubmitClick = onGenderSubmit)
        3 -> NotificationSection(onSubmitClick = onNotificationSubmit)
        4 -> FinishSetupSection(onFinishClick = onFinish)
        else -> {
            Text(
                text = "Unexpected section index: $sectionIndex",
                color = Color.Red,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun NameSection(
    modifier: Modifier = Modifier,
    onSubmitClick: (name: String) -> Unit = {}
) {

    val nameState = remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Welcome to Daily Tarot!\nLet's get to know each other",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(48.dp))

        AuroraTextField(
            value = nameState.value,
            onValueChange = { nameState.value = it },
            placeholder = "Enter your name",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            errorMessage = errorMessage
        )

        AuroraButton(
            loading = false,
            text = "SUBMIT",
            onClick = {
                if (nameState.value.isEmpty()) {
                    errorMessage = "Please enter your name"
                } else {
                    errorMessage = ""
                    onSubmitClick(nameState.value)
                }
            },
        )

    }
}

@Composable
fun DateOfBirthSection(
    modifier: Modifier = Modifier,
    onSubmitClick: (date: String) -> Unit = {}
) {
    var showSheet by remember { mutableStateOf(false) }
    var pickedDate by remember { mutableStateOf<LocalDate?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Select your date of birth",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Let the stars guide us to your Sun and Moon signs",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuroraTextField(
            value = pickedDate?.toString() ?: "",
            onValueChange = {},
            placeholder = "Pick a date",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            enabled = false,
            errorMessage = errorMessage,
            onClick = {
                showSheet = true
                Timber.e("Date Picker Clicked")
            }
        )

        AuroraButton(
            text = "SUBMIT",
            onClick = {
                if (pickedDate != null) {
                    errorMessage = ""
                    onSubmitClick(pickedDate.toString())
                } else {
                    errorMessage = "Please select a date"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        )

        DatePickerSheet(
            show = showSheet,
            onDismiss = { showSheet = false },
            initialDate = pickedDate ?: LocalDate.now(),
            onDateSelected = { date ->
                pickedDate = date
            }
        )

    }
}

@Composable
fun GenderSection(
    modifier: Modifier = Modifier,
    genderOptions: List<String>,
    onSubmitClick: (gender: String) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Let us know your gender\nidentity",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "to determine your astrological signs and\npredictions",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(24.dp))

        repeat(genderOptions.size) { index ->
            AuroraButton(
                text = genderOptions[index].uppercase(),
                onClick = { onSubmitClick(genderOptions[index]) },
            )
        }
    }
}


@Composable
fun NotificationSection(
    modifier: Modifier = Modifier,
    onSubmitClick: () -> Unit = {}
) {

    val context = LocalContext.current

    val requestNotificationPermission = rememberNotificationPermissionRequester(
        context = context,
        onPermissionResult = { isGranted ->
            if (isGranted) {
                // Optional: Show a confirmation or toast
                onSubmitClick()
            } else {
                // Optional: Show denied message
            }
        }
    )


    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Stay connected with daily\nupdates",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "This is to remind you of new predictions, daily tarot readings, and updates.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(24.dp))

        AuroraButton(
            text = "YES, SEND ME NOTIFICATIONS",
            onClick = { requestNotificationPermission() },
        )

        AuroraOutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            text = "NO, THANK YOU",
            onClick = {  },
        )
    }
}

@Composable
fun FinishSetupSection(
    modifier: Modifier = Modifier,
    onFinishClick: () -> Unit = {}
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(1f)
        ) {

            Image(
                modifier = Modifier.matchParentSize(),
                painter = painterResource(id = R.drawable.ic_tarot_bg),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.background.copy(alpha = 0.6f)
                            )
                        )
                    )
            )
        }

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Your Tarot journey begins now.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Start your journey with Daily Tarot.",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "The answers you seek may be closer than you think.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuroraButton(
                text = "GET A TAROT READING",
                onClick = { onFinishClick() },
            )

        }
    }
}
