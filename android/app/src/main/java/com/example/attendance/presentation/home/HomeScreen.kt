package com.example.attendance.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.attendance.domain.model.AttendanceStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance") }
            )
        },
        bottomBar = {
            com.example.attendance.presentation.components.AttendanceBottomBar(
                currentRoute = "home",
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Digital Clock
                Text(
                    text = state.currentTime,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 64.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Status Card
                val statusText = when (state.attendance?.status) {
                    AttendanceStatus.WORKING -> "勤務中"
                    AttendanceStatus.LEFT -> "退勤済"
                    else -> "勤務外"
                }

                val statusColor = when (state.attendance?.status) {
                    AttendanceStatus.WORKING -> Color(0xFF4CAF50) // Green
                    AttendanceStatus.LEFT -> Color(0xFF9E9E9E)    // Grey
                    else -> Color(0xFFFF9800)                      // Orange
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "現在のステータス",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Clock In Button
                    Button(
                        onClick = viewModel::clockIn,
                        enabled = state.attendance == null || state.attendance?.status == AttendanceStatus.OFF,
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("出勤", fontSize = 20.sp)
                    }

                    // Clock Out Button
                    Button(
                        onClick = viewModel::clockOut,
                        enabled = state.attendance?.status == AttendanceStatus.WORKING,
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .padding(start = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("退勤", fontSize = 20.sp)
                    }
                }

                if (state.error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.error ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
