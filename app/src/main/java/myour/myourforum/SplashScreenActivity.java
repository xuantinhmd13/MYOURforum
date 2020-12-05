package myour.myourforum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import myour.myourforum.home.HomeActivity;
import myour.myourforum.model.Category;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity {

    private CountDownTimer timerLoadingApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        loadApp();
    }

    private void loadApp() {
        timerLoadingApp = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //do nothing.
            }

            @Override
            public void onFinish() {
                if (Program.isNetworkAvailable(SplashScreenActivity.this)) {
                    loadDataFromServer();
                } else {
                    Toast.makeText(SplashScreenActivity.this, "Không có mạng!", Toast.LENGTH_SHORT).show();
                    timerLoadingApp.start();
                }

            }
        }.start();
    }

    private void loadDataFromServer() {
        Program.request.getAllCategory().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.code() == 200) {
                    Program.categoryList = new ArrayList<>();
                    Program.categoryList = response.body();
                    startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
                    finish();
                } else
                    Toast.makeText(SplashScreenActivity.this, Program.ERR_API_SERVER, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(SplashScreenActivity.this, Program.ERR_API_FAILURE, Toast.LENGTH_SHORT).show();
                timerLoadingApp.start();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        timerLoadingApp.cancel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerLoadingApp.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timerLoadingApp.start();
    }
}