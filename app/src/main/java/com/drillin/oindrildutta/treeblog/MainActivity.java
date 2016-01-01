package com.drillin.oindrildutta.treeblog;

import android.R.id;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.drillin.oindrildutta.treeblog.R.layout;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends ListActivity implements listAdd {
    protected static final String BLOGS = "blogs";
    private final String[] COLORS = {"#1abc9c", "#2ecc71", "#3498db", "#9b59b6", "#34495e", "#16a085", "#27ae60", "#2980b9", "#8e44ad", "#2c3e50", "#f1c40f", "#e67e22", "#e74c3c", "#f39c12", "#d35400", "#c0392b", "#fc970b"};
    //private static final String PREFS_FILE = "com.drillin.treeblog.preferences";
    //private final String TAG = MainActivity.class.getSimpleName();
    final int col = Color.parseColor("#5cb860");
    final int colOpp = Color.parseColor("#A3479F");
    //private final String DOWN_DATA = "LENGTH";
    private final String MAIN_DATA = "JSON";
    private final String COLOR_DATA = "COL";
    private final String SORT_DATA = "SORT";
    private final String MODE_DATA = "MODE";

    @Bind(R.id.background) RelativeLayout background;
    @Bind(id.empty) TextView retry;
    @Bind(id.list) ListView list;
    //@Bind(R.id.blogSearch) SearchView search;
    @Bind(R.id.progressBar) ProgressBar bar;
    @Bind(R.id.dateToggle) ToggleButton dateToggle;
    @Bind(R.id.titleToggle) ToggleButton titleToggle;
    @Bind(R.id.authorToggle) ToggleButton authorToggle;
    //@Bind(R.id.scoreToggle) ToggleButton scoreToggle;

    private static final String LIST_STATE = "listState";
    //private SharedPreferences.Editor prefEditor;
    private Parcelable mListState;
    private final Random gen = new Random();
    private ArrayList<Blog> blogs;
    private boolean[] sortChecks;
    private ListAdapter lAdapter;
    private String jsonData;
    //private int downloaded;
    private int sortMode;
    private int color;
    private int index;
    private int top;

    // https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html#foreground.type=image&for
    // eground.space.trim=0&foreground.space.pad=0.05&foreColor=fff%2C0&crop=0&backgroundShape=squar
    // e&backColor=fff%2C100&effects=shadow

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        /*SharedPreferences sharedPreferences = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        prefEditor = sharedPreferences.edit();
        prefEditor.apply();
        jsonData = sharedPreferences.getString(MAIN_DATA, "");
        downloaded = sharedPreferences.getInt(DOWN_DATA, -1);*/
        jsonData = "";
        color = Color.parseColor(COLORS[gen.nextInt(COLORS.length)]);
        sortChecks = new boolean[]{false, false, false};
        sortMode = 0;
        if(savedInstanceState != null) {
            //downloaded = savedInstanceState.getInt(DOWN_DATA, -1);//downloaded);
            jsonData = savedInstanceState.getString(MAIN_DATA, jsonData);//jsonData);
            sortChecks = savedInstanceState.getBooleanArray(SORT_DATA);
            sortMode = savedInstanceState.getInt(MODE_DATA, sortMode);
            mListState = savedInstanceState.getParcelable(LIST_STATE);
            color = savedInstanceState.getInt(COLOR_DATA, color);
        }
        blogs = new ArrayList<>();
        /*search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setUp(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                setUp(query);
                return false;
            }
        });*/
        setUp();
    }

    private void setUp() {
        ButterKnife.bind(this);
        bar.setIndeterminate(true);
        retry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setUp();
            }
        });
        dateToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sortChecks[0] = dateToggle.isChecked();
                sortChecks[1] = false;
                sortChecks[2] = false;
                sortMode = 0;
                updateChecks();
            }
        });
        titleToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sortChecks[0] = false;
                sortChecks[1] = titleToggle.isChecked();
                sortChecks[2] = false;
                sortMode = 1;
                updateChecks();
            }
        });
        authorToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sortChecks[0] = false;
                sortChecks[1] = false;
                sortChecks[2] = authorToggle.isChecked();
                sortMode = 2;
                updateChecks();
            }
        });
        background.setBackground(new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{col, color}));
        index = list.getFirstVisiblePosition();
        top = list.getChildAt(0) == null ? 0 : list.getChildAt(0).getTop() - list.getPaddingTop();
        if(jsonData != null && !Objects.equals(jsonData, ""))
            getOldPosts(jsonData);
        else
            startGetBlog("http://blog.teamtreehouse.com/api/get_recent_summary/?page=");
    }

    private void updateChecks() {
        dateToggle.setChecked(sortChecks[0]);
        if (dateToggle.isChecked())
            dateToggle.setTextColor(colOpp);
        else
            dateToggle.setTextColor(col);
        titleToggle.setChecked(sortChecks[1]);
        if (titleToggle.isChecked())
            titleToggle.setTextColor(colOpp);
        else
            titleToggle.setTextColor(col);
        authorToggle.setChecked(sortChecks[2]);
        if (authorToggle.isChecked())
            authorToggle.setTextColor(colOpp);
        else
            authorToggle.setTextColor(col);
        //Toast.makeText(getApplicationContext(), " "+sortChecks[0]+" "+sortChecks[1]+" "+sortChecks[2], Toast.LENGTH_SHORT).show();
        if(blogs != null) {
            for(int i = 0; i < blogs.size(); i++)
                blogs.get(i).setSortMode(sortMode);
            Collections.sort(blogs);
            if (sortChecks[sortMode])
                Collections.reverse(blogs);
        }
        lAdapter.notifyDataSetChanged();
    }

    private void cleanUp() {
        ButterKnife.bind(this);
        blogs = removeDuplicates(blogs);
        lAdapter = new ListAdapter(this, blogs, this);
        setListAdapter(lAdapter);
        lAdapter.notifyDataSetChanged();
        if (mListState != null)
            getListView().onRestoreInstanceState(mListState);
        mListState = null;
        updateChecks();
        bar.setIndeterminate(false);
    }

    private ArrayList<Blog> removeDuplicates(ArrayList<Blog> blogs) {
        ArrayList<Blog> cleansed = new ArrayList<>();
        for(int i = 0; i < blogs.size(); i++)
            if(!cleansed.contains(blogs.get(i)))
                cleansed.add(blogs.get(i));
        return cleansed;
    }

    private void wifiReset() {
        ButterKnife.bind(this);
        sortChecks[0] = false;
        sortChecks[1] = false;
        sortChecks[2] = false;
        updateChecks();
        list.setSelectionFromTop(index, top);
    }

    private boolean getNetworkAvailability() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void alertUserAboutError(String title, String message, String button) {
        AlertDialogFragment dialog = new AlertDialogFragment(title, message, button);
        dialog.show(getFragmentManager(), "error_dialog");
    }

    /*private int getOldCount(String data) {
        try {
            JSONArray arr = new JSONArray(data);
            return arr.length();
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }*/

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(getApplicationContext(), BlogActivity.class);
        i.putExtra("blogs", blogs);
        i.putExtra("id", position);
        startActivity(i);
    }

    private void getOldPosts(String data) {
        //Toast.makeText(getApplicationContext(), "Using Local.", Toast.LENGTH_SHORT).show();
        blogs = new ArrayList<>();
        if(data.isEmpty())
            alertUserAboutError("No Wifi!", "You Need To Connect To Wifi", "Ok!");
            //Toast.makeText(getApplicationContext(), "You Need To Connect To Wifi", Toast.LENGTH_SHORT).show();
        else {
            try {
                JSONArray arr = new JSONArray(data);
                for(int i = 0; i < arr.length(); i++) {
                    JSONObject post = arr.getJSONObject(i);
                    blogs.add(i, new Blog(i, post.getString("url"), post.getString("title"), post.getString("date"), post.getString("author")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cleanUp();
    }

    @Override
    public void startGetBlog(final String base) {
        if(!getNetworkAvailability()) {
            getOldPosts(jsonData);
            return;
        }
        ButterKnife.bind(this);
        bar.setIndeterminate(true);
        //Toast.makeText(getApplicationContext(), "Using Wifi.", Toast.LENGTH_SHORT).show();
        index = list.getFirstVisiblePosition();
        top = list.getChildAt(0) == null ? 0 : list.getChildAt(0).getTop() - list.getPaddingTop();
        new OkHttpClient().newCall(new Builder().url(base+'1').build()).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
            }

            @Override
            public void onResponse(Response response) {
                try {
                    if (response.isSuccessful()) {
                        final JSONObject data = new JSONObject(response.body().string());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    getBlogPosts(base, data.getInt("count"), data.getInt("pages"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getBlogPosts(String base, final int count, int pages) {
        final int page = blogs.size()/count + 1;
        if(page <= pages) {
            new OkHttpClient().newCall(new Builder().url(base + page).build()).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                }

                @Override
                public void onResponse(Response response) {
                    try {
                        if (response.isSuccessful()) {
                            JSONArray blogList = new JSONObject(response.body().string()).getJSONArray("posts");
                            for (int j = 0; j < blogList.length(); j++) {
                                JSONObject post = (JSONObject) blogList.get(j);
                                blogs.add(count * (page - 1) + j, new Blog(count * (page - 1) + j, post.getString("url"), post.getString("title"), post.getString("date"), post.getString("author")));
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cleanUp();
                                    wifiReset();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        prefEditor.apply();
        if(blogs != null) {
            prefEditor.putString(MAIN_DATA, new Gson().toJson(blogs));
            prefEditor.putInt(DOWN_DATA, blogs.size());
        }
    }*/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(blogs != null) {
            outState.putString(MAIN_DATA, new Gson().toJson(blogs));
            //outState.putInt(DOWN_DATA, blogs.size());
            mListState = getListView().onSaveInstanceState();
            outState.putParcelable(LIST_STATE, mListState);
        }
        outState.putBooleanArray(SORT_DATA, sortChecks);
        outState.putInt(MODE_DATA, sortMode);
        outState.putInt(COLOR_DATA, color);
    }
}