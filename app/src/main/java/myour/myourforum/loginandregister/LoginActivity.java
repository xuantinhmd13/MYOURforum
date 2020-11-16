package myour.myourforum.loginandregister;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import myour.myourforum.Program;
import myour.myourforum.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private SharedPreferences sharedPreferences;

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
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, Program.REQUEST_CODE_LOGIN);
            }
        });

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButtonLogin(v);
            }
        });
    }

    private void clickButtonLogin(View v) {
        if (isValidInputData()) {
            //TODO:
        }

//        User user = new User();
//        user.setEmail(str_email);
//        user.setPassword(Validation.setMD5(str_pass));
//
//        // check login
//        ProgressDialogF.showLoading(this);
//        apiService.getMember(user).enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                ProgressDialogF.hideLoading();
//                if (response.code() == 200) { //login success
//                    isChecked_checkbox(); // check remember me is checked?
//                    User user = response.body();
//                    Program.user = new User(); // set Program.user
//                    setMember(user);
//                    Log.d("result_program_member", Program.user.getEmail());
//
//                    //Intent intent=new Intent(LoginActivity.this, HomePageActivity.class);
//                    finish();
//                    //startActivity(intent);
//                } else { // login fail
//                    showError(edt_email_login, "Wrong email or password. Please try again!");
//                    return;
//                }
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) { // not conected service
//                ProgressDialogF.hideLoading();
//                Log.d("login_fail", t.getMessage() + "AAAA");
//                Toast.makeText(getApplicationContext(), "Login Failed! An error has occurred!", Toast.LENGTH_LONG).show();
//                edt_email_login.setSelection(edt_email_login.getText().toString().length());
//                edt_email_login.requestFocus();
//            }
//        });
    }

    private boolean isValidInputData() {
        String email = binding.editTextEmail.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();
        if (Validation.getEmailErr(email) != null) {
            binding.editTextEmail.setError(Validation.getEmailErr(email));
            binding.editTextEmail.requestFocus();
            return false;
        }
        if (Validation.getPasswordErr(password, false) != null) {
            binding.editTextPassword.setError(Validation.getPasswordErr(password, false));
            binding.editTextPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void setControl() {
        setUpRemeberAccount();
    }

    private void setUpRemeberAccount() {
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        binding.editTextEmail.setText(sharedPreferences.getString("email", ""));
        binding.editTextPassword.setText(sharedPreferences.getString("password", ""));
        binding.checkBoxRemember.setChecked(sharedPreferences.getBoolean("remember", false));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Program.REQUEST_CODE_LOGIN && resultCode == RESULT_OK && data != null) {
            binding.editTextEmail.setText(data.getStringExtra("#email"));
        }
    }
}