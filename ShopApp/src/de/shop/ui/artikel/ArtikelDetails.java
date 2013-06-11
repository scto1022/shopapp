package de.shop.ui.artikel;

import static de.shop.util.Constants.ARTIKEL_KEY;

import de.shop.R;
import de.shop.data.Kunde;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class ArtikelDetails extends Activity {
	private static final String LOG_TAG = ArtikelDetails.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout);
        
        final Bundle extras = getIntent().getExtras();
        if (extras == null) {
        	return;
        }

        final Kunde kunde = (Kunde) extras.getSerializable(ARTIKEL_KEY);
        Log.d(LOG_TAG, kunde.toString());
        fillValues(kunde);
    }
    
    private void fillValues(Kunde kunde) {
        final TextView txtId = (TextView) findViewById(R.id.artikel_id);
    	txtId.setText(kunde.id.toString());
    	
    	final TextView txtName = (TextView) findViewById(R.id);
    	txtName.setText(kunde.name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
