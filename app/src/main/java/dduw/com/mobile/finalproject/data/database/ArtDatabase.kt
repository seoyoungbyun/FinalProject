package dduw.com.mobile.finalproject.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ArtDetail::class], version = 1)
abstract class ArtDatabase : RoomDatabase() {
    abstract fun artDao(): ArtDao

    companion object {
        @Volatile
        private var INSTANCE: ArtDatabase? = null

        fun getDatabase(context: Context): ArtDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArtDatabase::class.java,
                    "art_database"
                )
                    .fallbackToDestructiveMigration() // 스키마 변경 시 데이터 삭제
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}