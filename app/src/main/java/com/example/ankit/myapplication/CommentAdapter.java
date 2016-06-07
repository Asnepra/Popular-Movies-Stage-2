package com.example.ankit.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.ankit.myapplication.data_object.ReviewData;

import java.util.ArrayList;

/**
 * Created by Ankit on 4/21/2016.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    Context mContext;
    ArrayList<ReviewData> commentDataList;

    public CommentAdapter(Context context, ArrayList<ReviewData> commentDataList) {
        this.mContext = context;
        this.commentDataList = commentDataList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView author, description;
        ImageView poster;


        public ViewHolder(View view) {
            super(view);
            author = (TextView) view.findViewById(R.id.commentors_name);
            description = (TextView) view.findViewById(R.id.commentors_review);
            poster = (ImageView) view.findViewById(R.id.list_item_icon);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color = generator.getRandomColor();
        for (int i=0;i<commentDataList.size();i++)
        {
            color = generator.getRandomColor();
        }


        String authorName=commentDataList.get(position).getAuthor();
        String firstCharacter= String.valueOf(authorName.charAt(0));
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .toUpperCase()
                .width(80)
                .height(80)
                .endConfig()
                .buildRound(firstCharacter, color);
        holder.author.setText(authorName);
        holder.description.setText(commentDataList.get(position).getContent());
        holder.poster.setImageDrawable(drawable);
    }


    @Override
    public int getItemCount() {
        return (null != commentDataList ? commentDataList.size() : 0);
    }
}
