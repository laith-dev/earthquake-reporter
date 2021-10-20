package com.example.android.quakereport.loaders;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.example.android.quakereport.data.Earthquake;
import com.example.android.quakereport.utils.QueryUtils;

import java.util.List;

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    private static final String TAG = EarthquakeLoader.class.getName();

    /**
     * The url used to fetch quake data from the internet.
     */
    private final String url;

    /**
     * Constructs a new {@link EarthquakeLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public EarthquakeLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Earthquake> loadInBackground() {
        if (this.url == null) {
            return null;
        }

        return QueryUtils.fetchEarthquakeData(url);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}