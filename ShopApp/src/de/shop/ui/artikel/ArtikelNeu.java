package de.shop.ui.artikel;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import de.shop.R;
import de.shop.data.Artikel;
import static de.shop.util.Constants.ARTIKEL_KEY;

public class ArtikelNeu extends Fragment {
	
private static final String LOG_TAG = ArtikelNeu.class.getSimpleName();
	
	private Artikel artikel;
	private EditText newBezeichnung;
	private EditText newPreis;
//	private EditText newVerfuegbarkeit;
	private RadioButton rbja;
	private RadioButton rbnein;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		artikel = (Artikel) getArguments().get(ARTIKEL_KEY);
		Log.d(LOG_TAG, artikel.toString());
        
		// Voraussetzung fuer onOptionsItemSelected()
		setHasOptionsMenu(true);
    	
		// attachToRoot = false, weil die Verwaltung des Fragments durch die Activity erfolgt
		return inflater.inflate(R.layout.artikel_neu, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
	}
}
