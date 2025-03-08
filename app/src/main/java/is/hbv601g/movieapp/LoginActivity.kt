package `is`.hbv601g.movieapp

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "token_db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_TOKEN = "token_table"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TOKEN = "token"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_TOKEN ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TOKEN TEXT)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TOKEN")
        onCreate(db)
    }

    fun insertToken(token: String): Long {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_TOKEN, token)
        }
        return db.insert(TABLE_TOKEN, null, contentValues)
    }

    fun getLatestToken(): String? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TOKEN,
            arrayOf(COLUMN_TOKEN),
            null,
            null,
            null,
            null,
            "$COLUMN_ID DESC",
            "1"
        )
        val token = if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TOKEN))
        } else {
            null
        }
        cursor.close()
        return token
    }

}


class LoginActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use the XML layout
        setContentView(R.layout.activity_login)

        // Get references to views
        val emailEditText = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val passwordEditText = findViewById<EditText>(R.id.editTextTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonSend)
        val switchButton = findViewById<Button>(R.id.buttonSwitch)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            sendLoginRequest(email, password)
        }

        switchButton.setOnClickListener {
            runOnUiThread {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun sendLoginRequest(email: String, password: String) {

        val jsonBody = """
        {
            "email": "$email",
            "password": "$password"
        }
    """.trimIndent()

        val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://hugbunadarverkefni1-moviedb.onrender.com/user/login")
            .post(requestBody)
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Login", "Request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseString = response.body?.string()

                if (response.isSuccessful && responseString != null) {
                    Log.d("Login", "Login successful: $responseString")
                    if (responseString.startsWith("Invalid")) {
                        // Invalid credentials – notify the user.
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "Invalid credentials!", Toast.LENGTH_SHORT).show()
                        }
                        return
                    } else {
                        // Save token to SQLite
                        val dbHelper = DBHelper(this@LoginActivity)
                        dbHelper.insertToken(responseString)

                        val tokenFromDb = dbHelper.getLatestToken()
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "Token: $tokenFromDb", Toast.LENGTH_LONG).show()
                        }

                    }
                } else {
                    Log.e("Login", "Login failed with code: ${response.code}")
                }
                runOnUiThread {
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        })
    }
}
