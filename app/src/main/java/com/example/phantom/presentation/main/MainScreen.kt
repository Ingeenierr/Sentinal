package com.example.phantom.presentation.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.phantom.presentation.viewmodel.TrustViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: TrustViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PHANTOM Security") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Trust Score Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Current Trust Score",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Box(
                        modifier = Modifier.size(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { uiState.currentTrustScore / 100f },
                            strokeWidth = 12.dp,
                            modifier = Modifier.size(150.dp)
                        )
                        
                        Text(
                            text = "${uiState.currentTrustScore.toInt()}",
                            style = MaterialTheme.typography.displayMedium
                        )
                    }
                    
                    Text(
                        text = when {
                            uiState.currentTrustScore >= 80 -> "High Trust"
                            uiState.currentTrustScore >= 60 -> "Moderate Trust"
                            uiState.currentTrustScore >= 40 -> "Low Trust"
                            else -> "Very Low Trust"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
            
            // Behavior Analysis Status
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Behavior Analysis",
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (uiState.isMonitoringActive) "✓ Active" else "○ Inactive",
                            color = if (uiState.isMonitoringActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "Monitoring user behavior patterns"
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (uiState.securityActive) "✓ Active" else "○ Inactive",
                            color = if (uiState.securityActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "Security measures enabled"
                        )
                    }
                }
            }
            
            // Recent Defensive Actions
            if (uiState.recentDefensiveActions.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Recent Defensive Actions",
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LazyColumn {
                            items(uiState.recentDefensiveActions.size) { index ->
                                val action = uiState.recentDefensiveActions[index]
                                Text(text = "- ${action.actionType}: ${action.timestamp}")
                                
                                if (index < uiState.recentDefensiveActions.size - 1) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}