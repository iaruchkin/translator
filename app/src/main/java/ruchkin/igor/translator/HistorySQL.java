package ruchkin.igor.translator;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


import static ruchkin.igor.translator.MainActivity.lang;
import static ruchkin.igor.translator.MainActivity.trans;
import static ruchkin.igor.translator.MainActivity.words;

import static ruchkin.igor.translator.MainActivity.LOG_TAG;


public class HistorySQL extends AppCompatActivity {

    DBHelper dbHelper;

    static ArrayList<Item> items = new ArrayList<Item>();
    FavAdapter favAdapter;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // создаем объект для создания и управления версиями БД
        dbHelper = new DBHelper(this);

        // создаем адаптер
        favAdapter = new FavAdapter(this, items);

        // настраиваем список
        ListView lvMain = (ListView) findViewById(R.id.lvMain);
        lvMain.setAdapter(favAdapter);

        //readDB();
    }
    // получаем данные для адаптера
    public static void fillData(String words, String trans, String lang) {
          if ((words.length() != 0)) {
             /*try {
                  TimeUnit.SECONDS.sleep(1);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }*/
              items.add(new Item(words, trans, lang, false));
          }
      }

    // выводим информацию об избранном
    public void Favorites(View v) {
        String result = "Избранное:";
        for (Item p : favAdapter.getBox()) {
            if (p.box)
                result += "\n" + p.word;
        }
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        /*
        for (Item p : favAdapter.getBox()) {
            if (p.box)
                Favorites.fillFavs();
        }
         */

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //writeDB();
        //dbHelper.close();
    }
    @Override
    protected void onPause(){
        super.onPause();
        //writeDB();
        //dbHelper.close();
    }
    @Override
    protected void onResume(){
        super.onResume();
        readDB();
    }

    void readDB() {

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

    void clearDB() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "--- Clear Htable: ---");
        // удаляем все записи
        int clearCount = db.delete("Htable", null, null);
        Log.d(LOG_TAG, "deleted rows count = " + clearCount);
    }
}