package de.shop.service;

import static android.app.ProgressDialog.STYLE_SPINNER;
import static de.shop.ui.main.Prefs.mock;
import static de.shop.ui.main.Prefs.timeout;
import static de.shop.util.Constants.KUNDEN_ID_PREFIX_PATH;
import static de.shop.util.Constants.KUNDEN_PATH;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import de.shop.R;
import de.shop.data.Artikel;
import de.shop.data.Kunde;
import de.shop.util.InternalShopError;

public class KundeService extends Service {
	public static final String LOG_TAG = KundeService.class.getSimpleName();
	
	private final KundeServiceBinder binder = new KundeServiceBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public class KundeServiceBinder extends Binder {
		
		private ProgressDialog progressDialog;
		private ProgressDialog showProgressDialog(Context ctx) {
			progressDialog = new ProgressDialog(ctx);
			progressDialog.setProgressStyle(STYLE_SPINNER);  // Kreis (oder horizontale Linie)
			progressDialog.setMessage(getString(R.string.s_bitte_warten));
			progressDialog.setCancelable(true);      // Abbruch durch Zuruecktaste
			progressDialog.setIndeterminate(true);   // Unbekannte Anzahl an Bytes werden vom Web Service geliefert
			progressDialog.show();
			return progressDialog;
		}
		
		public List<Long> sucheIds(String prefix) {
			final String path = KUNDEN_ID_PREFIX_PATH + "/" + prefix;
		    Log.v(LOG_TAG, "sucheIds: path = " + path);

    		final List<Long> ids = mock
   				                   ? Mock.sucheKundeIdsByPrefix(prefix)
   				                   : WebServiceClient.getJsonLongList(path);

			Log.d(LOG_TAG, "sucheIds: " + ids.toString());
			return ids;
		}
		
		public HttpResponse<Artikel> sucheKundeById(Long id, final Context ctx) {
			
			// (evtl. mehrere) Parameter vom Typ "Long", Resultat vom Typ "Artikel"
			final AsyncTask<Long, Void, HttpResponse<Kunde>> sucheKundeByIdTask = new AsyncTask<Long, Void, HttpResponse<Kunde>>() {
				@Override
	    		protected void onPreExecute() {
					progressDialog = showProgressDialog(ctx);
				}
				
				@Override
				// Neuer Thread, damit der UI-Thread nicht blockiert wird
				protected HttpResponse<Kunde> doInBackground(Long... ids) {
					final Long id = ids[0];
		    		final String path = KUNDEN_PATH + "/" + id;
		    		Log.v(LOG_TAG, "path = " + path);
		    		final HttpResponse<Kunde> result = mock
		    				                                   ? Mock.sucheKundeById(id)
		    				                                   : WebServiceClient.getJsonSingle(path, Kunde.class);

					Log.d(LOG_TAG + ".AsyncTask", "doInBackground: " + result);
					return result;
				}
				
				@Override
	    		protected void onPostExecute(HttpResponse<Kunde> unused) {
					progressDialog.dismiss();
	    		}
			};

    		sucheKundeByIdTask.execute(id);
    		HttpResponse<Kunde> result = null;
	    	try {
	    		result = sucheKundeByIdTask.get(timeout, SECONDS);
			}
	    	catch (Exception e) {
	    		throw new InternalShopError(e.getMessage(), e);
			}
	    	
    		if (result.responseCode != HTTP_OK) {
	    		return result;
		    }
    		
//    		setBestellungenUri(result.resultObject);
		    return result;
		}
		
		// Aufruf in einem eigenen Thread
		public Kunde getKunde(Long id) {
			
			// (evtl. mehrere) Parameter vom Typ "Long", Resultat vom Typ "Kunde"
			final AsyncTask<Long, Void, Kunde> getKundeTask = new AsyncTask<Long, Void, Kunde>() {

				@Override
	    		protected void onPreExecute() {
					Log.d(LOG_TAG, "... ProgressDialog im laufenden Thread starten ...");
				}
				
				@Override
				// Neuer Thread (hier: Emulation des REST-Aufrufs), damit der UI-Thread nicht blockiert wird
				protected Kunde doInBackground(Long... ids) {
					final Long kundeId = ids[0];
					final Kunde kunde = new Kunde(kundeId, "Name" + kundeId);
					Log.d(LOG_TAG + ".AsyncTask", "doInBackground: " + kunde);
					return kunde;
				}
				
				@Override
	    		protected void onPostExecute(Kunde kunde) {
					Log.d(LOG_TAG, "... ProgressDialog im laufenden Thread beenden ...");
	    		}
			};
			
	    	getKundeTask.execute(id);
	    	Kunde kunde = null;
	    	try {
				kunde = getKundeTask.get(3L, TimeUnit.SECONDS);
			}
	    	catch (Exception e) {
	    		Log.e(LOG_TAG, e.getMessage(), e);
			}
	    	
	    	return kunde;
		}
	}
}
