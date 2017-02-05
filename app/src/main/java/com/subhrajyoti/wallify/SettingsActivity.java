package com.subhrajyoti.wallify;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.polaric.colorful.CActivity;
import org.polaric.colorful.Colorful;

public class SettingsActivity extends CActivity {

    private static final String ANALYTICS_ID = "Settings";
    private static FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
        toolbar.setTitle(getString(R.string.settings));
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        getFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new PrefsFragment()).commit();

    }



    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            findPreference("dark").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if (o.toString().equals("true"))
                        Colorful.config(getActivity())
                                .primaryColor(Colorful.ThemeColor.BLUE)
                                .accentColor(Colorful.ThemeColor.PINK)
                                .translucent(false)
                                .dark(true)
                                .apply();
                    else
                        Colorful.config(getActivity())
                                .primaryColor(Colorful.ThemeColor.BLUE)
                                .accentColor(Colorful.ThemeColor.PINK)
                                .translucent(false)
                                .dark(false)
                                .apply();
                    Intent intent = getActivity().getIntent();
                    getActivity().finish();
                    startActivity(intent);

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, ANALYTICS_ID);
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ANALYTICS_ID);
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Dark mode changed");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                    return true;
                }
            });
            findPreference("about").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.app_name));
                    builder.setMessage(R.string.app_description);

                    String positiveText = getString(android.R.string.ok);
                    builder.setPositiveButton(positiveText,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // positive button logic
                                }
                            });

                    builder.setNegativeButton(getString(R.string.github),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // negative button logic
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(getString(R.string.github_link)));
                                    startActivity(i);

                                }
                            });

                    AlertDialog dialog = builder.create();
                    // display dialog
                    dialog.show();
                    return false;
                }
            });


        }
    }
}
