package ruchkin.igor.translator;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;


public class LanguageActivity extends AppCompatActivity {

    ListView lvMain;
    static int i=93;

    public static String[] names=new String[i];
    public static String[] codes=new String[i];

    public static final String LOG_TAG = "my_log";
    String lg="",code="",ui="ru";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ParseLanguage().execute();

        setContentView(R.layout.activity_language);
                lvMain = (ListView) findViewById(R.id.lvMain);

        // создаем адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        // присваиваем адаптер списку
        lvMain.setAdapter(adapter);

        lvMain.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v2,
                                    int position, long id) {
                Log.d(LOG_TAG, "itemClick: position = " + position);
                lg=names[position];
                code=codes[position];
                Log.d(LOG_TAG, "lg = " + lg);

                Intent intent = new Intent();
                intent.putExtra("language", lg);
                intent.putExtra("code", code);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    //получение списка языков
    public class ParseLanguage extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "test";

        @Override
        public String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            String API_KEY="trnsl.1.1.20170330T164132Z.fc6610cc34ee6a54.75b432b125d415fdf12a173d25329ed124e73458";

            try {
                String urlStr="https://translate.yandex.net/api/v1.5/tr.json/getLangs?key="+API_KEY+"&ui=" + ui;
                URL url = new URL(urlStr);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                resultJson = buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        public void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            JSONObject dataJsonObj = null;

            try {
                dataJsonObj = new JSONObject(strJson);

                String langStr = dataJsonObj.getString("langs");
                langStr=langStr.substring(1, langStr.length()-1);

                Log.d(LOG_TAG,"langs: " + langStr);
                //сделать массив из строки
                String[] langArr = langStr.split(",");

                //сортировка по алфавиту
                Arrays.sort(langArr, new Comparator<String>() {
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });

                int j=langArr.length; //93
                 for(int i=0;i<j;i++){
                    String[] Array=langArr[i].split(":");
                    names[i]=Array[1].substring(1, Array[1].length()-1);;
                    codes[i]=Array[0].substring(1, Array[0].length()-1);;
                }

                Log.d(LOG_TAG,"j: " + j);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
