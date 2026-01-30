package com.example.phantom.presentation.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.phantom.domain.model.RiskLevel
import com.example.phantom.domain.model.TrustScore
import com.example.phantom.presentation.viewmodel.SecurityDashboardViewModel
import com.example.phantom.util.MonitoringServiceHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityDashboardScreen(
    viewModel: SecurityDashboardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val trustScore by viewModel.trustScore.collectAsState()
    val trustScoreHistory by viewModel.trustScoreHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PHANTOM Security Dashboard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { viewModel.refreshData() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }

        // Service status
        ServiceStatusCard(context)

        // Trust Score Card
        trustScore?.let { score ->
            TrustScoreCard(score)
        } ?: run {
            Card {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("Loading trust score...")
                }
            }
        }

        // Trust History Chart Placeholder
        trustScoreHistory?.let { history ->
            TrustHistoryCard(history)
        }

        // Loading indicator
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        // Error display
        error?.let { errorMsg ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text(
                    text = "Error: $errorMsg",
                    color = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun ServiceStatusCard(context: Context) {
    val isServiceEnabled = remember {
        MonitoringServiceHelper.isServiceEnabled(context)
    }

    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Behavioral Monitoring",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (isServiceEnabled) {
                    Text(
                        text = "Active",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "Inactive",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            if (!isServiceEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { 
                        MonitoringServiceHelper.openAccessibilitySettings(context)
                    }
                ) {
                    Text("Enable Service")
                }
            }
        }
    }
}

@Composable
fun TrustScoreCard(trustScore: TrustScore) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Current Trust Score",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Trust score display with color coding
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = trustScore.score.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = when (trustScore.riskLevel) {
                        RiskLevel.CRITICAL -> MaterialTheme.colorScheme.error
                        RiskLevel.HIGH -> MaterialTheme.colorScheme.errorContainer
                        RiskLevel.MEDIUM -> MaterialTheme.colorScheme.secondary
                        RiskLevel.LOW -> MaterialTheme.colorScheme.primary
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = when (trustScore.riskLevel) {
                    RiskLevel.CRITICAL -> "Critical Risk"
                    RiskLevel.HIGH -> "High Risk"
                    RiskLevel.MEDIUM -> "Medium Risk"
                    RiskLevel.LOW -> "Low Risk"
                },
                fontWeight = FontWeight.Bold,
                color = when (trustScore.riskLevel) {
                    RiskLevel.CRITICAL -> MaterialTheme.colorScheme.error
                    RiskLevel.HIGH -> MaterialTheme.colorScheme.errorContainer
                    RiskLevel.MEDIUM -> MaterialTheme.colorScheme.secondary
                    RiskLevel.LOW -> MaterialTheme.colorScheme.primary
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (trustScore.anomalyDetected) {
                Text(
                    text = "Anomaly Detected: ${trustScore.anomalyType}",
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Confidence: ${(trustScore.confidence * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun TrustHistoryCard(history: com.example.phantom.domain.model.TrustScoreHistory) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Trust Score History",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(text = "Average Score: ${history.averageScore.toInt()}")
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = if (history.trend > 0) "Trend: Improving" 
                      else if (history.trend < 0) "Trend: Declining" 
                      else "Trend: Stable"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Show recent scores (simplified visualization)
            LazyColumn {
                items(history.scores.take(10)) { score ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${score.score}/100")
                        Text(
                            text = when (score.riskLevel) {
                                RiskLevel.CRITICAL -> "Critical"
                                RiskLevel.HIGH -> "High"
                                RiskLevel.MEDIUM -> "Medium"
                                RiskLevel.LOW -> "Low"
                            },
                            color = when (score.riskLevel) {
                                RiskLevel.CRITICAL -> MaterialTheme.colorScheme.error
                                RiskLevel.HIGH -> MaterialTheme.colorScheme.errorContainer
                                RiskLevel.MEDIUM -> MaterialTheme.colorScheme.secondary
                                RiskLevel.LOW -> MaterialTheme.colorScheme.primary
                            }
                        )
                    }
                }
            }
        }
    }
}