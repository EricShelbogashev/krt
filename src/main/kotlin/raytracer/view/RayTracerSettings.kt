package raytracer.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import raytracer.model.RayTracer

@Composable
fun RayTracerSettings(rayTracer: RayTracer, onUpdateSettings: (() -> Unit)) {
    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Ray Tracer Settings", style = MaterialTheme.typography.h6)

        // Maximum Depth Slider
        var maxDepth by remember { mutableStateOf(rayTracer.maxDepth.toFloat()) }
        Text("Max Depth: ${maxDepth.toInt()}")

        Slider(
            value = maxDepth,
            onValueChange = { newValue ->
                maxDepth = newValue
                rayTracer.maxDepth = newValue.toInt()
                onUpdateSettings()
            },
            valueRange = 1f..20f,
            steps = 19
        )
    }
}