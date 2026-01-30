package com.example.phantom.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phantom.domain.model.TrustScore
import com.example.phantom.domain.model.TrustScoreHistory
import com.example.phantom.domain.usecase.CalculateTrustScoreUseCase
import com.example.phantom.domain.usecase.GetTrustScoreHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecurityDashboardViewModel @Inject constructor(
    private val calculateTrustScoreUseCase: CalculateTrustScoreUseCase,
    private val getTrustScoreHistoryUseCase: GetTrustScoreHistoryUseCase
) : ViewModel() {

    private val _trustScore = MutableStateFlow<TrustScore?>(null)
    val trustScore: StateFlow<TrustScore?> = _trustScore.asStateFlow()

    private val _trustScoreHistory = MutableStateFlow<TrustScoreHistory?>(null)
    val trustScoreHistory: StateFlow<TrustScoreHistory?> = _trustScoreHistory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadTrustScore()
        loadTrustScoreHistory()
    }

    fun loadTrustScore() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val score = calculateTrustScoreUseCase()
                _trustScore.value = score
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadTrustScoreHistory() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val history = getTrustScoreHistoryUseCase()
                _trustScoreHistory.value = history
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshData() {
        loadTrustScore()
        loadTrustScoreHistory()
    }
}