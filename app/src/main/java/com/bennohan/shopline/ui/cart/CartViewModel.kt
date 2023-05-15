package com.bennohan.shopline.ui.cart

import androidx.lifecycle.viewModelScope
import com.bennohan.shopline.api.ApiService
import com.bennohan.shopline.base.BaseViewModel
import com.bennohan.shopline.data.Product
import com.bennohan.shopline.data.Session
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session,
) : BaseViewModel() {

    private var _product = MutableSharedFlow<List<Product?>>()
    var responseProduct = _product.asSharedFlow()

    fun getCart(
    ) = viewModelScope.launch {
        ApiObserver({ apiService.showCart() },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray(ApiCode.DATA).toList<Product>(gson)
                    _apiResponse.send(ApiResponse().responseSuccess())
                    _product.emit(data)

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.send(ApiResponse().responseError())

                }
            })
    }

    fun deleteCart(id: Int,position: Int) = viewModelScope.launch {
        _apiResponse.send(ApiResponse().responseLoading())
        ApiObserver({ apiService.deleteCart(id) },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _apiResponse.send(ApiResponse().responseSuccess(flagView = position))
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.send(ApiResponse().responseError())

                }
            })
    }

    fun editCart(id: Int,qty: Int) = viewModelScope.launch {
        _apiResponse.send(ApiResponse().responseLoading())
        ApiObserver({ apiService.editCart(id,qty) },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _apiResponse.send(ApiResponse().responseSuccess())
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.send(ApiResponse().responseError())

                }
            })
    }


}