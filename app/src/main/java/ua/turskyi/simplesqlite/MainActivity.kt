package ua.turskyi.simplesqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * kotlin implementation of this lesson
 * https://startandroid.ru/ru/uroki/vse-uroki-spiskom/74-urok-34-hranenie-dannyh-sqlite.html
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val LOG_TAG = "myLogs"
    }

    private var dbHelper: DBHelper? = null
    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnAdd.setOnClickListener(this)
        btnRead.setOnClickListener(this)
        btnUpd.setOnClickListener(this)
        btnDel.setOnClickListener(this)
        btnClear.setOnClickListener(this)
        // создаем объект для создания и управления версиями БД
        dbHelper = DBHelper(this)
    }

    override fun onClick(v: View) { // создаем объект для данных
        val contentValues = ContentValues()
        // получаем данные из полей ввода
        val name = etName.text.toString()
        val email = etEmail.text.toString()
        val id = etID.text.toString()
        // подключаемся к БД
        val db = dbHelper!!.writableDatabase
        when (v.id) {
            R.id.btnAdd -> {
                Log.d(LOG_TAG, "--- Insert in mytable: ---")
                // подготовим данные для вставки в виде пар: наименование столбца - значение
                contentValues.put("name", name)
                contentValues.put("email", email)
                // вставляем запись и получаем ее ID
                val rowID = db.insert("mytable", null, contentValues)
                Log.d(LOG_TAG, "row inserted, ID = $rowID")
            }
            R.id.btnRead -> {
                Log.d(LOG_TAG, "--- Rows in mytable: ---")
                // делаем запрос всех данных из таблицы mytable, получаем Cursor
                val cursor = db.query("mytable", null, null, null, null, null, null)
                // ставим позицию курсора на первую строку выборки
// если в выборке нет строк, вернется false
                if (cursor.moveToFirst()) { // определяем номера столбцов по имени в выборке
                    val idColIndex = cursor.getColumnIndex("id")
                    val nameColIndex = cursor.getColumnIndex("name")
                    val emailColIndex = cursor.getColumnIndex("email")
                    do { // получаем значения по номерам столбцов и пишем все в лог
                        Log.d(LOG_TAG,
                                "ID = " + cursor.getInt(idColIndex) + ", name = "
                                        + cursor.getString(nameColIndex) + ", email = "
                                        + cursor.getString(emailColIndex))
                        // переход на следующую строку
// а если следующей нет (текущая - последняя), то false -
// выходим из цикла
                    } while (cursor.moveToNext())
                } else Log.d(LOG_TAG, "0 rows")
                cursor.close()
            }
            R.id.btnClear -> {
                Log.d(LOG_TAG, "--- Clear mytable: ---")
                // удаляем все записи
                val clearCount = db.delete("mytable", null, null)
                Log.d(LOG_TAG, "deleted rows count = $clearCount")
            }
            R.id.btnUpd -> {
                if (id.equals("", ignoreCase = true)) {
                    return
                }
                Log.d(LOG_TAG, "--- Update mytable: ---")
                // подготовим значения для обновления
                contentValues.put("name", name)
                contentValues.put("email", email)
                // обновляем по id
                val updCount = db.update("mytable", contentValues, "id = ?", arrayOf(id))
                Log.d(LOG_TAG, "updated rows count = $updCount")
            }
            R.id.btnDel -> {
                if (id.equals("", ignoreCase = true)) {
                    return
                }
                Log.d(LOG_TAG, "--- Delete from mytable: ---")
                // удаляем по id
                val delCount = db.delete("mytable", "id = $id", null)
                Log.d(LOG_TAG, "deleted rows count = $delCount")
            }
        }
        // закрываем подключение к БД
        dbHelper?.close()
    }

    inner class DBHelper(context: Context?) : SQLiteOpenHelper(context, "myDB", null,
            1) {
        override fun onCreate(db: SQLiteDatabase) {
            Log.d(LOG_TAG, "--- onCreate database ---")
            // создаем таблицу с полями
            db.execSQL("create table mytable ("
                    + "id integer primary key autoincrement,"
                    + "name text,"
                    + "email text" + ");")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
    }
}