package myour.myourforum.homepage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import com.jcodecraeer.xrecyclerview.ProgressStyle;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

import myour.myourforum.Program;
import myour.myourforum.R;
import myour.myourforum.admin.AdminActivity;
import myour.myourforum.databinding.ActivityHomeBinding;
import myour.myourforum.loginandregister.LoginActivity;
import myour.myourforum.model.Category;
import myour.myourforum.model.Post;
import myour.myourforum.profile.ProfileActivity;
import myour.myourforum.util.LoadingScreen;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private HomeAdapter homeAdapter;

    private List<String> categoryListName;
    private List<Post> postList;
    private Menu optionMenu;

    private boolean isToasted = false;
    private boolean isLoaded = false;
    private int categoryId = 0;
    private int pageIndexMain = 0;
    private int size = 5;
    private String keyWord = "";
    private String TAG = "#homepage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setControl();
        setEvent();
    }

    private void setEvent() {
        binding.spinnerCategory.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                categoryId = position;
                loadDataFromServer();
            }
        });

        binding.fabUpTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.recyclerViewHomePage.smoothScrollToPosition(1);
            }
        });

        binding.fabCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:
            }
        });

        binding.fabYourPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:
            }
        });
    }

    private void setControl() {
        //interface.
        binding.toolBar.setTitle("  MYOUR forum");
        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setIcon(R.drawable.ic_homepage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //init
        categoryListName = new ArrayList<>();
        postList = new ArrayList<>();

        getCategoryListName();
        loadDataFromServer();// get list data to set for content
    }

    private void getCategoryListName() {
        categoryListName.add(0, "Tất cả");
        for (Category category : Program.categoryList) {
            categoryListName.add(category.getName());
        }
        binding.spinnerCategory.attachDataSource(categoryListName);
    }


    private void loadDataFromServer() {
        if (keyWord.equals("")) {
            getPostByCategory();
        } else {
            searchPost(keyWord);
        }
    }

    private void searchPost(String keyWord) {
    }

    private void getPostByCategory() {
        pageIndexMain = 0;
        LoadingScreen.show(this);
        Program.request.getPostByCategory(categoryId, pageIndexMain, size).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                LoadingScreen.hide();
                if (response.code() == 200) {
                    postList = response.body();
                    if (postList.size() == 0) {
                        binding.textViewEmpty.setVisibility(View.VISIBLE);
                        binding.recyclerViewHomePage.setVisibility(View.GONE);
                    } else {
                        binding.textViewEmpty.setVisibility(View.GONE);
                        binding.recyclerViewHomePage.setVisibility(View.VISIBLE);
                    }
                    if (!isLoaded) {
                        homeAdapter = new HomeAdapter(postList, HomeActivity.this);
                        binding.recyclerViewHomePage.setAdapter(homeAdapter);
                        binding.recyclerViewHomePage.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
                        binding.recyclerViewHomePage.setPullRefreshEnabled(false);
                        isLoaded = true;
                    } else {
                        homeAdapter.update(postList);
                        binding.recyclerViewHomePage.setLoadingMoreEnabled(true);
                    }
                } else
                    Toast.makeText(HomeActivity.this, Program.ERR_API_SERVER, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                LoadingScreen.hide();
                Toast.makeText(HomeActivity.this, Program.ERR_API_FAILURE, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "ERR_API " + t.toString());
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }
        getMenuInflater().inflate(R.menu.main_option_menu_home_page, menu);
        optionMenu = menu;
        controlActivity(optionMenu);
        return super.onPrepareOptionsMenu(menu);
    }

    private void controlActivity(Menu optionMenu) {
        if (Program.user != null) {
            //hide login.
            optionMenu.findItem(R.id.menu_home_login).setVisible(false);
            //open features.
            optionMenu.findItem(R.id.menu_home_profile).setVisible(true);
            if (Program.user.isAdminRole())
                optionMenu.findItem(R.id.menu_home_manager).setVisible(true);
            else optionMenu.findItem(R.id.menu_home_manager).setVisible(false);
        } else {
            //show login.
            optionMenu.findItem(R.id.menu_home_login).setVisible(true);
            //hide features.
            optionMenu.findItem(R.id.menu_home_manager).setVisible(false);
            optionMenu.findItem(R.id.menu_home_profile).setVisible(false);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home_login:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.menu_home_manager:
                if (Program.user.isAdminRole())
                    startActivity(new Intent(this, AdminActivity.class));
                break;
            case R.id.menu_home_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}