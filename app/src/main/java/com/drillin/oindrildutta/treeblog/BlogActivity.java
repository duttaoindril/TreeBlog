package com.drillin.oindrildutta.treeblog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BlogActivity extends AppCompatActivity {
    private static final String BLOGS = MainActivity.BLOGS;
    @Bind(R.id.blogView) WebView blogView;
    @Bind(R.id.previous) Button left;
    @Bind(R.id.post) Button right;

    private ArrayList<Blog> blogs;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog);
        id = getIntent().getIntExtra("id", 0);
        blogs = (ArrayList<Blog>) getIntent().getSerializableExtra(BLOGS);
        if(savedInstanceState != null) {
            id = savedInstanceState.getInt("id", id);
            blogs = (ArrayList<Blog>) savedInstanceState.getSerializable(BLOGS);
        }
        ButterKnife.bind(this);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id--;
                if(id < 0) {
                    Toast.makeText(getApplicationContext(), "You are at the first post!", Toast.LENGTH_SHORT).show();
                    id = 0;
                } else
                    loadBlog();
            }
        });
        right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                id++;
                if(id >= blogs.size()) {
                    Toast.makeText(getApplicationContext(), "You are at the last post!\nGo back and load more.", Toast.LENGTH_SHORT).show();
                    id = blogs.size()-1;
                } else
                    loadBlog();
            }
        });
        loadBlog();
    }

    private void loadBlog() {
        ButterKnife.bind(this);
        blogView.loadUrl(blogs.get(id).getUrl());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("id", id);
        outState.putSerializable(BLOGS, blogs);
    }
}
