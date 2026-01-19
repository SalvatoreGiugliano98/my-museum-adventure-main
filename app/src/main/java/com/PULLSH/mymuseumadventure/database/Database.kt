package com.PULLSH.mymuseumadventure.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.PULLSH.mymuseumadventure.themes.Theme
import com.PULLSH.mymuseumadventure.riddles.Riddle
import com.PULLSH.mymuseumadventure.zone.Zone

@Dao
interface ThemeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThemes(themes: List<Theme>)

    @Query("SELECT * FROM themes")
    suspend fun getAllThemes(): List<Theme>

    @Query("SELECT * FROM themes WHERE id = :id")
    suspend fun getTheme(id: Int): Theme

    @Update
    suspend fun updateTheme(theme: Theme)

    @Query("DELETE FROM themes")
    suspend fun deleteAllThemes()
}

@Dao
interface RiddleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRiddles(riddles: List<Riddle>)

    @Query("SELECT * FROM riddles")
    suspend fun getAllRiddles(): List<Riddle>

    @Update
    suspend fun updateRiddle(riddle: Riddle)

    @Update
    suspend fun updateRiddles(riddle: List<Riddle>)

    @Query("SELECT * FROM riddles WHERE id = :id")
    suspend fun getRiddleById(id: Int): Riddle

    @Query("SELECT artworkID FROM riddles WHERE id = :id")
    suspend fun getArtworkId(id: Int): Int

    @Delete
    suspend fun deleteRiddle(riddle: Riddle)

    @Query("DELETE FROM riddles")
    suspend fun deleteAllRiddles()
}

@Dao
interface ZoneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZones(zones: List<Zone>)

    @Query("SELECT * FROM zone")
    suspend fun getAllZones(): List<Zone>

    @Update
    suspend fun updateZone(zone: Zone)

    @Delete
    suspend fun deleteZone(zone: Zone)

    @Query("DELETE FROM zone")
    suspend fun deleteAllZones()
}

@Database(entities = [Theme::class, Riddle::class, Zone::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun themeDao(): ThemeDao
    abstract fun riddleDao(): RiddleDao
    abstract fun zoneDao(): ZoneDao
    suspend fun clearAllData() {
        themeDao().deleteAllThemes()
        riddleDao().deleteAllRiddles()
        zoneDao().deleteAllZones()
    }
}

object DatabaseProvider {
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
    fun destroyInstance() {
        INSTANCE = null
    }
}