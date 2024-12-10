package com.barlion.myfirstapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barlion.myfirstapp.ui.theme.Purple500
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ToDoListScreen() {
    // State for tasks
    var tasks by remember { mutableStateOf(listOf<Pair<String, String>>()) } // Pair(task, timestamp)
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTask by remember { mutableStateOf(TextFieldValue("")) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background with gradient
        Image(
            painter = painterResource(id = R.drawable.todo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0xAA000000))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "To Do List",
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Task list
            if (tasks.isEmpty()) {
                Text(
                    text = "No tasks yet! Tap the + button to add one!",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                tasks.forEachIndexed { index, task ->
                    TaskItem(
                        task = task.first,
                        timestamp = task.second,
                        onDelete = {
                            tasks = tasks.toMutableList().apply { removeAt(index) }
                        },
                        onComplete = {
                            tasks = tasks.toMutableList().apply {
                                set(index, task.copy(first = "${task.first} ✅"))
                            }
                        }
                    )
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { showAddTaskDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            shape = CircleShape,
            containerColor = Purple500
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Task", tint = Color.White)
        }

        // Add Task Dialog
        AnimatedVisibility(visible = showAddTaskDialog) {
            AlertDialog(
                onDismissRequest = { showAddTaskDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        if (newTask.text.isNotBlank()) {
                            val timestamp = getCurrentTimestamp()
                            tasks = tasks + Pair(newTask.text, timestamp)
                            newTask = TextFieldValue("")
                        }
                        showAddTaskDialog = false
                    }) {
                        Text("Add", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddTaskDialog = false }) {
                        Text("Cancel")
                    }
                },
                text = {
                    Column {
                        Text("Add a new task")
                        BasicTextField(
                            value = newTask,
                            onValueChange = { newTask = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                .padding(16.dp),
                            singleLine = true
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun TaskItem(task: String, timestamp: String, onDelete: () -> Unit, onComplete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(2.dp, RoundedCornerShape(8.dp))
            .background(Color(0xFF121212), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onComplete) {
                Icon(Icons.Filled.Check, contentDescription = "Complete Task", tint = Color.Green)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete Task", tint = Color.Red)
            }
        }
        Text(
            text = "Added: $timestamp",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

fun getCurrentTimestamp(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return dateFormat.format(Date())
}
