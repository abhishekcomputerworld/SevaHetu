package com.ritualkart.sevahetu.utiliy;

import android.content.Context;
import android.content.SharedPreferences;

import com.ritualkart.sevahetu.model.SelectedProductItems;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class javapref {
    javapref(Context  context){
        this.context=context;
    }
    private String PREFS_NAME = "SEVAHETU";
    private Context context;
    private SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);




    public void removeSelectedProductItemModel(String uniqueId) {
        Map<String,SelectedProductItems> outputMap = loadMap();
        outputMap.remove(uniqueId);
        if (sharedPref != null){
            JSONObject jsonObject = new JSONObject(outputMap);
            String jsonString = jsonObject.toString();
            sharedPref.edit()
                    .remove("My_map")
                    .putString("My_map", jsonString)
                    .apply();
        }
    }

    public void  addSelectedProductItemModel( String uniqueId,SelectedProductItems selectedProductItems ) {
        Map<String,SelectedProductItems> outputMap = loadMap();
        outputMap.put(uniqueId,selectedProductItems);
        if (sharedPref != null){
            JSONObject jsonObject = new JSONObject(outputMap);
            String jsonString = jsonObject.toString();
            sharedPref.edit()
                    .remove("My_map")
                    .putString("My_map", jsonString)
                    .apply();
        }
    }

    public  Boolean  packageAvailable(String uniqueId) {
         HashMap<String,SelectedProductItems> myStrings =new HashMap(loadMap());
        return myStrings.containsKey(uniqueId);
    }

    public   SelectedProductItems getSelectedProductItemModel(String uniqueId) {
              return loadMap().get(uniqueId);
    }

    private Map<String,SelectedProductItems> loadMap() {
        Map<String,SelectedProductItems> outputMap = new HashMap<>();
        SharedPreferences pSharedPref = context.getSharedPreferences("MyVariables", Context.MODE_PRIVATE);
        try {
            if (pSharedPref != null) {
                String jsonString = pSharedPref.getString("My_map", (new JSONObject()).toString());
                if (jsonString != null) {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Iterator<String> keysItr = jsonObject.keys();
                    while (keysItr.hasNext()) {
                        String key = keysItr.next();
                        SelectedProductItems value = (SelectedProductItems) jsonObject.get(key);
                        outputMap.put(key, value);
                    }
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return outputMap;
    }
}
