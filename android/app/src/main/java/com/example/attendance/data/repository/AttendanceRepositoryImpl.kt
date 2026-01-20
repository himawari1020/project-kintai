package com.example.attendance.data.repository

import com.example.attendance.domain.model.Attendance
import com.example.attendance.domain.model.AttendanceStatus
import com.example.attendance.domain.repository.AttendanceRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AttendanceRepository {

    private val collection = firestore.collection("attendance_records")

    // Helper to get today's date string YYYY-MM-DD
    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun getTodayAttendance(userId: String): Flow<Attendance?> = callbackFlow {
        val query = collection
            .whereEqualTo("userId", userId)
            // We ideally want to query by date too, but let's filter the latest one for today
            .whereEqualTo("date", getTodayDateString())
            .limit(1)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(null)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val doc = snapshot.documents[0]
                val attendance = mapDocumentToAttendance(doc.id, doc.data)
                trySend(attendance)
            } else {
                trySend(null)
            }
        }

        awaitClose { listener.remove() }
    }

    override fun getMonthlyAttendance(userId: String, yearMonth: String): Flow<List<Attendance>> = callbackFlow {
        // yearMonth is "YYYY-MM"
        // We want all dates starting with this prefix.
        // Since date is stored as "YYYY-MM-DD", checking for range or prefix works.
        // String range: start = "YYYY-MM-01", end = "YYYY-MM-31" (or just prefix match logic)
        // Firestore doesn't support prefix match strictly without range.
        // Let's use range: >= "YYYY-MM" and <= "YYYY-MM\uf8ff" to cover all chars.

        val start = yearMonth
        val end = "$yearMonth\uf8ff"

        val query = collection
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("date", start)
            .whereLessThanOrEqualTo("date", end)
            .orderBy("date", Query.Direction.DESCENDING) // Show latest first

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val list = snapshot.documents.map { doc ->
                    mapDocumentToAttendance(doc.id, doc.data)
                }
                trySend(list)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }


    override suspend fun clockIn(userId: String): Result<Unit> {
        return try {
            val now = Date()
            val dateStr = getTodayDateString()
            
            val newAttendance = hashMapOf(
                "userId" to userId,
                "date" to dateStr,
                "clockIn" to Timestamp(now),
                "clockOut" to null,
                "status" to AttendanceStatus.WORKING.name,
                "workTimeMinutes" to 0,
                "createdAt" to Timestamp(now),
                "updatedAt" to Timestamp(now)
            )

            collection.add(newAttendance).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clockOut(userId: String, attendanceId: String): Result<Unit> {
        return try {
            val now = Date()
            // We need to fetch the document first to calculate work time, 
            // but for simple clock out we just update status and time.
            // Ideally, we should do this in a Transaction or Cloud Function to calculate minutes accurately.
            // For now, client-side calculation (approximation) or just saving the timestamp.
            
            // Let's just update the timestamp and status. 
            // WorkTime calculation can be done when reading or via Cloud Function triggers.
            
            val updates = hashMapOf<String, Any>(
                "clockOut" to Timestamp(now),
                "status" to AttendanceStatus.LEFT.name,
                "updatedAt" to Timestamp(now)
            )

            collection.document(attendanceId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapDocumentToAttendance(id: String, data: Map<String, Any>?): Attendance {
        if (data == null) return Attendance()

        val clockInTimestamp = data["clockIn"] as? Timestamp
        val clockOutTimestamp = data["clockOut"] as? Timestamp
        val createdAtTimestamp = data["createdAt"] as? Timestamp
        val updatedAtTimestamp = data["updatedAt"] as? Timestamp
        val statusStr = data["status"] as? String ?: "OFF"

        return Attendance(
            id = id,
            userId = data["userId"] as? String ?: "",
            date = data["date"] as? String ?: "",
            clockIn = clockInTimestamp?.toDate(),
            clockOut = clockOutTimestamp?.toDate(),
            status = try {
                AttendanceStatus.valueOf(statusStr)
            } catch (e: IllegalArgumentException) {
                AttendanceStatus.OFF
            },
            workTimeMinutes = (data["workTimeMinutes"] as? Long)?.toInt(),
            createdAt = createdAtTimestamp?.toDate(),
            updatedAt = updatedAtTimestamp?.toDate()
        )
    }
}
