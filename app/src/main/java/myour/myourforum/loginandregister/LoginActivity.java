package myour.myourforum.loginandregister;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.mindrot.jbcrypt.BCrypt;

import myour.myourforum.Program;
import myour.myourforum.databinding.ActivityLoginBinding;
import myour.myourforum.model.User;
import myour.myourforum.util.LoadingScreen;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private String emailLogin;
    private String passwordLogin;
    private String TAG = "#LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setControl();
        setEvent();
    }

    private void setEvent() {

        binding.textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "forgot pass", Toast.LENGTH_SHORT).show();
                //TODO:
            }
        });

        binding.textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTextViewRegister();
            }
        });

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButtonLogin(v);
            }
        });

        binding.imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void clickTextViewRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivityForResult(intent, Program.REQUEST_CODE_LOGIN);
    }

    private void clickButtonLogin(View v) {
        getDataFromEditText();
        if (isValidInputData()) {
            //TODO:
            LoadingScreen.show(this);
            Program.request.login(emailLogin).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    LoadingScreen.hide();
                    if (response.code() == 200 && response.body() != null) {
                        loginAsyncTask(response.body());
                    } else if (response.code() == 204) {
                        binding.editTextEmail.setError("Email này không khớp với bất kì tài khoản nào!");
                        binding.editTextEmail.requestFocus();
                    } else
                        Toast.makeText(LoginActivity.this, Program.ERR_API_SERVER, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    LoadingScreen.hide();
                    Toast.makeText(LoginActivity.this, Program.ERR_API_FAILURE, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loginAsyncTask(User userLogin) {
        new AsyncTask<String, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                LoadingScreen.show(LoginActivity.this);
            }

            @Override
            protected Boolean doInBackground(String... strings) {
                return BCrypt.checkpw(strings[0], strings[1]);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                LoadingScreen.hide();
                login(result, userLogin);
            }
        }.execute(passwordLogin, userLogin.getPassword());
    }

    private void login(Boolean result, User userLogin) {
        if (result) {
            Program.user = new User();
            Program.user = userLogin;
            setUpRemeberAccount(true);
            finish();
        } else {
            binding.editTextPassword.setError("Sai mật khẩu!");
            binding.editTextPassword.requestFocus();
        }
    }

    private boolean isValidInputData() {
        if (Validation.getEmailErr(emailLogin) != null) {
            binding.editTextEmail.setError(Validation.getEmailErr(emailLogin));
            binding.editTextEmail.requestFocus();
            return false;
        }
        if (Validation.getPasswordErr(passwordLogin, false) != null) {
            binding.editTextPassword.setError(Validation.getPasswordErr(passwordLogin, false));
            binding.editTextPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void getDataFromEditText() {
        emailLogin = binding.editTextEmail.getText().toString().trim();
        passwordLogin = binding.editTextPassword.getText().toString().trim();
    }

    private void setControl() {
        setUpRemeberAccount(false);
    }

    private void setUpRemeberAccount(boolean isLoginSuccess) {
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        if (!isLoginSuccess) {
            binding.editTextEmail.setText(sharedPreferences.getString("email", ""));
            binding.editTextPassword.setText(sharedPreferences.getString("password", ""));
            binding.checkBoxRemember.setChecked(sharedPreferences.getBoolean("remember", false));
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (binding.checkBoxRemember.isChecked()) {
                editor.putString("email", emailLogin);
                editor.putString("password", passwordLogin);
                editor.putBoolean("remember", true);
                editor.apply();
            } else {
                editor.remove("email");
                editor.remove("password");
                editor.remove("remember");
                editor.apply();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Program.REQUEST_CODE_LOGIN && resultCode == RESULT_OK && data != null) {
            binding.editTextEmail.setText(data.getStringExtra("#email"));
            binding.editTextPassword.setText("");
            binding.checkBoxRemember.setChecked(false);
        }
    }
}