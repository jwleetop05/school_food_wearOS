/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.cafeteria_watch.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.material.*
import com.example.cafeteria_watch.presentation.api.getJSONCafeteria
import com.example.cafeteria_watch.presentation.model.GetDataInfo
import com.example.cafeteria_watch.presentation.theme.Cafeteria_watchTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp(lifecycleScope)
        }
    }
}

@Composable
fun WearApp(lifecycleCoroutineScope: LifecycleCoroutineScope) {
    Cafeteria_watchTheme {
        /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
         * version of LazyColumn for wear devices with some added features. For more information,
         * see d.android.com/wear/compose.
         */
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            toDayAndNextDayCafeteria(lifecycleCoroutineScope)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun toDayAndNextDayCafeteria(lifecycleCoroutineScope: LifecycleCoroutineScope) {
    val list = remember {
        mutableStateListOf<GetDataInfo>()
    }
    val scope = MainScope()
    val scaffoldState = rememberScaffoldState()
    val pagerState = rememberPagerState()
    val menus = remember{
        mutableStateListOf<List<String>?>()
    }
    DisposableEffect(key1 = 0) {
        scope.launch {
            while (true) {
                val nowDate: String = "yyyyMMdd".getTimenow();
                val addDay: String = twoAddDay()
                Log.d("time", "${nowDate} / $addDay")
                getJSONCafeteria(list, nowDate, addDay);
                delay(5000)
            }
        }
        onDispose {
            scope.cancel()
        }
    }

    Scaffold(scaffoldState = scaffoldState, modifier = Modifier.background(color = Color.White)) { it ->
            ScalingLazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                try {
                    if(list.isEmpty()) {
                        item { Text(text = "데이터를 불러오는 중입니다.",color=Color.Black) }
                    } else {
                        items(list.toList()) { dataInfo: GetDataInfo ->
                            if (dataInfo.mealServiceDietInfo.isNullOrEmpty() || dataInfo.mealServiceDietInfo[0]?.head.isNullOrEmpty()) {
                                Text(text = "데이터를 불러오지 못했습니다.",color=Color.Black)
                            } else {
                                dataInfo.mealServiceDietInfo[0]?.head?.map { getHeadState ->
                                    val TrueHead =
                                        dataInfo.mealServiceDietInfo[0]?.head?.get(1)?.result;
                                    if (getHeadState != null) {
                                        if (TrueHead != null) {
                                            val headType = TrueHead.code.split('-')[0]
                                            val headCode = TrueHead.code.split('-')[1]
                                            Log.d("헤드 타입, 코드", "$headType / $headCode")
                                            when (headType) {
                                                "INFO" -> when (headCode) {
                                                    "000" -> {
                                                        if (dataInfo.mealServiceDietInfo[1]?.row.isNullOrEmpty()) {
                                                            Text(text = "data body를 찾지 못했습니다.",color=Color.Black)
                                                        } else {
                                                            dataInfo.mealServiceDietInfo[1]?.row?.let { data ->
                                                                Column() {
                                                                    Column(
                                                                        Modifier
                                                                            .fillMaxWidth()
                                                                            .height(50.dp)) {
                                                                        Text(text = "${data[pagerState.currentPage]?.startDate} 급식",color=Color.Black)
                                                                        Text(text = "${data[pagerState.currentPage]?.cal_info}", color=Color.Black)
                                                                    }
                                                                    HorizontalPager(
                                                                        modifier = Modifier.fillMaxSize(),
                                                                        count = data.size,
                                                                        verticalAlignment = Alignment.Top,
                                                                        state = pagerState
                                                                    ) {
                                                                        page(data[pagerState.currentPage]?.foodName?.split("<br/>"))
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    "200" -> {
                                                        Text(text = "해당하는 데이터가 없습니다.",color=Color.Black)
                                                    }
                                                    else -> Text(text = "데이터를 불러오지 못했습니다.",color=Color.Black)

                                                }
                                                "ERROR" -> when (headCode) {
                                                    "337" -> Text(text = "일별 트래픽 제한을 넘은 호출입니다. 오늘은 더이상 호출할 수 없습니다.",color=Color.Black)
                                                    "500" -> Text(text = "서버 오류입니다. 지속적으로 발생시 홈페이지로 문의(Q&A) 바랍니다.",color=Color.Black)
                                                    "600" -> Text(text = "데이터베이스 연결 오류입니다. 지속적으로 발생시 홈페이지로 문의(Q&A) 바랍니다.",color=Color.Black)
                                                    else -> Text(text = "데이터를 불러오지 못했습니다.",color=Color.Black)
                                                }
                                                else -> Text(text = "데이터를 불러오지 못했습니다.",color=Color.Black)
                                            }
                                        } else {

                                        }
                                    } else {
                                        Text("데이터를 불러오지 못했습니다." ,color=Color.Black)
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.d("익셉션", e.message.toString())
                }
                item {
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ){
                        HorizontalPagerIndicator(
                            pagerState = pagerState,
                            Modifier.padding(5.dp)
                        )
                    }
                }
            }
    }
}
@Composable
fun page(
    foodInfo: List<String>?, ) {
    return Box(
        Modifier
            .fillMaxSize()

            .background(
                color = Color.White
            )) {
        Column() {
            foodInfo?.mapIndexed { idx, it ->
                Text(text = it, color = Color.Black)
            }
        }
    }
}
fun String.getTimenow(): String {
    return try {
        val date = Date(System.currentTimeMillis())
        val dateFormat = SimpleDateFormat(this)
        dateFormat.format(date)
    } catch (e: Exception) {
        Log.d("Exception : ", e.message.toString())
        ""
    }
}
fun twoAddDay():String {
    return try {
        val cal = Calendar.getInstance()
        cal.time = Date(System.currentTimeMillis())
        val dateFormat = SimpleDateFormat("yyyyMMdd")
        cal.add(Calendar.DATE, 2)
        dateFormat.format(cal.time)
    } catch (e: Exception) {
        Log.d("Exception : ", e.message.toString())
        ""
    }
}