package com.diegobergonsi.wakedeck.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [DeviceEntity::class], version = 3, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao

    companion object {
        /** v3 adds the SSH settings for remote shutdown. */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE devices ADD COLUMN sshUser TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE devices ADD COLUMN sshPort INTEGER NOT NULL DEFAULT 22")
                db.execSQL("ALTER TABLE devices ADD COLUMN sshHostKey TEXT NOT NULL DEFAULT ''")
            }
        }

        /** v2 adds the per-device "show in launcher menu" flag. Existing devices keep it off. */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE devices ADD COLUMN showInLauncher INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
