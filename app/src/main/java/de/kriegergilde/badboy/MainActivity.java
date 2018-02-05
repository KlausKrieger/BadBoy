package de.kriegergilde.badboy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * // TODO: TODOs ausbauen
 *
 *
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2_000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 500;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * Callback for Location events.
     */
    private LocationCallback mLocationCallback;

    /**
     * Represents a geographical location.
     */
    public static Location mCurrentLocation;

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
                if (lifeInc >= 0) {
                    p.setLifeCurrent(p.getLifeCurrent() + lifeInc);
                } else {
                    p.setLifeCurrent(Math.min(p.getLifeCurrent(), Protagonist.getLifeMax() + lifeInc));
                }
                Protagonist.setHenchman(next);
                updateUI();
            }
        });

        Button buttonCloth = (Button) findViewById(R.id.buttonCloth);
        buttonCloth.setAllCaps(false);
        buttonCloth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Protagonist p = Protagonist.protagonist;
                Cloth next = Cloth.values()[Protagonist.getCloth().ordinal() + 1];
                Protagonist.protagonist.addCurrency(-next.getPrice());
                int lifeInc = next.getLifeBonus() - Protagonist.getCloth().getLifeBonus();
                if (lifeInc >= 0) {
                    p.setLifeCurrent(p.getLifeCurrent() + lifeInc);
                } else {
                    p.setLifeCurrent(Math.min(p.getLifeCurrent(), Protagonist.getLifeMax() + lifeInc));
                }
                Protagonist.setCloth(next);
                updateUI();
            }
        });

        Button buttonFace = (Button) findViewById(R.id.buttonFace);
        buttonFace.setAllCaps(false);
        buttonFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Protagonist p = Protagonist.protagonist;
                Face next = Face.values()[Protagonist.getFace().ordinal() + 1];
                p.addCurrency(-next.getPrice());
                int lifeInc = next.getLifeBonus() - Protagonist.getFace().getLifeBonus();
                if (lifeInc >= 0) {
                    p.setLifeCurrent(p.getLifeCurrent() + lifeInc);
                } else {
                    p.setLifeCurrent(Math.min(p.getLifeCurrent(), Protagonist.getLifeMax() + lifeInc));
                }
                Protagonist.setFace(next);
                updateUI();
            }
        });

        Button buttonWeapon = (Button) findViewById(R.id.buttonWeapon);
        buttonWeapon.setAllCaps(false);
        buttonWeapon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Protagonist p = Protagonist.protagonist;
                Weapon next = Weapon.values()[Protagonist.getWeapon().ordinal() + 1];
                p.addCurrency(-next.getPrice());
                int lifeInc = next.getLifeBonus() - Protagonist.getWeapon().getLifeBonus();
                if (lifeInc >= 0) {
                    p.setLifeCurrent(p.getLifeCurrent() + lifeInc);
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
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), VenuesActivity.class);
                //intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Request vorbereiten (noch nicht abschicken)
        prepareLocationRequests();

    }



    /**
     * bereitet alles für Location Requests vor, setzt sie aber noch nicht ab.
     * (wird in onCreate gerufen)
     */
    private void prepareLocationRequests() {

        // create Location Callback
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                //Toast.makeText(MainActivity.this, mCurrentLocation.toString(), Toast.LENGTH_SHORT).show();
                updateUI();
            }
        };

        // create and configure Location Request
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void onStart() {
        super.onStart();
        //Toast.makeText(this, "in onStart()", Toast.LENGTH_SHORT).show();

        Protagonist.restoreBaseData(this);
        updateUI();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Toast.makeText(this, "in onStop()", Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onPause() {
        super.onPause();
        //Toast.makeText(this, "in onPause()", Toast.LENGTH_SHORT).show();

        stopLocationUpdates();

        Protagonist.storeBaseData(this);

    }


    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(this, "in onResume()", Toast.LENGTH_SHORT).show();

        startLocationUpdates();
        Protagonist.restoreBaseData(this);
        updateUI();
    }


    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback, Looper.myLooper());

        updateUI();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
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

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }


    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }
}
