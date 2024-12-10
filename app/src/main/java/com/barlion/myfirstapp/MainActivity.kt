package com.barlion.myfirstapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.barlion.myfirstapp.ui.theme.MyFirstAppTheme
import com.barlion.myfirstapp.ui.theme.Purple500
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        setContent {
            MyFirstAppTheme {
                val navController = rememberNavController()

                // Check if user opted to stay logged in
                val isLoggedIn = sharedPreferences.getBoolean("keep_logged_in", false)

                NavHost(
                    navController = navController,
                    startDestination = if (isLoggedIn) "menu" else "login_signup"
                ) {
                    composable("login_signup") {
                        LoginSignupScreen(auth, navController, sharedPreferences)
                    }
                    composable("menu") {
                        MenuScreen(navController)
                    }
                    composable("todo_list") {
                        ToDoListScreen()
                    }
                    // Add other services here as composable routes
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Label for the text field
        Text(
            text = label,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // The actual text field
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x88FFFFFF), shape = MaterialTheme.shapes.small)
                .padding(16.dp),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}

@Composable
fun LoginSignupScreen(auth: FirebaseAuth, navController: NavController, sharedPreferences: SharedPreferences) {
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var keepLoggedIn by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.firstback),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(24.dp)
        ) {
            Text(
                text = if (isLogin) "Login" else "Sign Up",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(40.dp))

            CustomTextField(label = "Email", value = email, onValueChange = { email = it })
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Checkbox for "Keep Me Logged In"
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = keepLoggedIn, onCheckedChange = { keepLoggedIn = it })
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Keep Me Logged In", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (isLogin) {
                        loginUser(auth, email, password, navController, sharedPreferences, keepLoggedIn)
                    } else {
                        signupUser(auth, email, password, navController, sharedPreferences, keepLoggedIn)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple500, contentColor = Color.White),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = if (isLogin) "Login" else "Sign Up", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { isLogin = !isLogin }) {
                Text(
                    text = if (isLogin) "Don't have an account? Sign Up" else "Already have an account? Login",
                    color = Purple500,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun loginUser(
    auth: FirebaseAuth,
    email: String,
    password: String,
    navController: NavController,
    sharedPreferences: SharedPreferences,
    keepLoggedIn: Boolean
) {
    if (email.isBlank() || password.isBlank()) {
        Toast.makeText(auth.app.applicationContext, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
        return
    }

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Save "keep me logged in" state
                if (keepLoggedIn) {
                    sharedPreferences.edit().putBoolean("keep_logged_in", true).apply()
                }

                navController.navigate("menu")
                Toast.makeText(auth.app.applicationContext, "Login successful!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(auth.app.applicationContext, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}

fun signupUser(
    auth: FirebaseAuth,
    email: String,
    password: String,
    navController: NavController,
    sharedPreferences: SharedPreferences,
    keepLoggedIn: Boolean
) {
    if (email.isBlank() || password.isBlank()) {
        Toast.makeText(auth.app.applicationContext, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Save "keep me logged in" state
                if (keepLoggedIn) {
                    sharedPreferences.edit().putBoolean("keep_logged_in", true).apply()
                }

                navController.navigate("menu")
                Toast.makeText(auth.app.applicationContext, "Signup successful!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(auth.app.applicationContext, "Signup failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}
