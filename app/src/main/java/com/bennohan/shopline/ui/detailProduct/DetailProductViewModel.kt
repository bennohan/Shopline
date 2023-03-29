package com.bennohan.shopline.ui.detailProduct

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.bennohan.shopline.api.ApiService
import com.bennohan.shopline.base.BaseViewModel
import com.bennohan.shopline.data.ImageSlide
import com.bennohan.shopline.data.Product
import com.bennohan.shopline.data.Session
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class DetailProductViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session,
) : BaseViewModel() {

    var product = MutableSharedFlow<Product>()
    val imageSlider = MutableSharedFlow<List<ImageSlide>>()


    fun getProduct(id: Int) = viewModelScope.launch {
        ApiObserver({ apiService.getProductById(id) },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<Product>(gson)
                    Log.d("check data","check data $data")
                    product.emit(data)
                    val image = response.getJSONArray("image_sliders").toList<ImageSlide>(gson)
                    imageSlider.emit(image)
                    _apiResponse.send(ApiResponse().responseSuccess())
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.send(ApiResponse().responseError())
                }
            })
    }

    //Add Cart
    fun addCart(
        sizeId : Int?,
        totalProduct : Int? = 1
    ) = viewModelScope.launch {
        _apiResponse.send(ApiResponse().responseLoading())
        ApiObserver({ apiService.addCart(sizeId, totalProduct) },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
//                    val responsee = response.getJSONObject("info").toString()
//                    val token = response.getString("token")
//                    session.setValue(Cons.TOKEN.API_TOKEN,token)

                    _apiResponse.send(ApiResponse().responseSuccess())
//                    Log.d("cek response","cek response : $responsee" )

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.send(ApiResponse().responseError())

                }
            })
    }


}