package com.sminds.smindstridentapp;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {
        private final String INTRO = "intro";
        private final String NAME = "name";
        private SharedPreferences sminds_trident_app_prefs;
        private Context context;

        public PreferenceHelper(Context context) {
            sminds_trident_app_prefs = context.getSharedPreferences("sminds_trident_shared",
                    Context.MODE_PRIVATE);
            this.context = context;
        }

        public void putIsLogin(boolean loginorout) {
            SharedPreferences.Editor edit = sminds_trident_app_prefs.edit();
            edit.putBoolean(INTRO, loginorout);
            edit.commit();
        }
        public boolean getIsLogin() {
            return sminds_trident_app_prefs.getBoolean(INTRO, false);
        }

        public void putName(String loginorout) {
            SharedPreferences.Editor edit = sminds_trident_app_prefs.edit();
            edit.putString(NAME, loginorout);
            edit.commit();
        }
        public String getName() {
            return sminds_trident_app_prefs.getString(NAME, "");
        }
    }
