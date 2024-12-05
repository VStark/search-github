package com.sg.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.sg.data.db.dao.RemoteKeyDao
import com.sg.data.db.dao.RepoDao
import com.sg.data.db.dao.StarredReposDao
import com.sg.data.db.dto.RemoteKeyEntity
import com.sg.data.db.dto.RepoEntity
import com.sg.data.db.dto.StarredRepoEntity
import kotlinx.coroutines.Dispatchers


@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>

@Database(
    entities = [RepoEntity::class, RemoteKeyEntity::class, StarredRepoEntity::class],
    version = 2,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reposDao(): RepoDao
    abstract fun remoteKeysDao(): RemoteKeyDao
    abstract fun starredReposDao(): StarredReposDao
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .addMigrations(*MIGRATIONS)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

val appDatabase: AppDatabase by lazy {
    getRoomDatabase(dbBuilder!!)
}

@Suppress("EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE")
expect var dbBuilder: RoomDatabase.Builder<AppDatabase>?

internal val MIGRATIONS = arrayOf<Migration>(
    object : Migration(1, 2) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL("CREATE TABLE IF NOT EXISTS `starred_repos` " +
                    "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`repo_id` INTEGER NOT NULL, `user_id` INTEGER NOT NULL)"
            )
            connection.execSQL("CREATE INDEX IF NOT EXISTS `index_starred_repos_repo_id` " +
                    "ON `starred_repos` (`repo_id`)"
            )
            connection.execSQL("CREATE INDEX IF NOT EXISTS `index_starred_repos_user_id` " +
                    "ON `starred_repos` (`user_id`)"
            )
        }
    }
)