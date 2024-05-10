package raytracer.view


import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import raytracer.model.Camera


@Composable
fun CameraSettings(camera: Camera, onUpdateSettings: () -> Unit) {
    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Camera Settings", style = MaterialTheme.typography.h6)

        // Field of View Slider
        var fov by remember { mutableStateOf(camera.fov) }
        Text("Field of View: ${fov.toInt()}°")
        Slider(
            value = fov.toFloat(),
            onValueChange = { newValue ->
                fov = newValue.toDouble()
                camera.fov = newValue.toDouble()
                onUpdateSettings()
            },
            valueRange = 1f..180f,
            steps = 179,
            modifier = Modifier.width(200.dp)  // Applying width here
        )

        // Aperture Slider
        var aperture by remember { mutableStateOf(camera.aperture) }
        Text("Aperture: $aperture")
        Slider(
            value = aperture.toFloat(),
            onValueChange = { newValue ->
                aperture = newValue.toDouble()
                camera.aperture = newValue.toDouble()
                onUpdateSettings()
            },
            valueRange = 0.0f..10f,
            steps = 199,
            modifier = Modifier.width(200.dp)  // Applying width here
        )

        // Focus Distance Slider
        var focusDist by remember { mutableStateOf(camera.focusDist) }
        Text("Focus Distance: $focusDist")
        Slider(
            value = focusDist.toFloat(),
            onValueChange = { newValue ->
                focusDist = newValue.toDouble()
                camera.focusDist = newValue.toDouble()
                onUpdateSettings()
            },
            valueRange = 0.0f..10.0f,
            steps = 999,
            modifier = Modifier.width(200.dp)  // Applying width here
        )

        var samplesPerPixel by remember { mutableStateOf(camera.samplesPerPixel) }
        Text("Samples per pixel: $samplesPerPixel")
        Slider(
            value = samplesPerPixel.toFloat(),
            onValueChange = { newValue ->
                samplesPerPixel = newValue.toInt()
                camera.samplesPerPixel = samplesPerPixel
                onUpdateSettings()
            },
            valueRange = 1.0f..50.0f,
            steps = 49,
            modifier = Modifier.width(200.dp)  // Applying width here
        )

        var movementSpeed by remember { mutableStateOf(camera.movementSpeed) }
        Text("Movement speed: $movementSpeed")
        Slider(
            value = movementSpeed.toFloat(),
            onValueChange = { newValue ->
                movementSpeed = newValue.toDouble()
                camera.movementSpeed = movementSpeed
                onUpdateSettings()
            },
            valueRange = 0.1f..3.0f,
            steps = 100,
            modifier = Modifier.width(200.dp)  // Applying width here
        )

        // Image Width Integer Field
        var imageWidth by remember { mutableStateOf(camera.imageWidth) }
        OutlinedTextField(
            value = imageWidth.toString(),
            onValueChange = { newValue ->
                newValue.toIntOrNull()?.let {
                    imageWidth = it
                    camera.imageWidth = it
                    onUpdateSettings()
                }
            },
            label = { Text("Image Width") }
        )

        // Image Height Integer Field
        var imageHeight by remember { mutableStateOf(camera.imageHeight) }
        OutlinedTextField(
            value = imageHeight.toString(),
            onValueChange = { newValue ->
                newValue.toIntOrNull()?.let {
                    imageHeight = it
                    camera.imageHeight = it
                    onUpdateSettings()
                }
            },
            label = { Text("Image Height") }
        )
    }
}
