package myour.myourforum.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import myour.myourforum.Program;
import myour.myourforum.R;
import myour.myourforum.api.RESTfulAPIConfig;
import myour.myourforum.databinding.ItemPostHomeBinding;
import myour.myourforum.model.Post;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private List<Post> postList;
    private Context context;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public HomeAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    public void update(List<Post> postList) {
        this.postList = postList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ItemPostHomeBinding binding = ItemPostHomeBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(postList.get(position));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemPostHomeBinding binding;

        public ViewHolder(@NonNull ItemPostHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().getRootView().setOnClickListener(this);
        }

        public void bindData(Post post) {
            binding.textViewCategory.setText(Program.categoryList.get(post.getCategoryId() - 1).getName());
            binding.textViewCreateTime.setText(post.getCreateTime());
            binding.textViewTitle.setText(post.getTitle());
            binding.textViewViewCount.setText(post.getViewCount() + " lượt xem");
            binding.textViewUsername.setText(post.getAuthorUsername());

            //set update time.
            if (post.getUpdateTime() != null)
                binding.textViewUpdateTime.setVisibility(View.VISIBLE);
            else binding.textViewUpdateTime.setVisibility(View.GONE);


            //set image for post.
            String imageName = "post" + post.getId() + "_image0.jpg";
            loadImageToImageView(imageName);
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

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}