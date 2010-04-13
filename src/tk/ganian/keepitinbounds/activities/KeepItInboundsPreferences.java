package tk.ganian.keepitinbounds.activities;

import tk.ganian.keepitinbounds.R;
import tk.ganian.keepitinbounds.services.KeepItInboundsService;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class KeepItInboundsPreferences extends PreferenceActivity implements
SharedPreferences.OnSharedPreferenceChangeListener{

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.preferences);
		KeepItInboundsService.preferences
				.registerOnSharedPreferenceChangeListener(this);

	}

	/** Called when the activity is recalled after not been visible. */
	public void onStart() {
		super.onStart();
	}

	/**
	 * Called when the activity is recalled after another activity being in
	 * front of it.
	 */
	public void onResume() {
		super.onResume();
	}

	/**
	 * Called when another activity comes in front of activity.
	 */
	public void onPause() {
		super.onPause();
	}

	/**
	 * Called when the activity is no longer visible.
	 */
	public void onStop() {
		super.onStop();
	}

	/**
	 * Called when the activity is killed.
	 */
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		
	}

}