package algonquin.cst2335.rose0230;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
    String latInput;
    String longInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //This part goes at the top of the onCreate function:
        queue = Volley.newRequestQueue(this);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


        binding.sendButton.setOnClickListener( click ->  {
            String latitude = null;
            String longitude = null;

            String url = "api.sunrisesunset.io/json?lat="+ latitude +"&lng="+longitude+"&timezone=CA&date=today";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        // this is called if it worked

                        try {
                            JSONObject results = response.getJSONObject("results") ;

                            String sunrise = results.getString("sunrise");
                            String sunset = results.getString("sunset");

                            }
                        catch (JSONException e){
                            throw new RuntimeException(e);
                        }



                    });



            queue.add(request); //this actually launches the request onResponse, or onErrorResponse will get called

        });

    }

}
