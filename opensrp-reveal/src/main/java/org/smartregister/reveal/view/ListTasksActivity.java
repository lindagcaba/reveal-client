package org.smartregister.reveal.view;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.vijay.jsonwizard.customviews.TreeViewDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.reveal.BuildConfig;
import org.smartregister.reveal.R;
import org.smartregister.reveal.activity.BaseMapActivity;
import org.smartregister.reveal.activity.RevealJsonFormActivity;
import org.smartregister.reveal.application.RevealApplication;
import org.smartregister.reveal.contract.ListTaskContract;
import org.smartregister.reveal.contract.UserLocationContract.UserLocationView;
import org.smartregister.reveal.model.CardDetails;
import org.smartregister.reveal.model.MosquitoCollectionCardDetails;
import org.smartregister.reveal.model.SprayCardDetails;
import org.smartregister.reveal.presenter.ListTaskPresenter;
import org.smartregister.reveal.util.Constants.Action;
import org.smartregister.reveal.util.RevealMapHelper;
import org.smartregister.util.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.ona.kujaku.callbacks.OnLocationComponentInitializedCallback;
import io.ona.kujaku.utils.Constants;

import static org.smartregister.reveal.util.Constants.ANIMATE_TO_LOCATION_DURATION;
import static org.smartregister.reveal.util.Constants.CONFIGURATION.DEFAULT_LOCATION_BUFFER_RADIUS_IN_METRES;
import static org.smartregister.reveal.util.Constants.CONFIGURATION.LOCATION_BUFFER_RADIUS_IN_METRES;
import static org.smartregister.reveal.util.Constants.CONFIGURATION.UPDATE_LOCATION_BUFFER_RADIUS;
import static org.smartregister.reveal.util.Constants.Intervention.IRS;
import static org.smartregister.reveal.util.Constants.Intervention.MOSQUITO_COLLECTION;
import static org.smartregister.reveal.util.Constants.JSON_FORM_PARAM_JSON;
import static org.smartregister.reveal.util.Constants.REQUEST_CODE_GET_JSON;
import static org.smartregister.reveal.util.Constants.VERTICAL_OFFSET;
import static org.smartregister.reveal.util.Utils.getGlobalConfig;
import static org.smartregister.reveal.util.FamilyConstants.Intent.START_REGISTRATION;


/**
 * Created by samuelgithengi on 11/20/18.
 */
public class ListTasksActivity extends BaseMapActivity implements ListTaskContract.ListTaskView,
        View.OnClickListener, SyncStatusBroadcastReceiver.SyncStatusListener, UserLocationView, OnLocationComponentInitializedCallback {

    private static final String TAG = "ListTasksActivity";

    private AllSharedPreferences sharedPreferences;

    private ListTaskPresenter listTaskPresenter;

    private View rootView;

    private TextView campaignTextView;
    private TextView operationalAreaTextView;
    private TextView districtTextView;
    private TextView facilityTextView;
    private TextView operatorTextView;

    private GeoJsonSource geoJsonSource;

    private GeoJsonSource selectedGeoJsonSource;

    private ProgressDialog progressDialog;

    private MapboxMap mMapboxMap;

    private DrawerLayout mDrawerLayout;

    private CardView sprayCardView;
    private TextView tvSprayStatus;
    private TextView tvPropertyType;
    private TextView tvSprayDate;
    private TextView tvSprayOperator;
    private TextView tvFamilyHead;
    private TextView tvReason;

    private CardView mosquitoCollectionCardView;
    private TextView tvMosquitoCollectionStatus;
    private TextView tvMosquitoTrapSetDate;
    private TextView tvMosquitoTrapFollowUpDate;

    private RefreshGeowidgetReceiver refreshGeowidgetReceiver = new RefreshGeowidgetReceiver();

    private boolean hasRequestedLocation;

    private Snackbar syncProgressSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tasks);

        listTaskPresenter = new ListTaskPresenter(this);
        rootView = findViewById(R.id.content_frame);

        sharedPreferences = RevealApplication.getInstance().getContext().allSharedPreferences();

        initializeMapView(savedInstanceState);
        initializeDrawerLayout();
        initializeProgressDialog();

        findViewById(R.id.btn_add_structure).setOnClickListener(this);

        initializeCardViews();

        syncProgressSnackbar = Snackbar.make(rootView, getString(org.smartregister.R.string.syncing), Snackbar.LENGTH_INDEFINITE);
    }

    private void initializeCardViews() {
        sprayCardView = findViewById(R.id.spray_card_view);
        sprayCardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //intercept clicks and interaction of map below card view
                return true;
            }
        });

        mosquitoCollectionCardView = findViewById(R.id.mosquito_collection_card_view);

        findViewById(R.id.btn_add_structure).setOnClickListener(this);

        findViewById(R.id.btn_collapse_spray_card_view).setOnClickListener(this);

        tvSprayStatus = findViewById(R.id.spray_status);
        tvPropertyType = findViewById(R.id.property_type);
        tvSprayDate = findViewById(R.id.spray_date);
        tvSprayOperator = findViewById(R.id.user_id);
        tvFamilyHead = findViewById(R.id.family_head);
        tvReason = findViewById(R.id.reason);

        tvMosquitoCollectionStatus = findViewById(R.id.trap_collection_status);
        tvMosquitoTrapSetDate = findViewById(R.id.trap_set_date);
        tvMosquitoTrapFollowUpDate = findViewById(R.id.trap_follow_up_date);

        findViewById(R.id.change_spray_status).setOnClickListener(this);

        findViewById(R.id.register_family).setOnClickListener(this);

        findViewById(R.id.btn_collapse_mosquito_collection_card_view).setOnClickListener(this);
    }

    @Override
    public void closeCardView(int id) {
        if (id == R.id.btn_collapse_spray_card_view) {
            setViewVisibility(sprayCardView, false);
        } else if (id == R.id.btn_collapse_mosquito_collection_card_view) {
            setViewVisibility(mosquitoCollectionCardView, false);
        }
    }

    private void setViewVisibility(View view, boolean isVisible) {
        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void initializeMapView(Bundle savedInstanceState) {
        kujakuMapView = findViewById(R.id.kujakuMapView);

        kujakuMapView.getMapboxLocationComponentWrapper().setOnLocationComponentInitializedCallback(this);

        kujakuMapView.onCreate(savedInstanceState);

        kujakuMapView.showCurrentLocationBtn(true);

        Float locationBufferRadius = Float.valueOf(getGlobalConfig(LOCATION_BUFFER_RADIUS_IN_METRES, DEFAULT_LOCATION_BUFFER_RADIUS_IN_METRES.toString()));
        kujakuMapView.setLocationBufferRadius(locationBufferRadius);

        kujakuMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mMapboxMap = mapboxMap;

                RevealMapHelper.addSymbolLayers(mapboxMap, ListTasksActivity.this);

                mapboxMap.setMinZoomPreference(10);
                mapboxMap.setMaxZoomPreference(21);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .zoom(16)
                        .build();
                mapboxMap.setCameraPosition(cameraPosition);

                geoJsonSource = mapboxMap.getSourceAs(getString(R.string.reveal_datasource_name));

                selectedGeoJsonSource = mapboxMap.getSourceAs(getString(R.string.selected_datasource_name));

                listTaskPresenter.onMapReady();

                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
                        listTaskPresenter.onMapClicked(mapboxMap, point);
                    }
                });
            }
        });
    }

    private void initializeDrawerLayout() {

        mDrawerLayout = findViewById(R.id.drawer_layout);

        ImageButton mDrawerMenuButton = findViewById(R.id.drawerMenu);
        mDrawerMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {//do nothing
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {//do nothing
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                listTaskPresenter.onDrawerClosed();
            }

            @Override
            public void onDrawerStateChanged(int newState) {//do nothing
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        headerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                headerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int minimumOperatorMargin = getResources().getDimensionPixelSize(R.dimen.operator_top_margin);
                int screenHeightPixels = getResources().getDisplayMetrics().heightPixels;
                //if content of hamburger menu is bigger than screen; scroll content
                if (screenHeightPixels < headerView.getHeight() + minimumOperatorMargin) {
                    headerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                    View operator = headerView.findViewById(R.id.operator_label);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) operator.getLayoutParams();
                    params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    operator.setLayoutParams(params);
                } else {//content of hamburger menu fits on screen; set menu height to screen height
                    headerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                            screenHeightPixels - getResources().getDimensionPixelSize(R.dimen.hamburger_margin)));
                }
            }
        });

        try {
            ((TextView) headerView.findViewById(R.id.application_version))
                    .setText(getString(R.string.app_version, Utils.getVersion(this)));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        String buildDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(new Date(BuildConfig.BUILD_TIMESTAMP));
        ((TextView) headerView.findViewById(R.id.application_updated)).setText(getString(R.string.app_updated, buildDate));

        campaignTextView = headerView.findViewById(R.id.campaign_selector);
        operationalAreaTextView = headerView.findViewById(R.id.operational_area_selector);
        districtTextView = headerView.findViewById(R.id.district_label);
        facilityTextView = headerView.findViewById(R.id.facility_label);
        operatorTextView = headerView.findViewById(R.id.operator_label);

        operationalAreaTextView.setOnClickListener(this);

        campaignTextView.setOnClickListener(this);

        headerView.findViewById(R.id.logout_button).setOnClickListener(this);
        headerView.findViewById(R.id.sync_button).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.operational_area_selector)
            listTaskPresenter.onShowOperationalAreaSelector();
        else if (v.getId() == R.id.campaign_selector)
            listTaskPresenter.onShowCampaignSelector();
        else if (v.getId() == R.id.logout_button)
            RevealApplication.getInstance().logoutCurrentUser();
        else if (v.getId() == R.id.sync_button) {
            org.smartregister.reveal.util.Utils.startImmediateSync();
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (v.getId() == R.id.btn_add_structure) {
            listTaskPresenter.onAddStructureClicked();
        } else if (v.getId() == R.id.change_spray_status) {
            listTaskPresenter.onChangeInterventionStatus(IRS);
        } else if (v.getId() == R.id.btn_record_mosquito_collection) {
            listTaskPresenter.onChangeInterventionStatus(MOSQUITO_COLLECTION);
        } else if (v.getId() == R.id.btn_collapse_spray_card_view) {
            setViewVisibility(tvReason, false);
            closeCardView(v.getId());
        } else if (v.getId() == R.id.register_family) {
            registerFamily();
        } else if (v.getId() == R.id.btn_collapse_mosquito_collection_card_view) {
            closeCardView(v.getId());
        }
    }

    private void registerFamily() {
        Intent intent = new Intent(this, FamilyRegisterActivity.class);
        intent.putExtra(START_REGISTRATION, true);
        startActivity(intent);
    }

    @Override
    public void onLocationComponentInitialized() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            kujakuMapView.getMapboxLocationComponentWrapper()
                    .getLocationComponent()
                    .applyStyle(getApplicationContext(), R.style.LocationComponentStyling);
        }
    }

    @Override
    public void showOperationalAreaSelector(Pair<String, ArrayList<String>> locationHierarchy) {
        try {
            TreeViewDialog treeViewDialog = new TreeViewDialog(ListTasksActivity.this,
                    R.style.AppTheme_WideDialog,
                    new JSONArray(locationHierarchy.first), locationHierarchy.second, locationHierarchy.second);
            treeViewDialog.setCancelable(true);
            treeViewDialog.setCanceledOnTouchOutside(true);
            treeViewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    listTaskPresenter.onOperationalAreaSelectorClicked(treeViewDialog.getName());
                }
            });
            treeViewDialog.show();
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

    }

    @Override
    public void showCampaignSelector(List<String> campaigns, String entireTreeString) {
        try {
            TreeViewDialog treeViewDialog = new TreeViewDialog(ListTasksActivity.this,
                    R.style.AppTheme_WideDialog,
                    new JSONArray(entireTreeString), new ArrayList<>(campaigns), new ArrayList<>(campaigns));
            treeViewDialog.show();
            treeViewDialog.setCanceledOnTouchOutside(true);
            treeViewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    listTaskPresenter.onCampaignSelectorClicked(treeViewDialog.getValue(), treeViewDialog.getName());
                }
            });
            treeViewDialog.show();
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void setGeoJsonSource(@NonNull FeatureCollection featureCollection, Geometry operationalAreaGeometry) {
        if (geoJsonSource != null) {
            geoJsonSource.setGeoJson(featureCollection);
            if (operationalAreaGeometry != null) {
                mMapboxMap.setCameraPosition(mMapboxMap.getCameraForGeometry(operationalAreaGeometry));
            }
        }
    }

    @Override
    public void lockNavigationDrawerForSelection() {
        mDrawerLayout.openDrawer(GravityCompat.START);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);

    }

    @Override
    public void unlockNavigationDrawer() {
        if (mDrawerLayout.getDrawerLockMode(GravityCompat.START) == DrawerLayout.LOCK_MODE_LOCKED_OPEN) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    @Override
    public void displayNotification(int title, int message, Object... formatArgs) {
        if (formatArgs.length == 0)
            new AlertDialog.Builder(this).setMessage(message).setTitle(title).setPositiveButton(R.string.ok, null).show();
        else
            new AlertDialog.Builder(this).setMessage(getString(message, formatArgs)).setTitle(title).setPositiveButton(R.string.ok, null).show();
    }

    @Override
    public void displayNotification(String message) {
        new AlertDialog.Builder(this).setMessage(message).setTitle(R.string.fetch_structures_title).setPositiveButton(R.string.ok, null).show();
    }

    @Override
    public void openCardView(CardDetails cardDetails) {
        if (cardDetails instanceof SprayCardDetails) {
            populateSprayCardTextViews((SprayCardDetails) cardDetails);
            sprayCardView.setVisibility(View.VISIBLE);
        } else if (cardDetails instanceof MosquitoCollectionCardDetails) {
            populateMosquitoCollectionCardTextViews((MosquitoCollectionCardDetails) cardDetails);
            mosquitoCollectionCardView.setVisibility(View.VISIBLE);
        }
    }

    private void populateSprayCardTextViews(SprayCardDetails sprayCardDetails) {
        tvSprayStatus.setTextColor(getResources().getColor(sprayCardDetails.getStatusColor()));
        tvSprayStatus.setText(sprayCardDetails.getStatusMessage());
        tvPropertyType.setText(sprayCardDetails.getPropertyType());
        tvSprayDate.setText(sprayCardDetails.getSprayDate());
        tvSprayOperator.setText(sprayCardDetails.getSprayOperator());
        tvFamilyHead.setText(sprayCardDetails.getFamilyHead());
        if (!TextUtils.isEmpty(sprayCardDetails.getReason())) {
            tvReason.setVisibility(View.VISIBLE);
            tvReason.setText(sprayCardDetails.getReason());
        } else {
            tvReason.setVisibility(View.GONE);
        }
    }

    private void populateMosquitoCollectionCardTextViews(MosquitoCollectionCardDetails mosquitoCollectionCardDetails) {
        tvMosquitoCollectionStatus.setText(mosquitoCollectionCardDetails.getStatus());
        tvMosquitoTrapSetDate.setText(mosquitoCollectionCardDetails.getTrapSetDate());
        tvMosquitoTrapFollowUpDate.setText(mosquitoCollectionCardDetails.getTrapFollowUpDate());
    }

    @Override
    public void startJsonForm(JSONObject form) {
        Intent intent = new Intent(getApplicationContext(), RevealJsonFormActivity.class);
        try {
            intent.putExtra(JSON_FORM_PARAM_JSON, form.toString());
            startActivityForResult(intent, REQUEST_CODE_GET_JSON);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void displaySelectedFeature(Feature feature, LatLng point) {
        adjustFocusPoint(point);
        kujakuMapView.centerMap(point, ANIMATE_TO_LOCATION_DURATION, mMapboxMap.getCameraPosition().zoom);
        if (selectedGeoJsonSource != null) {
            selectedGeoJsonSource.setGeoJson(FeatureCollection.fromFeature(feature));
        }
    }

    private void adjustFocusPoint(LatLng point) {
        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL || screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            point.setLatitude(point.getLatitude() + VERTICAL_OFFSET);
        }
    }

    @Override
    public void clearSelectedFeature() {
        if (selectedGeoJsonSource != null) {
            try {
                selectedGeoJsonSource.setGeoJson(new com.cocoahero.android.geojson.FeatureCollection().toJSON().toString());
            } catch (JSONException e) {
                Log.e(TAG, "Error clearing selected feature");
            }
        }
    }

    @Override
    public void displayToast(@StringRes int resourceId) {
        Toast.makeText(this, resourceId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GET_JSON && resultCode == RESULT_OK && data.hasExtra(JSON_FORM_PARAM_JSON)) {
            String json = data.getStringExtra(JSON_FORM_PARAM_JSON);
            Log.d(TAG, json);
            listTaskPresenter.saveJsonForm(json);
        } else if (requestCode == Constants.RequestCode.LOCATION_SETTINGS && hasRequestedLocation) {
            if (resultCode == RESULT_OK) {
                listTaskPresenter.getLocationPresenter().waitForUserLocation();
            } else if (resultCode == RESULT_CANCELED) {
                listTaskPresenter.getLocationPresenter().onGetUserLocationFailed();
            }
            hasRequestedLocation = false;
        }
    }

    @Override
    public void setCampaign(String campaign) {
        campaignTextView.setText(campaign);
    }

    @Override
    public void setOperationalArea(String operationalArea) {
        operationalAreaTextView.setText(operationalArea);
    }

    @Override
    public void setDistrict(String district) {
        org.smartregister.reveal.util.Utils.setTextViewText(districtTextView, R.string.district, district);
    }

    @Override
    public void setFacility(String facility) {
        org.smartregister.reveal.util.Utils.setTextViewText(facilityTextView, R.string.facility, facility);
    }

    @Override
    public void setOperator() {
        org.smartregister.reveal.util.Utils.setTextViewText(operatorTextView, R.string.operator, sharedPreferences.fetchRegisteredANM());
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(R.string.fetching_structures_title);
        progressDialog.setMessage(getString(R.string.fetching_structures_message));
    }

    @Override
    public void showProgressDialog(@StringRes int title, @StringRes int message) {
        if (progressDialog != null) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(getString(message));
            progressDialog.show();
        }
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public Location getUserCurrentLocation() {
        return kujakuMapView.getLocationClient() == null ? null : kujakuMapView.getLocationClient().getLastLocation();
    }

    @Override
    public void requestUserLocation() {
        kujakuMapView.setWarmGps(true, getString(R.string.location_service_disabled), getString(R.string.location_services_disabled_spray));
        hasRequestedLocation = true;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void onDestroy() {
        listTaskPresenter = null;
        super.onDestroy();
    }

    @Override
    public void onSyncStart() {
        if (SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
            syncProgressSnackbar.show();
        }
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {
        syncProgressSnackbar.dismiss();
        if (fetchStatus.equals(FetchStatus.fetchedFailed)) {
            Snackbar.make(rootView, org.smartregister.R.string.sync_failed, Snackbar.LENGTH_LONG).show();
        } else if (fetchStatus.equals(FetchStatus.fetched)
                || fetchStatus.equals(FetchStatus.nothingFetched)) {
            Snackbar.make(rootView, org.smartregister.R.string.sync_complete, Snackbar.LENGTH_LONG).show();
        } else if (fetchStatus.equals(FetchStatus.noConnection)) {
            Snackbar.make(rootView, org.smartregister.R.string.sync_failed_no_internet, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        onSyncInProgress(fetchStatus);
    }

    @Override
    public void onResume() {
        super.onResume();
        SyncStatusBroadcastReceiver.getInstance().addSyncStatusListener(this);
        IntentFilter filter = new IntentFilter(Action.STRUCTURE_TASK_SYNCED);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(refreshGeowidgetReceiver, filter);
        listTaskPresenter.onInitializeDrawerLayout();
    }

    @Override
    public void onPause() {
        SyncStatusBroadcastReceiver.getInstance().removeSyncStatusListener(this);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(refreshGeowidgetReceiver);
        super.onPause();
    }

    private class RefreshGeowidgetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras != null && extras.getBoolean(UPDATE_LOCATION_BUFFER_RADIUS)) {
                String bufferRadius = getGlobalConfig(LOCATION_BUFFER_RADIUS_IN_METRES, DEFAULT_LOCATION_BUFFER_RADIUS_IN_METRES.toString());
                kujakuMapView.setLocationBufferRadius(Float.valueOf(bufferRadius));
            } else {
                listTaskPresenter.refreshStructures();
            }
        }
    }
}
