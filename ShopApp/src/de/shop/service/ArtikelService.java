package de.shop.service;

import static android.app.ProgressDialog.STYLE_SPINNER;
import static de.shop.ui.main.Prefs.mock;
import static de.shop.ui.main.Prefs.timeout;
import static de.shop.util.Constants.ARTIKEL_PATH;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.concurrent.TimeUnit.SECONDS;
import static de.shop.util.Constants.ARTIKEL_ID_PREFIX_PATH;
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
import de.shop.util.InternalShopError;


public class ArtikelService extends Service {
	public static final String LOG_TAG = ArtikelService.class.getSimpleName();
	
	private final ArtikelServiceBinder binder = new ArtikelServiceBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public class ArtikelServiceBinder extends Binder {
		
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
			final String path = ARTIKEL_ID_PREFIX_PATH + "/" + prefix;
		    Log.v(LOG_TAG, "sucheIds: path = " + path);

    		final List<Long> ids = mock
   				                   ? Mock.sucheArtikelIdsByPrefix(prefix)
   				                   : WebServiceClient.getJsonLongList(path);

			Log.d(LOG_TAG, "sucheIds: " + ids.toString());
			return ids;
		}
		public HttpResponse<Artikel> sucheArtikelById(Long id, final Context ctx) {
			
			// (evtl. mehrere) Parameter vom Typ "Long", Resultat vom Typ "Artikel"
			final AsyncTask<Long, Void, HttpResponse<Artikel>> sucheArtikelByIdTask = new AsyncTask<Long, Void, HttpResponse<Artikel>>() {
				@Override
	    		protected void onPreExecute() {
					progressDialog = showProgressDialog(ctx);
				}
				
				@Override
				// Neuer Thread, damit der UI-Thread nicht blockiert wird
				protected HttpResponse<Artikel> doInBackground(Long... ids) {
					final Long id = ids[0];
		    		final String path = ARTIKEL_PATH + "/" + id;
		    		Log.v(LOG_TAG, "path = " + path);
		    		final HttpResponse<Artikel> result = mock
		    				                                   ? Mock.sucheArtikelById(id)
		    				                                   : WebServiceClient.getJsonSingle(path, Artikel.class);

					Log.d(LOG_TAG + ".AsyncTask", "doInBackground: " + result);
					return result;
				}
				
				@Override
	    		protected void onPostExecute(HttpResponse<Artikel> unused) {
					progressDialog.dismiss();
	    		}
			};

    		sucheArtikelByIdTask.execute(id);
    		HttpResponse<Artikel> result = null;
	    	try {
	    		result = sucheArtikelByIdTask.get(timeout, SECONDS);
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
		public Artikel getArtikel(Long id) {
			
			// (evtl. mehrere) Parameter vom Typ "Long", Resultat vom Typ "Artikel"
			final AsyncTask<Long, Void, Artikel> getArtikelTask = new AsyncTask<Long, Void, Artikel>() {

				@Override
	    		protected void onPreExecute() {
					Log.d(LOG_TAG, "... ProgressDialog im laufenden Thread starten ...");
				}
				
				@Override
				// Neuer Thread (hier: Emulation des REST-Aufrufs), damit der UI-Thread nicht blockiert wird
				protected Artikel doInBackground(Long... ids) {
					final Long artikelId = ids[0];
					final Artikel artikel = new Artikel(artikelId,"bezeichnung",0.0,"Ja",0);
					Log.d(LOG_TAG + ".AsyncTask", "doInBackground: " + artikel);
					return artikel;
				}
				
				@Override
	    		protected void onPostExecute(Artikel artikel) {
					Log.d(LOG_TAG, "... ProgressDialog im laufenden Thread beenden ...");
	    		}
			};
			
	    	getArtikelTask.execute(id);
	    	Artikel artikel = null;
	    	try {
				artikel = getArtikelTask.get(3L, TimeUnit.SECONDS);
			}
	    	catch (Exception e) {
	    		Log.e(LOG_TAG, e.getMessage(), e);
			}
	    	
	    	return artikel;
		}
		public HttpResponse<Artikel> createArtikel(Artikel artikel, final Context ctx) {
			Log.d(LOG_TAG,"create artikel vom ServiceBinder wird aufgerufen");
			// (evtl. mehrere) Parameter vom Typ "Artikel", Resultat vom Typ "void"
			final AsyncTask<Artikel, Void, HttpResponse<Artikel>> createArtikelTask = new AsyncTask<Artikel, Void, HttpResponse<Artikel>>() {
				@Override
	    		protected void onPreExecute() {
					progressDialog = showProgressDialog(ctx);
				}
				
				@Override
				// Neuer Thread, damit der UI-Thread nicht blockiert wird
				protected HttpResponse<Artikel> doInBackground(Artikel... artikels) {
					final Artikel artikel = artikels[0];
		    		final String path = ARTIKEL_PATH;
		    		Log.v(LOG_TAG, "path = " + path);
		    		Log.v(LOG_TAG, artikel.toString());

		    		final HttpResponse<Artikel> result = mock
                                                               ? Mock.createArtikel(artikel)
                                                               : WebServiceClient.postJson(artikel, path);
		    		
					Log.d(LOG_TAG + ".AsyncTask", "doInBackground: " + result);
					return result;
				}
				
				@Override
	    		protected void onPostExecute(HttpResponse<Artikel> unused) {
					progressDialog.dismiss();
	    		}
			};
			
			createArtikelTask.execute(artikel);
			HttpResponse<Artikel> response = null; 
			try {
				response = createArtikelTask.get(timeout, SECONDS);
			}
	    	catch (Exception e) {
	    		throw new InternalShopError(e.getMessage(), e);
			}
			
			artikel.id = Long.valueOf(response.content);
			final HttpResponse<Artikel> result = new HttpResponse<Artikel>(response.responseCode, response.content, artikel);
			return result;
	    }
	
	}
	}
