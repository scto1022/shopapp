package de.shop.ui.kunde;

import static de.shop.util.Constants.KUNDE_KEY;
import static java.net.HttpURLConnection.HTTP_OK;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.shop.R;
import de.shop.data.Bestellung;
import de.shop.data.Kunde;
import de.shop.service.HttpResponse;
import de.shop.service.KundeService.KundeServiceBinder;
import de.shop.ui.main.Main;
import de.shop.ui.main.Prefs;

public class KundeDetails extends Fragment {

	private static final String LOG_TAG = KundeDetails.class.getSimpleName();
	private Kunde kunde;
	private List<Long> bestellungenIds;
	private KundeServiceBinder kundeServiceBinder;

	private LazyAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		kunde = (Kunde) getArguments().get(KUNDE_KEY);
		Log.d(LOG_TAG, kunde.toString());
		setHasOptionsMenu(true);
		// attachToRoot = false, weil die Verwaltung des Fragments durch die
		// Activity erfolgt
		return inflater.inflate(R.layout.kunde_details, container, false);
	}

	@Override
	// Nur aufgerufen, falls setHasOptionsMenu(true) in onCreateView()
	// aufgerufen wird
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.einstellungen:
			getFragmentManager().beginTransaction()
					.replace(R.id.details, new Prefs()).addToBackStack(null)
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
		// actionBar.setNavigationMode(NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false); // Titel der App
														// ausblenden, um mehr
														// Platz fuer die Tabs
														// zu haben

		final Bundle args = new Bundle(1);
		args.putSerializable(KUNDE_KEY, kunde);
		final TextView txtId = (TextView) view.findViewById(R.id.kunde_id);
		txtId.setText(kunde.id.toString());

		final TextView txtName = (TextView) view.findViewById(R.id.name);
		txtName.setText(kunde.name);

		final TextView txtPreis = (TextView) view
				.findViewById(R.id.kunde_vname);
		txtPreis.setText(kunde.vname);

		final TextView txtverf = (TextView) view.findViewById(R.id.kunde_email);
		txtverf.setText(kunde.email);

		// if (Main.class.equals(activity.getClass())) {
		Main main = (Main) activity;
		kundeServiceBinder = main.getKundeServiceBinder();
		// }
		// else {
		// Log.e(LOG_TAG, "Activity " + activity.getClass().getSimpleName() +
		// " nicht beruecksichtigt.");
		// return;
		// }

		bestellungenIds = kundeServiceBinder.sucheBestellungenIdsByKundeId(
				kunde.id, view.getContext());


		if (bestellungenIds == null || bestellungenIds.isEmpty()) {

		} else {
			Log.d(LOG_TAG, "Starte get! (Alle Bestellungen)");
			
			
			
			HttpResponse<Bestellung> bstlngnRes = kundeServiceBinder
					.sucheBestellungenByKundeId(kunde.id, view.getContext());
			List<Bestellung> bstlngn = bstlngnRes.resultList;
			bstlngn.add(0, new Bestellung("ID", "Preis"));

			Log.d(LOG_TAG, "get beendet!");

			final ListView list = (ListView) view.findViewById(R.id.best_list);
			int anzahl = bestellungenIds.size();

			if (bstlngnRes.responseCode != HTTP_OK) {
				return;
			}
			for (int i = 0; i < anzahl; i++) {
				Log.d(LOG_TAG, String.valueOf(bstlngn.get(i).gesamtpreis));
			}
			adapter = new LazyAdapter(main, R.layout.row_layout,
					bstlngn.toArray(new Bestellung[0]));
			list.setAdapter(adapter);

		}
	}

	public class LazyAdapter extends ArrayAdapter<Bestellung> {

		public Context context;
		public int layoutResourceId;
		public Bestellung data[] = null;

		public LazyAdapter(Context context, int layoutResourceId,
				Bestellung[] data) {

			super(context, layoutResourceId, data);
			this.layoutResourceId = layoutResourceId;
			this.context = context;
			this.data = data;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			BestellungHolder holder;
			if (row == null) {
				LayoutInflater inflater = ((Activity) context)
						.getLayoutInflater();
				row = inflater.inflate(layoutResourceId, parent, false);

				holder = new BestellungHolder();
				holder.id = (TextView) row.findViewById(R.id.best_id);
				holder.gesamtpreis = (TextView) row
						.findViewById(R.id.best_gesamtpreis);
				row.setTag(holder);
			}

			Bestellung bestellung = data[position];

			TextView bestId = (TextView) row.findViewById(R.id.best_id);
			TextView bestGesPreis = (TextView) row
					.findViewById(R.id.best_gesamtpreis);

			// Setting all values in listview
			bestId.setText(bestellung.id + "");
			bestGesPreis.setText(bestellung.gesamtpreis + "€");
			return row;
		}
	}

	static class BestellungHolder {
		TextView id;
		TextView gesamtpreis;
	}

}
