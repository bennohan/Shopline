package com.bennohan.shopline.ui.home

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.shopline.R
import com.bennohan.shopline.base.BaseActivity
import com.bennohan.shopline.data.Cons
import com.bennohan.shopline.data.Product
import com.bennohan.shopline.data.Session
import com.bennohan.shopline.databinding.ActivityHomeBinding
import com.bennohan.shopline.databinding.ItemHomeBinding
import com.bennohan.shopline.ui.checkout.CheckoutActivity
import com.bennohan.shopline.ui.detailProduct.DetailProductActivity
import com.bennohan.shopline.ui.profile.ProfileActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {

    @Inject
    lateinit var session: Session

//    private val adapter by lazy {
//        binding.rvHome.adapter = ReactiveListAdapter<ItemHomeBinding , Product>(R.layout.item_home)
//            .initItem { position, data ->
//
//            }
//
//    }

    private val adapter by lazy {
        ReactiveListAdapter<ItemHomeBinding, Product>(R.layout.item_home).initItem { position, data ->
            openActivity<DetailProductActivity> {
                putExtra(Cons.PRODUCT.ID,  data)
            }
        }
    }

//    private val adapter = object : ReactiveListAdapter<ItemHomeBinding, Product>(R.layout.item_home) {
//        override fun onBindViewHolder(
//            holder: ItemViewHolder<ItemHomeBinding, Product>,
//            position: Int
//        ) {
//            val item = getItem(position)
//            item?.let { itm ->
////                holder.binding.data = itm
//                holder.bind(itm)
//                holder.itemView.setOnClickListener {
//                    openActivity<DetailProductActivity> {
//                        putExtra(Cons.PRODUCT.PRODUCT, item)
//                    }
//                }
//            }
//        }
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
        getUser()
        getProduct()

        window.navigationBarColor = resources.getColor(R.color.white)

        //Ask About This
        binding.rvHome.adapter = adapter
        binding.rvHome2.adapter = adapter

        viewModel.getProduct()

        binding.ivProfile.setOnClickListener {
            openActivity<ProfileActivity>()
        }

        binding.btnCart.setOnClickListener {
            openActivity<CheckoutActivity>()
        }


    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.SUCCESS -> {
                                val user = session.getUser()
                                binding.user = user

                            }
                            else -> {

                            }
                        }
                    }


                }
            }
        }
    }

    private fun getUser() {
        viewModel.getProfile()
    }

    private fun getProduct() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.product.collect { product ->
                        adapter.submitList(product)
                    }
//                    viewModel.getProduct.collect { notes ->
//                        adapter.submitList(notes)
//                    }
                }
            }
        }
    }


}