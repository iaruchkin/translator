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

import static ruchkin.igor.translator.MainActivity.lang;
import static ruchkin.igor.translator.MainActivity.trans;
import static ruchkin.igor.translator.MainActivity.words;

public class History extends AppCompatActivity {

    public static String LOG_TAG = "my_log";
    final String FILENAME = "file";

   static ArrayList<Item> items = new ArrayList<Item>();
    FavAdapter favAdapter;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        readFile();
        // создаем адаптер

        favAdapter = new FavAdapter(this, items);

        // настраиваем список
        ListView lvMain = (ListView) findViewById(R.id.lvMain);
        lvMain.setAdapter(favAdapter);
    }
   static int i;
    // получаем данные для адаптера
      public static void fillData() {
          if ((words.length() != 0)) {

              try {
                  TimeUnit.SECONDS.sleep(1);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }

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
        writeFile();
    }
    @Override
    protected void onPause(){
        super.onPause();
        writeFile();
    }
    @Override
    protected void onResume(){
        super.onResume();
        readFile();
    }

    void writeFile() {

        try {
        FileOutputStream fos =
                new FileOutputStream(
                        new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS), "name.ser")
                );
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(items);
        os.close();
        Log.d("MyApp","File has been written");
    } catch(Exception ex) {
        ex.printStackTrace();
        Log.d("MyApp","File didn't write");
    }

    }


    void readFile() {
        FileInputStream fis;
        try {
            fis = openFileInput("CalEvents");
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<Object> returnlist = (ArrayList<Object>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
