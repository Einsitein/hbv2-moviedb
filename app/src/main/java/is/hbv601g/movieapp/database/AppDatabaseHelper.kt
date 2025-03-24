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

        // Table for storing the profile picture URI.
        private const val TABLE_PROFILE = "profile_table"
        private const val COLUMN_PROFILE_ID = "id"
        private const val COLUMN_IMAGE_URI = "image_uri"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create the token table.
        val createTokenTable = "CREATE TABLE $TABLE_TOKEN (" +
                "$COLUMN_TOKEN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TOKEN TEXT)"
        db.execSQL(createTokenTable)

        // Create the profile table.
        val createProfileTable = "CREATE TABLE $TABLE_PROFILE (" +
                "$COLUMN_PROFILE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_IMAGE_URI TEXT)"
        db.execSQL(createProfileTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TOKEN")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PROFILE")
        onCreate(db)
    }

    /**
     * Inserts a new JWT token into the database.
     *
     * @param token The JWT token string.
     * @return The row ID of the newly inserted row.
     */
    fun insertToken(token: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TOKEN, token)
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
}
