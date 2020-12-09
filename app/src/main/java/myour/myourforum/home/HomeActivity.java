package myour.myourforum.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.FragmentManager;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

import myour.myourforum.Program;
import myour.myourforum.R;
import myour.myourforum.databinding.ActivityHomeBinding;
import myour.myourforum.loginandregister.LoginActivity;
import myour.myourforum.model.Category;
import myour.myourforum.model.Post;
import myour.myourforum.profile.ProfileActivity;
import myour.myourforum.util.LoadingScreen;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private HomeAdapter homeAdapter;

    private FragmentManager fragmentManager;
    private Menu optionMenu;

    private List<String> categoryListName;
    private List<Post> postList;

    private boolean isBackPressed = false;
    private boolean isLoaded = false;
    private int categoryId = 0;
    private int pageIndexMain = 0;
    private int pageIndexSearch = 0;
    private int size = 6;
    private String keyWord = "";
    private String TAG = "#HomeActivity";

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
                clickItemOfSpinner(position);
            }
        });

        binding.fabUpTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFabUpTop();
                binding.fabMenu.collapse();
            }
        });

        binding.fabCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFabCreatePost();
                binding.fabMenu.collapse();
            }
        });

        binding.recyclerViewHomePage.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                getDataFromServer();
            }

            @Override
            public void onLoadMore() {
                getMoreDataFromServer();
            }
        });

        if (homeAdapter != null)
            homeAdapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    clickItemOfPostList(position);
                }
            });

        binding.editTextSearch.addTextChangedListener(new TextWatcher() {
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
                keyWord = s.toString();
                getDataFromServer();
            }
        });
    }

    private void getMoreDataFromServer() {
        if (keyWord.isEmpty()) {
            pageIndexMain++;
            getPostByCategory(true);
        } else {
            pageIndexSearch++;
            searchPost(keyWord, true);
        }
    }

    private void clickItemOfPostList(int position) {
        PostViewFragment fragment = PostViewFragment.newInstance(postList.get(position - 1), null, false);
        if (fragmentManager.findFragmentByTag("#postView") == null)
            fragmentManager.beginTransaction().add(R.id.frame_layout_fullscreen_container, fragment, "#postView").addToBackStack(null).commit();
    }

    private void clickFabCreatePost() {
        if (fragmentManager.findFragmentByTag("#postEdit") == null)
            fragmentManager.beginTransaction().add(R.id.frame_layout_fullscreen_container, new PostEditFragment(), "#postEdit").addToBackStack(null).commit();
    }

    private void clickFabUpTop() {
        binding.recyclerViewHomePage.smoothScrollToPosition(1);
    }

    private void clickItemOfSpinner(int position) {
        categoryId = position;
        getDataFromServer();
    }

    private void setControl() {
        //interface.
        setSupportActionBar(binding.toolBar);
        //tool.
        fragmentManager = getSupportFragmentManager();
        //init.
        categoryListName = new ArrayList<>();
        postList = new ArrayList<>();
        //first load data.
        setOutDataSpinnerCategory();
        getDataFromServer();// get list data to set for content.
    }

    private void setOutDataSpinnerCategory() {
        categoryListName.clear();
        categoryListName.add(0, "Tất cả");
        for (Category category : Program.categoryList) {
            categoryListName.add(category.getName());
        }
        binding.spinnerCategory.attachDataSource(categoryListName);
    }


    public void getDataFromServer() {
        if (keyWord.isEmpty()) {
            pageIndexMain = 0;
            getPostByCategory(false);
        } else {
            pageIndexSearch = 0;
            searchPost(keyWord, false);
        }
    }

    private void searchPost(String keyWord, boolean isLoadMore) {
        LoadingScreen.show(this);
        Program.request.searchPost(keyWord, categoryId, pageIndexSearch, size).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                LoadingScreen.hide();
                if (response.code() == 200 && response.body() != null) {
                    if (isLoadMore)
                        postList.addAll(response.body());
                    else postList = response.body();
                    setUpRecyclerView(postList);
                } else
                    Toast.makeText(HomeActivity.this, Program.ERR_API_SERVER, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                LoadingScreen.hide();
                Toast.makeText(HomeActivity.this, Program.ERR_API_FAILURE, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPostByCategory(boolean isLoadMore) {
        LoadingScreen.show(this);
        Program.request.getPostByCategory(categoryId, pageIndexMain, size).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                LoadingScreen.hide();
                if (response.code() == 200 && response.body() != null) {
                    if (isLoadMore)
                        postList.addAll(response.body());
                    else postList = response.body();
                    setUpRecyclerView(postList);
                } else
                    Toast.makeText(HomeActivity.this, Program.ERR_API_SERVER, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                LoadingScreen.hide();
                Toast.makeText(HomeActivity.this, Program.ERR_API_FAILURE, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpRecyclerView(List<Post> list) {
        if (list.size() == 0) {
            binding.textViewEmpty.setVisibility(View.VISIBLE);
            binding.recyclerViewHomePage.setVisibility(View.GONE);
        } else {
            binding.textViewEmpty.setVisibility(View.GONE);
            binding.recyclerViewHomePage.setVisibility(View.VISIBLE);
        }
        if (!isLoaded) {
            homeAdapter = new HomeAdapter(list, HomeActivity.this);
            binding.recyclerViewHomePage.setAdapter(homeAdapter);
            binding.recyclerViewHomePage.setLoadingMoreProgressStyle(ProgressStyle.BallPulseSync);
            binding.recyclerViewHomePage.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
            binding.recyclerViewHomePage.setPullRefreshEnabled(true);
            binding.recyclerViewHomePage.setLoadingMoreEnabled(true);
            isLoaded = true;
            setEvent();
        } else {
            homeAdapter.update(list);
            binding.recyclerViewHomePage.refreshComplete();
            binding.recyclerViewHomePage.loadMoreComplete();
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }
        getMenuInflater().inflate(R.menu.main_option_menu_home, menu);
        optionMenu = menu;
        controlActivityWhenLogin(optionMenu);
        return super.onCreateOptionsMenu(menu);
    }

    private void controlActivityWhenLogin(Menu optionMenu) {
        controlOptionMenu(optionMenu);
        controlUserFeature();
    }

    private void controlUserFeature() {
        if (Program.user != null) {
            binding.fabCreatePost.setVisibility(View.VISIBLE);
        } else {
            binding.fabCreatePost.setVisibility(View.INVISIBLE);
        }
    }

    private void controlOptionMenu(Menu optionMenu) {
        if (Program.user != null) {
            //hide login.
            optionMenu.findItem(R.id.menu_home_login).setVisible(false);
            //open features.
            optionMenu.findItem(R.id.menu_home_profile).setVisible(true);
        } else {
            //show login.
            optionMenu.findItem(R.id.menu_home_login).setVisible(true);
            //hide features.
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
            case R.id.menu_home_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isBackPressed = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (optionMenu != null)
            controlActivityWhenLogin(optionMenu);
        getDataFromServer();
    }

    @Override
    protected void onDestroy() {
        binding.recyclerViewHomePage.destroy();
        Program.user = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStackImmediate();
            isBackPressed = false;
            onResume();
        } else {
            if (!isBackPressed) {
                Toast.makeText(this, "Nhấn lần nữa để thoát!", Toast.LENGTH_SHORT).show();
                isBackPressed = true;
            } else {
                super.onBackPressed();
                finishAffinity();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (binding.fabMenu.isExpanded()) {
                Rect outRect = new Rect();
                binding.fabMenu.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                    binding.fabMenu.collapse();
            }
        }
        return super.dispatchTouchEvent(event);
    }
}