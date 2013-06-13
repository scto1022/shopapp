package de.shop.ui.artikel;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import de.shop.R;
import de.shop.data.Artikel;
import de.shop.service.HttpResponse;
import de.shop.ui.main.Main;
import static de.shop.util.Constants.ARTIKEL_KEY;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

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
	//	Log.d(LOG_TAG, artikel.toString());
        
		// Voraussetzung fuer onOptionsItemSelected()
		setHasOptionsMenu(true);
    	
		// attachToRoot = false, weil die Verwaltung des Fragments durch die Activity erfolgt
		return inflater.inflate(R.layout.artikel_neu, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
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
		private void create(View view) {
			final Context ctx = view.getContext();
			final  String LOG_TAG = ArtikelNeu.class.getSimpleName();
			newBezeichnung = (EditText) view.findViewById(R.id.bezeichnung_new);
			final String artikelbezeichnung = newBezeichnung.getText().toString();
			if (TextUtils.isEmpty(artikelbezeichnung)) {
				newBezeichnung.setError(getString(R.string.a_artikelnr_fehlt));
	    		return;
	    	}
			final Main mainActivity = (Main) getActivity();
			Log.d(LOG_TAG, "er extrahiert jetzt die Werte");
			artikel.bezeichnung = artikelbezeichnung;
			newPreis = (EditText) view.findViewById(R.id.artikel_preis);
			final double preis = Double.parseDouble(newPreis.getText().toString());
				artikel.preis = preis;	
				artikel.verfuegbarkeit = "Ja";
			
			
			
			
			final HttpResponse<? extends Artikel> result = mainActivity.getArtikelServiceBinder().createArtikel(artikel, ctx);		
			
			
			 Log.d(LOG_TAG,"http response in artikelsucheID wurde befüllt : " + result.toString());

			

		
		} 
    }

