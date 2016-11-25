package com.dsm.spiralmoon.pbangchajja;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserPref {

    public SharedPreferences preference;
    public SharedPreferences.Editor editor;

    public UserPref (Context context) {
        preference = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preference.edit();
    }

    public void setUserId(String userid) {
        editor.putString("userid", userid);
        editor.commit();
    }
    public String getUserId() {
        return preference.getString("userid", null);
    }
    public void setLastLocation(double latitude, double longitude) {
        editor.putString("latitude", Double.toString(latitude));
        editor.putString("longitude", Double.toString(longitude));
        editor.commit();
    }
    public double getLastLatitude() {
        return Double.parseDouble(preference.getString("latitude", "0"));
    }
    public double getLastLongitude() {
        return Double.parseDouble(preference.getString("longitude", "0"));
    }
}
