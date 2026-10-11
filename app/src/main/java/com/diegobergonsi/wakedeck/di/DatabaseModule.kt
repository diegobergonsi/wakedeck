package com.diegobergonsi.wakedeck.di

import android.content.Context
import androidx.room.Room
import com.diegobergonsi.wakedeck.data.db.AppDatabase
import com.diegobergonsi.wakedeck.data.db.DeviceDao
import com.diegobergonsi.wakedeck.data.db.GroupDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun database(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "wakedeck.db")
            .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3, AppDatabase.MIGRATION_3_4)
            .build()

    @Provides
    fun deviceDao(db: AppDatabase): DeviceDao = db.deviceDao()

    @Provides
    fun groupDao(db: AppDatabase): GroupDao = db.groupDao()
}
