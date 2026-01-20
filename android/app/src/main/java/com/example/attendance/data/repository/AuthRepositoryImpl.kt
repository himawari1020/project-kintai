package com.example.attendance.data.repository

import com.example.attendance.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val isUserLoggedIn: Boolean
        get() = firebaseAuth.currentUser != null

    override val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    override fun login(email: String, password: String): Flow<Result<Unit>> = callbackFlow {
        if (email.isBlank() || password.isBlank()) {
            trySend(Result.failure(IllegalArgumentException("Email and password cannot be empty")))
            close()
            return@callbackFlow
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                trySend(Result.success(Unit))
                close()
            }
            .addOnFailureListener { exception ->
                trySend(Result.failure(exception))
                close()
            }
        awaitClose { }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}
