package com.project.demomap.fragments

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PatternItem
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.project.demomap.R

class MapsFragment : Fragment(R.layout.fragment_maps), OnMapReadyCallback, OnMapClickListener {
    private val latLngPosition: ArrayList<LatLng> = ArrayList()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private var polygon: Polygon? = null
    private var gMap: GoogleMap? = null
    private var marker: Marker? = null

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        // it converts the map_type into satellite map_type -->
        googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE

        // add a marker in sydney and move a camera -->
        val assam = LatLng(26.244156, 92.537842)
        gMap = googleMap
        googleMap.addMarker(
            MarkerOptions()
                .position(assam)
                .title("Assam")
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(assam))

        //  it used to get the current location, need fine and coarse location permission -->
        googleMap.isMyLocationEnabled = true

        // / polyline are useful to show a route or some other connection between points.
        val polyLine = googleMap.addPolyline(
            PolylineOptions()
                .clickable(true)
                .add(
                    LatLng(-35.016, 143.321),
                    LatLng(-34.747, 145.592),
                    LatLng(-34.747, 145.592),
                    LatLng(-33.501, 150.217),
                    LatLng(-32.306, 149.248),
                    LatLng(-32.491, 147.309)
                )
        )
        polyLine.tag = "A"
        polyLine.color = R.color.white
        polyLine.isClickable = true
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-23.684, 133.903), 4f))

        // this fun will make clickable polyline with customization -->
        googleMap.setOnPolylineClickListener { polyline -> // convert from solid stroke to doted stroke -->
            if (polyline.pattern == null || !polyline.pattern!!.contains(dot)) {
                polyline.pattern = patternPolylineDoted
            } else {
                polyline.pattern = null
            }
            Toast.makeText(
                context, "Route type " + polyline.tag.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }

        //  this is for onMapCLick -->
        googleMap.setOnMapClickListener(this)

        // add polygons to indicate areas on the map -->
        val polygon = googleMap.addPolygon(
            PolygonOptions()
                .clickable(true)
                .add(
                    LatLng(-27.457, 153.040),
                    LatLng(-33.852, 151.211),
                    LatLng(-37.813, 144.962),
                    LatLng(-34.928, 138.599)
                )
        )
        // this fun will make clickable polygon
        googleMap.setOnPolygonClickListener { polygon ->
            polygon.tag = "beta"
            stylePolygon(polygon)
        }
    }

    // custom style of polyline -->
    private val patternGapLengthPx = 20
    private val dot: PatternItem = Dot()
    private val gap: PatternItem = Gap(patternGapLengthPx.toFloat())

    // create stroke pattern of a gap followed by a dot -->
    private val patternPolylineDoted = listOf(dot, gap)

    // custom color and stroke width and dash and pattern for polygon  when click -->
    private val COLOR_WHITE_ARGB = -0x1
    private val COLOR_DARK_GREEN_ARGB = -0xc771c4
    private val COLOR_LIGHT_GREEN_ARGB = -0x7e387c
    private val COLOR_DARK_ORANGE_ARGB = -0xa80e9
    private val COLOR_LIGHT_ORANGE_ARGB = -0x657db
    private val POLYGON_STROKE_WIDTH_PX = 8
    private val PATTERN_DASH_LENGTH_PX = 20
    private val DASH: PatternItem = Dash(PATTERN_DASH_LENGTH_PX.toFloat())

    // Create a stroke pattern of a gap followed by a dash.
    private val PATTERN_POLYGON_ALPHA = listOf(gap, DASH)

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private val PATTERN_POLYGON_BETA = listOf(dot, gap, DASH, gap)

    private fun stylePolygon(polygon: Polygon) {
        // Get the data object stored with the polygon.
        val type = polygon.tag?.toString() ?: ""
        var pattern: List<PatternItem>? = null
        var strokeColor = R.color.black
        var fillColor = COLOR_WHITE_ARGB
        when (type) {
            "alpha" -> {
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA
                strokeColor = COLOR_DARK_GREEN_ARGB
                fillColor = COLOR_LIGHT_GREEN_ARGB
            }

            "beta" -> {
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA
                strokeColor = COLOR_DARK_ORANGE_ARGB
                fillColor = COLOR_LIGHT_ORANGE_ARGB
            }
        }
        polygon.strokePattern = pattern
        polygon.strokeWidth = POLYGON_STROKE_WIDTH_PX.toFloat()
        polygon.strokeColor = strokeColor
        polygon.fillColor = fillColor
    }

    override fun onMapClick(latLng: LatLng) {
        // add marker when we click on map -->
        marker = gMap?.addMarker(MarkerOptions().position(latLng).title("myMarker"))

        // store latLng in arrayList -->4
        latLngPosition.add(latLng)
        polygon?.strokeColor = R.color.red
////        var marker = MarkerOptions().position.toString()
////        Toast.makeText(context, marker, Toast.LENGTH_SHORT).show()
//        positionList = MarkerOptions().position(latLng)

        // create a polygon when we get 4 latLng in arrayList -->
        if (latLngPosition.size == 4) {
            polygon = gMap?.addPolygon(PolygonOptions().apply {
                clickable(true)
                latLngPosition.forEach {
                    add(it)
                }
            })
            latLngPosition.clear()
        }
    }
}