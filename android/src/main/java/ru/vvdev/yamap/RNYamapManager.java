package ru.vvdev.yamap;

import android.view.View;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.geometry.Point;
import java.util.ArrayList;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ru.vvdev.yamap.models.RNMarker;
import ru.vvdev.yamap.view.YaMapView;

public class RNYamapManager extends ViewGroupManager<YaMapView> {
    public static final String REACT_CLASS = "YamapView";

    private static final int SET_CENTER = 1;
    private static final int FIT_ALL_MARKERS = 2;
    private static final int TRY_BUILD_ROUTES = 3;

    RNYamapManager() { }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
                .put("onMarkerPress", MapBuilder.of("registrationName", "onMarkerPress"))
                .build();
    }

    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder()
                .put("routes", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onRoutesFound")))
                .build();
    }

    @Override
    public Map<String,Integer> getCommandsMap() {
        return MapBuilder.of(
                "setCenter",
                SET_CENTER,
                "fitAllMarkers",
                FIT_ALL_MARKERS,
                "tryBuildRoutes",
                TRY_BUILD_ROUTES);
    }

    @Override
    public void receiveCommand(
            YaMapView view,
            int commandType,
            @Nullable ReadableArray args) {
        Assertions.assertNotNull(view);
        Assertions.assertNotNull(args);
        switch (commandType) {
            case SET_CENTER:
                setCenter(view, args.getMap(0));
                return;
            case FIT_ALL_MARKERS:
                fitAllMarkers(view);
                return;
            default:
                throw new IllegalArgumentException(String.format(
                        "Unsupported command %d received by %s.",
                        commandType,
                        getClass().getSimpleName()));
        }
    }

    private YaMapView castToYaMapView(View view) {
        return (YaMapView) view;
    }

    @Nonnull
    @Override
    public YaMapView createViewInstance(@Nonnull ThemedReactContext context) {
        YaMapView view = new YaMapView(context);
        MapKitFactory.getInstance().onStart();
        view.onStart();
        return view;
    }

    private void setCenter(View view, ReadableMap center) {
        Point centerPosition = new Point(center.getDouble("lat"), center.getDouble("lon"));
        float zoom = (float) center.getDouble("zoom");
        castToYaMapView(view).setCenter(centerPosition, zoom);
    }

    private void fitAllMarkers(View view) {
        castToYaMapView(view).fitAllMarkers();
    }

    // props
    @ReactProp(name = "userLocationIcon")
    public void setUserLocationIcon(View view, String icon) {
        castToYaMapView(view).setUserLocationIcon(icon);
    }

    @ReactProp(name = "routeColors")
    public void setRouteColors(View view, ReadableMap colors) {
        castToYaMapView(view).setRouteColors(colors);
    }

    @ReactProp(name = "vehicles")
    public void requestRouteWithSpecificVehicles(View view, ReadableArray vehicles) {
        ArrayList<String> parsed = new ArrayList<>();
        if (vehicles != null) {
            for (int i = 0; i < vehicles.size(); ++i) {
                parsed.add(vehicles.getString(i));
            }
        }
        castToYaMapView(view).setAcceptVehicleTypes(parsed);
    }

    @ReactProp(name = "route")
    public void requestRoute(View view, ReadableMap points) {
        if (points == null) {
            castToYaMapView(view).removeRoute();
            return;
        }
        ReadableMap start = points.getMap("start");
        ReadableMap end = points.getMap("end");
        if (start != null && end != null) {
            Double startLat = start.getDouble("lat");
            Double startLon = start.getDouble("lon");
            Double endLat = end.getDouble("lat");
            Double endLon = end.getDouble("lon");
            if (startLat != null && startLon != null && endLat != null && endLon != null) {
                ArrayList<RequestPoint> routePoints = new ArrayList<>();
                routePoints.add(new RequestPoint(new Point(startLat, startLon), new ArrayList<Point>(), RequestPointType.WAYPOINT));
                routePoints.add(new RequestPoint(new Point(endLat, endLon), new ArrayList<Point>(), RequestPointType.WAYPOINT));
                castToYaMapView(view).requestRoute(routePoints);
            }
        }
    }

    @ReactProp(name = "markers")
    public void setMarkers(View view, ReadableArray markers) {
        ArrayList<RNMarker> parsed = new ArrayList<>();
        for (int i = 0; i < markers.size(); ++i) {
            ReadableMap markerMap = markers.getMap(i);
            double lon = markerMap.getDouble("lon");
            double lat = markerMap.getDouble("lat");
            String id = markerMap.getString("id");
            String uri = markerMap.getString("source");
            Integer zIndex = markerMap.getInt("zIndex");
            RNMarker marker = new RNMarker(lon, lat, id, zIndex, uri);
            parsed.add(marker);
        }
        castToYaMapView(view).setMarkers(parsed);
    }
}
