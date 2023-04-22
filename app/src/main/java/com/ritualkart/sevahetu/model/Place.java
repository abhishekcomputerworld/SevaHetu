package com.ritualkart.sevahetu.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Place {

    private String houseNumber;

    private String houseName;

    private String poi;

    private String poiDist;

    private String street;

    private String streetDist;

    private String subSubLocality;

    private String subLocality;

    private String locality;

    private String village;

    private String district;

    private String subDistrict;

    private String city;

    private String state;

    private String pincode;

    private String lat;

    private String lng;
    private String area;

    private String formattedAddress;

    private String placeId;

    public Place() {
    }

    public String getHouseNumber() {
        return this.houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getHouseName() {
        return this.houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getPoi() {
        return this.poi;
    }

    public void setPoi(String poi) {
        this.poi = poi;
    }

    public String getPoiDist() {
        return this.poiDist;
    }

    public void setPoiDist(String poiDist) {
        this.poiDist = poiDist;
    }

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetDist() {
        return this.streetDist;
    }

    public void setStreetDist(String streetDist) {
        this.streetDist = streetDist;
    }

    public String getSubSubLocality() {
        return this.subSubLocality;
    }

    public void setSubSubLocality(String subSubLocality) {
        this.subSubLocality = subSubLocality;
    }

    public String getSubLocality() {
        return this.subLocality;
    }

    public void setSubLocality(String subLocality) {
        this.subLocality = subLocality;
    }

    public String getLocality() {
        return this.locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getVillage() {
        return this.village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getDistrict() {
        return this.district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getSubDistrict() {
        return this.subDistrict;
    }

    public void setSubDistrict(String subDistrict) {
        this.subDistrict = subDistrict;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return this.pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLat() {
        return this.lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return this.lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getArea() {
        return this.area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFormattedAddress() {
        return this.formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getPlaceId() {
        return this.placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
