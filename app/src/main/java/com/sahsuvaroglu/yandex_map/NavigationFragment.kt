package com.sahsuvaroglu.yandex_map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.sahsuvaroglu.yandex_map.databinding.FragmentNavigationBinding
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider


class NavigationFragment : Fragment() {

    private lateinit var binding: FragmentNavigationBinding
    private lateinit var mapView: MapView
    private val currentLocation = Point(36.990971, 35.189239)
    private lateinit var searchManager: SearchManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNavigationBinding.inflate(layoutInflater)
        mapView = binding.mapview
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)


        binding.setLocationCardView.setOnClickListener {
            hideKeyboard(requireActivity(), requireView())
            mapView.map.move(CameraPosition(currentLocation, 14.0f, 0.0f, 0.0f))
        }

        return binding.root
    }
    private fun hideKeyboard(activity: FragmentActivity, view: View) {
        val imm: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun generateBitmapDescriptorFromRes(resId: Int, context: Context): Bitmap {
        val drawable = ContextCompat.getDrawable(context, resId)
        drawable!!.setBounds(
            0,
            0,
            drawable.intrinsicWidth,
            drawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.draw(canvas)
        return bitmap
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setIFAConventionLocation()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    private fun setIFAConventionLocation() {


        mapView.map.move(CameraPosition(currentLocation, 14.0f, 0.0f, 0.0f))

        mapView.map.mapObjects.addPlacemark(
            currentLocation,
            ImageProvider.fromBitmap(generateBitmapDescriptorFromRes(R.drawable.inactive_location_icon, requireContext()))
        )

        val targetLocation = Point(36.986267,35.335259)

        drawPolyline(targetLocation)

    }


    private fun drawPolyline(target: Point){

        mapView.map.mapObjects.addPlacemark(
            currentLocation,
            ImageProvider.fromBitmap(generateBitmapDescriptorFromRes(R.drawable.inactive_location_icon, requireContext()))
        )

        val drivingOptions = DrivingOptions().apply {
            routesCount = 1
        }

        val vehicleOptions = VehicleOptions()

        val drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()


        val points = listOf(
            RequestPoint(this.currentLocation, RequestPointType.WAYPOINT, null),
            RequestPoint(target, RequestPointType.WAYPOINT, null)
        )

        Handler(Looper.getMainLooper()).post {
            drivingRouter.requestRoutes(points, drivingOptions, vehicleOptions, object : DrivingSession.DrivingRouteListener {
                override fun onDrivingRoutes(drivingRoutes: MutableList<DrivingRoute>) {
                    if (drivingRoutes.isNotEmpty()) {
                        for (route in drivingRoutes) {
                            val polyline = mapView.map.mapObjects.addPolyline(route.geometry)
                            polyline.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.main_color))
                            polyline.strokeWidth = 5.0f
                        }
                    }
                }
                override fun onDrivingRoutesError(error: Error) {
                }
            })
        }
    }

}