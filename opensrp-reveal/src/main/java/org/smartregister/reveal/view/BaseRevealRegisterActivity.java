package org.smartregister.reveal.view;

import android.content.Context;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;
import org.smartregister.reveal.BuildConfig;
import org.smartregister.reveal.util.Country;
import org.smartregister.util.LangUtils;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.Map;

/**
 * Created by samuelgithengi on 7/30/20.
 */
public abstract class BaseRevealRegisterActivity extends BaseRegisterActivity {

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }


    @Override
    public void startFormActivity(JSONObject jsonObject) {//not used
    }

    @Override
    protected void attachBaseContext(Context base) {
        if (BuildConfig.BUILD_COUNTRY == Country.THAILAND) {
            LangUtils.saveLanguage(base.getApplicationContext(), "th");
        } else if(BuildConfig.BUILD_COUNTRY == Country.SENEGAL ) {
            LangUtils.saveLanguage(base.getApplicationContext(), "fr");
        } else if(BuildConfig.BUILD_COUNTRY == Country.RWANDA){
            LangUtils.saveLanguage(base.getApplicationContext(), "rw");
        } else if(BuildConfig.BUILD_COUNTRY == Country.MOZAMBIQUE){
            LangUtils.saveLanguage(base.getApplicationContext(), "pt");
        } else {
            LangUtils.saveLanguage(base.getApplicationContext(), "en");
        }
        super.attachBaseContext(base);
    }
}
