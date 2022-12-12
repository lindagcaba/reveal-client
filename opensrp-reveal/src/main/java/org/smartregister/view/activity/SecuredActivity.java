package org.smartregister.view.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.reveal.R;
import org.smartregister.broadcastreceivers.OpenSRPClientBroadCastReceiver;
import org.smartregister.event.Listener;
import org.smartregister.receiver.P2pProcessingStatusBroadcastReceiver;
import org.smartregister.service.ZiggyService;
import org.smartregister.util.Utils;
import org.smartregister.view.customcontrols.ProcessingInProgressSnackbar;

import java.util.Map;

import timber.log.Timber;

import static org.smartregister.AllConstants.ALERT_NAME_PARAM;
import static org.smartregister.AllConstants.CloudantSync;
import static org.smartregister.AllConstants.ENTITY_ID;
import static org.smartregister.AllConstants.FORM_SUCCESSFULLY_SUBMITTED_RESULT_CODE;
import static org.smartregister.event.Event.ON_LOGOUT;
import static org.smartregister.util.Log.logInfo;

public abstract class SecuredActivity extends MultiLanguageActivity implements P2pProcessingStatusBroadcastReceiver.StatusUpdate {
    public static final String LOG_TAG = "SecuredActivity";
    protected final int MENU_ITEM_LOGOUT = 2312;
    protected Listener<Boolean> logoutListener;
    protected ZiggyService ziggyService;
    private String metaData;
    private OpenSRPClientBroadCastReceiver openSRPClientBroadCastReceiver;

    private ProcessingInProgressSnackbar processingInProgressSnackbar;
    private P2pProcessingStatusBroadcastReceiver p2pProcessingStatusBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ziggyService = context().ziggyService();

        logoutListener = new Listener<Boolean>() {
            public void onEvent(Boolean data) {
                finish();
            }
        };
        ON_LOGOUT.addListener(logoutListener);

        if (context().IsUserLoggedOut()) {
            DrishtiApplication application = ( DrishtiApplication ) getApplication();
            application.logoutCurrentUser();
            return;
        }

        onCreation();

        // Intent replicationServiceIntent = new Intent(this, ReplicationIntentService.class);
        //startService(replicationServiceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (context().IsUserLoggedOut()) {
            DrishtiApplication application = ( DrishtiApplication ) getApplication();
            application.logoutCurrentUser();
            return;
        }

        onResumption();
        setupReplicationBroadcastReceiver();

        if (CoreLibrary.getInstance().getP2POptions() != null
                && CoreLibrary.getInstance().getP2POptions().isEnableP2PLibrary()) {
            if (p2pProcessingStatusBroadcastReceiver == null) {
                p2pProcessingStatusBroadcastReceiver = new P2pProcessingStatusBroadcastReceiver(this);
            }

            // Register listener to remove the SnackBar
            LocalBroadcastManager.getInstance(this)
                    .registerReceiver(p2pProcessingStatusBroadcastReceiver
                            , new IntentFilter(AllConstants.PeerToPeer.PROCESSING_ACTION));

            if (CoreLibrary.getInstance().isPeerToPeerProcessing()) {
                showProcessingInProgressBottomSnackbar(this);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.switchLanguageMenuItem) {
            String newLanguagePreference = context().userService().switchLanguagePreference();
            Utils.showShortToast(this, getString(R.string.language_change_prepend_message) + " " + newLanguagePreference + ".");

            return super.onOptionsItemSelected(item);
        } else if (i == MENU_ITEM_LOGOUT) {
            DrishtiApplication application = ( DrishtiApplication ) getApplication();
            application.logoutCurrentUser();

            return super.onOptionsItemSelected(item);
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Attaches a logout menu item to the provided menu
     *
     * @param menu The menu to attach the logout menu item
     */
    protected void attachLogoutMenuItem(Menu menu) {
        menu.add(0, MENU_ITEM_LOGOUT, menu.size(), R.string.logout_text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        attachLogoutMenuItem(menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected abstract void onCreation();

    protected abstract void onResumption();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isSuccessfulFormSubmission(resultCode)) {
            logInfo("Form successfully saved. MetaData: " + metaData);
            if (hasMetadata()) {
                Map<String, String> metaDataMap = new Gson()
                        .fromJson(metaData, new TypeToken<Map<String, String>>() {
                        }.getType());
                if (metaDataMap.containsKey(ENTITY_ID) && metaDataMap
                        .containsKey(ALERT_NAME_PARAM)) {
                    CoreLibrary.getInstance().context().alertService()
                            .changeAlertStatusToInProcess(metaDataMap.get(ENTITY_ID),
                                    metaDataMap.get(ALERT_NAME_PARAM));
                }
            }
        }
    }

    private boolean isSuccessfulFormSubmission(int resultCode) {
        return resultCode == FORM_SUCCESSFULLY_SUBMITTED_RESULT_CODE;
    }

    private boolean hasMetadata() {
        return this.metaData != null && !this.metaData.equalsIgnoreCase("undefined");
    }



    private void setupReplicationBroadcastReceiver() {
        // The filter's action is BROADCAST_ACTION
        IntentFilter opensrpClientIntentFilter = new IntentFilter(
                CloudantSync.ACTION_DATABASE_CREATED);
        opensrpClientIntentFilter.addAction(CloudantSync.ACTION_REPLICATION_COMPLETED);
        opensrpClientIntentFilter.addAction(CloudantSync.ACTION_REPLICATION_ERROR);
        opensrpClientIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        opensrpClientIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        opensrpClientIntentFilter.addAction(Intent.ACTION_DATE_CHANGED);

        openSRPClientBroadCastReceiver = new OpenSRPClientBroadCastReceiver(this);
        // Registers the OpenSRPClientBroadCastReceiver and its intent filters
        registerReceiver(openSRPClientBroadCastReceiver, opensrpClientIntentFilter);
    }

    public void showToast(String message) {
        Utils.showToast(this, message);
    }

    protected Context context() {
        return CoreLibrary.getInstance().context().updateApplicationContext(this.getApplicationContext());
    }

    public void showProcessingInProgressSnackbar(@NonNull AppCompatActivity appCompatActivity, int margin) {
        if (processingInProgressSnackbar == null) {
            // Create the snackbar
            View parentView = ((ViewGroup) appCompatActivity.findViewById(android.R.id.content))
                    .getChildAt(0);

            processingInProgressSnackbar = ProcessingInProgressSnackbar.make(parentView);

            if (margin != 0) {
                processingInProgressSnackbar.addBottomBarMargin(margin);
            }
            processingInProgressSnackbar.setDuration(BaseTransientBottomBar.LENGTH_INDEFINITE);
            processingInProgressSnackbar.show();
        } else if (!processingInProgressSnackbar.isShown()) {
            processingInProgressSnackbar.show();
        }
    }

    public void showProcessingInProgressBottomSnackbar(final @NonNull AppCompatActivity appCompatActivity) {
        final View bottomNavigationBar = appCompatActivity.findViewById(R.id.bottom_navigation);
        if (bottomNavigationBar != null) {
            bottomNavigationBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int height = bottomNavigationBar.getHeight();
                    showProcessingInProgressSnackbar(appCompatActivity, height);

                    bottomNavigationBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        } else {
            showProcessingInProgressSnackbar(appCompatActivity, 0);
        }
    }

    public void removeProcessingInProgressSnackbar() {
        if (processingInProgressSnackbar != null && processingInProgressSnackbar.isShown()) {
            processingInProgressSnackbar.dismiss();
        }
    }

    @Override
    public void onStatusUpdate(boolean isProcessing) {
        if (isProcessing) {
            showProcessingInProgressBottomSnackbar(this);
        } else {
            removeProcessingInProgressSnackbar();
        }
    }

    @Override
    protected void onPause() {

        super.onPause();

        if (openSRPClientBroadCastReceiver != null) {
            try {
                unregisterReceiver(openSRPClientBroadCastReceiver);
                openSRPClientBroadCastReceiver = null;
            } catch (IllegalArgumentException e) {
                Timber.e(e, "EXCEPTION %s", e.toString());
            }
        }

        if (p2pProcessingStatusBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this)
                    .unregisterReceiver(p2pProcessingStatusBroadcastReceiver);
        }

        removeProcessingInProgressSnackbar();
    }
}
