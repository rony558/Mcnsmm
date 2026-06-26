package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SmmViewModel
import com.example.ui.theme.*

@Composable
fun AuthView(
    viewModel: SmmViewModel,
    onAuthSuccess: () -> Unit
) {
    var isLoginMode by remember { mutableStateOf(true) }
    
    // Form Inputs
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var whatsappNumber by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var referralCodeInput by remember { mutableStateOf("") }

    var localError by remember { mutableStateOf<String?>(null) }

    val uiMessage by viewModel.uiMessage.collectAsState()

    // Keyboard states
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate900)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header Logo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        Brush.linearGradient(listOf(CosmicBlue, CosmicIndigo)),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "M",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "MCNSMM",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Slate100,
                letterSpacing = 1.sp
            )

            Text(
                text = if (isLoginMode) "Sign in to SMM Panel" else "Create your account",
                style = MaterialTheme.typography.bodyMedium,
                color = Slate400,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Error displays
            val activeError = localError ?: uiMessage
            if (activeError != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = activeError,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                localError = null
                                viewModel.clearUiMessage()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Input Form Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isLoginMode) {
                        // Full Name
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            placeholder = { Text("John Doe") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Slate100,
                                unfocusedTextColor = Slate300,
                                focusedBorderColor = CosmicBlue,
                                unfocusedBorderColor = Slate700
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_fullname_input")
                        )

                        // WhatsApp Number
                        OutlinedTextField(
                            value = whatsappNumber,
                            onValueChange = { whatsappNumber = it },
                            label = { Text("WhatsApp Number") },
                            placeholder = { Text("e.g. +91XXXXXXXXXX") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "WhatsApp") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Slate100,
                                unfocusedTextColor = Slate300,
                                focusedBorderColor = CosmicBlue,
                                unfocusedBorderColor = Slate700
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_whatsapp_input")
                        )
                    }

                    // Email/Gmail ID
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Gmail / Email ID") },
                        placeholder = { Text("you@gmail.com") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Slate100,
                            unfocusedTextColor = Slate300,
                            focusedBorderColor = CosmicBlue,
                            unfocusedBorderColor = Slate700
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(if (isLoginMode) "login_email_input" else "register_email_input")
                    )

                    // Password
                    var passwordVisible by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password (8+ digits)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                        trailingIcon = {
                            val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(icon, contentDescription = "Toggle password visibility")
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Slate100,
                            unfocusedTextColor = Slate300,
                            focusedBorderColor = CosmicBlue,
                            unfocusedBorderColor = Slate700
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(if (isLoginMode) "login_password_input" else "register_password_input")
                    )

                    if (!isLoginMode) {
                        // Confirm Password
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Slate100,
                                unfocusedTextColor = Slate300,
                                focusedBorderColor = CosmicBlue,
                                unfocusedBorderColor = Slate700
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_confirm_password_input")
                        )

                        // Optional Referral Code
                        OutlinedTextField(
                            value = referralCodeInput,
                            onValueChange = { referralCodeInput = it },
                            label = { Text("Referral Code (Optional)") },
                            placeholder = { Text("Code for ₹50 SignUp Bonus!") },
                            leadingIcon = { Icon(Icons.Default.CardGiftcard, contentDescription = "Referral") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Slate100,
                                unfocusedTextColor = Slate300,
                                focusedBorderColor = CosmicBlue,
                                unfocusedBorderColor = Slate700
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_referral_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Primary Button
                    Button(
                        onClick = {
                            localError = null
                            viewModel.clearUiMessage()

                            if (isLoginMode) {
                                if (email.isBlank() || password.isBlank()) {
                                    localError = "Please enter both Email and Password."
                                    return@Button
                                }
                                viewModel.login(email, password, onAuthSuccess)
                            } else {
                                if (fullName.isBlank()) {
                                    localError = "Full Name is required."
                                    return@Button
                                }
                                if (whatsappNumber.isBlank()) {
                                    localError = "WhatsApp Number is required."
                                    return@Button
                                }
                                if (email.isBlank()) {
                                    localError = "Email ID is required."
                                    return@Button
                                }
                                if (password.length < 8) {
                                    localError = "Password must be at least 8 digits/characters."
                                    return@Button
                                }
                                if (password != confirmPassword) {
                                    localError = "Passwords do not match."
                                    return@Button
                                }
                                viewModel.register(
                                    email = email,
                                    fullName = fullName,
                                    whatsappNumber = whatsappNumber,
                                    passwordHash = password,
                                    referredByCode = if (referralCodeInput.isBlank()) null else referralCodeInput,
                                    onSuccess = onAuthSuccess
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag(if (isLoginMode) "login_submit_button" else "register_submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isLoginMode) "LOGIN" else "CREATE ACCOUNT",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Divider for Social auth
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Slate700)
                Text(
                    text = "OR CONTINUE WITH",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate400,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Slate700)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google Authentication Buttons
            Button(
                onClick = {
                    // Google Auth Simulation
                    val testGoogleEmail = "google.test.${(100..999).random()}@gmail.com"
                    val testGoogleName = "Google User ${(10..99).random()}"
                    viewModel.registerOrLoginWithGoogle(testGoogleEmail, testGoogleName, onAuthSuccess)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag(if (isLoginMode) "google_login_button" else "google_register_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                border = ButtonDefaults.outlinedButtonBorder,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Google Logo",
                        tint = CosmicIndigo,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isLoginMode) "Login with Google" else "Register with Google",
                        color = Slate100,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mode switcher
            TextButton(
                onClick = {
                    isLoginMode = !isLoginMode
                    localError = null
                    viewModel.clearUiMessage()
                },
                modifier = Modifier.testTag("auth_mode_toggle_button")
            ) {
                Text(
                    text = if (isLoginMode) "Don't have an account? Register Now" else "Already registered? Login here",
                    color = CosmicIndigo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
