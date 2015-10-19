package com.pawciobiel.fgpst;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class FgpstPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
