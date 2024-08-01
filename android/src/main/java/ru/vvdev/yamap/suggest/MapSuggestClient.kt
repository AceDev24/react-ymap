package ru.vvdev.yamap.suggest

import com.facebook.react.bridge.ReadableMap
import com.yandex.mapkit.geometry.Point
import ru.vvdev.yamap.utils.Callback

interface MapSuggestClient {
    /**
     * Получить саджесты по тексту `text`.
     * Вернуть результат в метод `onSuccess` в случае успеха, в случае неудачи в `onError`
     */
    fun suggest(
        text: String?,
        onSuccess: Callback<List<MapSuggestItem?>?>?,
        onError: Callback<Throwable?>?
    )

    fun suggest(
        text: String?,
        options: ReadableMap?,
        onSuccess: Callback<List<MapSuggestItem?>?>?,
        onError: Callback<Throwable?>?
    )

    fun suggestPoint(
        point: Point,
        onSuccess: Callback<MapSearchItem?>,
        onError: Callback<Throwable?>?
    )

    fun suggestAddress(
        text: String,
        onSuccess: Callback<Point?>,
        onError: Callback<Throwable?>?
    )

    /**
     * Остановить сессию поиска саджестов
     */
    fun resetSuggest()
}
