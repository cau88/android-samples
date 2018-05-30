package edu.dartmouth.cs.maptest;

import android.content.Context;
import android.location.Address;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

public class PinsListLoader extends AsyncTaskLoader<List<Comment>>{

    private PinsDataSource dataSource;
    PinsListLoader(@NonNull Context context) {
        super(context);
        // create the database
        this.dataSource = new PinsDataSource(context);
    }

    // worker thread loads the comments
    @Nullable
    @Override
    public List<Comment> loadInBackground() {
        return dataSource.getAllPins();
    }
}
