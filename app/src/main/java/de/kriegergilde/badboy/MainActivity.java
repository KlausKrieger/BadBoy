package de.kriegergilde.badboy;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * // TODO: TODOs ausbauen
 *
 *
 */
public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static Location mLastLocation;

    private GoogleApiClient mGoogleApiClient;

    protected LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button buttonHenchman = (Button) findViewById(R.id.buttonHenchman);
        buttonHenchman.setAllCaps(false);
        buttonHenchman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Protagonist p = Protagonist.protagonist;
                Henchman next = Henchman.values()[Protagonist.getHenchman().ordinal() + 1];
                p.addCurrency(-next.getPrice());
                int lifeInc = next.getLifeBonus() - Protagonist.getHenchman().getLifeBonus();
                if (lifeInc >= 0){
                    p.setLifeCurrent(p.getLifeCurrent()+lifeInc);
                } else {
                    p.setLifeCurrent(Math.min(p.getLifeCurrent(), Protagonist.getLifeMax() + lifeInc));
                }
                Protagonist.setHenchman(next);
                updateUI();
            }
        });

        Button buttonCloth = (Button) findViewById(R.id.buttonCloth);
        buttonCloth.setAllCaps(false);
        buttonCloth.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Protagonist p = Protagonist.protagonist;
                Cloth next = Cloth.values()[Protagonist.getCloth().ordinal()+1];
                Protagonist.protagonist.addCurrency(-next.getPrice());
                int lifeInc = next.getLifeBonus() - Protagonist.getCloth().getLifeBonus();
                if (lifeInc >= 0){
                    p.setLifeCurrent(p.getLifeCurrent()+lifeInc);
                } else {
                    p.setLifeCurrent(Math.min(p.getLifeCurrent(), Protagonist.getLifeMax() + lifeInc));
                }
                Protagonist.setCloth(next);
                updateUI();
            }
        });

        Button buttonFace = (Button) findViewById(R.id.buttonFace);
        buttonFace.setAllCaps(false);
        buttonFace.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Protagonist p = Protagonist.protagonist;
                Face next = Face.values()[Protagonist.getFace().ordinal()+1];
                p.addCurrency(-next.getPrice());
                int lifeInc = next.getLifeBonus() - Protagonist.getFace().getLifeBonus();
                if (lifeInc >= 0){
                    p.setLifeCurrent(p.getLifeCurrent()+lifeInc);
                } else {
                    p.setLifeCurrent(Math.min(p.getLifeCurrent(), Protagonist.getLifeMax() + lifeInc));
                }
                Protagonist.setFace(next);
                updateUI();
            }
        });

        Button buttonWeapon = (Button) findViewById(R.id.buttonWeapon);
        buttonWeapon.setAllCaps(false);
        buttonWeapon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Protagonist p = Protagonist.protagonist;
                Weapon next = Weapon.values()[Protagonist.getWeapon().ordinal()+1];
                p.addCurrency(-next.getPrice());
                int lifeInc = next.getLifeBonus() - Protagonist.getWeapon().getLifeBonus();
                if (lifeInc >= 0){
                    p.setLifeCurrent(p.getLifeCurrent()+lifeInc);
                } else {
                    p.setLifeCurrent(Math.min(p.getLifeCurrent(), Protagonist.getLifeMax() + lifeInc));
                }
                Protagonist.setWeapon(next);
                updateUI();
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), VenuesActivity.class);
                //intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);    }
        });

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        createLocationRequest();

        updateUI();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(10_000);//UPDATE_INTERVAL_IN_MILLISECONDS

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(5_000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();

        Protagonist.restoreBaseData(this);
        updateUI();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();

        Protagonist.storeBaseData(this);

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }

        Protagonist.restoreBaseData(this);
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        startLocationUpdates();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Toast.makeText(this, "connection to google play services failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {

        {
            TextView textView = (TextView) findViewById(R.id.textViewRank);
            textView.setText("Rang: " + Protagonist.getRank());
        }

        {
            TextView textView = (TextView) findViewById(R.id.textViewLife);
            textView.setText("Gesundheit(Schutz): " + Protagonist.protagonist.getLifeCurrent() + "/" + Protagonist.getLifeMax());
        }

        {
            TextView textView = (TextView) findViewById(R.id.textViewCurrency);
            textView.setText("Zaster: " + Protagonist.protagonist.getCurrency() + "€");
        }

        {
            Henchman h = Protagonist.getHenchman();
            TextView henchmanView = (TextView) findViewById(R.id.textViewHenchman);
            henchmanView.setText(h.asString(false));
            Button buttonHenchman = (Button) findViewById(R.id.buttonHenchman);
            if (h.isMax()) {
                buttonHenchman.setEnabled(false);
                buttonHenchman.setText("-");
            } else {
                if (h.next().getPrice() > Protagonist.protagonist.getCurrency()) {
                    buttonHenchman.setEnabled(false);
                } else {
                    buttonHenchman.setEnabled(true);
                }
                buttonHenchman.setText("besorgen: "
                        + Protagonist.getHenchman().next().asString(true));
            }
        }

        {
            Cloth c = Protagonist.getCloth();
            TextView clothView = (TextView) findViewById(R.id.textViewCloth);
            clothView.setText(c.asString(false));
            Button buttonCloth = (Button) findViewById(R.id.buttonCloth);
            if (c.isMax()) {
                buttonCloth.setEnabled(false);
                buttonCloth.setText("-");
            } else {
                if (c.next().getPrice() > Protagonist.protagonist.getCurrency()) {
                    buttonCloth.setEnabled(false);
                } else {
                    buttonCloth.setEnabled(true);
                }
                buttonCloth.setText("besorgen: "
                        + Protagonist.getCloth().next().asString(true));
            }
        }

        {
            Face f = Protagonist.getFace();
            TextView faceView = (TextView) findViewById(R.id.textViewFace);
            faceView.setText(f.asString(false));
            Button buttonFace = (Button) findViewById(R.id.buttonFace);
            if (f.isMax()) {
                buttonFace.setEnabled(false);
                buttonFace.setText("-");
            } else {
                if (f.next().getPrice() > Protagonist.protagonist.getCurrency()) {
                    buttonFace.setEnabled(false);
                } else {
                    buttonFace.setEnabled(true);
                }
                buttonFace.setText("besorgen: "
                        + Protagonist.getFace().next().asString(true));
            }
        }

        {
            Weapon w = Protagonist.getWeapon();
            TextView weaponView = (TextView) findViewById(R.id.textViewWeapon);
            weaponView.setText(w.asString(false));
            Button buttonWeapon = (Button) findViewById(R.id.buttonWeapon);
            if (w.isMax()) {
                buttonWeapon.setEnabled(false);
                buttonWeapon.setText("-");
            } else {
                if (w.next().getPrice() > Protagonist.protagonist.getCurrency()) {
                    buttonWeapon.setEnabled(false);
                } else {
                    buttonWeapon.setEnabled(true);
                }
                buttonWeapon.setText("besorgen: "
                        + Protagonist.getWeapon().next().asString(true));
            }
        }

        {
            TextView textView = (TextView) findViewById(R.id.textViewCoup);
            textView.setText("aktueller Coup: " + Protagonist.getCoupName());
        }

        {
            TextView textView = (TextView) findViewById(R.id.textViewCoupDetails);
            textView.setText("\tErfolgschance: " + Protagonist.getSuccessChance() + "%"
                    + "\n\tBeute bei Erfolg: " + Protagonist.getCoupMoneyMin()+"-"+Protagonist.getCoupMoneyMax()+"€"
                    + "\n\tSchaden bei Misserfolg: " + Protagonist.getCoupDamageMin()+"-"+Protagonist.getCoupDamageMax());
        }
    }
}
