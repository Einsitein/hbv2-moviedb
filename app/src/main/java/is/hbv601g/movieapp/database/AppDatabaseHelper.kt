package `is`.hbv601g.movieapp.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * A helper class to manage local storage for both the JWT token and profile picture URI.
 */
class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "app_db"
        private const val DATABASE_VERSION = 1

        // Table for storing the JWT token.
        private const val TABLE_TOKEN = "token_table"
        private const val COLUMN_TOKEN_ID = "id"
        private const val COLUMN_TOKEN = "token"
        private const val COLUMN_EMAIL = "email"

        // Table for storing the profile picture URI.
        private const val TABLE_PROFILE = "profile_table"
        private const val COLUMN_PROFILE_ID = "id"
        private const val COLUMN_IMAGE_URI = "image_uri"

        // Table for storing favorites info
        private const val TABLE_FAVORITES = "favorites_table"
        private const val COLUMN_FAVORITES_MOVIEID = "movie_id"
        private const val COLUMN_FAVORITES_EMAIL = "email"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create the token table.
        val createTokenTable = "CREATE TABLE $TABLE_TOKEN (" +
                "$COLUMN_TOKEN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_EMAIL TEXT, " +
                "$COLUMN_TOKEN TEXT)"
        db.execSQL(createTokenTable)

        // Create the profile table.
        val createProfileTable = "CREATE TABLE $TABLE_PROFILE (" +
                "$COLUMN_PROFILE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_IMAGE_URI TEXT)"
        db.execSQL(createProfileTable)

        // Create the favorites table.
        val createFavoritesTable = "CREATE TABLE $TABLE_FAVORITES (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_FAVORITES_MOVIEID INTEGER, " +
                "$COLUMN_FAVORITES_EMAIL TEXT)"
        db.execSQL(createFavoritesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TOKEN")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PROFILE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITES")
        onCreate(db)
    }

    fun insertFavoriteMovie(email: String, movieId: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FAVORITES_MOVIEID, movieId)
            put(COLUMN_FAVORITES_EMAIL, email)
        }
        return db.insert(TABLE_FAVORITES, null, values)
    }

    fun getMovieFavorites(movieId: String): List<String> {
        val db = readableDatabase
        val userIds = mutableListOf<String>()
        val cursor = db.query(
            TABLE_FAVORITES,
            arrayOf(COLUMN_FAVORITES_EMAIL),
            "$COLUMN_FAVORITES_MOVIEID = ?",
            arrayOf(movieId),
            null,
            null,
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                val userId = it.getString(it.getColumnIndexOrThrow(COLUMN_FAVORITES_EMAIL))
                userIds.add(userId)
            }
        }

        return userIds
    }

    fun getUserFavorites(email: String): List<String> {
        val db = readableDatabase
        val movieIds = mutableListOf<String>()
        val cursor = db.query(
            TABLE_FAVORITES,
            arrayOf(COLUMN_FAVORITES_MOVIEID),
            "$COLUMN_FAVORITES_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                val movieId = it.getString(it.getColumnIndexOrThrow(COLUMN_FAVORITES_MOVIEID))
                movieIds.add(movieId)
            }
        }

        return movieIds
    }



    /**
     * Inserts a new JWT token into the database.
     *
     * @param token The JWT token string.
     * @return The row ID of the newly inserted row.
     */
    fun insertToken(token: String, email: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TOKEN, token)
            put(COLUMN_EMAIL, email)
        }
        return db.insert(TABLE_TOKEN, null, values)
    }

    /**
     * Retrieves the latest JWT token.
     *
     * @return The latest token, or null if none exists.
     */
    fun getLatestToken(): String? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TOKEN,
            arrayOf(COLUMN_TOKEN),
            null,
            null,
            null,
            null,
            "$COLUMN_TOKEN_ID DESC",
            "1"
        )
        val token = if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TOKEN))
        } else null
        cursor.close()
        return token
    }


    fun getLatestEmail(): String? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TOKEN,
            arrayOf(COLUMN_EMAIL),
            null,
            null,
            null,
            null,
            "$COLUMN_TOKEN_ID DESC",
            "1"
        )
        val email = if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
        } else null
        cursor.close()
        return email
    }

    /**
     * Inserts the profile picture URI into the database.
     *
     * @param uri The URI string of the profile picture.
     * @return The row ID of the newly inserted row.
     */
    fun insertProfilePicture(uri: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IMAGE_URI, uri)
        }
        return db.insert(TABLE_PROFILE, null, values)
    }

    /**
     * Retrieves the latest profile picture URI.
     *
     * @return The URI string, or null if none exists.
     */
    fun getProfilePicture(): String? {
        try{
            val db = readableDatabase
            val cursor = db.query(
                TABLE_PROFILE,
                arrayOf(COLUMN_IMAGE_URI),
                null,
                null,
                null,
                null,
                "$COLUMN_PROFILE_ID DESC",
                "1"
            )
            val uri = if (cursor.moveToFirst()) {
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI))
            } else null
            cursor.close()
            return uri
        }
        catch(e: Exception) {
            return null
        }

    }
}
