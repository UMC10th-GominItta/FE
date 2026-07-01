package com.gominitta.android.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.gominitta.android.ui.theme.spacing

/**
 * Scaffold placeholder for the Detail destination.
 *
 * Navigation contract:
 *  - [onNavigateBack] is called when the user presses Back. The actual
 *    popBackStack() call lives in [AppNavHost]; this screen only knows
 *    about the lambda, keeping it decoupled from navigation internals.
 *
 * Replace the body with real product UI when design is ready.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar   = {
            TopAppBar(
                title              = { Text("Detail") },
                navigationIcon     = {
                    TextButton(onClick = onNavigateBack) {
                        Text("← Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier              = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(MaterialTheme.spacing.md),
            verticalArrangement   = Arrangement.Center,
            horizontalAlignment   = Alignment.CenterHorizontally,
        ) {
            Text(
                text      = "Detail Screen",
                style     = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text      = "Placeholder — navigation round-trip proof",
                style     = MaterialTheme.typography.bodyMedium,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier  = Modifier.padding(top = MaterialTheme.spacing.sm),
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
            TextButton(onClick = onNavigateBack) {
                Text("← Go Back to Home")
            }
        }
    }
}
