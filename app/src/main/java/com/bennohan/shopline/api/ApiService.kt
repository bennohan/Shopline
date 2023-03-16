package com.bennohan.shopline.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    //login
    @FormUrlEncoded
    @POST("api/auth/login")
    suspend fun login(
        @Field("phone_number") phone: String,
        @Field("password") password: String
    ): String

    //getProfile
    @GET("api/profile")
    suspend fun getProfile() : String

    //list Product
    @GET("api/product")
    suspend fun getProduct() : String

    //list Product
    @GET("api/product/{id}")
    suspend fun getProductById(
        @Path("id") id : Int
    ) : String


    //update Profile
    @FormUrlEncoded
    @POST("api/profile")
    suspend fun updateProfile(
        @Field("name") name: String,
        @Field("phone_number") phone: String,
    ) : String

    //logout
    @POST("api/auth/logout")
    suspend fun logout() : String

    //Checkout
    @FormUrlEncoded
    @POST("api/checkout")
    suspend fun checkout(
        @Field("alamat") alamat : String,
        @Field("provinsi") provinsi : String,
        @Field("kota") kota : String,
        @Field("kecamatan") kecamatan : String,
        @Field("notes") notes : String,
    ) : String

}