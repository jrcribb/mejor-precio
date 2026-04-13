package com.example.mejorprecio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mejorprecio.ui.theme.MejorPrecioTheme
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var darkTheme by remember { mutableStateOf(false) }
            var fontScale by remember { mutableFloatStateOf(1f) }

            MejorPrecioTheme(darkTheme = darkTheme, fontScale = fontScale) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MejorPrecioApp(
                        isDark = darkTheme,
                        onThemeToggle = { darkTheme = !darkTheme },
                        fontScale = fontScale,
                        onFontScaleChange = { fontScale = it }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MejorPrecioApp(
    isDark: Boolean,
    onThemeToggle: () -> Unit,
    fontScale: Float,
    onFontScaleChange: (Float) -> Unit
) {
    var rows by remember {
        mutableStateOf(List(3) { ComparisonRow(id = it) })
    }

    val (results, winnerIndex) = remember(rows) {
        ComparisonEngine.calculate(rows)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Text(
            text = "Mejor Precio",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Compara 3 opciones por precio unitario.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )

        // Tools
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onThemeToggle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Icon(if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, null)
                Spacer(Modifier.width(4.dp))
                Text(if (isDark) "Claro" else "Oscuro")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onFontScaleChange((fontScale - 0.05f).coerceAtLeast(0.85f)) }) {
                    Icon(Icons.Default.Remove, "Reducir letra")
                }
                IconButton(onClick = { onFontScaleChange((fontScale + 0.05f).coerceAtMost(1.3f)) }) {
                    Icon(Icons.Default.Add, "Aumentar letra")
                }
            }
        }

        // Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Nueva comparación", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { rows = List(3) { ComparisonRow(id = it) } }) {
                        Icon(Icons.Default.Refresh, null)
                        Text("Nueva")
                    }
                }

                rows.forEachIndexed { index, row ->
                    if (index == 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "Precio ($)",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                "Cantidad",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                "Unidad",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    val result = results.getOrNull(index)
                    val isWinner = winnerIndex == index
                    val textColor = if (isWinner) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
                    
                    ComparisonRowItem(
                        row = row,
                        isWinner = isWinner,
                        result = result,
                        onRowChange = { updatedRow ->
                            rows = rows.toMutableList().also { it[index] = updatedRow }
                        },
                        textColor = textColor
                    )
                }
            }
        }

        // Result Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            val winner = winnerIndex?.let { rows[it] }
            val winnerResult = winnerIndex?.let { results[it] }

            Column(modifier = Modifier.padding(16.dp)) {
                Text("Resultado", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                if (winnerResult != null) {
                    Text(
                        "Mejor opción: Opción ${winnerIndex!! + 1} ($${String.format("%.2f", winnerResult.comparativePrice)} / ${winnerResult.label})",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        "Completa al menos 2 líneas para ver el resultado.",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun ComparisonRowItem(
    row: ComparisonRow,
    isWinner: Boolean,
    result: CalculationResult?,
    onRowChange: (ComparisonRow) -> Unit,
    textColor: Color
) {
    val bgColor = if (isWinner) MaterialTheme.colorScheme.onBackground else Color.Transparent

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(10.dp))
            .border(1.dp, if (isWinner) Color.Transparent else MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = row.price,
                onValueChange = { onRowChange(row.copy(price = it)) },
                placeholder = { Text("0.00", fontSize = 12.sp, color = textColor.copy(alpha = 0.5f)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                colors = textFieldColors(isWinner, textColor)
            )
            TextField(
                value = row.quantity,
                onValueChange = { onRowChange(row.copy(quantity = it)) },
                placeholder = { Text("0.00", fontSize = 12.sp, color = textColor.copy(alpha = 0.5f)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                colors = textFieldColors(isWinner, textColor)
            )
            
            UnitDropdown(
                selectedUnit = row.unit,
                onUnitSelected = { onRowChange(row.copy(unit = it)) },
                modifier = Modifier.weight(1f),
                textColor = textColor
            )
        }
        
        if (result != null) {
            Text(
                text = "$${String.format("%.2f", result.comparativePrice)} / ${result.label}",
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
                style = MaterialTheme.typography.labelMedium,
                color = textColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropdown(
    selectedUnit: ProductUnit,
    onUnitSelected: (ProductUnit) -> Unit,
    modifier: Modifier,
    textColor: Color
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(4.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = textColor
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp, 
                textColor.copy(alpha = 0.3f)
            )
        ) {
            Text(selectedUnit.symbol)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ProductUnit.entries.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit.symbol) },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun textFieldColors(isWinner: Boolean, textColor: Color) = TextFieldDefaults.colors(
    focusedContainerColor = if (isWinner) textColor.copy(alpha = 0.05f) else Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedTextColor = textColor,
    unfocusedTextColor = textColor,
    cursorColor = textColor,
    focusedIndicatorColor = if (isWinner) textColor.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary,
    unfocusedIndicatorColor = if (isWinner) Color.Transparent else MaterialTheme.colorScheme.outline
)
