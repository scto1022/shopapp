package de.shop.ui.artikel;

import static de.shop.util.Constants.ARTIKEL_KEY;
import de.shop.R;
import de.shop.data.Artikel;
import de.shop.ui.main.Prefs;
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

public class ArtikelDetails extends Fragment {
	private static final String LOG_TAG = ArtikelDetails.class.getSimpleName();
	private Artikel artikel;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        artikel = (Artikel) getArguments().get(ARTIKEL_KEY);
        Log.d(LOG_TAG, artikel.toString());
        setHasOptionsMenu(true);
		// attachToRoot = false, weil die Verwaltung des Fragments durch die Activity erfolgt
		return inflater.inflate(R.layout.artikel_details, container, false);
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
    	args.putSerializable(ARTIKEL_KEY, artikel);
		final TextView txtId = (TextView) view.findViewById(R.id.artikel_id);
    	txtId.setText(artikel.id.toString());
    	
    	final TextView txtName = (TextView) view.findViewById(R.id.bezeichnung);
    	txtName.setText(artikel.bezeichnung);
    	final TextView txtPreis = (TextView) view.findViewById(R.id.artikel_preis);
    	String preis = ""+artikel.preis;
    	txtPreis.setText(preis);
    	
    	final TextView txtverf = (TextView) view.findViewById(R.id.artikel_verfuegbar);
      	txtverf.setText(artikel.verfuegbarkeit);
//	    Tab tab = actionBar.newTab()
//	                       .setText(getString(R.string.k_stammdaten))
//	                       .setTabListener(new TabListener<artikelStammdaten>(activity,
//	                    		                                            artikelStammdaten.class,
//	                    		                                            args));
//	    actionBar.addTab(tab);
//	    
//	    tab = actionBar.newTab()
//                       .setText(getString(R.string.k_bestellungen))
//                       .setTabListener(new TabListener<artikelBestellungen>(activity,
//                    		                                              artikelBestellungen.class,
//                    		                                              args));
//	    actionBar.addTab(tab);
//
//	    tab = actionBar.newTab()
//                .setText(getString(R.string.k_karte))
//                .setTabListener(new TabListener<artikelKarte>(activity,
//             		                                        artikelKarte.class,
//             		                                        args));
//	    actionBar.addTab(tab);
//	}
	
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.artikel_details);
//        
//        final Bundle extras = getIntent().getExtras();
//        if (extras == null) {
//        	return;
//        }
//
//        final Artikel artikel = (Artikel) extras.getSerializable(ARTIKEL_KEY);
//        Log.d(LOG_TAG, artikel.toString()+ "aufruf methode! extrahieren hat geklappt!");
//        fillValues(artikel);
//    }
//    
//    private void fillValues(Artikel artikel) {
//        final TextView txtId = (TextView) findViewById(R.id.artikel_id);
//    	txtId.setText(artikel.id.toString());
//    	
//    	final TextView txtName = (TextView) findViewById(R.id.bezeichnung);
//    	txtName.setText(artikel.bezeichnung);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
   }
}
