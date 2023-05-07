package com.example.cafeteria_watch.presentation.api

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.cafeteria_watch.BuildConfig
import com.example.cafeteria_watch.presentation.model.GetDataInfo
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
val apiKey = BuildConfig.API_KEY
fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        }

        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    })

    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, SecureRandom())

    val sslSocketFactory = sslContext.socketFactory

    val builder = OkHttpClient.Builder()
    builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
    builder.hostnameVerifier { hostname, session -> true }

    return builder
}
fun getJSONCafeteria(cList: SnapshotStateList<GetDataInfo>, startDate: String, endDate: String) {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://open.neis.go.kr/hub/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(getUnsafeOkHttpClient().build())
        .build()

    val retrofitAPIMethod = retrofit.create(retrofitAPI::class.java)

    val call: Call<GetDataInfo> = retrofitAPIMethod.getCafeteria(
        key = apiKey,
        type = "json",
        pIndex = 1,
        pSize = 3,
        ATPT_OFCDC_SC_CODE = "B10",
        SD_SCHUL_CODE = "7010572",
        MLSV_FROM_YMD = startDate,
        MLSV_TO_YMD = endDate,
    )

    call.enqueue(object : Callback<GetDataInfo?> {
        override fun onResponse(call: Call<GetDataInfo?>, response: Response<GetDataInfo?>) {
            Log.d("아아","${response.code()}")
            if(response.isSuccessful) {
                var list: GetDataInfo = response.body()!!
                Log.d("아아","${list}")
                if(cList.isNotEmpty()) {
                    cList.clear()
                }
                cList.add(list)

            } else if(response.code() == 429){

            } else {

            }
        }

        override fun onFailure(call: Call<GetDataInfo?>, t: Throwable) {

        }
    })
}