package com.barisaslankan.beerapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface BeerApi {

    @GET("beers")
    suspend fun getBeers(
        @Query("page") page : Int,
        @Query("per_page") pageCount : Int
    ) : List<BeerDto>

}