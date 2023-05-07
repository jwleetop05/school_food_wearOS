package com.example.cafeteria_watch.presentation.api

import com.example.cafeteria_watch.presentation.model.GetDataInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface retrofitAPI {
    @GET("mealServiceDietInfo")
    fun getCafeteria(
        @Query("key") key: String,
        @Query("type") type: String,
        @Query("pIndex") pIndex: Int,
        @Query("pSize") pSize: Int,
        @Query("ATPT_OFCDC_SC_CODE") ATPT_OFCDC_SC_CODE: String,
        @Query("SD_SCHUL_CODE") SD_SCHUL_CODE: String,
        @Query("MLSV_FROM_YMD") MLSV_FROM_YMD:String,
        @Query("MLSV_TO_YMD") MLSV_TO_YMD:String): Call<GetDataInfo>
}