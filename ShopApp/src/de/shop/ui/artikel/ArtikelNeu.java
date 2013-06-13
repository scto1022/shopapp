package de.shop.ui.artikel;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import de.shop.R;
import de.shop.data.Artikel;
import de.shop.service.HttpResponse;
import de.shop.ui.main.Main;
import de.shop.ui.main.Prefs;
import static de.shop.util.Constants.ARTIKEL_KEY;
public class ArtikelNeu extends Fragment implements OnClickListener {
	
private static final String LOG_TAG = ArtikelNeu.class.getSimpleName();
	
	private Artikel artikel;
	private EditText newBezeichnung;
	private EditText newPreis;
//	private EditText newVerfuegbarkeit;
	private RadioButton rbja;
	private RadioButton rbnein;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	//	artikel = (Artikel) getArguments().get(ARTIKEL_KEY);
Log.d(LOG_TAG, "View wird aufgebaut");
        
		// Voraussetzung fuer onOptionsItemSelected()
		setHasOptionsMenu(true);
    	
		// attachToRoot = false, weil die Verwaltung des Fragments durch die Activity erfolgt
		return inflater.inflate(R.layout.artikel_neu, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.d(LOG_TAG, "View wird aufgebaut");
		final  String LOG_TAG = ArtikelNeu.class.getSimpleName();
		newBezeichnung = (EditText) view.findViewById(R.id.bezeichnung_new);
		newPreis = (EditText) view.findViewById(R.id.preis_new);
		rbja = (RadioButton) view.findViewById(R.id.ja);
		rbnein = (RadioButton) view.findViewById(R.id.nein);
final Main mainActivity = (Main) getActivity();
		
		mainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		view.findViewById(R.id.btn_anlegen).setOnClickListener(this);
		
		
	}
	@Override // OnClickListener
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_anlegen:
				Log.d(LOG_TAG,"create wird ausgeführt");
				create(view);
				break;
				
			default:
				break;
		}
		
	}
	@Override
	// Nur aufgerufen, falls setHasOptionsMenu(true) in onCreateView() aufgerufen wird
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main, menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.einstellungen:
				getFragmentManager().beginTransaction()
                                    .replace(R.id.details, new Prefs())
                                    .addToBackStack(null)
                                    .commit();
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
		private void create(View view) {
			final  String LOG_TAG = ArtikelNeu.class.getSimpleName();
			final Context ctxx = view.getContext();
			Log.d(LOG_TAG,"Create Aufruf ");
			final String artikelNameStr = newBezeichnung.getText().toString();
			if(artikelNameStr.isEmpty())
				Log.d(LOG_TAG," Artikelbezeichnung wird nicht extrahiert und ist null!");
			Log.d(LOG_TAG,artikelNameStr);
			if (TextUtils.isEmpty(artikelNameStr)) {
				newBezeichnung.setError(getString(R.string.a_bezeichnung_fehlt));
				return;
			}
			final String artikelPreis = newPreis.getText().toString();
			if (TextUtils.isEmpty(artikelPreis)) {
				newPreis.setError(getString(R.string.a_preis_fehlt));
				return;
			}
			
			final Double preis = Double.valueOf(artikelPreis);
			Log.d(LOG_TAG, preis + "wurde in double umgewandelt!");
			artikel = new Artikel();
			artikel.bezeichnung = artikelNameStr;
			artikel.preis = preis;
			if(rbja.isChecked())
			artikel.verfuegbarkeit = "ja";
			else {
			artikel.verfuegbarkeit ="nein";}
			final Main mainActivity = (Main) getActivity();
			final HttpResponse<? extends Artikel> result = mainActivity.getArtikelServiceBinder().createArtikel(artikel, ctxx);		
			
			
			 Log.d(LOG_TAG,"http response in artikelsucheID wurde befüllt : " + result.toString());

			final Artikel artikel = result.resultObject;
			final Bundle args = new Bundle(1);
			args.putSerializable(ARTIKEL_KEY, artikel);
			 final Fragment neuesFragment = new ArtikelDetails();
				neuesFragment.setArguments(args);
				
				// Kein Name (null) fuer die Transaktion, da die Klasse BackStageEntry nicht verwendet wird
				getFragmentManager().beginTransaction()
				                    .replace(R.id.details, neuesFragment)
				                    .addToBackStack(null)
				                    .commit();
			
		
		} 
    }

