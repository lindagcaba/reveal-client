package org.smartregister.reveal.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.reveal.R;
import org.smartregister.reveal.repository.RevealMappingHelper;
import org.smartregister.reveal.util.Constants.StructureType;

import java.util.ArrayList;

import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.linear;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.zoom;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleStrokeColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleStrokeOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleStrokeWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import static org.smartregister.reveal.util.Constants.CONFIGURATION.DEFAULT_INDEX_CASE_CIRCLE_RADIUS_IN_METRES;
import static org.smartregister.reveal.util.Constants.CONFIGURATION.INDEX_CASE_CIRCLE_RADIUS_IN_METRES;
import static org.smartregister.reveal.util.Constants.GeoJSON.IS_INDEX_CASE;
import static org.smartregister.reveal.util.Constants.GeoJSON.TYPE;
import static org.smartregister.reveal.util.Utils.calculateZoomLevelRadius;
import static org.smartregister.reveal.util.Utils.createGeoJSONCircle;
import static org.smartregister.reveal.util.Utils.getGlobalConfig;

/**
 * Created by samuelgithengi on 2/20/19.
 */
public class RevealMapHelper {

    private static final String TAG = RevealMapHelper.class.getName();

    private static final String LARVAL_BREEDING_ICON = "larval-breeding-icon";

    private static final String MOSQUITO_COLLECTION_ICON = "mosquito-collection-icon";

    private static final String INDEX_CASE_TARGET_ICON = "index-case-target-icon";

    public static final String LARVAL_BREEDING_LAYER = "larval-breeding-layer";

    public static final String MOSQUITO_COLLECTION_LAYER = "mosquito-collection-layer";

    public static final String INDEX_CASE_SYMBOL_LAYER = "index-case-symbol-layer";

    public static final String INDEX_CASE_CIRCLE_LAYER = "index-case-circle-layer";

    private static final String INDEX_CASE_SOURCE = "index_case_source";

    private CircleLayer indexCaseCircleLayer = null;

    private LineLayer lineLayer = null;

    private Location indexCaseLocation = null;

    private GeoJsonSource indexCaseSource = new GeoJsonSource(INDEX_CASE_SOURCE);

    private float radius = Float.valueOf(getGlobalConfig(INDEX_CASE_CIRCLE_RADIUS_IN_METRES, DEFAULT_INDEX_CASE_CIRCLE_RADIUS_IN_METRES.toString()));

    public static void addCustomLayers(@NonNull Style mMapboxMapStyle, Context context) {

        Expression dynamicIconSize = interpolate(linear(), zoom(),
                literal(13.98f), literal(0),
                literal(17.79f), literal(1.5f),
                literal(18.8f), literal(2));

        // mosquito collection symbol layer
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_mosquito);
        mMapboxMapStyle.addImage(MOSQUITO_COLLECTION_ICON, icon);
        SymbolLayer symbolLayer = new SymbolLayer(MOSQUITO_COLLECTION_LAYER, context.getString(R.string.reveal_datasource_name));
        symbolLayer.setProperties(
                iconImage(MOSQUITO_COLLECTION_ICON),
                iconSize(dynamicIconSize));
        symbolLayer.setFilter(eq(get(TYPE), StructureType.MOSQUITO_COLLECTION_POINT));
        mMapboxMapStyle.addLayer(symbolLayer);

        // larval breeding symbol layer
        icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_breeding);
        mMapboxMapStyle.addImage(LARVAL_BREEDING_ICON, icon);
        symbolLayer = new SymbolLayer(LARVAL_BREEDING_LAYER, context.getString(R.string.reveal_datasource_name));
        symbolLayer.setProperties(
                iconImage(LARVAL_BREEDING_ICON),
                iconSize(dynamicIconSize));
        symbolLayer.setFilter(eq(get(TYPE), StructureType.LARVAL_BREEDING_SITE));
        mMapboxMapStyle.addLayer(symbolLayer);
    }

    public void addIndexCaseLayers(MapboxMap mapboxMap, Context context, FeatureCollection featureCollection) {
        Style mMapboxMapStyle = mapboxMap.getStyle();

        // index case symbol layer
        Expression dynamicIconSize = interpolate(linear(), zoom(),
                literal(11.98f), literal(1),
                literal(17.79f), literal(3f),
                literal(18.8f), literal(4));

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_index_case_target_icon);
        mMapboxMapStyle.addImage(INDEX_CASE_TARGET_ICON, icon);
        SymbolLayer symbolLayer = new SymbolLayer(INDEX_CASE_SYMBOL_LAYER, context.getString(R.string.reveal_datasource_name));
        symbolLayer.setProperties(iconImage(INDEX_CASE_TARGET_ICON), iconSize(dynamicIconSize),
                iconIgnorePlacement(true), iconAllowOverlap(true));
        symbolLayer.setFilter(eq(get(IS_INDEX_CASE), Boolean.TRUE.toString()));
        mMapboxMapStyle.addLayer(symbolLayer);

        // index case circle layer
        mapboxMap.getStyle().addSource(indexCaseSource);
        indexCaseCircleLayer = new CircleLayer(INDEX_CASE_CIRCLE_LAYER, INDEX_CASE_SOURCE);
        indexCaseCircleLayer.withProperties(
                circleOpacity(0f),
                circleColor(Color.parseColor("#ffffff")),
                circleStrokeColor("#ffffff"),
                circleStrokeWidth(2f),
                circleStrokeOpacity(1f)
        );

        GeoJsonSource polygonSource = null;
        Feature indexCase = getIndexCase(featureCollection);
        indexCaseLocation = (new RevealMappingHelper()).getCenter(indexCase.geometry().toJson());

        FeatureCollection circleFeatureCollection;
        try {
            circleFeatureCollection = createGeoJSONCircle(new LatLng(indexCaseLocation.getLatitude(), indexCaseLocation.getLongitude()), 1, null );
            polygonSource = new GeoJsonSource("polygon", circleFeatureCollection);
            mapboxMap.getStyle().addSource(polygonSource);
        } catch (JSONException e) {
            Log.e(TAG, e.getStackTrace().toString());
        }

        lineLayer = new LineLayer("polygon", polygonSource.getId());
        lineLayer.withProperties(
                lineWidth(2f),
                lineColor("#ffffff"),
                lineJoin("round")
        );
        mMapboxMapStyle.addLayer(lineLayer);

        updateIndexCaseLayers(mapboxMap, featureCollection,context);
    }

    public void updateIndexCaseLayers(MapboxMap mapboxMap, FeatureCollection featureCollection, Context context) {
        try {
            if (featureCollection != null) {
                Feature indexCase = getIndexCase(featureCollection);
                if (indexCase != null) {
                    // create index case point
                    indexCaseLocation = (new RevealMappingHelper()).getCenter(indexCase.geometry().toJson());
                    JSONObject feature = new JSONObject(indexCase.toJson());
                    JSONObject geometry = new JSONObject();
                    geometry.put("type", "Point");
                    geometry.put("coordinates", new JSONArray(new Double[]{indexCaseLocation.getLongitude(), indexCaseLocation.getLatitude()}));
                    feature.put("geometry", geometry);
                    resizeIndexCaseCircle(mapboxMap, context);
                    indexCaseSource.setGeoJson(Feature.fromJson(feature.toString()));
                } else {
                    indexCaseSource.setGeoJson(FeatureCollection.fromFeatures(new ArrayList<>()));
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getStackTrace().toString());
        }
    }

    public void resizeIndexCaseCircle(MapboxMap mapboxMap, Context context) {
        if (indexCaseLocation != null && indexCaseCircleLayer != null) {
            float circleRadius = calculateZoomLevelRadius(mapboxMap, mapboxMap.getCameraPosition().target.getLatitude(), radius, context);
            indexCaseCircleLayer.setProperties(circleRadius(circleRadius));
        }
    }

    public Feature getIndexCase(FeatureCollection featureCollection) {
        Feature indexCase = null;
        for (Feature feature : featureCollection.features()) {
            if (feature.hasProperty(IS_INDEX_CASE) && feature.getBooleanProperty(IS_INDEX_CASE)) {
                indexCase = feature;
            }
        }
        return indexCase;
    }

    public CircleLayer getIndexCaseCircleLayer() {
        return indexCaseCircleLayer;
    }
}
