package com.nhatnguyen.youtubedowload;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class YoutubeAdapter extends RecyclerView.Adapter<YoutubeViewHolder> implements Filterable {
    ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    List<YoutubeModel> youtubeModelArrayList;
    List<YoutubeModel> youtubeModelArrayListOld;
    Context context;

    public YoutubeAdapter(ArrayList<YoutubeModel> youtubeModelArrayList, Context context) {
        this.youtubeModelArrayList = youtubeModelArrayList;
        this.youtubeModelArrayListOld = youtubeModelArrayList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public YoutubeViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video_view, parent, false);
        return new YoutubeViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull YoutubeViewHolder holder, int position) {
        YoutubeModel current = youtubeModelArrayList.get(position);
        String imageThumbUrl = "https://i.ytimg.com/vi/" + current.getYoutube_url() + "/maxresdefault.jpg";
        Picasso.with(context).load(imageThumbUrl).into(holder.imageViewItem);
        holder.title.setText(current.getTitle());
        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(current.getId()));
        holder.linearDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Youtube")
                        .child(youtubeModelArrayList.get(position).getId())
                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            youtubeModelArrayList.remove(youtubeModelArrayList.get(position));
                            notifyDataSetChanged();
                            Snackbar snackbar = Snackbar
                                    .make(holder.swipeRevealLayout, "Đã Xóa", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                });
            }
        });
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullScreenActivity.class);
                intent.putExtra("YoutubeUrl", youtubeModelArrayList.get(position));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        holder.imageViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullScreenActivity.class);
                intent.putExtra("YoutubeUrl", youtubeModelArrayList.get(position));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return youtubeModelArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strsearch = constraint.toString();
                if (strsearch.isEmpty()) {
                    youtubeModelArrayList = youtubeModelArrayListOld;
                } else {
                    List<YoutubeModel> list = new ArrayList<>();
                    for (YoutubeModel youtubeModel : youtubeModelArrayListOld) {
                        if (youtubeModel.getTitle().toLowerCase().contains(strsearch.toLowerCase())) {
                            list.add(youtubeModel);
                        }
                    }
                    youtubeModelArrayList = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = youtubeModelArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                youtubeModelArrayList = (List<YoutubeModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
