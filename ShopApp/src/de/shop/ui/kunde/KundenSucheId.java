package de.shop.ui.kunde;

import static de.shop.util.Constants.KUNDE_KEY;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

import java.util.Collections;
import java.util.List;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import de.shop.R;
import de.shop.data.Kunde;
import de.shop.service.HttpResponse;
import de.shop.ui.main.Main;
import de.shop.ui.main.Prefs;

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
		 Log.d(LOG_TAG,"http response in kundensucheID wurde bef�llt : " + result.toString());
		if (result.responseCode == HTTP_NOT_FOUND) {
			final String msg = getString(R.string.k_kunde_not_found, kundenIdStr);
			kundenIdTxt.setError(msg);
			return;
		}
		
		final Kunde kunde =(Kunde) result.resultObject;
		 Log.d(LOG_TAG,"umwandlung von json in kunde hat geklappt : " + kunde.toString());
		final Bundle args = new Bundle(1);
		args.putSerializable(KUNDE_KEY, kunde);
		
		final Fragment neuesFragment = new KundeDetails();
		neuesFragment.setArguments(args);
		
		// Kein Name (null) fuer die Transaktion, da die Klasse BackStageEntry nicht verwendet wird
		getFragmentManager().beginTransaction()
		                    .replace(R.id.details, neuesFragment)
		                    .addToBackStack(null)
		                    .commit();
	}
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		kundenIdTxt = (AutoCompleteTextView) view.findViewById(R.id.kunde_id_auto);
		final ArrayAdapter<Long> adapter = new AutoCompleteIdAdapter(kundenIdTxt.getContext());
    	kundenIdTxt.setAdapter(adapter);
    	kundenIdTxt.setOnEditorActionListener(this);
    	
		// kundeSucheId (this) ist gleichzeitig der Listener, wenn der Suchen-Button angeklickt wird
		// und implementiert deshalb die Methode onClick() unten
    	view.findViewById(R.id.btn_suchen).setOnClickListener(this);
    	
	    // Evtl. vorhandene Tabs der ACTIVITY loeschen
    	final ActionBar actionBar = getActivity().getActionBar();
    	actionBar.setDisplayShowTitleEnabled(true);
    	actionBar.removeAllTabs();
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
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	
	
	// Fuer die Verwendung von AutoCompleteTextView in der Methode onViewCreated()
    private class AutoCompleteIdAdapter extends ArrayAdapter<Long> {
    	private LayoutInflater inflater;
     
    	public AutoCompleteIdAdapter(Context ctx) {
    		super(ctx, -1);
    		inflater = LayoutInflater.from(ctx);
    	}
     
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		// TextView ist die Basisklasse von EditText und wiederum AutoCompleteTextView
    		final TextView tv = convertView == null
    				            ? (TextView) inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false)
    				            : (TextView) convertView;
     
    		tv.setText(String.valueOf(getItem(position)));  // Long als String innerhalb der Vorschlagsliste
    		return tv;
    	}
     
    	@Override
    	public Filter getFilter() {
    		// Filter ist eine abstrakte Klasse.
    		// Zu einer davon abgeleiteten ANONYMEN Klasse wird ein Objekt erzeugt
    		// Abstrakte Methoden muessen noch implementiert werden, und zwar HIER
    		// performFiltering() wird durch Android in einem eigenen (Worker-) Thread aufgerufen
    		Filter filter = new Filter() {
    			@Override
    			protected FilterResults performFiltering(CharSequence constraint) {
    				List<Long> idList = null;
    				if (constraint != null) {
    					// Liste der IDs, die den bisher eingegebenen Praefix (= constraint) enthalten
    					idList = sucheIds((String) constraint);
    				}
    				if (idList == null) {
    					// Leere Liste, falls keine IDs zum eingegebenen Praefix gefunden werden
    					idList = Collections.emptyList();
    				}
     
    				final FilterResults filterResults = new FilterResults();
    				filterResults.values = idList;
    				filterResults.count = idList.size();
     
    				return filterResults;
    			}
    			
    	    	private List<Long> sucheIds(String idPrefix) {
    	    		final Main mainActivity = (Main) getActivity();
    				final List<Long> ids = mainActivity.getKundeServiceBinder().sucheIds(idPrefix);
    				return ids;
    	    	}
     
    			@Override
    			protected void publishResults(CharSequence contraint, FilterResults results) {
    				clear();
    				@SuppressWarnings("unchecked")
					final List<Long> idList = (List<Long>) results.values;
    				// Ermittelte IDs in die anzuzeigende Vorschlagsliste uebernehmen
    				if (idList != null && !idList.isEmpty()) {
    					addAll(idList);
    				}

    				if (results.count > 0) {
    					notifyDataSetChanged();
    				}
    				else {
    					notifyDataSetInvalidated();
    				}
    			}
     
    			@Override
    			public CharSequence convertResultToString(Object resultValue) {
    				// Long-Objekt als String
    				return resultValue == null ? "" : String.valueOf(resultValue);
    			}
    		};
    		
    		return filter;
    	}
    }
	
}
