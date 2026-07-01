package com.gominitta.android.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gominitta.android.ui.theme.GominittaTheme

/**
 * Scaffold-level button wrapping Material 3 [Button].
 *
 * Replace or extend this composable when the design system matures.
 * All feature screens should use this instead of calling [Button] directly
 * so that global style changes propagate without touching feature code.
 *
 * Styling comes automatically from [GominittaTheme] — colour (primary token),
 * shape (small token), and typography (labelLarge token).
 */
@Composable
fun GominittaButton(
    text:     String,
    onClick:  () -> Unit,
    modifier: Modifier = Modifier,
    enabled:  Boolean  = true,
) {
    Button(
        onClick  = onClick,
        modifier = modifier,
        enabled  = enabled,
    ) {
        Text(text = text)
    }
}

// ---- Previews ---------------------------------------------------------------

@Preview(name = "Button – Light", showBackground = true)
@Composable
private fun GominittaButtonLightPreview() {
    GominittaTheme(darkTheme = false) {
        GominittaButton(text = "Confirm", onClick = {})
    }
}

@Preview(name = "Button – Dark", showBackground = true)
@Composable
private fun GominittaButtonDarkPreview() {
    GominittaTheme(darkTheme = true) {
        GominittaButton(text = "Confirm", onClick = {})
    }
}

@Preview(name = "Button – Disabled", showBackground = true)
@Composable
private fun GominittaButtonDisabledPreview() {
    GominittaTheme {
        GominittaButton(text = "Disabled", onClick = {}, enabled = false)
    }
}
