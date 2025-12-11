package com.placeautocompletepicker

import android.app.Activity
import android.content.Intent
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.BaseActivityEventListener
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

@ReactModule(name = PlaceAutocompletePickerModule.NAME)
class PlaceAutocompletePickerModule(reactContext: ReactApplicationContext) :
  NativePlaceAutocompletePickerSpec(reactContext) {

  private var _promise: Promise? = null

  override fun getName(): String {
    return NAME
  }

  override fun initialize(apiKey: String?) {
    if (!Places.isInitialized()) {
      if (apiKey != null) {
        Places.initialize(reactApplicationContext, apiKey)
      }
    }
  }

  override fun open(options: ReadableMap?, promise: Promise?) {
    _promise = promise
    val currentActivity = reactApplicationContext.currentActivity

    if (currentActivity == null) {
      promise?.reject("E_ACTIVITY_DOES_NOT_EXIST", "Activity doesn't exist")
      return
    }

    // Extract mode from options
    val mode = options?.getString("mode")
    val autocompleteMode = if (mode == "overlay") {
      AutocompleteActivityMode.OVERLAY
    } else {
      AutocompleteActivityMode.FULLSCREEN
    }

    val fields = listOf(Place.Field.ID, Place.Field.DISPLAY_NAME, Place.Field.LOCATION, Place.Field.FORMATTED_ADDRESS, Place.Field.ADDRESS_COMPONENTS)
    val intentBuilder = Autocomplete.IntentBuilder(autocompleteMode, fields)

    // Extract and apply countries filter from options
    val countries = options?.getArray("countries")
    if (countries != null && countries.size() > 0) {
      val countryList = mutableListOf<String>()
      for (i in 0 until countries.size()) {
        countries.getString(i)?.let { countryList.add(it) }
      }
      if (countryList.isNotEmpty()) {
        intentBuilder.setCountries(countryList)
      }
    }

    val intent = intentBuilder.build(currentActivity)
    currentActivity.startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
  }

  private val activityEventListener = object : BaseActivityEventListener() {
    override fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
      if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
        _promise?.let { promise ->
          when (resultCode) {
            Activity.RESULT_OK -> {
              data?.let {
                val place = Autocomplete.getPlaceFromIntent(it)
                val placeMap = Arguments.createMap().apply {
                  putString("name", place.displayName)
                  putString("address", place.formattedAddress)
                  place.location?.let { latLng ->
                    putDouble("latitude", latLng.latitude)
                    putDouble("longitude", latLng.longitude)
                  }

                  val addressComponentsArray = Arguments.createArray()
                  var extractedPostalCode: String? = null
                  place.addressComponents?.asList()?.forEach { component ->
                    val componentMap = Arguments.createMap().apply {
                      val typesArray = Arguments.createArray()
                      component.types.forEach { type -> typesArray.pushString(type) }
                      putArray("types", typesArray)
                      putString("longName", component.name)
                      putString("shortName", component.shortName)
                    }
                    addressComponentsArray.pushMap(componentMap)

                    if (component.types.contains("postal_code")) {
                      extractedPostalCode = component.name
                    }
                  }
                  putString("postalCode", extractedPostalCode)
                  putArray("addressComponents", addressComponentsArray)
                }
                promise.resolve(placeMap)
              } ?: promise.reject("E_NO_DATA", "No data was returned from the picker")
            }
            else -> {
              promise.reject("E_PICKER_CANCELLED", "Picker was cancelled or failed")
            }
          }
          _promise = null
        }
      }
    }
  }

  init {
    reactApplicationContext.addActivityEventListener(activityEventListener)
  }

  companion object {
    const val NAME = "PlaceAutocompletePicker"
    private const val AUTOCOMPLETE_REQUEST_CODE = 1
  }
}
