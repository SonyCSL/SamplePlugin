/*
 * Copyright (C) 2013-2014 Sony Computer Science Laboratories, Inc. All Rights Reserved.
 * Copyright (C) 2014 Sony Corporation. All Rights Reserved.
 */

package com.sonycsl.Kadecot.plugin.sample;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * This class is a implementation of Settings activity. <br>
 * This activity is able to be launched by Kadecot devices tab.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        getFragmentManager().beginTransaction()
                .add(R.id.container, SettingsFragment.newInstance())
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static final class SettingsFragment extends PreferenceFragment {

        public static SettingsFragment newInstance() {
            return new SettingsFragment();
        }

        public SettingsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.sample_preferences);
        }
    }
}
