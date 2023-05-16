package com.bennohan.shopline.ui.search

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
class SearchViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session,
) : BaseViewModel() {

     private var _product = MutableSharedFlow<List<Product?>>()
    var product = _product.asSharedFlow()
//    private var _productt = MutableSharedFlow<List<Product?>>()
    var allProduct = _product.asSharedFlow()


    fun getProduct(
    ) = viewModelScope.launch {
        ApiObserver({ apiService.getProduct() },
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


}