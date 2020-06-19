package com.subhrajyoti.wallify

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog.Builder
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.subhrajyoti.wallify.databinding.ActivitySettingsBinding
import org.polaric.colorful.CActivity
import org.polaric.colorful.Colorful
import org.polaric.colorful.Colorful.ThemeColor.BLUE
import org.polaric.colorful.Colorful.ThemeColor.PINK

class SettingsActivity : CActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_settings)

        binding.toolbar.title = getString(R.string.settings)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, PrefsFragment()).commit()
    }

    class PrefsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.preferences)

            findPreference("dark").onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference: Preference?, o: Any ->
                if (o.toString() == "true") Colorful.config(activity)
                        .primaryColor(BLUE)
                        .accentColor(PINK)
                        .translucent(false)
                        .dark(true)
                        .apply() else Colorful.config(activity)
                        .primaryColor(BLUE)
                        .accentColor(PINK)
                        .translucent(false)
                        .dark(false)
                        .apply()
                val intent = requireActivity().intent
                requireActivity().finish()
                startActivity(intent)
                true
            }
            findPreference("about").onPreferenceClickListener = Preference.OnPreferenceClickListener { preference: Preference? ->
                val builder = Builder(requireContext())
                builder.setTitle(getString(R.string.app_name))
                builder.setMessage(R.string.app_description)
                val positiveText = getString(R.string.ok)
                builder.setPositiveButton(positiveText) { _, _ -> }
                builder.setNegativeButton(getString(R.string.github)) { _, _ ->
                    // negative button logic
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(getString(R.string.github_link))
                    startActivity(i)
                }
                val dialog = builder.create()
                // display dialog
                dialog.show()
                false
            }
        }
    }
}