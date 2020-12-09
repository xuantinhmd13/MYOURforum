package myour.myourforum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

import myour.myourforum.home.HomeActivity;
import myour.myourforum.model.Category;
import myour.myourforum.model.User;
import myour.myourforum.util.LoadingScreen;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity {

    private CountDownTimer timerLoadingApp;
    private String TAG = "#SplashScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        loadApp();
    }

    private void loadApp() {
        timerLoadingApp = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //do nothing.
            }

            @Override
            public void onFinish() {
                if (Program.isNetworkAvailable(SplashScreenActivity.this)) {
                    getDataFromServer();
                } else {
                    Toast.makeText(SplashScreenActivity.this, "Không có mạng!", Toast.LENGTH_SHORT).show();
                    timerLoadingApp.start();
                }
            }
        }.start();
    }

    private void getDataFromServer() {
        Program.request.getAllCategory().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.code() == 200 && response.body() != null) {
                    Program.categoryList = response.body();
                    //Program.categoryList = response.body();
                    autoLogin();
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

    private void autoLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("#login", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("#rememberLogin", false)) {
            Program.request.login(sharedPreferences.getString("#email", "")).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.code() == 200 && response.body() != null) {
                        loginAsyncTask(response.body(), sharedPreferences.getString("#password", ""));
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    //do nothing.
                }
            });
        } else {
            startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
            finish();
        }
    }

    private void login(Boolean result, User userLogin) {
        if (result) {
            Program.user = new User();
            Program.user = userLogin;
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
        }
        startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
        finish();
    }

    private void loginAsyncTask(User userLogin, String passwordLogin) {
        new AsyncTask<String, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Toast.makeText(SplashScreenActivity.this, "Kiểm tra đăng nhập ...", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected Boolean doInBackground(String... strings) {
                return BCrypt.checkpw(strings[0], strings[1]);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                login(result, userLogin);
            }
        }.execute(passwordLogin, userLogin.getPassword());
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerLoadingApp.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (timerLoadingApp != null) {
            timerLoadingApp.cancel();
            timerLoadingApp.start();
        }
    }
}