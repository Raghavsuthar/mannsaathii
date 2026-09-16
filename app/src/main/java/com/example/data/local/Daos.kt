package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Query("SELECT * FROM patient_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<PatientProfile?>

    @Query("SELECT * FROM patient_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfileDirect(): PatientProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: PatientProfile)
}

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routine_items ORDER BY sortOrder ASC, id ASC")
    fun getAllRoutines(): Flow<List<RoutineItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(item: RoutineItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(items: List<RoutineItem>)

    @Update
    suspend fun updateRoutine(item: RoutineItem)

    @Delete
    suspend fun deleteRoutine(item: RoutineItem)

    @Query("UPDATE routine_items SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompletion(id: Long, completed: Boolean)

    @Query("UPDATE routine_items SET isDelayed = :delayed WHERE id = :id")
    suspend fun setDelayed(id: Long, delayed: Boolean)

    @Query("DELETE FROM routine_items")
    suspend fun clearAll()
}

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medications ORDER BY id ASC")
    fun getAllMedications(): Flow<List<Medication>>

    @Query("SELECT * FROM medications WHERE id = :id LIMIT 1")
    suspend fun getMedicationById(id: Long): Medication?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(med: Medication): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedications(meds: List<Medication>)

    @Update
    suspend fun updateMedication(med: Medication)

    @Delete
    suspend fun deleteMedication(med: Medication)

    @Query("UPDATE medications SET status = :status, takenTime = :takenTime WHERE id = :id")
    suspend fun updateStatus(id: Long, status: MedicationStatus, takenTime: String?)

    @Query("DELETE FROM medications")
    suspend fun clearAll()
}

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY id ASC")
    fun getAllAppointments(): Flow<List<Appointment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appt: Appointment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointments(appts: List<Appointment>)

    @Update
    suspend fun updateAppointment(appt: Appointment)

    @Delete
    suspend fun deleteAppointment(appt: Appointment)

    @Query("DELETE FROM appointments")
    suspend fun clearAll()
}

@Dao
interface FamilyDao {
    @Query("SELECT * FROM family_members ORDER BY sortPriority ASC, id ASC")
    fun getAllFamilyMembers(): Flow<List<FamilyMember>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyMember(member: FamilyMember): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyMembers(members: List<FamilyMember>)

    @Update
    suspend fun updateFamilyMember(member: FamilyMember)

    @Delete
    suspend fun deleteFamilyMember(member: FamilyMember)

    @Query("DELETE FROM family_members")
    suspend fun clearAll()
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY updatedAt DESC, id DESC")
    fun getAllMemories(): Flow<List<Memory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: Memory): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemories(memories: List<Memory>)

    @Update
    suspend fun updateMemory(memory: Memory)

    @Delete
    suspend fun deleteMemory(memory: Memory)

    @Query("DELETE FROM memories")
    suspend fun clearAll()
}

@Dao
interface MoodDao {
    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun getAllMoodEntries(): Flow<List<MoodEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntry(entry: MoodEntry): Long

    @Query("DELETE FROM mood_entries")
    suspend fun clearAll()
}

@Dao
interface EmergencyContactDao {
    @Query("SELECT * FROM emergency_contacts ORDER BY priority ASC, id ASC")
    fun getAllContacts(): Flow<List<EmergencyContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: EmergencyContact): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<EmergencyContact>)

    @Update
    suspend fun updateContact(contact: EmergencyContact)

    @Delete
    suspend fun deleteContact(contact: EmergencyContact)

    @Query("DELETE FROM emergency_contacts")
    suspend fun clearAll()
}

@Dao
interface CaregiverNoteDao {
    @Query("SELECT * FROM caregiver_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<CaregiverNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: CaregiverNote): Long

    @Delete
    suspend fun deleteNote(note: CaregiverNote)

    @Query("DELETE FROM caregiver_notes")
    suspend fun clearAll()
}
