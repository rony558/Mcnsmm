package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.SmmViewModel
import com.example.ui.SmmViewModelFactory
import com.example.ui.components.AdminPanelView
import com.example.ui.components.AuthView
import com.example.ui.components.SplashView
import com.example.ui.components.UserDashboardView
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    // Permission request contract for Push Notifications (Android 13+)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Push Notifications Enabled ✅", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            MyApplicationTheme {
                val viewModel: SmmViewModel = viewModel(
                    factory = SmmViewModelFactory(application)
                )

                val isSplashCompleted by viewModel.isSplashCompleted.collectAsState()
                val currentUserEmail by viewModel.currentUserEmail.collectAsState()
                val isAdminMode by viewModel.isAdminMode.collectAsState()
                val inAppNotification by viewModel.inAppNotification.collectAsState()

                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        !isSplashCompleted -> {
                            SplashView(onSplashFinished = {
                                viewModel.completeSplash()
                            })
                        }
                        currentUserEmail == null -> {
                            AuthView(
                                viewModel = viewModel,
                                onAuthSuccess = {
                                    // Handled reactively via email update
                                }
                            )
                        }
                        isAdminMode -> {
                            AdminPanelView(
                                viewModel = viewModel,
                                onBackToUserDashboard = {
                                    viewModel.setAdminMode(false)
                                }
                            )
                        }
                        else -> {
                            UserDashboardView(
                                viewModel = viewModel,
                                onBackToAuth = {
                                    // Handled reactively
                                }
                            )
                        }
                    }

                    // Simulated Real-Time Push Notification Banner Overlay
                    AnimatedVisibility(
                        visible = inAppNotification != null,
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .statusBarsPadding()
                            .padding(16.dp)
                    ) {
                        inAppNotification?.let { msg ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.clearInAppNotification() }
                                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                                    .testTag("push_notification_banner")
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                MaterialTheme.colorScheme.primary,
                                                RoundedCornerShape(20.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.NotificationsActive,
                                            contentDescription = "Notification",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.width(12.dp))
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "MCNSMM Push System",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = msg,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                            lineHeight = 16.sp,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }

                            // Auto dismiss banner after 5 seconds
                            LaunchedEffect(msg) {
                                delay(5000)
                                viewModel.clearInAppNotification()
                            }
                        }
                    }
                }
            }
        }
    }
}
