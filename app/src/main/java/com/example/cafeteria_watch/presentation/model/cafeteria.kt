package com.example.cafeteria_watch.presentation.model

import com.google.gson.annotations.SerializedName

data class GetDataInfo(
    @SerializedName("mealServiceDietInfo") val mealServiceDietInfo: List<GetDataHeadAndRow?>?
)
data class GetDataHeadAndRow(
    @SerializedName("head") val head: List<GetHeadState?>?,
    @SerializedName("row") val row: List<GetDataRow?>?,
)
data class GetHeadState(
    @SerializedName("list_total_count") val list_total_count: Int,
    @SerializedName("RESULT") val result: GetStateMessage?,
)
data class GetStateMessage(
    @SerializedName("CODE") val code: String,
    @SerializedName("MESSAGE") val message: String,
)
data class GetDataRow(
    @SerializedName("ATPT_OFCDC_SC_CODE") val atpt_ofcdc_sc_code : String,
    @SerializedName("ATPT_OFCDC_SC_NM") val atpt_ofcdc_sc_nm : String,
    @SerializedName("SD_SCHUL_CODE") val sd_schul_code: String,
    @SerializedName("SCHUL_NM") val schul_nm: String,
    @SerializedName("MMEAL_SC_CODE") val mmeal_sc_code: String,
    @SerializedName("MMEAL_SC_NM") val mmeal_sc_nm: String,
    @SerializedName("MLSV_YMD") val date: String,
    @SerializedName("MLSV_FGR") val personnel: String,
    @SerializedName("DDISH_NM") val foodName: String,
    @SerializedName("ORPLC_INFO") val original: String,
    @SerializedName("CAL_INFO") val cal_info: String,
    @SerializedName("NTR_INFO") val ntr_info: String,
    @SerializedName("MLSV_FROM_YMD") val startDate: String,
    @SerializedName("MLSV_TO_YMD") val endDate: String,
)