package de.shop.ui.kunde;

import static de.shop.util.Constants.ARTIKEL_KEY;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import de.shop.R;
import de.shop.data.Artikel;
import de.shop.data.Kunde;
import de.shop.service.HttpResponse;
import de.shop.ui.artikel.ArtikelDetails;
import de.shop.ui.artikel.ArtikelSucheId;
import de.shop.ui.main.Main;

public class KundenSucheId extends Fragment implements OnClickListener, OnEditorActionListener {
	private AutoCompleteTextView kundenIdTxt;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// attachToRoot = false, weil die Verwaltung des Fragments durch die Activity erfolgt
		return inflater.inflate(R.layout.kunden_suche_id, container, false);
	}
	
	@Override // OnClickListener
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_suchen:
				suchen(view);
				break;
				
			default:
				break;
		}
    }
	
	private void suchen(View view) {
		final Context ctx = view.getContext();
		final  String LOG_TAG = KundenSucheId.class.getSimpleName();
		final String kundenIdStr = kundenIdTxt.getText().toString();
		if (TextUtils.isEmpty(kundenIdStr)) {
			kundenIdTxt.setError(getString(R.string.k_kundennr_fehlt));
    		return;
    	}
		
		final Long kundenId = Long.valueOf(kundenIdStr);
		final Main mainActivity = (Main) getActivity();
		final HttpResponse<? extends Kunde> result = mainActivity.getKundeServiceBinder().sucheKundeById(kundenId, ctx);
		 Log.d(LOG_TAG,"http response in kundensucheID wurde befüllt : " + result.toString());
		if (result.responseCode == HTTP_NOT_FOUND) {
			final String msg = getString(R.string.a_artikel_not_found, kundenIdStr);
			kundenIdTxt.setError(msg);
			return;
		}
		
		final Kunde kunde =(Kunde) result.resultObject;
		 Log.d(LOG_TAG,"umwandlung von json in kunde hat geklappt : " + kunde.toString());
		final Bundle args = new Bundle(1);
		args.putSerializable(ARTIKEL_KEY, kunde);
		
		final Fragment neuesFragment = new KundeDetails();
		neuesFragment.setArguments(args);
		
		// Kein Name (null) fuer die Transaktion, da die Klasse BackStageEntry nicht verwendet wird
		getFragmentManager().beginTransaction()
		                    .replace(R.id.details, neuesFragment)
		                    .addToBackStack(null)
		                    .commit();
	}

	@Override
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
