package ruchkin.igor.translator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import static ruchkin.igor.translator.HistorySQL.fillData;
import static ruchkin.igor.translator.MainActivity.LOG_TAG;
import static ruchkin.igor.translator.MainActivity.lang;
import static ruchkin.igor.translator.MainActivity.trans;
import static ruchkin.igor.translator.MainActivity.words;


public class DBService {

    public static DBHelper dbHelper;

    public static void writeDB() {
        // создаем объект для данных
        ContentValues cv = new ContentValues();
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Log.d(LOG_TAG, "--- Insert in Htable: ---");
        // подготовим данные для вставки в виде пар: наименование столбца - значение
        cv.put("word", words);
        cv.put("trans", trans);
        cv.put("lang", lang);
        // вставляем запись и получаем ее ID
        long rowID = db.insert("Htable", null, cv);
        Log.d(LOG_TAG, "row inserted, ID = " + rowID);
    }

    public static void readDB() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Log.d(LOG_TAG, "--- Rows in Htable table: ---");
        // делаем запрос всех данных из таблицы HistoryDB, получаем Cursor
        Cursor c = db.query("Htable", null, null, null, null, null, null);

        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int wordColIndex = c.getColumnIndex("word");
            int transColIndex = c.getColumnIndex("trans");
            int langColIndex = c.getColumnIndex("lang");

            do {
                // получаем значения по номерам столбцов и пишем все в лог и в переменные
                Log.d(LOG_TAG,
                        "ID = " + c.getInt(idColIndex) +
                                ", word = " + c.getString(wordColIndex) +
                                ", trans = " + c.getString(transColIndex) +
                                ", lang = " + c.getString(langColIndex));
                fillData(c.getString(wordColIndex),c.getString(transColIndex),c.getString(langColIndex));
                // fillData("112","sdada","sds");
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();
    }

    public static void cleanDB() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "--- Clear Htable: ---");
        // удаляем все записи
        int clearCount = db.delete("Htable", null, null);
        Log.d(LOG_TAG, "deleted rows count = " + clearCount);
    }

}

