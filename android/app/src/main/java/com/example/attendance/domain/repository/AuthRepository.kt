package com.example.attendance.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isUserLoggedIn: Boolean
    val currentUserId: String?

    fun login(email: String, password: String): Flow<Result<Unit>>
    fun logout()
}
