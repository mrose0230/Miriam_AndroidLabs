package algonquin.cst2335.rose0230;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

import algonquin.cst2335.rose0230.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    RequestQueue queue = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //This part goes at the top of the onCreate function:
        queue = Volley.newRequestQueue(this);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


        binding.loginButton.setOnClickListener( click ->  {
            String cityName = binding.passwordText.getText().toString();

            //this goes in the button click handler:
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + URLEncoder.encode(cityName) +
                    "&appid=7e943c97096a9784391a981c4d878b22&units=metric";

            // ...

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        // This is called if it worked

                        try {
                            JSONObject main = response.getJSONObject("main");
                            double temperature = main.getDouble("temp");

                            // Get temperature, minimum, and humidity
                            double minimum = main.getDouble("temp_min");
                            int humidity = main.getInt("humidity");

                            // Get weather array
                            JSONArray weather = response.getJSONArray("weather");
                            int arraySize = weather.length();
                            String conditions = "";

                            for (int i = 0; i < arraySize; i++) {
                                JSONObject obj = weather.getJSONObject(i);
                                String iconName = obj.getString("icon");

                                // Get weather description
                                String description = obj.getString("description");
                                conditions += description;
                                if (i < arraySize - 1) {
                                    conditions += ", ";
                                }

                                String imageUrl = "http://openweathermap.org/img/w/" + iconName + ".png";

                                File weatherImage = new File(getFilesDir() + "/" + iconName + ".png");

                                if (weatherImage.exists()) {
                                    Bitmap theImage = BitmapFactory.decodeFile(weatherImage.getAbsolutePath());
                                    binding.weatherImage.setImageBitmap(theImage);
                                } else {
                                    ImageRequest imgReq = new ImageRequest(imageUrl,
                                            (Response.Listener<Bitmap>) bitmap -> {
                                                // Do something with loaded bitmap...
                                                FileOutputStream fOut = null;
                                                try {
                                                    fOut = openFileOutput(iconName + ".png", Context.MODE_PRIVATE);
                                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                                                    fOut.flush();
                                                    fOut.close();
                                                    binding.weatherImage.setImageBitmap(bitmap);
                                                } catch (FileNotFoundException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }, 1024, 1024, ImageView.ScaleType.CENTER, null,
                                            (error) -> {
                                                Log.e("MainActivity", "ImageRequest Error: " + error.toString());
                                            });

                                    queue.add(imgReq);
                                }
                            }

                            // Update TextViews for temperature and conditions
                            binding.temperatureText.setText("Temperature: " + temperature + "°C");
                            binding.conditionsText.setText("Conditions: " + conditions);

                        } catch (JSONException e) {
                            // Handle JSON parsing error
                            Log.e("MainActivity", "Error parsing JSON: " + e.getMessage());
                        }
                    },
                    error -> {
                        // This is called if there is an error
                        Log.e("MainActivity", "Error: " + error.toString());
                    });

            queue.add(request); // This actually launches the request onResponse, or onErrorResponse will get called


        });

    }

}