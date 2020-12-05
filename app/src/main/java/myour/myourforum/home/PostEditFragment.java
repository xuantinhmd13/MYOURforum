package myour.myourforum.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import myour.myourforum.Program;
import myour.myourforum.databinding.FragmentPostEditBinding;
import myour.myourforum.model.Category;

public class PostEditFragment extends Fragment {

    private FragmentPostEditBinding binding;

    private List<String> categoryListName;

    public PostEditFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPostEditBinding.inflate(getLayoutInflater());

        setControl();
        setEvent();

        return binding.getRoot();
    }

    private void setEvent() {
        binding.editTextTitle.addTextChangedListener(new TextWatcher() {
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
                limitLineOfEditText();
            }
        });
        binding.imageViewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:
            }
        });
    }

    private void limitLineOfEditText() {
        if (binding.editTextTitle.getLineCount() > 3) {
            binding.editTextTitle.getText().delete(binding.editTextTitle.getSelectionEnd() - 1, binding.editTextTitle.getSelectionStart());
        }
    }

    private void setControl() {
        //init.
        categoryListName = new ArrayList<>();
        binding.textViewUsername.setText(Program.user.getUsername());
        setDataSpinnerCategory();
    }

    private void setDataSpinnerCategory() {
        categoryListName.clear();
        for (Category category : Program.categoryList) {
            categoryListName.add(category.getName());
        }
        binding.spinnerCategory.attachDataSource(categoryListName);
    }
}