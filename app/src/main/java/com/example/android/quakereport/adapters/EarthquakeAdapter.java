package com.example.android.quakereport.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.android.quakereport.R;
import com.example.android.quakereport.data.Earthquake;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    // Location offset and primary location separator.
    private static final String LOCATION_SEPARATOR = " of ";

    public EarthquakeAdapter(Context context, ArrayList<Earthquake> earthquakes) {
        super(context, 0, earthquakes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Reuse an existing View.
        View quakeItemLayout = convertView;

        // If it doesn't exist, create it.
        if (quakeItemLayout == null) {
            quakeItemLayout = LayoutInflater.from(getContext())
                    .inflate(R.layout.earthquake_list_item, parent, false);
        }

        Earthquake currentEarthquake = getItem(position);

        /* Fill the details of the currentEarthquake in quakeItemLayout contents. */
        TextView magnitudeTV = quakeItemLayout.findViewById(R.id.magnitude_tv);
        magnitudeTV.setText(formatMag(currentEarthquake.getMagnitude()));

        /* Set the proper background color on the magnitude circle.
         * Fetch the background from the TextView, which is a GradientDrawable. */
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeTV.getBackground();

        // Set the color on the magnitude circle.
        magnitudeCircle.setColor(getMagnitudeColor(currentEarthquake.getMagnitude()));

        String[] location = currentEarthquake.getLocation().split(LOCATION_SEPARATOR);

        TextView offsetLocation = quakeItemLayout.findViewById(R.id.location_offset);
        TextView primaryLocation = quakeItemLayout.findViewById(R.id.primary_location);

        /* If the location string specifies both an offset and a primary location. */
        if (location.length > 1) {
            location[0] += LOCATION_SEPARATOR;
            offsetLocation.setText(location[0]);
            primaryLocation.setText(location[1]);
        } else {
            offsetLocation.setText(R.string.near_the);
            primaryLocation.setText(location[0]);
        }

        Date dateObject = new Date(currentEarthquake.getTime());

        TextView dateTV = quakeItemLayout.findViewById(R.id.date_tv);
        dateTV.setText(formatDate(dateObject));

        TextView timeTV = quakeItemLayout.findViewById(R.id.time_tv);
        timeTV.setText(formatTime(dateObject));

        return quakeItemLayout;
    }

    private String formatMag(double mag) {
        return new DecimalFormat("0.0").format(mag);
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResId;
        if (magnitude >= 0.0 && magnitude <= 2.0) {
            magnitudeColorResId = R.color.magnitude1;
        } else if (magnitude >= 2.0 && magnitude <= 3.0) {
            magnitudeColorResId = R.color.magnitude2;
        } else if (magnitude >= 3.0 && magnitude <= 4.0) {
            magnitudeColorResId = R.color.magnitude3;
        } else if (magnitude >= 4.0 && magnitude <= 5.0) {
            magnitudeColorResId = R.color.magnitude4;
        } else if (magnitude >= 5.0 && magnitude <= 6.0) {
            magnitudeColorResId = R.color.magnitude5;
        } else if (magnitude >= 6.0 && magnitude <= 7.0) {
            magnitudeColorResId = R.color.magnitude6;
        } else if (magnitude >= 7.0 && magnitude <= 8.0) {
            magnitudeColorResId = R.color.magnitude7;
        } else if (magnitude >= 8.0 && magnitude <= 9.0) {
            magnitudeColorResId = R.color.magnitude8;
        } else if (magnitude >= 9.0 && magnitude <= 10.0) {
            magnitudeColorResId = R.color.magnitude9;
        } else {
            magnitudeColorResId = R.color.magnitude10plus;
        }

        return ContextCompat.getColor(getContext(), magnitudeColorResId);
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date date) {
        return new SimpleDateFormat("MMM dd, yyyy").format(date);
        /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String formattedDate = simpleDateFormat.format(date);
        return formattedDate;*/
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date date) {
        return new SimpleDateFormat("h:mm a").format(date);
    }

}