package com.sg.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import com.sg.data.db.dao.RemoteKeyDao
import com.sg.data.db.dao.ReposPageDao
import com.sg.data.db.dao.ReposDao
import com.sg.data.db.dao.StarredReposDao
import com.sg.data.db.dto.RemoteKeyEntity
import com.sg.data.db.dto.RepoEntity
import com.sg.data.db.dto.RepoPageEntity
import com.sg.data.db.dto.StarredRepoEntity
import kotlinx.coroutines.Dispatchers


@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

@Database(
    entities = [
        RepoEntity::class,
        RemoteKeyEntity::class,
        StarredRepoEntity::class,
        RepoPageEntity::class
    ],
    version = 3,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reposDao(): ReposDao
    abstract fun reposPageDao(): ReposPageDao
    abstract fun remoteKeysDao(): RemoteKeyDao
    abstract fun starredReposDao(): StarredReposDao
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .addMigrations(*MIGRATIONS)
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

val appDatabase: AppDatabase by lazy {
    getRoomDatabase(dbBuilder!!)
}

@Suppress("EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE")
expect var dbBuilder: RoomDatabase.Builder<AppDatabase>?

internal val MIGRATIONS = arrayOf(
    object : Migration(1, 2) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL(
                "CREATE TABLE IF NOT EXISTS `starred_repos` " +
                        "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`repo_id` INTEGER NOT NULL, `user_id` INTEGER NOT NULL)"
            )
            connection.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_starred_repos_repo_id` " +
                        "ON `starred_repos` (`repo_id`)"
            )
            connection.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_starred_repos_user_id` " +
                        "ON `starred_repos` (`user_id`)"
            )
        }
    },
    object : Migration(2, 3) {
        override fun migrate(connection: SQLiteConnection) {
            // repos_page
            connection.execSQL("CREATE TABLE IF NOT EXISTS " +
                    "`repos_page` (`repo_id` INTEGER NOT NULL, " +
                    "`query` TEXT NOT NULL, `page` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`repo_id`))"
            )
            connection.execSQL("CREATE INDEX IF NOT EXISTS " +
                    "`index_repos_page_query` ON `repos_page` (`query`)"
            )
            connection.execSQL("CREATE INDEX IF NOT EXISTS " +
                    "`index_repos_page_page` ON `repos_page` (`page`)"
            )

            // repos
            connection.execSQL("DROP TABLE IF EXISTS `repos`")
            connection.execSQL("DROP INDEX IF EXISTS `index_repos_repo_id`")
            connection.execSQL("CREATE TABLE IF NOT EXISTS `repos` " +
                    "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`repo_id` INTEGER NOT NULL, `node_id` TEXT NOT NULL, " +
                    "`name` TEXT NOT NULL, `owner` TEXT NOT NULL)"
            )
            connection.execSQL("CREATE INDEX IF NOT EXISTS " +
                    "`index_repos_repo_id` ON `repos` (`repo_id`)"
            )
        }
    }
)