package com.example.ankit.myapplication.data_object;

import android.os.Parcel;
import android.os.Parcelable;


public class ReviewData implements Parcelable {

    private String id;
    private String author;
    private String content;
    private String url;

    public static final Creator<ReviewData> CREATOR = new Creator<ReviewData>() {
        @Override
        public ReviewData createFromParcel(Parcel in) {
            return new ReviewData(in);
        }

        @Override
        public ReviewData[] newArray(int size) {
            return new ReviewData[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);
    }

    public ReviewData() {

    }

    protected ReviewData(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }
}
