package myour.myourforum.profile;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

import myour.myourforum.Program;
import myour.myourforum.api.RESTfulAPIService;
import myour.myourforum.databinding.FragmentDialogChangePasswordBinding;
import myour.myourforum.home.HomeActivity;
import myour.myourforum.util.LoadingScreen;
import myour.myourforum.util.Validation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogChangePasswordFragment extends DialogFragment {

    private FragmentDialogChangePasswordBinding binding;

    private String passwordNow;
    private String passwordNew;
    private String rePasswordNew;

    public DialogChangePasswordFragment() {
    }

    public static DialogChangePasswordFragment newInstance() {
        DialogChangePasswordFragment fragment = new DialogChangePasswordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDialogChangePasswordBinding.inflate(getLayoutInflater());

        setControl();
        setEvent();

        return binding.getRoot();
    }

    private void setEvent() {
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButtonOk();
            }
        });
        binding.editTextRePasswordNew.addTextChangedListener(new TextWatcher() {
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
                isRePasswordNewMatch();
            }
        });
    }

    private void clickButtonOk() {
        getInData();
        if (isValidInputData()) {
            checkPasswordMatch();
        }
    }

    private void checkPasswordMatch() {
        new AsyncTask<String, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                LoadingScreen.show(getContext());
            }

            @Override
            protected Boolean doInBackground(String... strings) {
                return BCrypt.checkpw(strings[0], strings[1]);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                LoadingScreen.hide();
                changePassword(result);
            }
        }.execute(passwordNow, Program.user.getPassword());
    }

    private void changePassword(Boolean result) {
        if (result) {
            if (passwordNew.equals(passwordNow)) {
                binding.editTextPasswordNew.setError("Không được trùng với mật khẩu cũ");
                binding.editTextPasswordNew.requestFocus();
                return;
            }
            encryptPassword();
        } else {
            binding.editTextPassword.setError("Sai mật khẩu!");
            binding.editTextPassword.requestFocus();
        }
    }

    private void encryptPassword() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                LoadingScreen.show(getContext());
            }

            @Override
            protected String doInBackground(String... strings) {
                return BCrypt.hashpw(strings[0], BCrypt.gensalt(Program.BCRYPT_LOG_ROUND));
            }

            @Override
            protected void onPostExecute(String passwordEncrypted) {
                super.onPostExecute(passwordEncrypted);
                LoadingScreen.hide();
                updateNewPassword(passwordEncrypted);
            }
        }.execute(passwordNew);
    }

    private void updateNewPassword(String passwordEncrypted) {
        requestUpdatePassword(passwordEncrypted);
    }

    private void requestUpdatePassword(String passwordEncrypted) {
        LoadingScreen.show(getContext());
        RESTfulAPIService.request.updatePassword(Program.user.getId(), passwordEncrypted, Program.getDateTimeNow()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                LoadingScreen.hide();
                if (response.code() == 200) {
                    Toast.makeText(getContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    Program.user = null;
                    dismiss();
                    Objects.requireNonNull(getActivity()).finishAffinity();
                    startActivity(new Intent(getContext(), HomeActivity.class));
                } else
                    Toast.makeText(getContext(), Program.ERR_API_SERVER, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LoadingScreen.hide();
                Toast.makeText(getContext(), Program.ERR_API_FAILURE, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean isValidInputData() {
        if (Validation.getErrPassword(passwordNow, false) != null) {
            binding.editTextPassword.setError(Validation.getErrPassword(passwordNow, false));
            binding.editTextPassword.requestFocus();
            return false;
        }
        if (Validation.getErrPassword(passwordNew, true) != null
                || !isRePasswordNewMatch()) {
            binding.editTextPasswordNew.setError(Validation.getErrPassword(passwordNew, true));
            binding.editTextPasswordNew.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isRePasswordNewMatch() {
        passwordNew = binding.editTextPasswordNew.getText().toString().trim();
        rePasswordNew = binding.editTextRePasswordNew.getText().toString().trim();
        if (!passwordNew.equals(rePasswordNew)) {
            binding.editTextRePasswordNew.setError("Mật khẩu không khớp!");
            binding.editTextRePasswordNew.requestFocus();
            return false;
        }
        return true;
    }

    private void getInData() {
        passwordNow = binding.editTextPassword.getText().toString().trim();
        passwordNew = binding.editTextPasswordNew.getText().toString().trim();
        rePasswordNew = binding.editTextRePasswordNew.getText().toString().trim();
    }

    private void setControl() {
        setCancelable(false);
        Objects.requireNonNull(getDialog()).getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getDialog()).getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}