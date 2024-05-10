package raytracer.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import raytracer.model.Point3
import raytracer.model.World

@Composable
fun ObjectController(world: World) {
    var selectedId by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = selectedId,
            onValueChange = { selectedId = it },
            label = { Text("Enter Object ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Button(onClick = { moveObject(world, selectedId, Point3(0.0, 0.0, 1.0)) }) {
                Text("Move Forward")
            }
            Button(onClick = { moveObject(world, selectedId, Point3(0.0, 0.0, -1.0)) }) {
                Text("Move Backward")
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Button(onClick = { moveObject(world, selectedId, Point3(-1.0, 0.0, 0.0)) }) {
                Text("Move Left")
            }
            Button(onClick = { moveObject(world, selectedId, Point3(1.0, 0.0, 0.0)) }) {
                Text("Move Right")
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Button(onClick = { moveObject(world, selectedId, Point3(0.0, 1.0, 0.0)) }) {
                Text("Move Up")
            }
            Button(onClick = { moveObject(world, selectedId, Point3(0.0, -1.0, 0.0)) }) {
                Text("Move Down")
            }
        }
    }
}

fun moveObject(world: World, objectId: String, offset: Point3) {
//    world.r.firstOrNull { it.id() == objectId }?.translate(offset)
}