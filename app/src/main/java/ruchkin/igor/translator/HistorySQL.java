package ruchkin.igor.translator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;

import static ruchkin.igor.translator.DBService.*;

public class HistorySQL extends AppCompatActivity implements View.OnClickListener {

    static ArrayList<Item> items = new ArrayList<Item>();
    FavAdapter favAdapter;
    Button clean;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        clean=(Button)findViewById(R.id.clean);
        clean.setOnClickListener(this);
        // создаем объект для создания и управления версиями БД
        dbHelper = new DBHelper(this);

        // создаем адаптер
        favAdapter = new FavAdapter(this, items);

        // настраиваем список
        ListView lvMain = (ListView) findViewById(R.id.lvMain);
        lvMain.setAdapter(favAdapter);

        readDB();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clean:
                cleanDB();
                Toast.makeText(this, "History deleted", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // получаем данные для адаптера
    public static void fillData(String words, String trans, String lang) {
          if ((words.length() != 0)) {
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
        dbHelper.close();
    }
    @Override
    protected void onPause(){
        super.onPause();
        dbHelper.close();
    }
    @Override
    protected void onResume(){
        super.onResume();
        readDB();
    }
}