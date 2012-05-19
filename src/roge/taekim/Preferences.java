package roge.taekim;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class Preferences extends PreferenceActivity{
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preferences);
        
        Preference ads_enabled=(Preference)this.findPreference("ads_enabled");
        ads_enabled.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getBaseContext(),"You will have to restart this app for this change to take place.",Toast.LENGTH_LONG).show();
                
                return true;
            }
        });
    }
}
