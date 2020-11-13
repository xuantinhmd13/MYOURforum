package myour.myourforum.homepage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import myour.myourforum.databinding.ActivityHomeBinding;
import myour.myourforum.login.LoginActivity;
import myour.myourforum.model.Category;
import myour.myourforum.model.Post;
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
                if (response.code() == 204) {
                    binding.textViewEmpty.setVisibility(View.VISIBLE);
                    binding.recyclerViewHomePage.setVisibility(View.GONE);
                    Log.d(TAG, "NO POST");
                }
                if (response.code() == 200) {
                    postList = response.body();
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
        //controlActivity(optionMenu);
        return super.onPrepareOptionsMenu(menu);
    }

//    private void controlActivity(Menu optionMenu) {
//        if (Program.user != null) {
//            //hide login.
//            optionMenu.findItem(R.id.menu_home_login).setVisible(false);
//            //open features.
//            optionMenu.findItem(R.id.menu_home_your_post).setVisible(true);
//            optionMenu.findItem(R.id.menu_home_profile).setVisible(true);
//            optionMenu.findItem(R.id.menu_home_logout).setVisible(true);
//            if (Program.user.isAdminRole())
//                optionMenu.findItem(R.id.menu_home_manager).setVisible(true);
//            else optionMenu.findItem(R.id.menu_home_manager).setVisible(false);
//        } else {
//            //show login.
//            optionMenu.findItem(R.id.menu_home_login).setVisible(true);
//            //hide features.
//            optionMenu.findItem(R.id.menu_home_your_post).setVisible(false);
//            optionMenu.findItem(R.id.menu_home_manager).setVisible(false);
//            optionMenu.findItem(R.id.menu_home_profile).setVisible(false);
//            optionMenu.findItem(R.id.menu_home_logout).setVisible(false);
//        }
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_home_login:
//                startActivity(new Intent(this, LoginActivity.class));
//                break;
//            case R.id.menu_home_manager:
//                if (Program.user.isAdminRole()) {
//                    startActivity(new Intent(this, AdminMainActivity.class));
//                } else {
//                    startActivity(new Intent(this, MainActivity.class));
//                }
//                break;
//            case R.id.menuHomeManage:
//                if (Program.user.isAdminRole()) {
//                    startActivity(new Intent(this, AdminMainActivity.class));
//                } else {
//                    startActivity(new Intent(this, MainActivity.class));
//                }
//                break;
//
//            case R.id.menuHomeUserProfile:
//                isHomePageActivity = true;
//                if (fragmentManagerHomePage.findFragmentByTag("profileFrag") == null) {
//                    fragmentManagerHomePage.beginTransaction().add(R.id.fullscreenFragmentContainerHomePage, new ProfileFragment(), "profileFrag").commit();
//                }
//                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                break;
//            case R.id.menuHomeLogout:
//
//                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
//                alertDialog.setTitle("Confirm!");
//                alertDialog.setIcon(R.mipmap.ic_launcher);
//                alertDialog.setMessage("Do you really want to logout?");
//                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        ProgressDialogF.showLoading(alertDialog.getContext());
//                        apiService.logout(Program.user.getId()).enqueue(new Callback<String>() {
//                            @Override
//                            public void onResponse(Call<String> call, Response<String> response) {
//                                ProgressDialogF.hideLoading();
//                                if (response.body().equals("success")) {
//                                    Program.user = null;
//                                    onResume();
//                                } else {
//                                    Toast.makeText(HomeActivity.this, "Logout failed! An error has occurred!", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<String> call, Throwable t) {
//                                ProgressDialogF.hideLoading();
//                                Toast.makeText(HomeActivity.this, "Logout failed! An error has occurred!", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                });
//                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                alertDialog.show();
//
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}