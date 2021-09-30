package com.nhatnguyen.youtubedowload;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhatnguyen.youtubedowload.utils.CheckConnect;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rcy_video;
    private Toolbar toolbar;
    private ArrayList<YoutubeModel> youtubeModelArrayList;
    private DatabaseReference dbAll;
    private ProgressBar progressBar;
    private FloatingActionButton fab_add;
    private SearchView searchView;
    private YoutubeAdapter youtubeAdapter;
    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!CheckConnect.haveNetworkConnection(getApplicationContext())) {
            CheckConnect.openWarningDialog(MainActivity.this, Gravity.CENTER);
        }
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.youtube);
        setSupportActionBar(toolbar);
        layout = findViewById(R.id.layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        //
        fab_add = findViewById(R.id.fab);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInfoDialog(Gravity.BOTTOM);
            }
        });
        //
        rcy_video = findViewById(R.id.ryc_video);

        rcy_video.setHasFixedSize(true);
        rcy_video.setLayoutManager(new LinearLayoutManager(this));
        youtubeModelArrayList = new ArrayList<>();
        youtubeAdapter = new YoutubeAdapter(youtubeModelArrayList, getApplicationContext());
        rcy_video.setAdapter(youtubeAdapter);
        rcy_video.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    fab_add.hide();
                } else {
                    fab_add.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        dbAll = FirebaseDatabase.getInstance().getReference("Youtube");
        dbAll.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapShot) {
                if (dataSnapShot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    for (DataSnapshot ds : dataSnapShot.getChildren()) {
                        String id = ds.getKey();
                        String title = ds.child("title").getValue(String.class);
                        String YoutubeUrl = ds.child("youtube_url").getValue(String.class);
                        YoutubeModel youtubeModel = new YoutubeModel(id, title, YoutubeUrl);
                        youtubeModelArrayList.add(youtubeModel);
                    }
                    youtubeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void openInfoDialog(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_bottom_add);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        if (Gravity.BOTTOM == gravity) {
            dialog.setCancelable(true);
        }
        TextView AddVideo = dialog.findViewById(R.id.AddVideo);
        TextView Downloadlink = dialog.findViewById(R.id.Downloadlink);
        AddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddVideoDialog(Gravity.CENTER);
                dialog.dismiss();

            }
        });
        Downloadlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddLinkYoutubeDialog(Gravity.CENTER);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void openAddLinkYoutubeDialog(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_link_youtube);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        if (Gravity.CENTER == gravity) {
            dialog.setCancelable(true);
        }
        EditText edit_link = dialog.findViewById(R.id.edit_link);
        Button btnDownload = dialog.findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = edit_link.getText().toString().trim();
                if (TextUtils.isEmpty(url)) {
                    edit_link.setError("Xin lỗi ! Bạn chưa điền đường dẫn");
                    return;
                } else {
                    YouTubeUriExtractor youTubeUriExtractor = new YouTubeUriExtractor(getApplication()) {
                        @Override
                        public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                            if (ytFiles != null) {
                                int itag = 18;
                                String downloadUrl = ytFiles.get(itag).getUrl();
                                String title = "My Video Download";
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
                            }
                            Snackbar snackbar = Snackbar
                                    .make(layout, "Đang tải Xuống ! Bạn chờ trong giây lát", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    };
                    youTubeUriExtractor.execute(url);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void openAddVideoDialog(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_add_link);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        if (Gravity.CENTER == gravity) {
            dialog.setCancelable(false);
        }
        EditText editLink = dialog.findViewById(R.id.edit_link);
        EditText editTitle = dialog.findViewById(R.id.edit_title);
        Button btnback = dialog.findViewById(R.id.btnBack);
        Button btnAdd = dialog.findViewById(R.id.btnAdd);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = dbAll.push().getKey();
                String link = editLink.getText().toString();
                String title = editTitle.getText().toString();
                if (TextUtils.isEmpty(link)) {
                    editLink.setError("Xin lỗi ! Bạn chưa điền đường dẫn");
                    return;
                }
                if (TextUtils.isEmpty(title)) {
                    editTitle.setError("Xin lỗi ! Bạn chưa điền tiêu đề");
                    return;
                }
                if (!TextUtils.isEmpty(link) && !TextUtils.isEmpty(title)) {
                    YoutubeModel add = new YoutubeModel(id, title, link);
                    dbAll.child(id).setValue(add);
//                    editLink.setText("");
//                    editTitle.setText("");
                    Toast.makeText(MainActivity.this, "Đã thêm thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                 //   finish();
                    startActivity(getIntent());
                }
            }
        });
        dialog.show();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.nav_search:
                Toast.makeText(this, "search", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_head, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.nav_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        //
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));
        //
        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_button);
        searchIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_search_white));
        //
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                youtubeAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                youtubeAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }
}