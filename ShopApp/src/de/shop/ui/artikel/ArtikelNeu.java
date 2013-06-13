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
	@Override // OnClickListener
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_suchen:
				create(view);
				break;
				
			default:
				break;
		}
	}
		private void create(View view) {
			final Context ctx = view.getContext();
			final  String LOG_TAG = ArtikelSucheId.class.getSimpleName();
			newBezeichnung = view.findViewById(R.id.bezeichnung_neu);
			final String artikelbezeichnung = newBezeichnung.getText().toString();
			if (TextUtils.isEmpty(artikelbezeichnung)) {
				newBezeichnung.setError(getString(R.string.a_artikelnr_fehlt));
	    		return;
	    	}
			final Main mainActivity = (Main) getActivity();
			artikel.bezeichnung = artikelbezeichnung;
			
			
			
			
			
			final HttpResponse<? extends Artikel> result = mainActivity.getArtikelServiceBinder().createArtikel(artikel, ctx);
			
			
			
			
			
			
			
			final Main mainActivity = (Main) getActivity();
			final HttpResponse<? extends Artikel> result = mainActivity.getArtikelServiceBinder().sucheArtikelById(artikelId, ctx);
			 Log.d(LOG_TAG,"http response in artikelsucheID wurde befüllt : " + result.toString());
			if (result.responseCode == HTTP_NOT_FOUND) {
				final String msg = getString(R.string.a_artikel_not_found, artikelIdStr);
				artikelIdTxt.setError(msg);
				return;
			}
			
			final Artikel artikel =(Artikel) result.resultObject;
			 Log.d(LOG_TAG,"umwandlung von json in artikel hat geklappt : " + artikel.toString());
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
}
