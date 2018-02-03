package com.udala

/**
 * Created by apjoe on 12/2/2017.
 */


data class State (
        var id : String,
        var country_id : String,
        var name : String
){
    override fun toString(): String {
        return name
    }
}

data class City (
        var id : String,
        var state_id : String,
        var name : String
){
    override fun toString(): String {
        return name
    }
}

data class Store (
        var id : String,
        var user_id : String,
        var attendant : String,
        var area_id : String,
        var line_of_business_id : String,
        var is_warehouse : Int,
        var phone : String,
        var name : String,
        var is_active : Int
){
    override fun toString(): String {
        return name
    }
}

data class Warehouse (
        var id : String,
        var user_id : String,
        var attendant : String,
        var area_id : String,
        var line_of_business_id : String,
        var is_warehouse : Int,
        var phone : String,
        var name : String,
        var is_active : Int
){
    override fun toString(): String {
        return name
    }
}

data class LoB (
        var id : String,
        var name : String
){
    override fun toString(): String {
        return name
    }
}


data class UserData (
        var id : String,
        var first_name : String,
        var last_name : String,
        var email : String,
        var phone : String,
        var company : String,
        var parent : String?,
        var area_id : String,
        var is_admin : Int,
        var token : String
)

data class Attendant (
        var id : String,
        var first_name : String,
        var last_name : String,
        var email : String,
        var phone : String,
        var company : String,
        var parent : String?
){
    override fun toString(): String {
        return first_name + " " +last_name
    }
}