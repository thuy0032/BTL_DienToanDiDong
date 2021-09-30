package com.nhatnguyen.youtubedowload;

import android.annotation.SuppressLint;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

public class YoutubeViewHolder extends RecyclerView.ViewHolder {
     TextView title;
     ImageView imageViewItem;
     SwipeRevealLayout swipeRevealLayout;
     LinearLayout linearDelete;
    public YoutubeViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        title=itemView.findViewById(R.id.title);
        imageViewItem=itemView.findViewById(R.id.imageViewItem);
        swipeRevealLayout=itemView.findViewById(R.id.swipeRevealLayout);
        linearDelete=itemView.findViewById(R.id.layoutDelete);
    }
}
