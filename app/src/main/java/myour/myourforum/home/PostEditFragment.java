package myour.myourforum.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gun0912.tedpermission.PermissionListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import myour.myourforum.Program;
import myour.myourforum.R;
import myour.myourforum.api.RESTfulAPIConfig;
import myour.myourforum.databinding.FragmentPostEditBinding;
import myour.myourforum.model.Category;
import myour.myourforum.model.Post;
import myour.myourforum.util.LoadingScreen;
import myour.myourforum.util.Validation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class PostEditFragment extends Fragment {

    private final String TAG = "#PostEditFragment";

    private FragmentPostEditBinding binding;
    private List<String> categoryListName;
    private Post postEdit;
    private File fileImagePostTemp;

    private String content;
    private String title;
    private int categoryId;
    private boolean isEditMode = false;

    public static PostEditFragment newInstance(Post postEdit) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("#postedit", postEdit);
        PostEditFragment fragment = new PostEditFragment();
        fragment.setArguments(bundle);
        return fragment;
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
                limitLineEditText();
            }
        });

        binding.imageViewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickImageViewPost();
            }
        });

        binding.buttonPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    clickButtonPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButtonBack();
            }
        });

        binding.buttonComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButtonComplete();
            }
        });

        binding.buttonReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButtonReload();
            }
        });
    }

    private void clickButtonReload() {
        LoadingScreen.show(getContext());
        Program.request.getPostById(postEdit.getId()).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                LoadingScreen.hide();
                if (response.code() == 200 && response.body() != null) {
                    postEdit = response.body();
                    setOutDataPostEdit();
                } else
                    Toast.makeText(getContext(), Program.ERR_API_SERVER, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                LoadingScreen.hide();
                Toast.makeText(getContext(), Program.ERR_API_FAILURE, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clickButtonComplete() {
        getInData();
        if (isValidInputData()) {
            if (isEditMode) {
                editPost(getPostEdited());
            } else {
                addPost(getNewPost());
            }
        }
    }

    private Post getPostEdited() {
        Post postEdited = new Post();
        postEdited.setPostUpdate(postEdit);
        //Update.
        postEdited.setTitle(title);
        postEdited.setContent(content);
        postEdited.setUpdateTime(Program.getDateTimeNow());
        postEdited.setCategoryId(binding.spinnerCategory.getSelectedIndex() + 1);
        return postEdited;
    }

    private void editPost(Post postEdited) {
        LoadingScreen.show(getContext());
        Program.request.updatePost(postEdited).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                LoadingScreen.hide();
                if (response.code() == 200) {
                    Toast.makeText(getContext(), "Cập nhật bài đăng thành công!", Toast.LENGTH_SHORT).show();
                    if (fileImagePostTemp != null) {
                        uploadImagePost(postEdited.getId());
                    } else {
                        ((HomeActivity) getActivity()).onResume();
                        getFragmentManager().popBackStackImmediate();
                    }
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

    private void addPost(Post newPost) {
        LoadingScreen.show(getContext());
        Program.request.addPost(newPost).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                LoadingScreen.hide();
                if (response.code() == 200 && response.body() != null) {
                    Toast.makeText(getContext(), "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                    if (fileImagePostTemp != null) {
                        uploadImagePost(response.body());
                    } else {
                        ((HomeActivity) getActivity()).onResume();
                        getFragmentManager().popBackStackImmediate();
                    }
                } else {
                    Toast.makeText(getContext(), Program.ERR_API_SERVER, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                LoadingScreen.hide();
                Toast.makeText(getContext(), Program.ERR_API_FAILURE, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImagePost(int postId) {
        String filePath = fileImagePostTemp.getAbsolutePath();
        String extension = filePath.substring(filePath.lastIndexOf('.'));
        String fileName = "post" + postId + "_image0" + extension;

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), fileImagePostTemp);
        MultipartBody.Part image = MultipartBody.Part.createFormData("image", fileName, requestBody);

        LoadingScreen.show(getContext());
        Program.request.uploadImagePost(image, postId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                LoadingScreen.hide();
                if (response.code() == 200) {
                    ((HomeActivity) getActivity()).onResume();
                    getFragmentManager().popBackStackImmediate();
                } else {
                    Toast.makeText(getContext(), "Tải lên hình ảnh bị lỗi!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), Program.ERR_API_SERVER, Toast.LENGTH_SHORT).show();
                }
                fileImagePostTemp.delete();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LoadingScreen.hide();
                Toast.makeText(getContext(), Program.ERR_API_FAILURE, Toast.LENGTH_SHORT).show();
                fileImagePostTemp.delete();
            }
        });
    }

    private void clickButtonBack() {
        getActivity().onBackPressed();
    }

    private void clickButtonPreview() throws IOException {
        getInData();
        if (fileImagePostTemp == null && isEditMode) {
            if (postEdit.isHasImage()) {
                Bitmap bitmap = ((BitmapDrawable) binding.imageViewPost.getDrawable()).getBitmap();
                fileImagePostTemp = convertImageToJPG(bitmap);
            }
        }
        PostViewFragment fragment = PostViewFragment.newInstance(getNewPost(), fileImagePostTemp, true);
        getFragmentManager().beginTransaction().add(R.id.frame_layout_fullscreen_container, fragment).addToBackStack(null).commit();
    }

    private Post getNewPost() {
        Post newPost = new Post();
        newPost.setCategoryId(categoryId);
        newPost.setTitle(title);
        newPost.setContent(content);
        newPost.setCreateTime(Program.getDateTimeNow());
        newPost.setUserId(Program.user.getId());
        return newPost;
    }

    private void getInData() {
        title = binding.editTextTitle.getText().toString().trim();
        content = binding.editTextContent.getText().toString().trim();
        categoryId = binding.spinnerCategory.getSelectedIndex() + 1;
    }

    private void clickImageViewPost() {
        Program.requestPermission(getContext(), Program.listStorgePermission, new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, Program.REQUEST_CODE_PICK_IMAGE);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getContext(), "Quyền truy cập của ứng dụng bị từ chối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void limitLineEditText() {
        if (binding.editTextTitle.getLineCount() > 3) {
            binding.editTextTitle.getText().delete(binding.editTextTitle.getSelectionEnd() - 1, binding.editTextTitle.getSelectionStart());
        }
    }

    private void setControl() {
        //init.
        categoryListName = new ArrayList<>();
        binding.textViewUsername.setText(Program.user.getUsername());
        setDataSpinnerCategory();
        //is Edit state.
        if (getArguments() != null) {
            isEditMode = true;
            postEdit = (Post) getArguments().get("#postedit");
            setOutDataPostEdit();
        } else isEditMode = false;
    }

    private void setOutDataPostEdit() {
        if (postEdit.isHasImage())
            setOutImageViewPostEdit();
        binding.textViewUsername.setText(Program.user.getUsername());
        binding.textViewCreateTime.setText("Sửa bài đăng");
        binding.spinnerCategory.setSelectedIndex(postEdit.getCategoryId() - 1);
        binding.editTextTitle.setText(postEdit.getTitle());
        binding.editTextContent.setText(postEdit.getContent());
        binding.buttonReload.setVisibility(View.VISIBLE);
    }

    private void setOutImageViewPostEdit() {
        String imageName = "post" + postEdit.getId() + "_image0.jpg";
        Picasso.get()
                .load(RESTfulAPIConfig.URL + "/image/" + imageName)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .centerInside()
                .fit()
                .placeholder(R.drawable.icon_image_null)
                .into(binding.imageViewPost);
    }

    private void setDataSpinnerCategory() {
        categoryListName.clear();
        for (Category category : Program.categoryList) {
            categoryListName.add(category.getName());
        }
        binding.spinnerCategory.attachDataSource(categoryListName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Program.REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                setOutImageViewPost(data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setOutImageViewPost(Uri data) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(data));
        fileImagePostTemp = convertImageToJPG(bitmap);
        Picasso.get()
                .load(fileImagePostTemp)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .centerInside()
                .fit()
                .placeholder(R.drawable.icon_image_null)
                .into(binding.imageViewPost);
    }

    private File convertImageToJPG(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + "~temp~_myourforum.jpg");
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(bytes.toByteArray());
        fo.close();
        return file;
    }

    private boolean isValidInputData() {
        if (Validation.getErrTitlePost(title) != null) {
            binding.editTextTitle.setError(Validation.getErrTitlePost(title));
            binding.editTextTitle.requestFocus();
            return false;
        }
        if (Validation.getErrContentPost(content) != null) {
            binding.editTextContent.setError(Validation.getErrContentPost(content));
            binding.editTextContent.requestFocus();
            return false;
        }
        return true;
    }
}