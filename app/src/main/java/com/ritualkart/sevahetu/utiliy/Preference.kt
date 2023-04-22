package com.ritualkart.sevahetu.utiliy

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ritualkart.sevahetu.model.Place
import com.ritualkart.sevahetu.model.SelectedProductItems
import com.ritualkart.sevahetu.model.response.ProfileResponse
import org.checkerframework.checker.units.qual.A
import org.json.JSONException
import org.json.JSONObject

class Preference(val context: Context) {

    private val PREFS_NAME = "SEVAHETU"
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        const val mobileNumber = "mobile_number"
        const val address = "address"
        const val selectedProductItem = "selectedProductItem"
        const val profileData = "profileData"

    }


    fun saveMobileNumber(mobile: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(mobileNumber, mobile)
        editor.apply()
    }

    fun getMobileNumber(): String? {
        return sharedPref.getString(mobileNumber, "")
    }

    fun saveAddress(place: Place) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        val gson = Gson()
        val json = gson.toJson(place)
        editor.putString(address, json)
        editor.apply()
    }

    fun getAddress(): Place? {
        val gson = Gson()
        val json = sharedPref.getString(address, "")
        return gson.fromJson(json, Place::class.java)
    }

    /* fun saveProfileModel(profileModel : ProfileData) {
         val editor: SharedPreferences.Editor = sharedPref.edit()
         val gson = Gson()
         val json = gson.toJson(profileModel)
         editor.putString(profileData, json)
         editor.apply()
     }

     fun getProfileModel(): ProfileData? {
         val gson = Gson()
         val json = sharedPref.getString(profileData, "")
         return gson.fromJson(json, ProfileData::class.java)
     }*/


    fun removeSelectedProductItemModel(uniqueId: String?) {
        val map = loadMap()
        map.remove(uniqueId)
        val gson = Gson()
        val hashMapString = gson.toJson(map)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(selectedProductItem, hashMapString)
        editor.apply()
    }

    fun addSelectedProductItemModel(
        uniqueId: String,
        selectedProductItems: SelectedProductItems
    ) {
        val outputMap = loadMap()
        outputMap.put(uniqueId, selectedProductItems)
        val gson = Gson()
        val hashMapString = gson.toJson(outputMap)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString("selectedProductItem", hashMapString)
        editor.apply()
    }

    fun packageAvailable(uniqueId: String?): Boolean {
        val myStrings: HashMap<String?, SelectedProductItems?> =
            loadMap() as HashMap<String?, SelectedProductItems?>
        return myStrings.containsKey(uniqueId)
    }

    fun getSelectedProductItemModel(uniqueId: String?): SelectedProductItems? {
        return loadMap()?.get(uniqueId)
    }

     fun loadMap(): MutableMap<String?, SelectedProductItems?> {
        /*val outputMap: MutableMap<String?, SelectedProductItems?> = HashMap()
         try {
             if (sharedPref != null) {
                 val jsonString = sharedPref.getString(selectedProductItem, JSONObject().toString())
                 if (jsonString != null) {
                     val jsonObject = JSONObject(jsonString)
                     val keysItr = jsonObject.keys()
                     while (keysItr.hasNext()) {
                         val key = keysItr.next()
                         val value = jsonObject[key] as SelectedProductItems
                         outputMap[key] = value
                     }
                 }
             }
         } catch (e: JSONException) {
             e.printStackTrace()
         }
         return outputMap*/

        val gson = Gson()
//         if(){
//
//         }
        val storedHashMapString: String? = sharedPref.getString(selectedProductItem, "")
        val type = object : TypeToken<HashMap<String?, SelectedProductItems?>?>() {}.type
        var testHashMap2:  HashMap<String?, SelectedProductItems?>?  =
            gson.fromJson(storedHashMapString, type)
         if(testHashMap2.isNullOrEmpty()){
             testHashMap2 = HashMap()
         }
        return testHashMap2

    }

     fun getCartData(): List<SelectedProductItems> {
          var list : ArrayList<SelectedProductItems> = ArrayList()
          for(map in loadMap()){
              map.value?.let { list.add(it) }
         }
         return list

    }
  /*  fun getAddedPackageListSize(): Int {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        val myStrings: MutableSet<String> =
            HashSet(sharedPref.getStringSet(selectedProductItem, HashSet()))
        return myStrings.size
    }

    fun addedPackageList(uniqueId: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        val myStrings: MutableSet<String> =
            HashSet(sharedPref.getStringSet(selectedProductItem, HashSet()))
        myStrings.add(uniqueId)
        editor.putStringSet(selectedProductItem, myStrings)
        editor.apply()
    }

    fun removeFromPackageList(uniqueId: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        val myStrings: MutableSet<String> =
            HashSet(sharedPref.getStringSet(selectedProductItem, HashSet()))
        myStrings.remove(uniqueId)
        editor.putStringSet(selectedProductItem, myStrings)
        editor.apply()
    }

    fun clearPackageList() {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.remove(selectedProductItem)
        editor.apply()
    }


    fun packageAvailable(uniqueId: String): Boolean {
        val myStrings: Set<String> =
            HashSet(sharedPref.getStringSet(selectedProductItem, HashSet()))
        return myStrings.contains(uniqueId)
    }
*/


    fun saveProfileModel(profileModel : ProfileResponse) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        val gson = Gson()
        val json = gson.toJson(profileModel)
        editor.putString(profileData, json)
        editor.apply()
    }

    fun getProfileModel(): ProfileResponse? {
        val gson = Gson()
        val json = sharedPref.getString(profileData, "")
        return gson.fromJson(json, ProfileResponse::class.java)
    }


    fun logoutUser() {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.remove(mobileNumber)
        editor.remove(address)
        editor.remove(selectedProductItem)
        editor.remove(profileData)
        editor.apply()
    }
}