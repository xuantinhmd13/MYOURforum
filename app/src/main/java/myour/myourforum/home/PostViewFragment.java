package myour.myourforum.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

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

    private boolean isPreviewMode = false;

    private Post post;

    public static PostViewFragment newInstance(Post post, File fileImagePreview, boolean isPreview) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("#post", post);
        bundle.putSerializable("#fileImagePreview", fileImagePreview);
        bundle.putBoolean("#isPreview", isPreview);
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

        binding.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButtonEdit();
            }
        });

        binding.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButtonDelete();
            }
        });
    }

    private void clickButtonDelete() {
        showDialogDeletePost();
    }

    private void showDialogDeletePost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xóa bài đăng");
        builder.setMessage("Bạn thực sự muốn xóa bài đăng này?");
        builder.setCancelable(false);
        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clickYesDialogDeletePost(dialogInterface);
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void clickYesDialogDeletePost(DialogInterface dialogInterface) {
        LoadingScreen.show(getContext());
        Program.request.deletePost(post.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                LoadingScreen.hide();
                if (response.code() == 200) {
                    Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                    getFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), Program.ERR_API_SERVER, Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LoadingScreen.hide();
                Toast.makeText(getContext(), Program.ERR_API_FAILURE, Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
    }

    private void clickButtonEdit() {
        getFragmentManager().popBackStack();
        PostEditFragment fragment = PostEditFragment.newInstance(post);
        getFragmentManager().beginTransaction().add(R.id.frame_layout_fullscreen_container, fragment).addToBackStack(null).commit();
    }

    private void clickButtonBack() {
        getActivity().onBackPressed();
    }

    private void setControl() {
        if (getArguments() != null) {
            post = (Post) getArguments().get("#post");
            isPreviewMode = getArguments().getBoolean("#isPreview");
            if (isPreviewMode)
                setOutDataPreview();
            else setOutData();
        } else getFragmentManager().popBackStackImmediate();
    }

    private void setOutData() {
        if (Program.user.getId() == post.getUserId()) {
            binding.buttonEdit.setVisibility(View.VISIBLE);
            binding.buttonDelete.setVisibility(View.VISIBLE);
        } else {
            binding.buttonEdit.setVisibility(View.INVISIBLE);
            binding.buttonDelete.setVisibility(View.INVISIBLE);
        }
        if (post.isHasImage())
            setOutImagePost();
        setOutViewCount();
        binding.textViewUsername.setText(post.getAuthorUsername());
        setOutOtherComponent();
    }

    private void setOutImagePostPreview(File fileImagePost) {
        Picasso.get()
                .load(fileImagePost)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .centerInside()
                .fit()
                .placeholder(R.drawable.icon_image_null)
                .into(binding.imageViewPost);
    }

    private void setOutViewCount() {
        LoadingScreen.show(getContext());
        Program.request.increaseViewCount(post.getId()).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                LoadingScreen.hide();
                if (response.code() == 200) {
                    post.setViewCount(response.body());
                    binding.textViewViewCount.setText(post.getViewCount() + "");
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

    private void setOutDataPreview() {
        binding.buttonEdit.setVisibility(View.INVISIBLE);
        binding.buttonDelete.setVisibility(View.INVISIBLE);
        setOutImagePostPreview((File) getArguments().get("#fileImagePreview"));
        binding.textViewUsername.setText(Program.user.getUsername());
        binding.textViewViewCount.setText("0");
        setOutOtherComponent();
    }

    private void setOutOtherComponent() {
        binding.textViewTitle.setText("   " + post.getTitle());
        binding.textViewCategory.setText(Program.categoryList.get(post.getCategoryId() - 1).getName());
        binding.textViewCreateTime.setText(post.getCreateTime());
        binding.textViewContent.setText("   " + post.getContent());
    }

    private void setOutImagePost() {
        String imageName = "post" + post.getId() + "_image0.jpg";
        Picasso.get()
                .load(RESTfulAPIConfig.URL + "/image/" + imageName)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .centerInside()
                .fit()
                .placeholder(R.drawable.icon_image_null)
                .into(binding.imageViewPost);
    }
}