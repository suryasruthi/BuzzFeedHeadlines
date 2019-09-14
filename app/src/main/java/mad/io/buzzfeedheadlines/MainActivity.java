package mad.io.buzzfeedheadlines;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<News> newsList = new ArrayList<>();
    News newsobj;

    TextView textViewHeadline ;
    TextView textViewdate ;
    TextView textViewdata ;
    ImageView imageView ;
    Button buttonprevious ;
    Button buttonnext ;
    Button buttonQuit;
    ProgressBar progressBar;
    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("BuzzFeed Headlines");


         textViewHeadline = findViewById(R.id.textViewHeadline);
         textViewdate = findViewById(R.id.textViewdate);
         textViewdata = findViewById(R.id.textViewdata);
         imageView = findViewById(R.id.imageView);
         buttonprevious = findViewById(R.id.buttonprevious);
         buttonnext = findViewById(R.id.buttonnext);
         buttonQuit = findViewById(R.id.buttonQuit);
         progressBar = findViewById(R.id.progressBar);

         buttonQuit.setOnClickListener(new View.OnClickListener() {
             @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
             @Override
             public void onClick(View v) {
                 finishAndRemoveTask();
             }
         });

         buttonprevious.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 if(isConnected()) {
                     if (i == 0)
                         i = newsList.size();

                     i = i - 1;
                     if (i < newsList.size() && i > -1) {
                         textViewHeadline.setText(newsList.get(i).title);
                         textViewdate.setText((newsList.get(i).publishedAt).substring(0,10));
                         textViewdata.setText(newsList.get(i).description);
                         Picasso.get().load(newsList.get(i).urlToImage).into(imageView);

                         // String imageURL = newsList.get(i).urlToImage;
                         //Bitmap myBitmap = getImageBitmap(imageURL);
                         //  imageView.setImageBitmap(myBitmap);

                     }
                 }
                 else
                 {
                     Toast.makeText(MainActivity.this, "Please check your internet Connection", Toast.LENGTH_SHORT).show();
                 }
             }
         });

         buttonnext.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 if(isConnected()) {
                     if (i == newsList.size() - 1)
                         i = -1;

                     i = i + 1;
                     if (i < newsList.size() && i > -1) {
                         textViewHeadline.setText(newsList.get(i).title);
                         textViewdate.setText((newsList.get(i).publishedAt).substring(0,10));
                         textViewdata.setText(newsList.get(i).description);
                         Picasso.get().load(newsList.get(i).urlToImage).into(imageView);
                     }
                 }
                 else
                 {
                     Toast.makeText(MainActivity.this, "Please check your internet Connection", Toast.LENGTH_SHORT).show();
                 }

             }
         });




        new loadNewsAsync().execute("https://newsapi.org/v2/top-headlines?sources=buzzfeed&apiKey=d41dd8dc0b224120b7a969dd6dd12e9e");

    }


    class loadNewsAsync extends AsyncTask<String, String, News> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(News s) {
            super.onPostExecute(s);

            progressBar.setVisibility(View.INVISIBLE);
            textViewHeadline.setText(s.title);
            textViewdate.setText(s.publishedAt.substring(0,10));
            textViewdata.setText(s.description);
            /*String imageURL = s.urlToImage;
            //Bitmap myBitmap = getImageBitmap(imageURL);
           //imageView.setImageBitmap(myBitmap);
           */

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected News doInBackground(String... strings) {

            try {
                String json = getAPIdata(strings[0].toString());
                JSONObject root = new JSONObject(json);
                JSONArray articlesArray = root.getJSONArray("articles");

                for (int i = 0; i < articlesArray.length(); i++) {
                    JSONObject articleJSON = new JSONObject();
                    articleJSON = articlesArray.getJSONObject(i);
                     newsobj = new News();
                    newsobj.setTitle(articleJSON.getString("title"));
                    newsobj.setDescription(articleJSON.getString("description"));
                    newsobj.setPublishedAt(articleJSON.getString("publishedAt"));
                    newsobj.setUrlToImage(articleJSON.getString("urlToImage"));
                    newsList.add(newsobj);
                }

                Log.d("demo", "doInBackground: "+json);
                Log.d("demo", "newsList: "+newsList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return newsobj;
        }
    }

    private String getAPIdata(String toString) {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(toString);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();


            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
                Log.d("demo", "getAPIdata: " + buffer.toString());
            }

            return buffer.toString();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    Bitmap getImageBitmap(String... strings) {
        try {

            URL url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI &&
                        networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }
}
