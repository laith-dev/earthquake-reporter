package com.example.android.quakereport.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.example.android.quakereport.R;
import com.example.android.quakereport.adapters.EarthquakeAdapter;
import com.example.android.quakereport.data.Earthquake;
import com.example.android.quakereport.loaders.EarthquakeLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity
 */
public class EarthquakeActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    private static final String TAG = EarthquakeActivity.class.getName();

    /* This is only really relevant if we were using multiple loaders in the same activity. */
    private static final int EARTHQUAKE_LOADER_ID = 1;

    /* Base URL for getting data from USGS website. */
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    /**
     * Lists the fetched earthquakes.
     */
    private ListView earthquakeLV;

    /**
     * A progress bar to show while fetching earthquake data from the web.
     */
    private ProgressBar fetchingEarthquakesBP;

    /**
     * TextView that is displayed when the list is empty.
     */
    private TextView emptyStateTV;

    private EarthquakeAdapter earthquakeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        earthquakeLV = findViewById(R.id.quakes_list);

        emptyStateTV = findViewById(R.id.no_data_found_text_view);
        earthquakeLV.setEmptyView(emptyStateTV);

        fetchingEarthquakesBP = findViewById(R.id.fetching_earthquakes_progress_bar);
        if (isNetworkConnected()) {
            /* Initialize the loader. Pass in the ID constant defined above and pass in null for
             * the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
             * because this activity implements the LoaderCallbacks interface).
             * */
            getSupportLoaderManager().initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            fetchingEarthquakesBP.setVisibility(View.GONE);
            emptyStateTV.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.earthquake_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.filter_options_menu_itm) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateUI(ArrayList<Earthquake> earthquakes) {
        if (earthquakes == null) {
            throw new IllegalStateException("updateUI(): earthquakes is null!");
        }

        if (earthquakes.isEmpty()) {
            emptyStateTV.setText(R.string.no_earthquakes);
        } else {
            earthquakeAdapter =
                    new EarthquakeAdapter(EarthquakeActivity.this, earthquakes);
            earthquakeLV.setAdapter(earthquakeAdapter);

            earthquakeLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Earthquake clickedEarthquake = earthquakes.get(position);
                    /*
                     * Another two ways to get the clickedEarthquake:
                     * clickedEarthquake = earthquakeAdapter.getItem(position);
                     * clickedEarthquake = (Earthquake) earthquakeLV.getItemAtPosition(position);
                     * */
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(clickedEarthquake.getUrl())
                    ));
                }
            });
        }
    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle bundle) {
        return new EarthquakeLoader(EarthquakeActivity.this, getUrlString());
    }

    private String getUrlString() {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this);

        String orderByPref = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        String minMagPref = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String limitPref = sharedPrefs.getString(
                getString(R.string.settings_limit_key),
                getString(R.string.settings_limit_default));

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson")
                .appendQueryParameter("limit", limitPref)
                .appendQueryParameter("minmag", minMagPref)
                .appendQueryParameter("orderby", orderByPref);

        return uriBuilder.toString();
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakeList) {
        fetchingEarthquakesBP.setVisibility(View.GONE);
        updateUI((ArrayList<Earthquake>) earthquakeList);
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        earthquakeAdapter.clear();
    }

    /**
     * Checks the internet connection.
     *
     * @return true if the device is connected to the internet otherwise -- false.
     */
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
}