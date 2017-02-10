package com.subhrajyoti.wallify;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.polaric.colorful.CActivity;
import org.polaric.colorful.Colorful;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends CActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        assert toolbar != null;
        toolbar.setTitle(getString(R.string.settings));
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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

            findPreference("dark").setOnPreferenceChangeListener((preference, o) -> {
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

                return true;
            });
            findPreference("about").setOnPreferenceClickListener(preference -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.app_name));
                builder.setMessage(R.string.app_description);

                String positiveText = getString(android.R.string.ok);
                builder.setPositiveButton(positiveText,
                        (dialog, which) -> {
                            // positive button logic
                        });

                builder.setNegativeButton(getString(R.string.github),
                        (dialog, which) -> {
                            // negative button logic
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(getString(R.string.github_link)));
                            startActivity(i);

                        });

                AlertDialog dialog = builder.create();
                // display dialog
                dialog.show();
                return false;
            });


        }
    }
}
