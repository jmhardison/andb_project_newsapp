package com.jonathanhardison.andb_project_newsapp;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

        /***
         * as preference is set, handle an event to update settings fragment info.
         * @param preference
         * @param newValue
         * @return
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String valString = newValue.toString();

            //change from value to label for clarity
            //otherwise use value itself.
            if (preference instanceof ListPreference){
                ListPreference listItemPref = (ListPreference) preference;
                int indexPref = listItemPref.findIndexOfValue(valString);
                if(indexPref >= 0){
                    CharSequence[] prefLabels = listItemPref.getEntries();
                    preference.setSummary(prefLabels[indexPref]);
                }
            }
            else{
                preference.setSummary(valString);
            }

            return true;
        }

        /***
         * oncreate method
         * @param savedInstanceState
         */
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            //hook to listen event
            Preference desiredCategory = findPreference(getString(R.string.settings_selected_category_key));
            bindPreferenceSumToValue(desiredCategory);

            //hook to listen event
            Preference desiredOrderBy = findPreference(getString(R.string.settings_orderby_key));
            bindPreferenceSumToValue(desiredOrderBy);
        }

        /***
         * helper to bind and set summary value.
         * @param preference
         */
        private void bindPreferenceSumToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}
