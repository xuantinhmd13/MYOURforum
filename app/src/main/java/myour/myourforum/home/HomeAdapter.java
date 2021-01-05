package myour.myourforum.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import myour.myourforum.Program;
import myour.myourforum.R;
import myour.myourforum.api.RESTfulAPIService;
import myour.myourforum.databinding.ItemPostHomeBinding;
import myour.myourforum.model.Post;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private final Context context;
    private List<Post> postList;
    private OnItemClickListener onItemClickListener;

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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ItemPostHomeBinding binding;

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
            if (post.isHasImage()) {
                String imageName = "post" + post.getId() + "_image0.jpg";
                loadImageToImageView(imageName);
            } else loadImageToImageView(null);
        }

        private void loadImageToImageView(String imageName) {
            if (imageName != null)
                Picasso.get()
                        .load(RESTfulAPIService.URL + "/image/" + imageName)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .centerInside()
                        .fit()
                        .placeholder(R.drawable.icon_image_null)
                        .into(binding.imageViewPost);
            else binding.imageViewPost.setImageResource(R.drawable.icon_image_null);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
