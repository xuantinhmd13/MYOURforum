package myour.myourforum.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import myour.myourforum.Program;
import myour.myourforum.R;
import myour.myourforum.api.RESTfulAPIConfig;
import myour.myourforum.databinding.FragmentPostViewBinding;
import myour.myourforum.model.Post;
import myour.myourforum.util.LoadingScreen;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostViewFragment extends Fragment {
    private FragmentPostViewBinding binding;

    private Post post;

    public static PostViewFragment newInstance(Post post) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("#post", post);
        PostViewFragment fragment = new PostViewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPostViewBinding.inflate(getLayoutInflater());

        setControl();
        setEvent();

        return binding.getRoot();
    }

    private void setEvent() {
        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButtonBack();
            }
        });
    }

    private void clickButtonBack() {
        getActivity().onBackPressed();
    }

    private void setControl() {
        if (getArguments() != null) {
            post = (Post) getArguments().get("#post");
            loadDataInput();
            setViewCount();
        } else getFragmentManager().popBackStack();
    }

    private void setViewCount() {
        LoadingScreen.show(getContext());
        Program.request.increaseViewCount(post.getId()).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                LoadingScreen.hide();
                if (response.code() == 200) {
                    post.setViewCount(response.body());
                    loadDataInput();
                } else
                    Toast.makeText(getContext(), Program.ERR_API_SERVER, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                LoadingScreen.hide();
                Toast.makeText(getContext(), Program.ERR_API_SERVER, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDataInput() {
        //load image.
        String imageName = "post" + post.getId() + "_image0.jpg";
        loadImageToImageView(imageName);

        binding.textViewUsername.setText(post.getAuthorUsername());
        binding.textViewTitle.setText("   " + post.getTitle());
        binding.textViewCategory.setText(Program.categoryList.get(post.getCategoryId() - 1).getName());
        binding.textViewCreateTime.setText(post.getCreateTime());
        binding.textViewViewCount.setText(post.getViewCount() + "");
        binding.textViewContent.setText("   " + post.getContent());
    }

    private void loadImageToImageView(String imageName) {
        Picasso.get()
                .load(RESTfulAPIConfig.URL + "/image/" + imageName)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .centerInside()
                .fit()
                .placeholder(R.drawable.ic_image_null)
                .into(binding.imageViewPost);
    }
}