package com.hdt.basecompose.ui.language

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun LanguageListContent(
    languages: List<LanguageItem>,
    selectedCode: String,
    enabled: Boolean = true,
    listState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier,
    onItemClick: (LanguageItem) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(languages, key = { it.code }) { lang ->
            LanguageRow(
                item = lang,
                isSelected = lang.code == selectedCode,
                enabled = enabled,
                onClick = { if (enabled) onItemClick(lang) },
            )
        }
    }
}

@Composable
private fun LanguageRow(
    item: LanguageItem,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val primary = MaterialTheme.colorScheme.primary
    val borderColor = if (isSelected) primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    val borderWidth = if (isSelected) 1.5.dp else 1.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        ),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = item.flagId,
                contentDescription = null,
                placeholder = painterResource(item.flagId),
                error = painterResource(item.flagId),
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            RadioButton(
                selected = isSelected,
                onClick = if (enabled) onClick else null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = primary,
                    unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                ),
            )
        }
    }
}
