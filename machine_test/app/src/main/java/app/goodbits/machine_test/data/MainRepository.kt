package com.tailormade.data

import android.content.Context
import app.goodbits.machine_test.api.ApiHelper
import com.tailormade.api.NetWorkCall
import org.json.JSONObject

class MainRepository {

suspend fun stateWise(
    context: Context
    ):NetWorkCall<JSONObject> =
    ApiHelper.safeApiCall(context = context,
    handleLoading = true,
    call = {
        ApiHelper.apiService.stateWise()
    })
}
