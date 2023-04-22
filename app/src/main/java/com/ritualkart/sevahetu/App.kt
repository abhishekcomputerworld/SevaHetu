package com.ritualkart.sevahetu

import android.app.Application
import com.mapmyindia.sdk.maps.MapmyIndia
import com.mmi.services.account.MapmyIndiaAccountManager

class App :Application() {


    override fun onCreate() {
        super.onCreate()
        MapmyIndiaAccountManager.getInstance().restAPIKey = getRestAPIKey()
        MapmyIndiaAccountManager.getInstance().mapSDKKey = getMapSDKKey()
        MapmyIndiaAccountManager.getInstance().atlasClientId = getAtlasClientId()
        MapmyIndiaAccountManager.getInstance().atlasClientSecret = getAtlasClientSecret()
        MapmyIndia.getInstance(this)
    }


    fun getAtlasClientId(): String? {
        return "33OkryzDZsKamfiPiKFdn3JedW3I1sO_efflBdDslqsmTSZmsD2SPw4DU3SPC459TYVRlA4DUCLLpIoO5jAOJQ=="
    }

    fun getAtlasClientSecret(): String? {
        return "lrFxI-iSEg8wS11NUoDEndR2zcztS6p73mr5dC4bfgeS1SSbfOhj2SKP-Vh0gbPinqh-w3f66JbgvjrDuq5OdXMZYOVLEuEG"
    }

    fun getMapSDKKey(): String? {
        return "4e1801731a2e64986f7af64debbfede3"
    }

    fun getRestAPIKey(): String? {
        return "4e1801731a2e64986f7af64debbfede3"
    }
}