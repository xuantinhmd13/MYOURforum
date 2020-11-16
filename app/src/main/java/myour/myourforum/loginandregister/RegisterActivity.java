package myour.myourforum.loginandregister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import myour.myourforum.Program;
import myour.myourforum.databinding.ActivityRegisterBinding;
import myour.myourforum.model.User;
import myour.myourforum.util.LoadingScreen;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    private String email;
    private String password;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setControl();
        setEvent();
    }

    private void setEvent() {
        binding.imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
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

    private void clickButtonRegister() {
        if (isValidInputData()) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUserName(userName);
            newUser.setPassWord(encrypt(password));
            newUser.setCreateTime(Program.getDateTimeNow());

            LoadingScreen.show(this);
            Program.request.registerUser(newUser).enqueue(new Callback<String>() {
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
                            binding.editTextUserName.setError("Tên này đã được sử dụng!");
                            binding.editTextUserName.requestFocus();
                            return;
                        }
                        if (response.body().equals("OK")) {
                            Toast.makeText(RegisterActivity.this, "Đăng kí thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("#email", newUser.getEmail());
                            setResult(RESULT_OK, intent);
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
    }

    private String encrypt(String password) {
        //TODO:
        return password;
    }

    private boolean isValidInputData() {
        email = binding.editTextEmail.getText().toString().trim();
        password = binding.editTextPassword.getText().toString().trim();
        userName = binding.editTextUserName.getText().toString().trim();
        if (Validation.getEmailErr(email) != null) {
            binding.editTextEmail.setError(Validation.getEmailErr(email));
            binding.editTextEmail.requestFocus();
            return false;
        }
        if (Validation.getUserNameErr(userName) != null) {
            binding.editTextUserName.setError(Validation.getUserNameErr(userName));
            binding.editTextUserName.requestFocus();
            return false;
        }
        if (Validation.getPasswordErr(password, true) != null
                || !isRePasswordMatch()) {
            binding.editTextPassword.setError(Validation.getPasswordErr(password, true));
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
    }

}