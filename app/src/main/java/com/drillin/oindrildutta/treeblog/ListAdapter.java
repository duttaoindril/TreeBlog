package com.drillin.oindrildutta.treeblog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Blog> blogs;
    private final listAdd listener;

    public ListAdapter(Context context, ArrayList<Blog> blogs, listAdd listener) {
        this.context = context;
        this.blogs = blogs;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        if(blogs == null)
            return 0;
        return blogs.size();
    }

    @Override
    public Object getItem(int position) {
        if(blogs == null)
            return null;
        return blogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0L;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.title = (TextView)convertView.findViewById(R.id.blogTitle);
            holder.author = (TextView)convertView.findViewById(R.id.blogAuthor);
            holder.date = (TextView)convertView.findViewById(R.id.blogDate);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        holder.title.setText(blogs.get(position).getTitle());
        holder.author.setText(blogs.get(position).getAuthor());
        holder.date.setText(blogs.get(position).getDate());
        if(position == getCount() - 1)
            listener.startGetBlog("http://blog.teamtreehouse.com/api/get_recent_summary/?page=");
        return convertView;
    }

    private static class ViewHolder {
        TextView title;
        TextView author;
        TextView date;
    }
}