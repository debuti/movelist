package tk.ganian.movelist.activities;

import tk.ganian.movelist.R;
import tk.ganian.movelist.services.MovelistService;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class Movelist extends Activity {
	
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
							MovelistPreferences.class));
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
	    MovelistService.preferences = PreferenceManager.getDefaultSharedPreferences(this);

		t = (TextView) findViewById(R.id.TextView01);
		t.setText((String)MovelistService.preferences.getString("prueba2", "defvalue"));

		t = (TextView) findViewById(R.id.TextView02);
		t = (TextView) findViewById(R.id.TextView03);

		t = (TextView) findViewById(R.id.TextView04);
	//	t.setText(MovelistService.preferences.getString("prueba", "defvalue"));
		
		Log.i(this.toString(), "hola");

	}

	/**
	 * Called when another activity comes in front of activity.
	 */
	public void onPause() {
		super.onPause();
		SharedPreferences.Editor ed = MovelistService.preferences.edit();
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