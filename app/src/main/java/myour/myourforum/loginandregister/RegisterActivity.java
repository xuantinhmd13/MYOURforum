package myour.myourforum.loginandregister;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.mindrot.jbcrypt.BCrypt;

import myour.myourforum.Program;
import myour.myourforum.api.RESTfulAPIService;
import myour.myourforum.databinding.ActivityRegisterBinding;
import myour.myourforum.model.User;
import myour.myourforum.util.LoadingScreen;
import myour.myourforum.util.Validation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = "#RegisterActivity";
    private ActivityRegisterBinding binding;
    private String emailRegister;
    private String passwordRegister;
    private String usernameRegister;
    private String password;
    private String rePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setControl();
        setEvent();
    }

    private void setEvent() {
        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButtonBack();
            }
        });

        binding.editTextRePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //do nothing.
            }

            @Override
            public void afterTextChanged(Editable s) {
                isRePasswordMatch();
            }
        });

        binding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButtonRegister();
            }
        });
    }

    private void clickButtonBack() {
        onBackPressed();
    }

    private void clickButtonRegister() {
        getInData();
        if (isValidInputData()) {
            encryptPassword();
        }
    }

    private void encryptPassword() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                LoadingScreen.show(RegisterActivity.this);
            }

            @Override
            protected String doInBackground(String... strings) {
                return BCrypt.hashpw(strings[0], BCrypt.gensalt(Program.BCRYPT_LOG_ROUND));
            }

            @Override
            protected void onPostExecute(String passwordEncrypted) {
                super.onPostExecute(passwordEncrypted);
                LoadingScreen.hide();
                register(passwordEncrypted);
            }
        }.execute(passwordRegister);
    }

    private void register(String passwordEncrypted) {
        User userRegister = getUserRegister(passwordEncrypted);
        requestRegister(userRegister);
    }

    private void requestRegister(User userRegister) {
        LoadingScreen.show(this);
        RESTfulAPIService.request.register(userRegister).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                LoadingScreen.hide();
                if (response.code() == 200 && response.body() != null) {
                    switch (response.body()) {
                        case "email":
                            binding.editTextEmail.setError("Email này đã tồn tại!");
                            binding.editTextEmail.requestFocus();
                            break;
                        case "username":
                            binding.editTextUsername.setError("Tên này đã được sử dụng!");
                            binding.editTextUsername.requestFocus();
                            break;
                        case "OK":
                            Toast.makeText(RegisterActivity.this, "Đăng kí thành công!", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK, new Intent().putExtra("#email", userRegister.getEmail()));
                            finish();
                            break;
                    }
                } else
                    Toast.makeText(RegisterActivity.this, Program.ERR_API_SERVER, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                LoadingScreen.hide();
                Toast.makeText(RegisterActivity.this, Program.ERR_API_FAILURE, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private User getUserRegister(String passwordEncrypted) {
        User userRegister = new User();
        userRegister.setEmail(emailRegister);
        userRegister.setUsername(usernameRegister);
        userRegister.setPassword(passwordEncrypted);
        userRegister.setCreateTime(Program.getDateTimeNow());
        return userRegister;
    }

    private void getInData() {
        emailRegister = binding.editTextEmail.getText().toString().trim();
        passwordRegister = binding.editTextPassword.getText().toString().trim();
        usernameRegister = binding.editTextUsername.getText().toString().trim();
        password = binding.editTextPassword.getText().toString().trim();
        rePassword = binding.editTextRePassword.getText().toString().trim();
    }

    private boolean isValidInputData() {
        if (Validation.getErrEmail(emailRegister) != null) {
            binding.editTextEmail.setError(Validation.getErrEmail(emailRegister));
            binding.editTextEmail.requestFocus();
            return false;
        }
        if (Validation.getErrUsername(usernameRegister) != null) {
            binding.editTextUsername.setError(Validation.getErrUsername(usernameRegister));
            binding.editTextUsername.requestFocus();
            return false;
        }
        if (Validation.getErrPassword(passwordRegister, true) != null
                || !isRePasswordMatch()) {
            binding.editTextPassword.setError(Validation.getErrPassword(passwordRegister, true));
            binding.editTextPassword.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isRePasswordMatch() {
        password = binding.editTextPassword.getText().toString().trim();
        rePassword = binding.editTextRePassword.getText().toString().trim();
        if (!password.equals(rePassword)) {
            binding.editTextRePassword.setError("Mật khẩu không khớp!");
            binding.editTextRePassword.requestFocus();
            return false;
        }
        return true;
    }

    private void setControl() {
        //do nothing.
    }
}