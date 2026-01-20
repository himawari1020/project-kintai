package com.example.attendance.presentation.settings

import androidx.lifecycle.ViewModel
import com.example.attendance.BuildConfig
import com.example.attendance.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firebaseAuth: FirebaseAuth // Directly accessing for email, or add to repository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        val currentUser = firebaseAuth.currentUser
        val email = currentUser?.email ?: "Unknown"
        val version = BuildConfig.VERSION_NAME

        _state.update { 
            it.copy(
                email = email,
                version = version
            ) 
        }
    }

    fun logout() {
        authRepository.logout()
    }
}
