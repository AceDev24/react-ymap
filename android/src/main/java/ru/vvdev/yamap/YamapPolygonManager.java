package ru.vvdev.yamap;

import android.view.View;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.yandex.mapkit.geometry.Point;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nonnull;

import ru.vvdev.yamap.view.YamapPolygon;

public class YamapPolygonManager extends ViewGroupManager<YamapPolygon> {
    public static final String REACT_CLASS = "YamapPolygon";

    YamapPolygonManager() { }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
                .put("onPress", MapBuilder.of("registrationName", "onPress"))
                .build();
    }

    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder()
                .build();
    }

    private YamapPolygon castToYaMapView(View view) {
        return (YamapPolygon) view;
    }

    @Nonnull
    @Override
    public YamapPolygon createViewInstance(@Nonnull ThemedReactContext context) {
        return new YamapPolygon(context);
    }

    // props
    @ReactProp(name = "points")
    public void setPoints(View view, ReadableArray points) {
        ArrayList<Point> parsed = new ArrayList<>();
        for (int i = 0; i < points.size(); ++i) {
            ReadableMap markerMap = points.getMap(i);
            double lon = markerMap.getDouble("lon");
            double lat = markerMap.getDouble("lat");
            Point point = new Point(lat, lon);
            parsed.add(point);
        }
        castToYaMapView(view).setPolygonPoints(parsed);
    }

    @ReactProp(name = "strokeWidth")
    public void setStrokeWidth(View view, float width) {
        castToYaMapView(view).setStrokeWidth(width);
    }

    @ReactProp(name = "strokeColor")
    public void setStrokeColor(View view, int color) {
        castToYaMapView(view).setStrokeColor(color);
    }
    @ReactProp(name = "fillColor")
    public void setFillColor(View view, int color) {
        castToYaMapView(view).setFillColor(color);
    }
    @ReactProp(name = "zIndex")
    public void setZIndex(View view, int zIndex) {
        castToYaMapView(view).setZIndex(zIndex);
    }
}
