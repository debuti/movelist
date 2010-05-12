package tk.ganian.movelist.activities;

import tk.ganian.movelist.R;
import tk.ganian.movelist.logic.ActionsDbAdapter;
import tk.ganian.movelist.services.MovelistService;
import tk.ganian.movelist.services.WakefulService;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class Movelist extends ListActivity {

	private int mActionNumber = 1;
	private ActionsDbAdapter mDbHelper;
	
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);
		setContentView(R.layout.list);
		ActionsDbAdapter mDbHelper = new ActionsDbAdapter(this);
		mDbHelper.open();
		fillData();
		
		Context context = getApplicationContext();
		WakefulService.acquireStaticLock(context);
		context.startService(new Intent(context, MovelistService.class));
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
		case R.id.add_action:
			createAction();
			return true;
		case R.id.remove_action:
			this.startActivity(new Intent(this, MovelistPreferences.class));
			return true;
		case R.id.preferences:
			this.startActivity(new Intent(this, MovelistPreferences.class));
			return true;
		case R.id.about:
			Dialog d = new Dialog(this);
			d.setContentView(R.layout.about);
			d.setTitle(this.getString(R.string.about_) + " v"
					+ this.getString(R.string.app_version));
			return true;
		}
		return super.onOptionsItemSelected(item);
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
//		MovelistService.preferences = PreferenceManager
//				.getDefaultSharedPreferences(this);
//
//		t = (TextView) findViewById(R.id.TextView01);
//		t.setText((String) MovelistService.preferences.getString("prueba2",
//				"defvalue"));
//
//		t = (TextView) findViewById(R.id.TextView02);
//		t = (TextView) findViewById(R.id.TextView03);
//
//		t = (TextView) findViewById(R.id.TextView04);
//		// t.setText(MovelistService.preferences.getString("prueba",
//		// "defvalue"));

		Log.i(this.toString(), "hola");

	}

	/**
	 * Called when another activity comes in front of activity.
	 */
	public void onPause() {
		super.onPause();
//		SharedPreferences.Editor ed = MovelistService.preferences.edit();
//	
//		ed.commit();
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

	private void fillData() {
		// Get all of the notes from the database and create the item list
		Cursor c = mDbHelper.fetchAllNotes();
		startManagingCursor(c);

		String[] from = new String[] { ActionsDbAdapter.KEY_TITLE };
		int[] to = new int[] { R.id.text1 };

		// Now create an array adapter and set it to display using our row
		SimpleCursorAdapter notes = new SimpleCursorAdapter(this,
				R.layout.list_row, c, from, to);
		setListAdapter(notes);
	}
	
	private void createAction() {
        String noteName = "Note " + mActionNumber++;
        mDbHelper.createNote(noteName, "");
        fillData();
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