package com.example.phantom.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phantom.domain.repository.BehavioralRepository
import com.example.phantom.data.model.DefensiveAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrustUiState(
    val currentTrustScore: Float = 0f,
    val isMonitoringActive: Boolean = false,
    val securityActive: Boolean = false,
    val recentDefensiveActions: List<DefensiveAction> = emptyList()
)

@HiltViewModel
class TrustViewModel @Inject constructor(
    private val behavioralRepository: BehavioralRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TrustUiState())
    val uiState: StateFlow<TrustUiState> = _uiState.asStateFlow()
    
    init {
        loadInitialData()
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            // Load initial trust score
            val trustScore = behavioralRepository.getCurrentTrustScore()
            val defensiveActions = behavioralRepository.getRecentDefensiveActions()
            
            _uiState.value = _uiState.value.copy(
                currentTrustScore = trustScore,
                isMonitoringActive = true,
                securityActive = trustScore < 60f, // Activate security when trust is low
                recentDefensiveActions = defensiveActions
            )
        }
    }
    
    fun refreshTrustScore() {
        viewModelScope.launch {
            val trustScore = behavioralRepository.getCurrentTrustScore()
            val defensiveActions = behavioralRepository.getRecentDefensiveActions()
            
            _uiState.value = _uiState.value.copy(
                currentTrustScore = trustScore,
                securityActive = trustScore < 60f,
                recentDefensiveActions = defensiveActions
            )
        }
    }
}