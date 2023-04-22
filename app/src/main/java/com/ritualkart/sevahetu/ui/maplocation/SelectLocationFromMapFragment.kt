package com.ritualkart.sevahetu.ui.maplocation

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.mapmyindia.sdk.geojson.Point
import com.mapmyindia.sdk.maps.MapView
import com.mapmyindia.sdk.maps.MapmyIndiaMap
import com.mapmyindia.sdk.maps.OnMapReadyCallback
import com.mapmyindia.sdk.maps.Style
import com.mapmyindia.sdk.maps.Style.OnStyleLoaded
import com.mapmyindia.sdk.maps.camera.CameraELocUpdateFactory
import com.mapmyindia.sdk.maps.camera.CameraPosition
import com.mapmyindia.sdk.maps.camera.CameraUpdateFactory
import com.mapmyindia.sdk.maps.geometry.LatLng
import com.mapmyindia.sdk.maps.location.LocationComponent
import com.mapmyindia.sdk.maps.location.LocationComponentActivationOptions
import com.mapmyindia.sdk.maps.location.engine.LocationEngine
import com.mapmyindia.sdk.maps.location.engine.LocationEngineCallback
import com.mapmyindia.sdk.maps.location.engine.LocationEngineRequest
import com.mapmyindia.sdk.maps.location.engine.LocationEngineResult
import com.mapmyindia.sdk.maps.location.permissions.PermissionsListener
import com.mapmyindia.sdk.maps.location.permissions.PermissionsManager
import com.mapmyindia.sdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment
import com.mapmyindia.sdk.plugins.places.autocomplete.ui.PlaceSelectionListener
import com.mapmyindia.sdk.plugins.places.common.a.a
import com.mapmyindia.sdk.plugins.places.common.a.b
import com.mapmyindia.sdk.plugins.places.common.a.d
import com.mapmyindia.sdk.plugins.places.common.a.e
import com.mapmyindia.sdk.plugins.places.placepicker.model.PlacePickerOptions
import com.mapmyindia.sdk.plugins.places.placepicker.viewmodel.PlacePickerViewModel
import com.mmi.services.api.Place
import com.mmi.services.api.autosuggest.model.ELocation
import com.ritualkart.sevahetu.R
import com.ritualkart.sevahetu.databinding.FragmentSelectLocationFromMapBinding
import com.ritualkart.sevahetu.utiliy.Constants
import com.ritualkart.sevahetu.utiliy.GpsUtils
import com.ritualkart.sevahetu.utiliy.Preference
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SelectLocationFromMapFragment : Fragment(), OnMapReadyCallback, MapmyIndiaMap.OnCameraMoveStartedListener, MapmyIndiaMap.OnCameraIdleListener,
    Observer<d<Place>>,
    PermissionsListener, MapmyIndiaMap.CancelableCallback,
    LocationEngineCallback<LocationEngineResult>{

    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentSelectLocationFromMapBinding? = null
    private val binding get() = _binding!!
    private var navController: NavController? = null
    private lateinit var sharedPreference: Preference

    private var permissionsManager: PermissionsManager? = null
    var bottomSheet: CurrentPlaceSelectionBottomSheet? = null
    private var viewModel: PlacePickerViewModel? = null
    private var options: PlacePickerOptions? = null
    private var markerImage: ImageView? = null
    private var mapmyIndiaMap: MapmyIndiaMap? = null
    private var mapView: MapView? = null
    private var userLocationButton: FloatingActionButton? = null
    private var ivSearch: SearchView? = null
    private var locationEngine: LocationEngine? = null
    private var includeReverseGeocode = false
    private var isCurrentLocation = true
    private var isGPS = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().setTheme(R.style.Theme_SevaHetu);
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        GpsUtils(requireContext()).turnGPSOn { isGPSEnable -> // turn on GPS
            isGPS = isGPSEnable
            Toast.makeText(requireContext(),"permission granted 2" ,1)

        }
    }

    private fun addSearchAutocomplete() {
        binding.searchBar.setOnClickListener { this@SelectLocationFromMapFragment.searchOptions() }
    }
    private fun searchOptions() {
        var var1: PlaceAutocompleteFragment?
        PlaceAutocompleteFragment.newInstance(options!!.searchPlaceOption()).also { var1 = it }
            .setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(var1: ELocation) {
                    if (var1 != null) {
                        this@SelectLocationFromMapFragment.bottomSheet?.setPlaceDetails(null as Place?)
                        isCurrentLocation = false
                        if (var1.latitude == null && var1.longitude == null) {
                            this@SelectLocationFromMapFragment.mapmyIndiaMap?.moveCamera(
                                CameraELocUpdateFactory.newELocZoom(var1.poiId, 14.0)
                            )
                        } else {
                            this@SelectLocationFromMapFragment.mapmyIndiaMap?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(var1.latitude.toDouble(), var1.longitude.toDouble()),
                                    14.0
                                )
                            )
                        }
                       includeReverseGeocode = false
                        this@SelectLocationFromMapFragment.viewModel!!.place = Place()
                        this@SelectLocationFromMapFragment.viewModel!!.place.setLat(var1.latitude)
                        this@SelectLocationFromMapFragment.viewModel!!.place.setLng(var1.longitude)
                        this@SelectLocationFromMapFragment.viewModel!!.place.setFormattedAddress(b.a(var1))
                        this@SelectLocationFromMapFragment.viewModel!!.place.setPlaceId(var1.poiId)
                        if (var1.addressTokens != null) {
                            this@SelectLocationFromMapFragment.viewModel!!.place.setCity(var1.addressTokens.city)
                            this@SelectLocationFromMapFragment.viewModel!!.place.setDistrict(var1.addressTokens.district)
                            this@SelectLocationFromMapFragment.viewModel!!.place.setHouseName(var1.addressTokens.houseName)
                            this@SelectLocationFromMapFragment.viewModel!!.place.setHouseNumber(var1.addressTokens.houseNumber)
                            this@SelectLocationFromMapFragment.viewModel!!.place.setLocality(var1.addressTokens.locality)
                            this@SelectLocationFromMapFragment.viewModel!!.place.setPincode(var1.addressTokens.pincode)
                            this@SelectLocationFromMapFragment.viewModel!!.place.setPoi(var1.addressTokens.poi)
                            this@SelectLocationFromMapFragment.viewModel!!.place.setState(var1.addressTokens.state)
                            this@SelectLocationFromMapFragment.viewModel!!.place.setStreet(var1.addressTokens.street)
                            this@SelectLocationFromMapFragment.viewModel!!.place.setSubDistrict(var1.addressTokens.subDistrict)
                            this@SelectLocationFromMapFragment.viewModel!!.place.setSubLocality(var1.addressTokens.subLocality)
                           viewModel!!.place.setSubSubLocality(var1.addressTokens.subSubLocality)
                            this@SelectLocationFromMapFragment.viewModel!!.place.setVillage(var1.addressTokens.village)
                        }
                        var var2: SelectLocationFromMapFragment
                        this@SelectLocationFromMapFragment.also {
                            var2 = it
                        }.bottomSheet?.setPlaceDetails(var2.viewModel?.place)
                    }
                  requireActivity().getSupportFragmentManager().popBackStack(
                        PlaceAutocompleteFragment::class.java.simpleName, 1
                    )
                    binding.searchBar.visibility=View.VISIBLE
                    binding.mapmyindiaPluginsImageViewMarker.visibility=View.VISIBLE
                    binding.mapmyindiaPluginsImageViewShadow.visibility=View.VISIBLE
                    requireActivity().window.setStatusBarColor(ContextCompat.getColor(requireContext(),R.color.purple_700));
                }

                override fun onCancel() {
                    requireActivity().getSupportFragmentManager().popBackStack(
                        PlaceAutocompleteFragment::class.java.simpleName, 1
                    )
                    binding.searchBar.visibility=View.VISIBLE
                    binding.mapmyindiaPluginsImageViewMarker.visibility=View.VISIBLE
                    binding.mapmyindiaPluginsImageViewShadow.visibility=View.VISIBLE
                    requireActivity().window.setStatusBarColor(ContextCompat.getColor(requireContext(),R.color.purple_700));

                }
            })
         requireActivity().getSupportFragmentManager().beginTransaction().add(
            R.id.mapmyindia_picker_fragment_container, var1!!,
            PlaceAutocompleteFragment::class.java.simpleName
        ).addToBackStack(
            PlaceAutocompleteFragment::class.java.simpleName
        ).commit()
        binding.searchBar.visibility=View.GONE
        binding.mapmyindiaPluginsImageViewMarker.visibility=View.GONE
        binding.mapmyindiaPluginsImageViewShadow.visibility=View.GONE
        requireActivity().window.setStatusBarColor(ContextCompat.getColor(requireContext(),R.color.purple_700));

    }

    private fun addBackButtonListener() {
      // binding.searchBar.setOnClickListener { requireActivity().finish() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectLocationFromMapBinding.inflate(inflater, container, false)
        binding.mapView.onCreate(savedInstanceState)
        requireActivity().window.setStatusBarColor(ContextCompat.getColor(requireContext(),R.color.purple_700));

      // val intent = PlacePicker.IntentBuilder().placeOptions(PlacePickerOptions.builder().build()).build(requireActivity())
       // startActivityForResult(intent, 101)

        if (savedInstanceState == null) {
            options = requireActivity().getIntent().getParcelableExtra<Parcelable>("com.mapmyindia.sdk.plugins.places.pickerOptions") as PlacePickerOptions?
        } else {
            options =
                savedInstanceState.getParcelable<Parcelable>("com.mapmyindia.sdk.plugins.places.pickerOptions") as PlacePickerOptions?
        }

        if (options == null) {
            options = PlacePickerOptions.builder().build()
        }
        viewModel = ViewModelProvider(this).get(PlacePickerViewModel::class.java)
        viewModel!!.getResults().observe(viewLifecycleOwner, this)
        this.bindViews()
        addSearchAutocomplete()
        addBackButtonListener()
       this.addPlaceSelectedButton()
      //  this.customizeViews()
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(this)

        return binding.root
    }
    private fun bindViews() {
        mapView =binding.mapView
        bottomSheet =binding.mapmyindiaPluginsPickerBottomSheet.findViewById(R.id.mapmyindia_plugins_picker_bottom_sheet)
        markerImage = binding.mapmyindiaPluginsImageViewMarker
        userLocationButton =binding.mapmyindiaPluginsPickerBottomSheet.findViewById(R.id.user_location_button)
        if (options!!.includeSearch()) {
            binding.searchBar!!.visibility = View.VISIBLE
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreference = Preference(requireContext())
        navController = Navigation.findNavController(view)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
       /* if(requestCode == 101 && resultCode == Activity.RESULT_OK) {
            val place: Place? = PlacePicker.getPlace(data!!)
            navController?.popBackStack()
        }*/
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == Constants.GPS_REQUEST) {
                isGPS = true // flag maintain before get location
                Toast.makeText(requireContext(),"permission granted",1)
                userLocationButton?.performClick()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SelectLocationFromMapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun bindListeners() {
        this.mapmyIndiaMap?.addOnCameraMoveStartedListener(this)
        this.mapmyIndiaMap?.addOnCameraIdleListener(this)
    }


    override fun onMapReady(var1: MapmyIndiaMap) {
        mapmyIndiaMap = var1
        var var2: PlacePickerOptions
        if (options.also { var2 = it!! } != null) {
            var1.setMaxZoomPreference(var2.mapMaxZoom())
            var1.setMinZoomPreference(options!!.mapMinZoom())
        }
        val var10000 = var1
        val var3: OnStyleLoaded
        var3 = OnStyleLoaded { var1 ->
            this@SelectLocationFromMapFragment.adjustCameraBasedOnOptions()
            if (this@SelectLocationFromMapFragment.options != null && options!!.includeDeviceLocationButton()) {
                this@SelectLocationFromMapFragment.enableLocationComponent(var1)
            } else {
                this@SelectLocationFromMapFragment.userLocationButton?.hide()
            }
            this@SelectLocationFromMapFragment.bindListeners()
        }
        var10000.getStyle(var3)
    }

    override fun onMapError(var1: Int, var2: String?) {}

    override fun onCameraMoveStarted(var1: Int) {
        if (markerImage!!.translationY == 0.0f) {
            markerImage!!.animate().translationY(-75.0f).setInterpolator(OvershootInterpolator())
                .setDuration(250L).start()
            val var2: Boolean
            var2 = if (var1 == 1) {
                true
            } else {
                false
            }
            includeReverseGeocode = var2
            if (var2) {
                viewModel!!.place = null
                if (bottomSheet!!.b()) {
                    bottomSheet!!.a()
                }
            }
        }
    }

    override fun onCameraIdle() {
        markerImage!!.animate().translationY(0.0f).setInterpolator(OvershootInterpolator())
            .setDuration(250L).start()
        if (mapmyIndiaMap!!.cameraPosition.zoom < 14.0) {
            mapmyIndiaMap!!.animateCamera(CameraUpdateFactory.zoomTo(14.0))
        } else {
            if (includeReverseGeocode) {
                bottomSheet!!.setPlaceDetails(null as Place?)
                viewModel!!.place = null
                this.makeReverseGeocodingSearch()
            }
        }
    }
    override fun onChanged(var1: d<Place>?) {
        if (var1!!.a == e.c) {
            viewModel!!.place = Place()
            val var10000 = viewModel!!.place
            val var10001 = Locale.US
            val var2: Array<Any?>
            var2 = arrayOfNulls(2)
            var2[0] = mapmyIndiaMap!!.cameraPosition.target.latitude
            var2[1] = mapmyIndiaMap!!.cameraPosition.target.longitude
            var10000.formattedAddress = String.format(var10001, "[%f, %f]", *var2)
        } else {
            viewModel!!.place = var1.b
        }
        bottomSheet!!.setPlaceDetails(viewModel!!.place)
    }

    override fun onExplanationNeeded(p0: MutableList<String>?) {
        Toast.makeText(
            requireContext(),
            "",
            1
        ).show()
    }


    override fun onPermissionResult(var1: Boolean) {
        if (var1) {
            mapmyIndiaMap!!.getStyle { var1 ->
                this@SelectLocationFromMapFragment.enableLocationComponent(
                    var1
                )
            }
        }
    }

    override fun onCancel() {
        viewModel!!.place = null
        var var1: CurrentPlaceSelectionBottomSheet
        if (bottomSheet.also { var1 = it!! } != null) {
            var1.setPlaceDetails(null as Place?)
        }
        makeReverseGeocodingSearch()
    }

    override fun onFinish() {
        viewModel!!.place = null
        var var1: CurrentPlaceSelectionBottomSheet
        if (bottomSheet.also { var1 = it!! } != null) {
            var1.setPlaceDetails(null as Place?)
        }
        makeReverseGeocodingSearch()
    }


    override fun onSuccess(var1: LocationEngineResult?) {
        if (var1 != null && isCurrentLocation && isLocationEnabled()) {
            val var6 = var1.lastLocation
            if (bottomSheet!!.b()) {
                bottomSheet!!.a()
            }
            isCurrentLocation = false
            val var10000 = mapmyIndiaMap
            val var2 = var6?.latitude
            val var4 = var6?.longitude
            var10000!!.animateCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(var2!!, var4!!), 16.0),
                this
            )
        }
    }

    override fun onFailure(p0: Exception) {
    }

    private fun makeReverseGeocodingSearch() {
        val var1 = mapmyIndiaMap!!.cameraPosition.target
        if (a.b(requireContext())) {
            if (var1 != null) {
                viewModel!!.reverseGeocode(Point.fromLngLat(var1.longitude, var1.latitude))
            }
        } else {
            Snackbar.make(
                bottomSheet!!,
                "check your conection",
                0
            ).show()
        }
    }

    private fun adjustCameraBasedOnOptions() {
        var var1: PlacePickerOptions
        if (options.also { var1 = it!! } != null) {
            if (var1.startingBounds() != null) {
                if (bottomSheet!!.b()) {
                    bottomSheet!!.a()
                }
                isCurrentLocation = false
                mapmyIndiaMap!!.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        options!!.startingBounds()!!, 0
                    ), this
                )
            } else if (options!!.statingCameraPosition() != null) {
                if (bottomSheet!!.b()) {
                    bottomSheet!!.a()
                }
                isCurrentLocation = false
                mapmyIndiaMap!!.moveCamera(
                    CameraUpdateFactory.newCameraPosition(
                        options!!.statingCameraPosition()!!
                    ), this
                )
            } else if (options!!.startingELocBounds() != null) {
                if (bottomSheet!!.b()) {
                    bottomSheet!!.a()
                }
                isCurrentLocation = false
                mapmyIndiaMap!!.moveCamera(
                    CameraELocUpdateFactory.newELocBounds(
                        options!!.startingELocBounds()!!,
                        0
                    ), this
                )
            } else if (options!!.startingELocPosition() != null) {
                if (bottomSheet!!.b()) {
                    bottomSheet!!.a()
                }
                isCurrentLocation = false
                mapmyIndiaMap!!.moveCamera(
                    CameraELocUpdateFactory.newELocZoom(
                        options!!.startingELocPosition()!!,
                        16.0
                    ), this
                )
            }
        }
    }
    private fun enableLocationComponent(var1: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(requireActivity())) {
            val var2: LocationComponent
            var2 = mapmyIndiaMap!!.locationComponent
            var2.activateLocationComponent(
                LocationComponentActivationOptions.builder(requireActivity(), var1).build()
            )
            var2.isLocationComponentEnabled = true
            locationEngine = var2.locationEngine
            LocationEngineRequest.Builder(1000L).setPriority(0).setMaxWaitTime(5000L).build()
            locationEngine!!.getLastLocation(this)
            var2.cameraMode = 8
            var2.renderMode = 18
            this.addUserLocationButton()
        } else {
            permissionsManager =  PermissionsManager(this)
            permissionsManager!!.requestLocationPermissions(requireActivity())
        }
    }
    private fun addUserLocationButton() {
        userLocationButton!!.show()
        userLocationButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(var1: View) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        "android.permission.ACCESS_FINE_LOCATION"
                    ) == 0 || ActivityCompat.checkSelfPermission(
                        requireContext(),
                        "android.permission.ACCESS_COARSE_LOCATION"
                    ) == 0
                ) {
                    if (this@SelectLocationFromMapFragment.mapmyIndiaMap?.getLocationComponent()
                        ?.getLastKnownLocation() != null
                    ) {
                        val var6: Location =
                            this@SelectLocationFromMapFragment.mapmyIndiaMap?.getLocationComponent()
                                ?.getLastKnownLocation()!!
                        if (this@SelectLocationFromMapFragment.bottomSheet?.b() == true) {
                            this@SelectLocationFromMapFragment.bottomSheet!!.a()
                        }
                        this@SelectLocationFromMapFragment.isCurrentLocation = false
                        val var10000: MapmyIndiaMap? = this@SelectLocationFromMapFragment.mapmyIndiaMap
                       // undefinedtype > var10001 = this
                        val var10002 = CameraPosition.Builder()
                        val var4 = var6.latitude
                        val var2 = var6.longitude
                        val var5 = CameraUpdateFactory.newCameraPosition(
                            var10002.target(
                                LatLng(
                                    var4,
                                    var2
                                )
                            ).zoom(17.5).build()
                        )
                        val var7: SelectLocationFromMapFragment = this@SelectLocationFromMapFragment
                        var10000?.animateCamera(var5, 1400, var7)
                    } else {
                        val var8: SelectLocationFromMapFragment = this@SelectLocationFromMapFragment
                        Toast.makeText(
                            requireContext(),
                            "place_picker_user_location_not_found",
                            0
                        ).show()
                    }
                }
            }
        })
    }
    private fun addPlaceSelectedButton() {
        var var1: View
        var1=binding.cvPlaceChoosen
       binding.cvPlaceChoosen.setOnClickListener(
            View.OnClickListener {
                if (this@SelectLocationFromMapFragment.viewModel?.place == null) {
                    Snackbar.make(
                        bottomSheet!!,
                       "(mapmyindia_plugins_place_picker_not_valid_selection)",
                        0
                    ).show()
                } else {
                    this@SelectLocationFromMapFragment.placeSelected()
                }
            })
        var var2: PlacePickerOptions
        if (options.also { var2 = it!! } != null) {
            /*if (var2.pickerButtonBackgroundColor() != null) {
                var1.setBackgroundColor(options!!.pickerButtonBackgroundColor()!!)
            } else {
                var1.setBackgroundResource(options!!.pickerButtonBackgroundResource())
            }*/
            binding.placeChosenButton.setTextColor(options!!.pickerButtonTextColor())
          //  binding.placeChosenButton.text = options!!.pickerButtonText()
        }
    }
    fun placeSelected() {
        val var1: Intent
        var1 = Intent()
        var1.putExtra(
            "com.mapmyindia.sdk.plugins.places.place",
            Gson().toJson(viewModel!!.place)
        )
        var1.putExtra(
            "com.mapmyindia.sdk.plugins.places.cameraPosition",
            mapmyIndiaMap!!.cameraPosition
        )
        requireActivity().setResult(-1, var1)
        //requireActivity().finish()

        val sheet = AddressBottomSheetFragment()

            sheet.setFragmentData(null,viewModel!!.place, false)

        activity?.supportFragmentManager?.let { it1 ->
            sheet.show(
                it1, "AddressBottomSheetFragment"
            )
        }
    }


}