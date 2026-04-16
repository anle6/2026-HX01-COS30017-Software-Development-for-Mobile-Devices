package com.example.petstrack

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarNoteDao {
    @Query("SELECT * FROM calendar_notes WHERE userId = :userId ORDER BY timestamp ASC")
    fun getNotes(userId: String): Flow<List<CalendarNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: CalendarNote)

    @Update
    suspend fun updateNote(note: CalendarNote)

    @Delete
    suspend fun deleteNote(note: CalendarNote)
}