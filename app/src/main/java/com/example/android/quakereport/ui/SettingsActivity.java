package com.example.android.quakereport.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.quakereport.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class EarthquakePreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference orderByPref = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderByPref);

            Preference minMagPref = findPreference(getString(R.string.settings_min_magnitude_key));
            bindPreferenceSummaryToValue(minMagPref);

            Preference limitPref = findPreference(getString(R.string.settings_limit_key));
            bindPreferenceSummaryToValue(limitPref);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String prefKey = preference.getKey();
            String newVal = newValue.toString();

            // Works for all list preferences regardless of the key
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(newVal);
                if (prefIndex >= 0) {
                    CharSequence[] entryLabels = listPreference.getEntries();
                    preference.setSummary(entryLabels[prefIndex]);
                }
                return true;
            } else if (prefKey.equals(getString(R.string.settings_min_magnitude_key))) {
                double newMinMag = Double.parseDouble(newVal);
                if (newMinMag < 1.0 || newMinMag > 10.0) {
                    Toast.makeText(
                            preference.getContext(),
                            "Min magnitude is 1 and max is 10",
                            Toast.LENGTH_SHORT)
                            .show();
                    return false;
                }
            } else if (prefKey.equals(getString(R.string.settings_limit_key))) {
                int newLimit = Integer.parseInt(newVal);
                if (newLimit < 1 || newLimit > 99) {
                    Toast.makeText(
                            preference.getContext(),
                            "Min limit is 1 and max is 99",
                            Toast.LENGTH_SHORT)
                            .show();
                    return false;
                }
            }

            preference.setSummary(newVal);
            return true;
        }

        /**
         * Shows the value of the preference below the title.
         *
         * @param preference the preference whose value should binned to.
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);

            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String prefString = sharedPrefs.getString(preference.getKey(), "");
            onPreferenceChange(preference, prefString);
        }
    }
}