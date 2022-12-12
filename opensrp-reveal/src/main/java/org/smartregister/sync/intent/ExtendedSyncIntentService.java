package org.smartregister.sync.intent;

import android.content.Intent;

import org.smartregister.CoreLibrary;
import org.smartregister.job.ValidateSyncDataServiceJob;
import org.smartregister.util.NetworkUtils;

import timber.log.Timber;


public class ExtendedSyncIntentService extends BaseSyncIntentService {


    public ExtendedSyncIntentService() {
        super("ExtendedSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        try {
            super.onHandleIntent(workIntent);
            if (NetworkUtils.isNetworkAvailable()) {
                startSyncValidation();
            }

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void startSyncValidation() {
        ValidateSyncDataServiceJob.scheduleJobImmediately(ValidateSyncDataServiceJob.TAG);
    }
}
