package raytracer.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import raytracer.model.Point3
import raytracer.model.World
import kotlin.math.*

@Composable
fun ObjectController(speed: Double, world: World, onUpdatePosition: () -> Unit) {
    var selectedId by remember { mutableStateOf<String?>(null) }
    val objectIds = remember { mutableStateListOf<String>() }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        objectIds.clear()
        objectIds.addAll(world.getObjectIds())
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = selectedId ?: "",
            onValueChange = { },
            label = { Text("Select Object ID") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(Icons.Filled.ArrowDropDown, "dropdown", Modifier.clickable { expanded = !expanded })
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            objectIds.forEach { id ->
                DropdownMenuItem(onClick = {
                    selectedId = id
                    expanded = false
                }) {
                    Text(id)
                }
            }
        }
        ControlPanel(selectedId, world, speed) {
            onUpdatePosition()
        }
    }
}

fun moveObject(world: World, id: String, point3: Point3, trigger: () -> Unit) {
    world.translateObjectById(id, point3)
    trigger()
}


fun Offset.coerceInCircle(center: Offset, radius: Float): Offset {
    val dx = x - center.x
    val dy = y - center.y
    return if (sqrt(dx * dx + dy * dy) <= radius) {
        this
    } else {
        val angle = atan2(dy, dx)
        Offset(
            x = center.x + cos(angle) * radius,
            y = center.y + sin(angle) * radius
        )
    }
}

fun getDirection(center: Offset, touch: Offset): Point3 {
    val dx = touch.x - center.x
    val dy = touch.y - center.y
    return when {
        abs(dx) > abs(dy) && dx > 0 -> Point3(0.02, 0.0, 0.0) // Right
        abs(dx) > abs(dy) && dx < 0 -> Point3(-0.02, 0.0, 0.0) // Left
        abs(dy) > abs(dx) && dy > 0 -> Point3(0.0, 0.0, 0.02) // Down
        abs(dy) > abs(dx) && dy < 0 -> Point3(0.0, 0.0, -0.02) // Up
        else -> Point3(0.0, 0.0, 0.0) // Center or negligible movement
    }
}

@Composable
fun ControlPanel(selectedId: String?, world: World, speed: Double, onUpdatePosition: () -> Unit) {
    val canvasSize = 150.dp  // Reduced size for compact layout
    val joystickRadius = canvasSize / 2
    var touchPosition by remember { mutableStateOf(Offset.Zero) }
    var realPosition by remember { mutableStateOf(Offset.Zero) }
    var centerPosition by remember { mutableStateOf(Offset.Zero) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { selectedId?.let { moveObject(world, it, Point3(0.0, speed, 0.0), onUpdatePosition) } }) {
            Text("Up")
        }

        Box(contentAlignment = Alignment.Center, modifier = Modifier
            .size(canvasSize)
            .onGloballyPositioned { layoutCoordinates ->
                centerPosition = layoutCoordinates.size.toSize().let {
                    Offset(it.width / 2f, it.height / 2f)
                }
                touchPosition = centerPosition
                realPosition = touchPosition
            }
            .pointerInput(selectedId) {
                detectDragGestures(onDragEnd = {
                    touchPosition = centerPosition; realPosition = centerPosition
                }) { change, dragAmount ->
                    val newPosition = realPosition + dragAmount
                    touchPosition = newPosition.coerceInCircle(centerPosition, joystickRadius.value / 2)
                    realPosition = newPosition
                    val direction = getDirection(centerPosition, touchPosition)
                    selectedId?.let {
                        moveObjectInDirection(world, it, direction, onUpdatePosition)
                    }
                    change.consume()
                }
            }
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(color = Color.Gray, center = centerPosition, radius = joystickRadius.toPx() / 2)
                drawCircle(color = Color.DarkGray, center = touchPosition, radius = joystickRadius.toPx() / 4)
            }
        }

        Button(onClick = { selectedId?.let { moveObject(world, it, Point3(0.0, -speed, 0.0), onUpdatePosition) } }) {
            Text("Down")
        }
    }
}

fun moveObjectInDirection(world: World, id: String, direction: Point3, trigger: () -> Unit) {
    world.translateObjectById(id, direction)
    trigger()
}