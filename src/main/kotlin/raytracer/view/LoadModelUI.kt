package raytracer.view

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import java.io.File

@Composable
fun LoadObject(onFilePicked: (File) -> Unit) {
    var showFilePicker by remember { mutableStateOf(false) }
    Button(onClick = { showFilePicker = true }) {
        Text("Load File")
    }

    val fileType = listOf("obj")
    FilePicker(show = showFilePicker, fileExtensions = fileType) { platformFile ->
        showFilePicker = false
        platformFile?.let {
            onFilePicked(File(platformFile.path))
        }
    }
}
