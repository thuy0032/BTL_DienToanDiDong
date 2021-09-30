package com.nhatnguyen.youtubedowload;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;
import de.hdodenhof.circleimageview.CircleImageView;

public class FullScreenActivity extends YouTubeBaseActivity {
    public static int PICK_STORAGE_PERMISSTION_CODE = 300;
    private YouTubePlayerView youTubePlayerView;
    private ImageView download, image;
    private Button play;
    private CircleImageView share;
    private YoutubeModel youtubeModel = null;
    private YouTubePlayer.OnInitializedListener monInitializedListener;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        youTubePlayerView = findViewById(R.id.youtube);
        share = findViewById(R.id.share);
        download = findViewById(R.id.download);
        play = findViewById(R.id.play);
        image = findViewById(R.id.image);
        layout=findViewById(R.id.layout);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkpermission();
            }
        });

        final Object object = getIntent().getSerializableExtra("YoutubeUrl");
        if (object instanceof YoutubeModel) {
            youtubeModel = (YoutubeModel) object;
        }
        if (youtubeModel != null) {
            monInitializedListener = new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    youTubePlayer.loadVideo(youtubeModel.getYoutube_url());
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                }
            };
            String imageThumbUrl = "https://i.ytimg.com/vi/" + youtubeModel.getYoutube_url() + "/maxresdefault.jpg";
            Picasso.with(this).load(imageThumbUrl).into(image);
        }
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play.setVisibility(View.GONE);
                image.setVisibility(View.GONE);
                youTubePlayerView.initialize(YoutubeConfig.getApiKey(), monInitializedListener);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = "https://www.youtube.com/watch?v=" + youtubeModel.getYoutube_url();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, link+"\n");
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "share"));
            }
        });
    }

    private void checkpermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PICK_STORAGE_PERMISSTION_CODE);

            } else {
                DowloadMyVideo();
            }
        } else {
            DowloadMyVideo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_STORAGE_PERMISSTION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                DowloadMyVideo();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startDowload() {

        // String url = "https://www.youtube.com/watch?v="+youtubeModel.getYoutube_url()+".mp4";
        // String url ="https://firebasestorage.googleapis.com/v0/b/congthucgiadinh.appspot.com/o/mp3%2Fvideo.mp4?alt=media&token=d944b6a4-7ae0-470d-bdc0-90e582436291";
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
//        request.setTitle("Download");
//        request.setDescription("DownLoad File....");
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, String.valueOf(System.currentTimeMillis()));
//        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//        if (downloadManager != null) {
//            downloadManager.enqueue(request);
//            Toast.makeText(this, "Tải xuống thành công !", Toast.LENGTH_SHORT).show();
//        }
    }

    public void DowloadMyVideo() {
        String url = "https://www.youtube.com/watch?v=" + youtubeModel.getYoutube_url();
        YouTubeUriExtractor youTubeUriExtractor = new YouTubeUriExtractor(this) {
            @Override
            public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                if (ytFiles != null) {
                   int itag = 18;
                    String downloadUrl = ytFiles.get(itag).getUrl();

                    String title=youtubeModel.getTitle();
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
                    request.setTitle(title);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title + ".mp4");
                    DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    request.allowScanningByMediaScanner();
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                    if (downloadManager != null) {
                        downloadManager.enqueue(request);
                    }
                    Snackbar snackbar = Snackbar
                            .make(layout, "Đang tải Xuống ! Bạn chờ trong giây lát", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        };
        youTubeUriExtractor.execute(url);
    }
}