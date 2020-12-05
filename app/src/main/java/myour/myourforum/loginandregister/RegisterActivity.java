package myour.myourforum.loginandregister;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;

import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.List;

import myour.myourforum.Program;
import myour.myourforum.databinding.ActivityRegisterBinding;
import myour.myourforum.model.User;
import myour.myourforum.util.LoadingScreen;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    private String emailRegister;
    private String passwordRegister;
    private String usernameRegister;
    private String TAG = "#RegisterActivity";

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
        getDataFromEditText();
        if (isValidInputData()) {
            User userRegister = new User();
            registerAsyncTask(userRegister);
        }
    }

    private void registerAsyncTask(User userRegister) {
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
                register(userRegister, passwordEncrypted);
            }
        }.execute(passwordRegister);
    }

    private void register(User userRegister, String passwordEncrypted) {
        userRegister.setEmail(emailRegister);
        userRegister.setUsername(usernameRegister);
        userRegister.setPassword(passwordEncrypted);
        userRegister.setCreateTime(Program.getDateTimeNow());

        LoadingScreen.show(this);
        Program.request.registerUser(userRegister).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                LoadingScreen.hide();
                if (response.code() == 200) {
                    if (response.body().equals("email")) {
                        binding.editTextEmail.setError("Email này đã tồn tại!");
                        binding.editTextEmail.requestFocus();
                        //TODO:
                        return;
                    }
                    if (response.body().equals("username")) {
                        binding.editTextUsername.setError("Tên này đã được sử dụng!");
                        binding.editTextUsername.requestFocus();
                        return;
                    }
                    if (response.body().equals("OK")) {
                        Toast.makeText(RegisterActivity.this, "Đăng kí thành công!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, new Intent().putExtra("#email", userRegister.getEmail()));
                        finish();
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

    private void getDataFromEditText() {
        emailRegister = binding.editTextEmail.getText().toString().trim();
        passwordRegister = binding.editTextPassword.getText().toString().trim();
        usernameRegister = binding.editTextUsername.getText().toString().trim();
    }

    private boolean isValidInputData() {
        if (Validation.getEmailErr(emailRegister) != null) {
            binding.editTextEmail.setError(Validation.getEmailErr(emailRegister));
            binding.editTextEmail.requestFocus();
            return false;
        }
        if (Validation.getUsernameErr(usernameRegister) != null) {
            binding.editTextUsername.setError(Validation.getUsernameErr(usernameRegister));
            binding.editTextUsername.requestFocus();
            return false;
        }
        if (Validation.getPasswordErr(passwordRegister, true) != null
                || !isRePasswordMatch()) {
            binding.editTextPassword.setError(Validation.getPasswordErr(passwordRegister, true));
            binding.editTextPassword.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isRePasswordMatch() {
        String password = binding.editTextPassword.getText().toString().trim();
        String repassword = binding.editTextRePassword.getText().toString().trim();
        if (!password.equals(repassword)) {
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