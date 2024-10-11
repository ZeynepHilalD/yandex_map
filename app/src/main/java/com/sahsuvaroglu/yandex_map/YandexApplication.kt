package com.sahsuvaroglu.yandex_map

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.transport.TransportFactory

class YandexApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("980be490-86aa-4ce4-b09c-6d9e5aaa0ef3")
        //MapKitFactory.setLocale(Locale("en").toString())
        MapKitFactory.initialize(this)
        TransportFactory.initialize(this)
        SearchFactory.initialize(this)

    }
}