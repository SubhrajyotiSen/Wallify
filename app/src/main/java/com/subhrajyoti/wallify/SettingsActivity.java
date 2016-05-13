package com.subhrajyoti.wallify;

import android.graphics.Color;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.prefs.ATEColorPreference;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;

import butterknife.Bind;

public class SettingsActivity extends BaseThemeActivity implements ColorChooserDialog.ColorCallback{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);


        getFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new PrefsFragment()).commit();

    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {

        final Config config = ATE.config(this, getATEKey());
        config.accentColor(selectedColor);
        config.commit();
        recreate();

    }

    public static class PrefsFragment extends PreferenceFragment {
        String mAteKey;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);


        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mAteKey = ((SettingsActivity) getActivity()).getATEKey();
            findPreference("updates").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    return false;
                }
            });

            findPreference("about").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new MaterialDialog.Builder(getActivity())
                            .title("Wallify")
                            .content("Created by Subhrajyoti Sen")
                            .show();
                    return false;
                }
            });
            findPreference("dark_theme").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Marks both theme configs as changed so MainActivity restarts itself on return
                    Config.markChanged(getActivity(), "light_theme");
                    Config.markChanged(getActivity(), "dark_theme");
                    // The dark_theme preference value gets saved by Android in the default PreferenceManager.
                    // It's used in getATEKey() of both the Activities.
                    getActivity().recreate();
                    return true;
                }
            });
            ATEColorPreference primaryColorPref = (ATEColorPreference) findPreference("accent_color");
            primaryColorPref.setColor(Config.primaryColor(getActivity(), mAteKey), Color.BLACK);
            primaryColorPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new ColorChooserDialog.Builder((SettingsActivity) getActivity(),R.string.app_name)
                            .preselect(Config.primaryColor(getActivity(), mAteKey))
                            .dynamicButtonColor(true)
                            .show();
                    return true;
                }
            });
        }
    }
}
