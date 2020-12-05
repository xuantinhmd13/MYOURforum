package myour.myourforum.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import myour.myourforum.R;
import myour.myourforum.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //TODO: begin code.
    }
}