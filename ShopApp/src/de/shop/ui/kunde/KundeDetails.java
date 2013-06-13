package de.shop.ui.kunde;

import static de.shop.util.Constants.KUNDE_KEY;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.shop.R;
import de.shop.data.Bestellung;
import de.shop.data.Kunde;
import de.shop.service.BestellungService.BestellungServiceBinder;
import de.shop.service.BestellungService.*;
import de.shop.service.KundeService.KundeServiceBinder;
import de.shop.ui.main.Main;
import de.shop.ui.main.Prefs;

public class KundeDetails extends Fragment {

	private static final String LOG_TAG = KundeDetails.class.getSimpleName();
	private Kunde kunde;
	private List<Long> bestellungenIds;
	private List<Bestellung> bestellungen;
	private KundeServiceBinder kundeServiceBinder;
	private BestellungServiceBinder bestellungServiceBinder;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        kunde = (Kunde) getArguments().get(KUNDE_KEY);
        Log.d(LOG_TAG, kunde.toString());
        setHasOptionsMenu(true);
		// attachToRoot = false, weil die Verwaltung des Fragments durch die Activity erfolgt
		return inflater.inflate(R.layout.kunde_details, container, false);
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
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		final Activity activity = getActivity();
		final ActionBar actionBar = activity.getActionBar();
		// (horizontale) Tabs; NAVIGATION_MODE_LIST fuer Dropdown Liste
		//actionBar.setNavigationMode(NAVIGATION_MODE_TABS);
	    actionBar.setDisplayShowTitleEnabled(false);  // Titel der App ausblenden, um mehr Platz fuer die Tabs zu haben

	    final Bundle args = new Bundle(1);
    	args.putSerializable(KUNDE_KEY, kunde);
		final TextView txtId = (TextView) view.findViewById(R.id.kunde_id);
    	txtId.setText(kunde.id.toString());
    	
    	final TextView txtName = (TextView) view.findViewById(R.id.name);
    	txtName.setText(kunde.name);
    	
    	final TextView txtPreis = (TextView) view.findViewById(R.id.kunde_vname);
    	txtPreis.setText(kunde.vname);
    	
    	final TextView txtverf = (TextView) view.findViewById(R.id.kunde_email);
      	txtverf.setText(kunde.email);
      	
		//if (Main.class.equals(activity.getClass())) {
			Main main = (Main) activity;
			kundeServiceBinder = main.getKundeServiceBinder();
			bestellungServiceBinder = main.getBestellungServiceBinder();
		//}
		//else {
		//	Log.e(LOG_TAG, "Activity " + activity.getClass().getSimpleName() + " nicht beruecksichtigt.");
		//	return;
		//}
		
      	bestellungenIds = kundeServiceBinder.sucheBestellungenIdsByKundeId(kunde.id, view.getContext());
		
      	final TextView txtBest = (TextView) view.findViewById(R.id.kunde_hasOrders);
      	
      	if (bestellungenIds == null || bestellungenIds.isEmpty()) {
      		txtBest.setText("Nope!");
		}
		else {
	        int anzahl = bestellungenIds.size();
	        bestellungen = new ArrayList<Bestellung>(anzahl);
			final String[] values = new String[anzahl];
			for (int i = 0; i < anzahl; i++) {
	        	bestellungen.add(null);
	        	values[i] = getString(R.string.k_kunde_bestellung_id, bestellungenIds.get(anzahl - i - 1));
	        	Log.d(LOG_TAG, values[i]);
	        }
			
	      	txtverf.setText(anzahl);
		}
   }
	
}
