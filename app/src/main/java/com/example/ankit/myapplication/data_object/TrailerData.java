package com.example.ankit.myapplication.data_object;

import android.os.Parcel;
import android.os.Parcelable;


public class TrailerData implements Parcelable {

    private String source;
    private String name;

    public static final Creator<TrailerData> CREATOR = new Creator<TrailerData>() {
        @Override
        public TrailerData createFromParcel(Parcel in) {
            return new TrailerData(in);
        }

        @Override
        public TrailerData[] newArray(int size) {
            return new TrailerData[size];
        }
    };
    public String trailerUrl;
    public String imageUrl;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(source);
        dest.writeString(name);
    }

    public TrailerData() {

    }

    protected TrailerData(Parcel in) {

        source = in.readString();
        name = in.readString();
    }
}
