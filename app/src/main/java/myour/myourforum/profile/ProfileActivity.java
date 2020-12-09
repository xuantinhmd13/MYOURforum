package myour.myourforum.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import myour.myourforum.Program;
import myour.myourforum.R;
import myour.myourforum.databinding.ActivityProfileBinding;
import myour.myourforum.home.HomeActivity;
import myour.myourforum.model.User;
import myour.myourforum.util.LoadingScreen;
import myour.myourforum.util.Validation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private Menu optionMenu;

    private String email;
    private String phoneNumber;
    private String description;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setControl();
        setEvent();
    }

    private void setEvent() {
        binding.imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickImageViewBack();
            }
        });
        binding.buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButtonChangePassword();
            }
        });
    }

    private void clickButtonChangePassword() {
        DialogChangePasswordFragment dialog = new DialogChangePasswordFragment();
        dialog.show(getSupportFragmentManager(), null);
    }

    private void clickImageViewBack() {
        finish();
    }

    private void setControl() {
        //interface.
        setSupportActionBar(binding.toolBar);
        setOutData();
    }

    private void setOutData() {
        String isNull = "<Chưa cập nhật>";
        binding.textViewUsername.setText(Program.user.getUsername());

        binding.textViewEmail.setText(Program.user.getEmail());
        binding.editTextEmail.setText(Program.user.getEmail());

        //Phone number.
        if (Program.user.getPhoneNumber() == null || Program.user.getPhoneNumber().isEmpty()) {
            binding.textViewPhoneNumber.setTextColor(Color.GRAY);
            binding.textViewPhoneNumber.setText(isNull);

            binding.editTextPhoneNumber.setText("");
        } else {
            binding.textViewPhoneNumber.setTextColor(Color.BLACK);
            binding.textViewPhoneNumber.setText(Program.user.getPhoneNumber());

            binding.editTextPhoneNumber.setText(Program.user.getPhoneNumber());
        }
        //Description.
        if (Program.user.getDescription() == null || Program.user.getDescription().isEmpty()) {
            binding.textViewDescription.setTextColor(Color.GRAY);
            binding.textViewDescription.setText(isNull);

            binding.editTextDescription.setText("");
        } else {
            binding.textViewDescription.setTextColor(Color.BLACK);
            binding.textViewDescription.setText(Program.user.getDescription());

            binding.editTextDescription.setText(Program.user.getDescription());
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }
        getMenuInflater().inflate(R.menu.main_option_menu_profile, menu);
        optionMenu = menu;
        controlOptionMenu(optionMenu);
        return super.onCreateOptionsMenu(optionMenu);
    }

    private void controlOptionMenu(Menu menu) {
        if (isEditMode) {
            menu.findItem(R.id.menu_profile_edit).setVisible(false);
            menu.findItem(R.id.menu_profile_complete).setVisible(true);
        } else {
            menu.findItem(R.id.menu_profile_edit).setVisible(true);
            menu.findItem(R.id.menu_profile_complete).setVisible(false);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile_sync:
                menuSync();
                break;
            case R.id.menu_profile_edit:
                menuEdit();
                break;
            case R.id.menu_profile_complete:
                menuComplete();
                break;
            case R.id.menu_profile_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void menuSync() {
        LoadingScreen.show(this);
        Program.request.getUserById(Program.user.getId()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                LoadingScreen.hide();
                if (response.code() == 200 && response.body() != null) {
                    Program.user = response.body();
                    setOutData();
                } else
                    Toast.makeText(ProfileActivity.this, Program.ERR_API_SERVER, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                LoadingScreen.hide();
                Toast.makeText(ProfileActivity.this, Program.ERR_API_FAILURE, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void menuComplete() {
        getInData();
        if (isValidInputData()) {
            editUser(getUserEdited());
        }
    }

    private void setUpAfterEditSuccess() {
        isEditMode = false;
        if (optionMenu != null)
            controlOptionMenu(optionMenu);
        setVisibleEditText();
        setOutData();
    }

    private void editUser(User userEdited) {
        LoadingScreen.show(this);
        Program.request.updateUser(userEdited).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                LoadingScreen.hide();
                if (response.code() == 200 && response.body() != null) {
                    Program.user = response.body();
                    setUpAfterEditSuccess();
                    Toast.makeText(ProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 204) {
                    binding.editTextEmail.setError("Email này đã tồn tại!");
                    binding.editTextEmail.requestFocus();
                } else
                    Toast.makeText(ProfileActivity.this, Program.ERR_API_SERVER, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                LoadingScreen.hide();
                Toast.makeText(ProfileActivity.this, Program.ERR_API_FAILURE, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private User getUserEdited() {
        User userEdited = new User();
        userEdited.setUserUpdate(Program.user);
        //Update.
        userEdited.setEmail(email);
        userEdited.setPhoneNumber(phoneNumber);
        userEdited.setDescription(description);
        userEdited.setUpdateTime(Program.getDateTimeNow());
        return userEdited;
    }

    private boolean isValidInputData() {
        if (Validation.getErrEmail(email) != null) {
            binding.editTextEmail.setError(Validation.getErrEmail(email));
            binding.editTextEmail.requestFocus();
            return false;
        }
        if (Validation.getErrPhoneNumber(phoneNumber) != null) {
            binding.editTextPhoneNumber.setError(Validation.getErrPhoneNumber(phoneNumber));
            binding.editTextPhoneNumber.requestFocus();
            return false;
        }
        if (Validation.getErrDescription(description) != null) {
            binding.editTextDescription.setError(Validation.getErrDescription(description));
            binding.editTextDescription.requestFocus();
            return false;
        }
        return true;
    }

    private void getInData() {
        email = binding.editTextEmail.getText().toString().trim();
        phoneNumber = binding.editTextPhoneNumber.getText().toString().trim();
        description = binding.editTextDescription.getText().toString().trim();
    }

    private void menuEdit() {
        isEditMode = true;
        if (optionMenu != null)
            controlOptionMenu(optionMenu);
        setVisibleEditText();
    }

    private void logout() {
        Program.user = null;
        setUpRemeberLogin();
        finishAffinity();
        startActivity(new Intent(this, HomeActivity.class));
    }

    private void setUpRemeberLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("#login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("#email");
        editor.remove("#password");
        editor.remove("#rememberLogin");
        editor.apply();
    }

    private void setVisibleEditText() {
        if (isEditMode) {
            binding.editTextEmail.setVisibility(View.VISIBLE);
            binding.editTextPhoneNumber.setVisibility(View.VISIBLE);
            binding.editTextDescription.setVisibility(View.VISIBLE);

            binding.textViewEmail.setVisibility(View.GONE);
            binding.textViewPhoneNumber.setVisibility(View.GONE);
            binding.textViewDescription.setVisibility(View.GONE);
        } else {
            binding.editTextEmail.setVisibility(View.GONE);
            binding.editTextPhoneNumber.setVisibility(View.GONE);
            binding.editTextDescription.setVisibility(View.GONE);

            binding.textViewEmail.setVisibility(View.VISIBLE);
            binding.textViewPhoneNumber.setVisibility(View.VISIBLE);
            binding.textViewDescription.setVisibility(View.VISIBLE);
        }
    }
}