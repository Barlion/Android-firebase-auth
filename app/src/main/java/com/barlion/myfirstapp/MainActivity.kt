package com.barlion.myfirstapp

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
import com.barlion.myfirstapp.ui.theme.MyFirstAppTheme
import com.barlion.myfirstapp.ui.theme.Purple500
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        setContent {
            MyFirstAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginSignupScreen(auth)
                }
            }
        }
    }
}

@Composable
fun LoginSignupScreen(auth: FirebaseAuth) {
    // Track whether the user is on the login or sign-up screen
    var isLogin by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.backg),
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
            // Title
            Text(
                text = if (isLogin) "Login" else "Sign Up",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // User input states
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            // Input fields
            CustomTextField(label = "Email", value = email, onValueChange = { email = it })
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                isPassword = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login/Sign-up button
            Button(
                onClick = {
                    if (isLogin) {
                        loginUser(auth, email, password)
                    } else {
                        signupUser(auth, email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple500,
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = if (isLogin) "Login" else "Sign Up",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Toggle login/sign-up
            TextButton(
                onClick = { isLogin = !isLogin },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = if (isLogin) "Don't have an account? Sign Up" else "Already have an account? Login",
                    color = Purple500,
                    fontWeight = FontWeight.Bold
                )
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
    Column {
        Text(
            text = label,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
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

fun loginUser(auth: FirebaseAuth, email: String, password: String) {
    if (email.isBlank() || password.isBlank()) {
        Toast.makeText(auth.app.applicationContext, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
        return
    }

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(auth.app.applicationContext, "Login successful!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(auth.app.applicationContext, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}

fun signupUser(auth: FirebaseAuth, email: String, password: String) {
    if (email.isBlank() || password.isBlank()) {
        Toast.makeText(auth.app.applicationContext, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(auth.app.applicationContext, "Signup successful!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(auth.app.applicationContext, "Signup failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}
