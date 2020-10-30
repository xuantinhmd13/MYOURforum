package myour.myourforum.homepage;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import myour.myourforum.api.RESTfulAPIRequest;
import myour.myourforum.databinding.ActivityHomePageBinding;
import myour.myourforum.model.Category;
import myour.myourforum.model.Post;

public class HomePageActivity extends AppCompatActivity {

    public static boolean isHomePageActivity = false;

    private ActivityHomePageBinding binding;
    private HomePageAdapter homePageAdapter;
    private RESTfulAPIRequest request;

    private FragmentManager fragmentManagerHomePage;
    private LinearLayoutManager layoutManager;

    private ArrayList<Category> categories;
    private ArrayList<Post> posts;
    private Menu optionMenu;

    private boolean isToasted = false;
    private boolean isLoaded = true;
    private int getSelectedIndex = 0;
    private String keyWord = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.editTextSearch.setHint("Hello TK13 sà");
    }
}