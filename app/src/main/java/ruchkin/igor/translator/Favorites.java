package ruchkin.igor.translator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

import static ruchkin.igor.translator.MainActivity.lang;
import static ruchkin.igor.translator.MainActivity.trans;
import static ruchkin.igor.translator.MainActivity.words;

public class Favorites extends AppCompatActivity {

    static ArrayList<Item> favs = new ArrayList<Item>();
    FavAdapter favAdapter;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // создаем адаптер
        //fillFavs();
        favAdapter = new FavAdapter(this, favs);

        // настраиваем список
        ListView lvMain = (ListView) findViewById(R.id.lvMain);
        lvMain.setAdapter(favAdapter);
    }

    // получаем данные для адаптера
    public static void fillFavs() {
            favs.add(new Item(words, trans, lang, false));
    }
}