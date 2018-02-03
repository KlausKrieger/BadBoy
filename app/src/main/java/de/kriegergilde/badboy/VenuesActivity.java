package de.kriegergilde.badboy;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static de.kriegergilde.badboy.MainActivity.mCurrentLocation;


/**
 *
 */
public class VenuesActivity extends ListActivity  {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // no more this
        // setContentView(R.layout.activity_venues);

        setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_venues, new String[0]));//TODO weg lassen möglich?

        ListView listView = getListView();
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // bewusstlos?
                if (Protagonist.protagonist.getLifeCurrent() <= 0){
                    Toast.makeText(VenuesActivity.this, "Du bist bewusstlos!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Venue venue = (Venue) parent.getAdapter().getItem(position);
                Long lastVisit = Protagonist.protagonist.getVisited().get(venue.getId());

                // range ok?
                if (venue.getDistance() > Constants.HACK_RANGE) {
                    Toast.makeText(VenuesActivity.this, "zu weit entfernt", Toast.LENGTH_SHORT).show();
                    return;
                }

                // cooldown?
                if (lastVisit == null || lastVisit + Protagonist.getCooldownMillis() <= System.currentTimeMillis()){
                    if (Dice.dice(100) <= Protagonist.getSuccessChance()){ // Erfolg?
                        int loot = Dice.dice(Protagonist.getCoupMoneyMin(), Protagonist.getCoupMoneyMax());
                        Toast.makeText(VenuesActivity.this, "Beute: "+loot+"€", Toast.LENGTH_SHORT).show();
                        Protagonist.protagonist.addCurrency(loot);
                    } else {// Misserfolg!
                        int damage = Dice.dice(Protagonist.getCoupDamageMin(), Protagonist.getCoupDamageMax());
                        Protagonist.protagonist.setLifeCurrent(Protagonist.protagonist.getLifeCurrent() - damage);
                        Toast.makeText(VenuesActivity.this, "Das ging schief! Du verlierst "+damage+" Gesundheit/Schutz!", Toast.LENGTH_SHORT).show();
                        if(Protagonist.protagonist.getLifeCurrent() <= 0){
                            Toast.makeText(VenuesActivity.this, "Du wirst brutal zusammengeschlagen und in einer Ecke liegen gelassen.", Toast.LENGTH_LONG).show();
                        }
                    }
                    Protagonist.protagonist.getVisited().put(venue.getId(), System.currentTimeMillis());
                } else {
                    long diffSecs = (lastVisit+Protagonist.getCooldownMillis() - System.currentTimeMillis()) / 1000;
                    long hours = diffSecs / 60 / 60;
                    long remainingMinutes = diffSecs / 60 % 60;
                    Toast.makeText(VenuesActivity.this, "zu unsicher (noch "+hours+"h"+remainingMinutes+"min)", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    protected void onStart() {
        super.onStart();
        new VenuesFetcherTask(this).execute();

        Protagonist.restoreBaseData(this);
        Protagonist.restoreVisitedData(this);
    }

    private static class VenuesFetcherTask extends AsyncTask<Void, Void, List<Venue>> {

        private VenuesActivity venuesActivity;

        private VenuesFetcherTask(VenuesActivity va){
            this.venuesActivity = va;
        }

        @Override
        protected List<Venue> doInBackground(Void... params) {
            if (mCurrentLocation == null){
                return null;
            }
            String CLIENT_ID = "U5AVH4K5RI3OW5RBPRFQ2VA11Z01AFIV1SDCNSERQM1S1PM1";
            String CLIENT_SECRET = "QQZB0TVTLJMUG5PT13552LMQ3LGPQDD3WM0I2L4ZOLSSMXAL";
            String VERSION = "20130815";
            String LOC = mCurrentLocation.getLatitude()+","+mCurrentLocation.getLongitude();
            String uri = "https://api.foursquare.com/v2/venues/search?client_id=" + CLIENT_ID
                    + "&client_secret=" + CLIENT_SECRET
                    + "&v=" + VERSION
                    + "&ll="+LOC
                    + "&radius="+Constants.SCAN_RANGE;
            String json = null;
            try {
                URL obj = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET"); // default is GET
                int responseCode = con.getResponseCode();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    json = response.toString();
                }
            } catch (Exception e) {
                return null; // TODO log?
            }

            // TODO progress etc siehe http://developer.android.com/guide/topics/ui/layout/listview.html
            // TODO mehrere Zeilen je listitem: http://developer.android.com/reference/android/widget/ArrayAdapter.html

            List<Venue> venues;
            // parse json:
            try {
                JSONObject jo = new JSONObject(json);
                jo = jo.getJSONObject("response");
                JSONArray ja = jo.getJSONArray("venues");
                venues = new ArrayList<>(ja.length());
                for(int i=0; i<ja.length(); i++){
                    JSONObject jsonVenue = ja.getJSONObject(i);
                    Venue venue = new Venue();

                    // id
                    venue.setId(jsonVenue.getString("id"));

                    // name
                    Long lastVisit = Protagonist.protagonist.getVisited().get(venue.getId());
                    if (lastVisit == null || lastVisit + Protagonist.getCooldownMillis() <= System.currentTimeMillis()) {
                        venue.setName("\u20AC " + jsonVenue.getString("name")); // Beute
                    } else {
                        venue.setName("\u2050 " + jsonVenue.getString("name")); // Cooldown aktiv
                    }

                    // distance
                    venue.setDistance(jsonVenue.getJSONObject("location").getInt("distance"));

                    venues.add(venue);
                }
            } catch (Exception e) {
                venues = null; // do nothing, return null;
                // e.getMessage(); // TODO log?
            }
            Collections.sort(venues, new Comparator<Venue>() {
                @Override
                public int compare(Venue v1, Venue v2) {
                    return v1.getDistance()-v2.getDistance();
                }
            });
            return venues;
        }

        protected void onPostExecute(List<Venue> results) {
            if (results != null) {
                ArrayAdapter<Venue> adapter = new ArrayAdapter<Venue>(venuesActivity, R.layout.activity_venues, results);
                venuesActivity.setListAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else {
                Toast toast = Toast.makeText(venuesActivity, "failed to load venues", Toast.LENGTH_LONG);
                toast.show();
            }

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Protagonist.storeBaseData(this);
        Protagonist.storeVisitedData(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Protagonist.restoreBaseData(this);
        Protagonist.restoreVisitedData(this);
    }

}
