package algonquin.cst2335.rose0230;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Switch;


public class MainActivity extends AppCompatActivity {
private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        Log.w(TAG, "In onCreate() - Loading Widgets");


        binding.loginButton.setOnClickListener(click ->{
            Log.i(TAG, "you clicked the button");

            Intent nextPage = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(nextPage);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(TAG, "application is now visible on the screen");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(TAG, "application is now responding to user input");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(TAG, "application no longer responds to user input");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(TAG, "application is no longer visible");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "any memory used by the application is freed");
    }


}