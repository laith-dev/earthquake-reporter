package com.example.android.quakereport.data;

public class Earthquake {

    /* Magnitude of the quake. */
    private final double magnitude;

    /* Location of the quake. */
    private final String location;

    /* Time in milliseconds of the earthquake. */
    private final long time;

    private final String url;

    /**
     * Constructs a new {@link Earthquake} object.
     *
     * @param magnitude is the magnitude (size) of the earthquake
     * @param location  is the city location of the earthquake
     * @param time      is the time in milliseconds (from the Epoch) when the
     *                  earthquake happened
     */
    public Earthquake(double magnitude, String location, long time, String url) {
        this.magnitude = magnitude;
        this.location = location;
        this.time = time;
        this.url = url;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public String getLocation() {
        return location;
    }

    public long getTime() {
        return time;
    }

    public String getUrl() {
        return url;
    }
}
