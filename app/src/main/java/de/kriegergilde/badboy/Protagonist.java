package de.kriegergilde.badboy;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.max;


public final class Protagonist implements Serializable {

    public final static Protagonist protagonist = new Protagonist();

    private long currency;
    private int lifeCurrent = Constants.BASE_LIFE;
    private long lastReggedMillis = 0; // time when last regg occured

    private static Henchman henchman = Henchman.NONE;
    private static Cloth cloth = Cloth.RAGS;
    private static Face face = Face.NONE;
    private static Weapon weapon = Weapon.NONE;
    private static Map<String, Long> visited = new HashMap<>(30);


    private Protagonist(){};

    public void checkRegeneration(){
        if (lifeCurrent == getLifeMax()){
            lastReggedMillis = System.currentTimeMillis();
        } else {
            long timePassed = System.currentTimeMillis() - lastReggedMillis;
            long millisPerPoint = 24*60*60*1000 / getLifeMax();
            int intervallsPassed = timePassed/millisPerPoint > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)(timePassed / millisPerPoint);
            lifeCurrent = Math.min(lifeCurrent+intervallsPassed, getLifeMax());
            lastReggedMillis = lastReggedMillis + intervallsPassed*millisPerPoint;
        }
    }

    public static int getEra(){
        return  max(max(henchman.getEra(), cloth.getEra()), max(face.getEra(), weapon.getEra()));
    }
    public static int getCooldownRedInHours(){
        return henchman.getCooldownRed()
                +cloth.getCooldownRed()
                +face.getCooldownRed()
                +weapon.getCooldownRed();
    }
    public static long getCooldownMillis(){
        return Constants.BASE_COOLDOWN_MILLIS - getCooldownRedInHours() * 60 * 60 * 1000;
    }
    public static int getLifeMax(){
        return Constants.BASE_LIFE
                + henchman.getLifeBonus()
                + cloth.getLifeBonus()
                + face.getLifeBonus()
                + weapon.getLifeBonus();
    }
    public static int getSuccessChance(){
        return Constants.BASE_SUCCESS_CHANCE
                + henchman.getSuccessBonus()
                + cloth.getSuccessBonus()
                + face.getSuccessBonus()
                + weapon.getSuccessBonus();
    }

    public static String getRank(){
        switch(getEra()){
            case 0: return "Bettler";
            case 10: return "Handtaschenräuber";
            case 20: return "Taschendieb";
            case 30: return "Straßenräuber";
            case 40: return "Zuhälter";
            case 50: return "Schwerverbrecher";
            case 60: return "lokaler Capo";
            case 70: return "Pate";
            default: return "unbek. Rang";
        }
    }

    public static String getCoupName(){
        switch(getEra()) {
            case 0:
                return "'haste mal nen Euro?'";
            case 10:
                return "Handtaschenraub";
            case 20:
                return "Taschendiebstahl";
            case 30:
                return "Raubüberfall (Straße)";
            case 40:
                return "Zuhälterei";
            case 50:
                return "Raubüberfall (Gebäude)";
            case 60:
                return "Schutzgelderpressung";
            case 70:
                return "Waffenschmuggel";
            default:
                return "unbek. Rang";
        }
    }

    public static int getCoupMoneyMin(){
        switch(getEra()) {
            case 0:
                return 1;
            case 10:
                return 5;
            case 20:
                return 20;
            case 30:
                return 40;
            case 40:
                return 50;
            case 50:
                return 200;
            case 60:
                return 300;
            case 70:
                return 4000;
            default:
                return 0;
        }
    }

    public static int getCoupMoneyMax(){
        switch(getEra()) {
            case 0:
                return 2;
            case 10:
                return 40;
            case 20:
                return 100;
            case 30:
                return 300;
            case 40:
                return 400;
            case 50:
                return 1200;
            case 60:
                return 2400;
            case 70:
                return 36000;
            default:
                return 0;
        }
    }

    public static int getCoupDamageMin(){
        switch(getEra()) {
            case 0:
                return 0;
            case 10:
                return 1;
            case 20:
                return 2;
            case 30:
                return 3;
            case 40:
                return 2;
            case 50:
                return 4;
            case 60:
                return 6;
            case 70:
                return 16;
            default:
                return 0;
        }
    }

    public static int getCoupDamageMax(){
        switch(getEra()) {
            case 0:
                return 0;
            case 10:
                return 2;
            case 20:
                return 4;
            case 30:
                return 7;
            case 40:
                return 8;
            case 50:
                return 10;
            case 60:
                return 16;
            case 70:
                return 24;
            default:
                return 0;
        }
    }

    public long getCurrency() {
        return currency;
    }

    public void setCurrency(long currency) {
        this.currency = currency;
    }

    public int getLifeCurrent() {
        return lifeCurrent;
    }

    public void setLifeCurrent(int lifeCurrent) {
        this.lifeCurrent = lifeCurrent;
    }

    public long getLastReggedMillis() {
        return lastReggedMillis;
    }

    public void setLastReggedMillis(long lastReggedMillis) {
        this.lastReggedMillis = lastReggedMillis;
    }

    public static Henchman getHenchman() {
        return henchman;
    }

    public static void setHenchman(Henchman henchman) {
        Protagonist.henchman = henchman;
    }

    public static Cloth getCloth() {
        return cloth;
    }

    public static void setCloth(Cloth cloth) {
        Protagonist.cloth = cloth;
    }

    public static Face getFace() {
        return face;
    }

    public static void setFace(Face face) {
        Protagonist.face = face;
    }

    public static Weapon getWeapon() {
        return weapon;
    }

    public static void setWeapon(Weapon weapon) {
        Protagonist.weapon = weapon;
    }

    public Map<String, Long> getVisited() {
        return visited;
    }

    public void setVisited(Map<String, Long> visited) {
        this.visited = visited;
    }

    public static void storeBaseData(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SHARED_PREFS_FILE, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("currency", protagonist.getCurrency());
        editor.putInt("henchman", Protagonist.getHenchman().ordinal());
        editor.putInt("cloth", Protagonist.getCloth().ordinal());
        editor.putInt("face", Protagonist.getFace().ordinal());
        editor.putInt("weapon", Protagonist.getWeapon().ordinal());
        editor.putInt("lifeCur", protagonist.getLifeCurrent());
        editor.putLong("lastReggedMillis", protagonist.getLastReggedMillis());
        editor.commit();
    }

    public static void restoreBaseData(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SHARED_PREFS_FILE, context.MODE_PRIVATE);

        protagonist.setCurrency(sp.getLong("currency", 0));

        int henchmanOrdinal = sp.getInt("henchman", 0);
        Protagonist.setHenchman(Henchman.values()[henchmanOrdinal]);

        int clothOrdinal = sp.getInt("cloth", 0);
        Protagonist.setCloth(Cloth.values()[clothOrdinal]);

        int faceOrdinal = sp.getInt("face", 0);
        Protagonist.setFace(Face.values()[faceOrdinal]);

        int weaponOrdinal = sp.getInt("weapon", 0);
        Protagonist.setWeapon(Weapon.values()[weaponOrdinal]);

        protagonist.setLifeCurrent(sp.getInt("lifeCur", Protagonist.getLifeMax()));
        protagonist.setLastReggedMillis(sp.getLong("lastReggedMillis", 0L));
        protagonist.checkRegeneration();

    }

    public static void storeVisitedData(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SHARED_PREFS_FILE, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        try {
            JSONArray a = new JSONArray();
            for (Map.Entry<String, Long> e : visited.entrySet()) {
                if (e.getValue()+Protagonist.getCooldownMillis() < System.currentTimeMillis()){
                    continue; // no need to save
                }
                JSONObject o = new JSONObject();
                o.put("id", e.getKey());
                o.put("time", e.getValue());
                a.put(o);
            }
            editor.putString("visitedAsJsonArray", a.toString());
            editor.commit();
        } catch (JSONException e) {
            ;// do not commit
        }
    }

    public static void restoreVisitedData(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SHARED_PREFS_FILE, context.MODE_PRIVATE);
        String visitedAsString = sp.getString("visitedAsJsonArray", "[]");
        try {
            visited.clear();
            JSONArray a = new JSONArray(visitedAsString);
            for(int i=0; i<a.length(); i++){
                JSONObject o = a.getJSONObject(i);
                String id = o.getString("id");
                long time = o.getLong("time");
                if(time+Protagonist.getCooldownMillis() > System.currentTimeMillis()){
                    visited.put(id, time);
                }
            }
        } catch (JSONException e) {
            Toast.makeText(context, "failed to load visited json", Toast.LENGTH_LONG).show();
        }
    }

    public long addCurrency(long amount){
        currency += amount;
        return currency;
    }
}
