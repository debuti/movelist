package tk.ganian.keepitinbounds.activities;

import tk.ganian.keepitinbounds.R;
import tk.ganian.keepitinbounds.services.KeepItInboundsService;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class KeepItInbounds extends Activity {
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

	}

	/** Called when the menu key is pressed. */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	/** Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.preferences:
			this
					.startActivity(new Intent(this,
							KeepItInboundsPreferences.class));
			return true;
		case R.id.about:
			Dialog d = new Dialog(this);
			d.setContentView(R.layout.about);
			d.setTitle(this.getString(R.string.about_) + " v"
					+ this.getString(R.string.app_version));
			return true;
		}
		return false;
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
		TextView t = new TextView(getApplicationContext());

		t = (TextView) findViewById(R.id.TextView01);
		t.setText(KeepItInboundsService.getCurrentImei());

		t = (TextView) findViewById(R.id.TextView02);
		t
				.setText(String.valueOf(KeepItInboundsService
						.getMinutesConsumed())
						+ "/"
						+ String.valueOf(KeepItInboundsService
								.getMinutesLimit()));

		t = (TextView) findViewById(R.id.TextView03);
		t.setText(String.valueOf(KeepItInboundsService.getBytes3GConsumed()));

		t = (TextView) findViewById(R.id.TextView04);
	//	t.setText(KeepItInboundsService.preferences.getString("prueba", "defvalue"));
		
		Log.i("TODO", "hola");

	}

	/**
	 * Called when another activity comes in front of activity.
	 */
	public void onPause() {
		super.onPause();
		SharedPreferences.Editor ed = KeepItInboundsService.preferences.edit();
      //  ed.putString("prueba", mCurViewMode);
        ed.commit();
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
}

// -Poner icono
// -Añadir LOG
// -Hacer objeto de preferences
// -HAcer qe el servicio se inicie siempre
// -Hacer que las notificaciones no sean pesadas
// -HAcer que el activity refresque la informacion sin ser una puta mierda
// -Almacenar cosas en la sd
// -Estar seguro de que no me van a matar el servicio. Escuchar algun evento
// continuo, quitar el ciclico de mierda ese