package ruchkin.igor.translator;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class MainActivity extends LanguageActivity implements OnClickListener, OnEditorActionListener {

    public static String LOG_TAG = "my_log",lang="en-ru",trans="",words="";
    public static int langCheck=0;
    public static String code1="en",code2="ru",lg1="Английский",lg2="Русский";

    TextView tText,def1;
    EditText editText;
    Button lang1;
    Button lang2;
    Button translate;
    ImageButton swap;

    TextView link;

    Button history;
    Button favorites;

    SharedPreferences sPref;
    final String SAVED_TEXT = "saved_text";

    DBHelper dbHelper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            // найдем View-элементы
            tText = (TextView) findViewById(R.id.tText);
            lang1 = (Button) findViewById(R.id.lang1);
            lang2 = (Button) findViewById(R.id.lang2);
            swap = (ImageButton) findViewById(R.id.swap);
            editText = (EditText) findViewById(R.id.editText);
            def1 = (TextView) findViewById(R.id.def1);
            link=(TextView) findViewById(R.id.link);
            history=(Button)findViewById(R.id.history);
            favorites=(Button)findViewById(R.id.favorites);

            // присвоим обработчик
            swap.setOnClickListener(this);
            lang1.setOnClickListener(this);
            lang2.setOnClickListener(this);
            history.setOnClickListener(this);
            favorites.setOnClickListener(this);

            lang1.setText(lg1);
            lang2.setText(lg2);

            editText.setOnEditorActionListener(this);

            loadText();
            // создаем объект для создания и управления версиями БД
            dbHelper = new DBHelper(this);

        String yand_link="Переведено сервисом «Яндекс.Переводчик»"+"<br><a href=\"http://translate.yandex.ru/\">translate.yandex.ru</a>";
        link.setText(Html.fromHtml(yand_link));

        }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        saveText();
    }
    @Override
    protected void onPause(){
        super.onPause();
        saveText();
    }
    @Override
    protected void onResume(){
        super.onResume();
        //loadText();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "onRestart");
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(LOG_TAG, "onRestoreInstanceState");
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState");
    }

    void saveText() {
        sPref = getPreferences(MODE_PRIVATE);
        Editor ed = sPref.edit();
        ed.putString(SAVED_TEXT, editText.getText().toString());
        ed.commit();
        //writeDB();
      //  Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
    }

    void loadText() {
        sPref = getPreferences(MODE_PRIVATE);
        String savedText = sPref.getString(SAVED_TEXT, "");
        editText.setText(savedText);

       // Toast.makeText(this, "Text loaded", Toast.LENGTH_SHORT).show();
    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {

            // обрабатываем нажатие кнопки enter
            new ParseTranslator().execute();
            new ParseDictionatry().execute();

            words = editText.getText().toString();

            writeDB();

            return true;
        }
         return false;
    }

    @Override
    public void onClick(View v) {

        // по id определеяем кнопку, вызвавшую этот обработчик
        new ParseTranslator().execute();
        new ParseLanguage().execute();
        new ParseDictionatry().execute();


        switch (v.getId()) {
            case R.id.swap:
                String lgBuff="";
                lgBuff=lg1;
                lg1=lg2;
                lg2=lgBuff;

                lang1.setText(lg1);
                lang2.setText(lg2);

                String codeBuff=code1;
                code1=code2;
                code2=codeBuff;

                lang = code1 + "-" + code2;
                editText.setText(tText.getText());
                break;

            case R.id.lang1:
                if ( !isOnline() ){
                    Toast.makeText(getApplicationContext(),
                            "Нет соединения с интернетом!",Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    langCheck=1;
                    Intent intent1 = new Intent(this, LanguageActivity.class);
                    startActivityForResult(intent1,1);
                }
                break;

            case R.id.lang2:
                if ( !isOnline() ){
                    Toast.makeText(getApplicationContext(),
                            "Нет соединения с интернетом!",Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    langCheck = 2;
                    Intent intent2 = new Intent(this, LanguageActivity.class);
                    startActivityForResult(intent2, 1);
                }
                    break;
            case R.id.history:
                //HistorySQL.fillData();
                // передаем данные на другое активити
                Intent intent3 = new Intent(this, HistorySQL.class);
                startActivity(intent3);
                break;

            case R.id.favorites:
                Intent intent4 = new Intent(this, Favorites.class);
                startActivity(intent4);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        //по нажатой кнопке определяем куда записать язык
        switch (langCheck) {
            case 0:
                lang = code1 + "-" + code2;
                break;
            case 1:
                lg1 = data.getStringExtra("language");
                code1 = data.getStringExtra("code");
                lang1.setText(lg1);
                lang = code1 + "-" + code2;
                break;
            case 2:
                lg2 = data.getStringExtra("language");
                code2 = data.getStringExtra("code");
                lang2.setText(lg2);
                lang = code1 + "-" + code2;
                break;
        }

     }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    protected boolean isOnline() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(cs);
                if (cm.getActiveNetworkInfo() == null) {
                    return false;
                    }
                else {
                    return true;
                }
    }

    public void writeDB() {
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

public class ParseTranslator extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "test";
        String TEXT = editText.getText().toString();

        @Override
        public String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
           String API_KEY="trnsl.1.1.20170330T164132Z.fc6610cc34ee6a54.75b432b125d415fdf12a173d25329ed124e73458";

            try {
                String urlStr="https://translate.yandex.net/api/v1.5/tr.json/translate?key="+API_KEY+"&lang=" + lang + "&text=" + URLEncoder.encode(TEXT, "UTF-8");
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
                // выводим целиком полученную json-строку
                Log.d(LOG_TAG, "json-строка переводчика: " + strJson);

                JSONObject dataJsonObj = null;
                String jsonTR = "";

                try {
                    dataJsonObj = new JSONObject(strJson);
                    JSONArray translation = dataJsonObj.getJSONArray("text");
                    jsonTR = translation.toString();
                    String TR=jsonTR.substring(2, jsonTR.length()-2);
                    trans=TR;
                    Log.d(LOG_TAG, "перевод: " + TR);
                    tText.setText(TR);

                    String code = dataJsonObj.getString("code");
                    Log.d(LOG_TAG, "код: " + code);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
    }

public class ParseDictionatry extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "test";
        String TEXT = editText.getText().toString();
        String defT="",text = "",pos = "",ts = "",gen="", asp="",meanT="";


        @Override
        public String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            String API_KEY="dict.1.1.20170413T163855Z.fd85b48720e0e846.63fd75ea52802756222396843ce1e6f73a802c7c";

            try {
                //String urlStr="https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=dict.1.1.20170413T163855Z.fd85b48720e0e846.63fd75ea52802756222396843ce1e6f73a802c7c&lang=en-ru&text=time";
                String urlStr="https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key="+API_KEY+"&lang=" + lang + "&text=" + URLEncoder.encode(TEXT, "UTF-8")+ "&ui=ru";
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

            // выводим целиком полученную json-строку
            Log.d(LOG_TAG, "json-строка словаря: " + strJson);
            JSONObject dataJsonObj = null;

            try {
                dataJsonObj = new JSONObject(strJson);

                JSONArray dictionary = dataJsonObj.getJSONArray("def");
                // достаем содержимое объекта - text
                for (int i = 0; i < dictionary.length(); i++) {
                    JSONObject def = dictionary.getJSONObject(i);
                    if (def.opt("text")!=null) {
                        text = def.getString("text");
                    }
                    if (def.opt("pos")!=null){
                        pos = def.getString("pos");
                    }
                    if (def.opt("ts")!=null){
                        ts = def.getString("ts");
                    }

                    Log.d(LOG_TAG, "общая информация: " + text + " " + ts + " " + pos);

                    //форматирование
                    text="<font color=\"black\">" + text + "</font>";
                    ts = "<font color=\"grey\">" + " " + "[" + ts + "]" + "</font>";
                    pos = "<br><i><font size="+"5"+" color=\"grey\">" + pos + "</font></i><br>";


                            if (i==0) {
                                defT = text + " " + ts + pos;
                            }

                            if (i>0) {
                                defT += pos;
                            }

                    // уровень глубже - перевод
                    JSONArray translation = def.getJSONArray("tr");
                    for (int j = 0; j < translation.length(); j++) {
                        JSONObject tr = translation.getJSONObject(j);

                        if (tr.opt("text")!=null){
                            text = tr.getString("text");
                        }
                        if (tr.opt("pos")!=null){
                            pos = tr.getString("pos");
                        }
                        if (tr.opt("gen")!=null){
                            gen = tr.getString("gen");
                        }
                        if (tr.opt("asp")!=null){
                            asp = tr.getString("asp");
                        }
                        Log.d(LOG_TAG, "перевод: " + text + " " + pos + " " + asp + gen );

                        //форматирование
                        gen = "<i><font color=\"grey\">" + " " + gen + "</font></i>";

                        if(i==0) {
                            defT += text + gen;
                        }

                        if(i>0) {
                            defT += text;
                        }

                            if (tr.optJSONArray("syn")!=null) {
                            JSONArray synonym = tr.getJSONArray("syn");
                            for (int k = 0; k < synonym.length(); k++) {
                                JSONObject syn = synonym.getJSONObject(k);

                                if (syn.opt("text")!=null){
                                    text = syn.getString("text");
                                }
                                if (syn.opt("pos")!=null){
                                    pos = syn.getString("pos");
                                }
                                if (syn.opt("gen")!=null){
                                    gen = syn.getString("gen");
                                }
                                if (syn.opt("asp")!=null){
                                    asp = syn.getString("asp");
                                }

                                //форматирование
                                gen = "<i><font color=\"grey\">" + " " + gen + "</font></i>";

                                if(i==0) {
                                    defT += ", " + text + " " + gen;
                                }
                                if(i>0) {
                                    defT += ", " + text;
                                }

                                Log.d(LOG_TAG, "синонимы: " + text + " " + pos + " " + asp + gen);
                            }
                        }

                        if (tr.optJSONArray("mean")!=null) {
                            JSONArray meaning = tr.getJSONArray("mean");
                                for (int ii = 0; ii < meaning.length(); ii++) {
                                    JSONObject mean = meaning.getJSONObject(ii);
                                    text = mean.getString("text");

                                    if(ii<meaning.length()-1) {
                                        meanT +=text+", ";
                                    }
                                    if(ii == meaning.length()-1) {
                                        meanT += text;
                                        //форматирование
                                        String mean1F = "<br><i><font color=\"olive\">" + "(" + meanT + ")" + "</font></i><br>";
                                        meanT="";
                                        if(i == 0) {
                                            defT+= mean1F;
                                        }
                                        if(i >0) {
                                            defT+= mean1F;
                                        }
                                    }
                                    Log.d(LOG_TAG, "значение: " + text);
                                }
                        }

                        if (tr.optJSONArray("ex")!=null) {
                            JSONArray explan = tr.getJSONArray("ex");
                                for (int jj = 0; jj < explan.length(); jj++) {
                                    JSONObject ex = explan.getJSONObject(jj);
                                    text = ex.getString("text");
                                    Log.d(LOG_TAG, "пояснение: " + text);

                                    //форматирование
                                    String ex1T = "<br><i><font color=\"grey\">" + text  + " - " + "</font></i>";
                                    if(i==0) {
                                        defT += ex1T;
                                    }
                                    if(i>0) {
                                        defT += ex1T;
                                    }

                                    JSONArray translate = ex.getJSONArray("tr");
                                    for (int kk = 0; kk < translate.length(); kk++) {
                                        JSONObject extr = translate.getJSONObject(kk);
                                        text = extr.getString("text");
                                        Log.d(LOG_TAG, "перевод: " + text);

                                        //форматирование
                                        String extr1T = "<i><font color=\"grey\">" + text  + "</font></i>";

                                        if(jj==explan.length()-1) {
                                            extr1T+="<br><br>";
                                        }

                                        if(i==0) {
                                            defT+= extr1T;
                                        }
                                        if(i==1) {
                                            defT+= extr1T;
                                        }
                                        if(i==2) {
                                            defT+= extr1T;
                                        }

                                        def1.setText(Html.fromHtml(defT));

                                    }
                            }
                        }

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


   }
