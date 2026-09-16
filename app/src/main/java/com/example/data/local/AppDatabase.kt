package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.*

class AppTypeConverters {
    @TypeConverter
    fun fromMedicationStatus(status: MedicationStatus): String = status.name

    @TypeConverter
    fun toMedicationStatus(value: String): MedicationStatus = try {
        MedicationStatus.valueOf(value)
    } catch (e: Exception) {
        MedicationStatus.PENDING
    }

    @TypeConverter
    fun fromMoodType(mood: MoodType): String = mood.name

    @TypeConverter
    fun toMoodType(value: String): MoodType = try {
        MoodType.valueOf(value)
    } catch (e: Exception) {
        MoodType.HAPPY
    }
}

@Database(
    entities = [
        PatientProfile::class,
        RoutineItem::class,
        Medication::class,
        Appointment::class,
        FamilyMember::class,
        Memory::class,
        MoodEntry::class,
        EmergencyContact::class,
        CaregiverNote::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(AppTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun routineDao(): RoutineDao
    abstract fun medicationDao(): MedicationDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun familyDao(): FamilyDao
    abstract fun memoryDao(): MemoryDao
    abstract fun moodDao(): MoodDao
    abstract fun emergencyContactDao(): EmergencyContactDao
    abstract fun caregiverNoteDao(): CaregiverNoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mannsaathi_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
