package com.bennohan.shopline.ui.search

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.shopline.R
import com.bennohan.shopline.base.BaseActivity
import com.bennohan.shopline.data.Cons
import com.bennohan.shopline.data.Product
import com.bennohan.shopline.databinding.ActivitySearchBinding
import com.bennohan.shopline.databinding.ItemHomeBinding
import com.bennohan.shopline.ui.detailProduct.DetailProductActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchActivity :
    BaseActivity<ActivitySearchBinding, SearchViewModel>(R.layout.activity_search),
    SearchView.OnQueryTextListener {

    private var pproduct = ArrayList<Product?>()

    private val adapter by lazy {
        ReactiveListAdapter<ItemHomeBinding, Product>(R.layout.item_home).initItem { position, data ->
            openActivity<DetailProductActivity> {
                putExtra(Cons.PRODUCT.ID, data)

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        search()
        observe()
        getProduct()

        binding.rvSearch.adapter = adapter

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorSearch)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorSearch)


        binding.btnBack.setOnClickListener {
            finish()
        }

    }

    private fun getProduct() {
        viewModel.getProduct()

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {

                            ApiStatus.SUCCESS -> {
                                loadingDialog.setResponse(it.message ?: return@collect)
                            }
                            ApiStatus.ERROR -> {
                                loadingDialog.dismiss()
                            }
                            else -> {

                            }
                        }
                    }


                }

                launch {
                    viewModel.product.collect { productt ->
                        pproduct.clear()
                        pproduct.addAll(productt)
                        android.util.Log.d("cekAllProduct", "cekAllProduct : $productt")
                        adapter.submitList(productt)
                    }
                }
            }
        }

    }

    private fun search() {
        binding.etSearch.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()) {
                val filter = pproduct.filter { it?.nameProduct?.contains("$text", true) == true }
                adapter.submitList(filter)

                if (filter.isEmpty()) {
                    binding.tvDataKosong.visibility = View.VISIBLE
                } else {
                    binding.tvDataKosong.visibility = View.GONE
                }
            } else {
                adapter.submitList(pproduct)
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        TODO("Not yet implemented")
    }
}



