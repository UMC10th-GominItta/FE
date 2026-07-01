package com.gominitta.android.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.gominitta.android.ui.theme.spacing

/**
 * Scaffold placeholder for the Home destination.
 *
 * Navigation contract:
 *  - [onNavigateToDetail] is called when the user taps the CTA. The actual
 *    [navController.navigate] call lives in [AppNavHost]; this screen only
 *    knows about the lambda, so it stays decoupled from navigation internals.
 *
 * Data-layer contract:
 *  - [viewModel] is provided by Hilt via [hiltViewModel()]. It injects
 *    [SampleRepository] through the interface — swapping [FakeSampleRepository]
 *    for a real API implementation requires zero changes here or in the ViewModel.
 *
 * Replace the body with real product UI when design is ready.
 */
@Composable
fun HomeScreen(
    onNavigateToDetail: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val greeting by viewModel.greeting.collectAsState()

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier              = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(MaterialTheme.spacing.md),
            verticalArrangement   = Arrangement.Center,
            horizontalAlignment   = Alignment.CenterHorizontally,
        ) {
            Text(
                text      = "Gominitta",
                style     = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text      = greeting.ifBlank { "Scaffold ready — replace with real Home UI" },
                style     = MaterialTheme.typography.bodyMedium,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier  = Modifier.padding(top = MaterialTheme.spacing.sm),
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
            Button(onClick = onNavigateToDetail) {
                Text("Open Detail →")
            }
        }
    }
}
