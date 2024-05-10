package raytracer.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import raytracer.model.RenderController

@Composable
fun RenderControllerSettings(renderController: RenderController, onUpdateSettings: () -> Unit) {
    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Render Controller Settings", style = MaterialTheme.typography.h6)

        var repaintIntervalMs by remember { mutableStateOf(renderController.repaintIntervalMs.toFloat()) }
        Text("Repaint Interval (ms): ${repaintIntervalMs.toInt()}")

        Slider(
            value = repaintIntervalMs,
            onValueChange = { newValue ->
                repaintIntervalMs = newValue
                renderController.repaintIntervalMs = newValue.toLong()
                onUpdateSettings()
            },
            valueRange = 10f..200f,
            steps = 19, // Adjust step count for suitable granularity,
            modifier = Modifier.width(200.dp)
        )

        // Batch Size Slider
        var batchSize by remember { mutableStateOf(renderController.batchSize.toFloat()) }
        Text("Batch Size: ${batchSize.toInt()}")

        Slider(
            value = batchSize,
            onValueChange = { newValue ->
                batchSize = newValue
                renderController.batchSize = newValue.toInt()
                onUpdateSettings()
            },
            valueRange = 1f..100f,
            steps = 99, // Adjust step count for suitable granularity
            modifier = Modifier.width(200.dp)
        )
    }
}
