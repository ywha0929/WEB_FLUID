package com.example.fluidguest;

import android.os.Parcel;
import android.os.Parcelable;



public class PJson implements Parcelable {
    private String jsonString;

    public PJson(){super();}
    public PJson(String jsonString)
    {
        this.jsonString = jsonString;
    }
    protected PJson(Parcel in)
    {
        this.jsonString = in.readString();
    }

    public static final Creator<PJson> CREATOR = new Creator<PJson>() {
        @Override
        public PJson createFromParcel(Parcel in) {
            return new PJson(in);
        }

        @Override
        public PJson[] newArray(int size) {
            return new PJson[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(jsonString);
    }
    public void readFromParcel(Parcel in)
    {
        this.jsonString = in.readString();
    }

    public String getString() {
        return jsonString;
    }
}