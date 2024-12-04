package com.sg.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.sg.data.db.dao.RemoteKeyDao
import com.sg.data.db.dao.RepoDao
import com.sg.data.db.dto.RemoteKeyEntity
import com.sg.data.db.dto.RepoEntity
import kotlinx.coroutines.Dispatchers


@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>

@Database(entities = [RepoEntity::class, RemoteKeyEntity::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
    abstract fun remoteKeysDao(): RemoteKeyDao
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

val appDatabase: AppDatabase by lazy {
    getRoomDatabase(dbBuilder!!)
}

@Suppress("EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE")
expect var dbBuilder: RoomDatabase.Builder<AppDatabase>?