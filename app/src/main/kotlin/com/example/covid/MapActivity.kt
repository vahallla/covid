package com.example.covid

import ListAdapter
import ListLayout
import KakaoAPI
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.covid.databinding.ActivityMapBinding
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory






open class MapActivity : AppCompatActivity() {

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK f01369257eb69e720630beba5c80b2dc"  // REST API 키
    }


    private lateinit var binding : ActivityMapBinding
    private val listItems = arrayListOf<ListLayout>()   // 리사이클러 뷰 아이템
    private val listAdapter = ListAdapter(listItems)    // 리사이클러 뷰 어댑터
    private var pageNumber = 1      // 검색 페이지 번호
    private var keyword = ""        // 검색 키워드
    private val ACCESS_FINE_LOCATION = 1000
    private var xpoint = ""
    private var ypoint = ""

    private val eventListener = MarkerEventListener(this)


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        val view = binding.root
        permissionCheck()
        setContentView(view)


        // 중심 좌표 이동 내 위치로
        startTracking()

        binding.mapView.setCalloutBalloonAdapter(CustomBalloonAdapter(layoutInflater))  // 커스텀 말풍선 등록
        binding.mapView.setPOIItemEventListener(eventListener)  // 마커 클릭 이벤트 리스너 등록

        // 중심 좌표 이동 내 위치로
//        val lm : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        val userNowLocation: Location? = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//        val uLatitude = userNowLocation?.latitude
//        val uLongitude = userNowLocation?.longitude
//
//        binding.mapView.setMapCenterPoint(uLatitude?.let { uLongitude?.let { it1 ->
//            MapPoint.mapPointWithGeoCoord(it,
//                it1
//            )
//        } },true)

        // 리사이클러 뷰
        binding.rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = listAdapter
        // 리스트 아이템 클릭 시 해당 위치로 이동
        listAdapter.setItemClickListener(object: ListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val mapPoint = MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
                binding.mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true)

            }

        })

//        Log.e("listItems[position].y,listItems[position].x", e.toString())



        //검색 버튼
        binding.btnSearch.setOnClickListener {
            keyword = binding.etSearchField.text.toString()
            binding.tvPageNumber.text = pageNumber.toString() // 수정 1
            searchKeyword(keyword,xpoint,ypoint, 10000,pageNumber)



        }
        // 이전 페이지 버튼
        binding.btnPrevPage.setOnClickListener {
            pageNumber--
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword,xpoint,ypoint, 10000,pageNumber)
        }

        // 다음 페이지 버튼
        binding.btnNextPage.setOnClickListener {
            pageNumber++
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword,xpoint,ypoint, 10000,pageNumber)
        }
        //위치추적 버튼
        binding.btnStart.setOnClickListener {
            if (checkLocationService()) {
                // GPS가 켜져있을 경우
                permissionCheck()
                startTracking()

            } else {
                // GPS가 꺼져있을 경우
                makeText(this, "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
            }
        }

        // 추적중지 버튼
        binding.btnStop.setOnClickListener {
            stopTracking()
        }

        //----------------------------------최신 버전 구분-------------------------------------
        //이게 있어야 최신 파일
    }




    // 키워드 검색 함수
    private fun searchKeyword(keyword: String, xpoint: String, ypoint: String, radius: Int ,page: Int) {

        val lm : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val userNowLocation: Location? = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        val uLatitude = userNowLocation?.latitude
        val uLongitude = userNowLocation?.longitude

        this@MapActivity.xpoint = uLongitude.toString() // 추가 된 부분 삭제하면 작동 함
        this@MapActivity.ypoint = uLatitude.toString() // 어? 왜 병원 검색할때는 익산시만 뜨지?


        val retrofit = Retrofit.Builder()          // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)            // 통신 인터페이스를 객체로 생성

        val call = api.getSearchKeyword(API_KEY, keyword, xpoint, ypoint, 10000 ,page)    // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>)
            {
                // 통신 성공
                addItemsAndMarkers(response.body())
                Log.w("LocalSearch","통신 성공: ")
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("LocalSearch", "통신 실패: ${t.message}")
            }
        })
    }




    // 검색 결과 처리 함수
    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있음
            listItems.clear()                   // 리스트 초기화
            binding.mapView.removeAllPOIItems() // 지도의 마커 모두 제거

            for (document in searchResult!!.documents) {
                // 결과를 리사이클러 뷰에 추가
                val item = ListLayout(document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble()
                )
                listItems.add(item)


                // 지도에 마커 추가
                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(),
                        document.x.toDouble())
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }

                binding.mapView.addPOIItem(point)


            }
            listAdapter.notifyDataSetChanged()


            binding.btnNextPage.isEnabled = !searchResult.meta.is_end // 페이지가 더 있을 경우 다음 버튼 활성화
            binding.btnPrevPage.isEnabled = pageNumber != 1             // 1페이지가 아닐 경우 이전 버튼 활성화



        } else {
            // 검색 결과 없음
            makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()

        }
    }



    private fun permissionCheck() {
        val preference = getPreferences(MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 상태
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 권한 거절 (다시 한 번 물어봄)
                val builder = AlertDialog.Builder(this)
                builder.setMessage("현재 위치를 확인하시려면 위치 권한을 허용해주세요.")
                builder.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)
                }
                builder.setNegativeButton("취소") { dialog, which ->

                }
                builder.show()
            } else {
                if (isFirstCheck) {
                    // 최초 권한 요청
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)
                } else {
                    // 다시 묻지 않음 클릭 (앱 정보 화면으로 이동)
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("현재 위치를 확인하시려면 설정에서 위치 권한을 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { dialog, which ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                        startActivity(intent)
                    }
                    builder.setNegativeButton("취소") { dialog, which ->

                    }
                    builder.show()
                }
            }
        } else {
            // 권한이 있는 상태
            startTracking()
        }
    }

    //권한 요청 후 행동
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 요청 후 승인됨 (추적 시작)
                makeText(this, "위치 권한이 승인되었습니다", Toast.LENGTH_SHORT).show()
                startTracking()

            } else {
                // 권한 요청 후 거절됨 (다시 요청 or 토스트)
                makeText(this, "위치 권한이 거절되었습니다", Toast.LENGTH_SHORT).show()
                permissionCheck()
            }
        }
    }

    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }



    // 위치추적 시작
    private fun startTracking() {
        binding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading


        val lm : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val userNowLocation: Location? = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        val uLatitude = userNowLocation?.latitude
        val uLongitude = userNowLocation?.longitude
        this@MapActivity.xpoint = uLongitude.toString() // 추가 된 부분 삭제하면 작동 함
        this@MapActivity.ypoint = uLatitude.toString()
//        Toast.makeText(this," $uLatitude $uLongitude",Toast.LENGTH_SHORT).show()
        Toast.makeText(this,"현재 내 위치를 찾습니다",Toast.LENGTH_SHORT).show()
        //11월 7일 추가된 부분 2

    }



    // 위치추적 중지
    private fun stopTracking() {
        binding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
        Toast.makeText(this,"내 위치 추적을 중지합니다.",Toast.LENGTH_SHORT).show()
    }
    // 위치값을 반환 받는다



}
//말풍선
class CustomBalloonAdapter(inflater: LayoutInflater): CalloutBalloonAdapter {
    val mCalloutBalloon: View = inflater.inflate(R.layout.balloon_layout, null)
    val name: TextView = mCalloutBalloon.findViewById(R.id.ball_tv_name)
    val address: TextView = mCalloutBalloon.findViewById(R.id.ball_tv_address)

    override fun getCalloutBalloon(poiItem: MapPOIItem?): View {
        // 마커 클릭 시 나오는 말풍선
        name.text = poiItem?.itemName   // 해당 마커의 정보 이용 가능
        address.text = "getCalloutBalloon"
        return mCalloutBalloon
    }

    override fun getPressedCalloutBalloon(poiItem: MapPOIItem?): View {
        // 말풍선 클릭 시
        address.text = "getPressedCalloutBalloon"
        return mCalloutBalloon
    }
}

//마커클릭이벤트

class MarkerEventListener(val context: Context): MapView.POIItemEventListener {
    override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
        // 마커 클릭 시
    }

    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?) {
        // 말풍선 클릭 시 (Deprecated)
        // 이 함수도 작동하지만 그냥 아래 있는 함수에 작성하자
    }

    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?, buttonType: MapPOIItem.CalloutBalloonButtonType?) {
        // 말풍선 클릭 시
        val builder = AlertDialog.Builder(context)
        val itemList = arrayOf("토스트", "마커 삭제", "취소")
        builder.setTitle("${poiItem?.itemName}")
        builder.setItems(itemList) { dialog, which ->
            when(which) {
                0 -> Toast.makeText(context, "토스트", Toast.LENGTH_SHORT).show()  // 토스트
                1 -> mapView?.removePOIItem(poiItem)    // 마커 삭제
                2 -> dialog.dismiss()   // 대화상자 닫기
            }
        }
        builder.show()
    }

    override fun onDraggablePOIItemMoved(mapView: MapView?, poiItem: MapPOIItem?, mapPoint: MapPoint?) {
        // 마커의 속성 중 isDraggable = true 일 때 마커를 이동시켰을 경우
    }
}