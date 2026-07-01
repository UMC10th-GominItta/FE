package com.gominitta.android.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.spacing

/**
 * Scaffold-level card wrapping Material 3 [ElevatedCard].
 *
 * Styling (shape, elevation, surface colour) flows automatically from
 * [GominittaTheme]. Feature screens slot content into [content] and
 * never depend on Material 3 card APIs directly.
 */
@Composable
fun GominittaCard(
    modifier: Modifier = Modifier,
    content:  @Composable ColumnScope.() -> Unit,
) {
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.md)) {
            content()
        }
    }
}

// ---- Previews ---------------------------------------------------------------

@Preview(name = "Card – Light", showBackground = true)
@Composable
private fun GominittaCardLightPreview() {
    GominittaTheme(darkTheme = false) {
        GominittaCard {
            Text(
                text  = "Card Title",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text  = "Card body text — placeholder content.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(name = "Card – Dark", showBackground = true)
@Composable
private fun GominittaCardDarkPreview() {
    GominittaTheme(darkTheme = true) {
        GominittaCard {
            Text(
                text  = "Card Title",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text  = "Card body text — placeholder content.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
