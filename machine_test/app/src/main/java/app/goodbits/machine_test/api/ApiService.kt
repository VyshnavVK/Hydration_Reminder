package com.tailormade.api


import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("state_district_wise.json")
    suspend fun stateWise(
    ): Response<JSONObject>


}