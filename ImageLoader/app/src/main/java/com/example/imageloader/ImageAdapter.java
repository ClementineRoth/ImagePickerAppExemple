package com.example.imageloader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.io.File;

public class ImageAdapter extends BaseAdapter {

    private Context thisContext;

    ///storage/sdcard0
    private File[] external_storage;

    public ImageAdapter(Context context, String [] name, int resourceId){
        thisContext=context;
        getImageGalerieListe();

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    public void getImageGalerieListe(){

    }

}
