package com.example.attendance.di

import com.example.attendance.data.repository.AuthRepositoryImpl
import com.example.attendance.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAttendanceRepository(
        attendanceRepositoryImpl: com.example.attendance.data.repository.AttendanceRepositoryImpl
    ): com.example.attendance.domain.repository.AttendanceRepository
}
