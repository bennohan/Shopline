package com.bennohan.shopline.ui.cart

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.shopline.R
import com.bennohan.shopline.base.BaseActivity
import com.bennohan.shopline.data.Product
import com.bennohan.shopline.databinding.ActivityCartBinding
import com.bennohan.shopline.databinding.ItemCartBinding
import com.bennohan.shopline.ui.checkout.CheckoutActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.snacked
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartActivity : BaseActivity<ActivityCartBinding, CartViewModel>(R.layout.activity_cart) {


    private val adapterCart by lazy {
        object : ReactiveListAdapter<ItemCartBinding, Product>(R.layout.item_cart) {
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemCartBinding, Product>,
                position: Int
            ) {

                //Get Product
                val item = getItem(position)
                item?.let { itm ->
                    val productId = item.id
                    var totalProduct = item.totalProduct ?: 1
                    Log.d("cek id", "cek id : $productId ")
                    holder.binding.data = itm
                    holder.bind(itm)

                    //Button Delete
                    holder.binding.btnDelete.setOnClickListener {

                        val builder = AlertDialog.Builder(this@CartActivity)
                        builder.setMessage("Apakah Anda Ingin Menghapus Produk")
                            .setCancelable(false)
                            //setPositive button (tombol untuk iya)
                            .setPositiveButton("Hapus") { dialog, id ->
                                binding.root.snacked("Product DiHapus")
                                productId?.let {
                                    viewModel.deleteCart(it, position)
                                }
                                getCart()
                            }
                            //setNegative Button Tombol untuk tidak
                            .setNegativeButton("Tidak") { dialog, id ->
                                // Dismiss the dialog
                                dialog.dismiss()
                            }
                        val alert = builder.create()
                        alert.show()
                    }

                    //Button Add
                    holder.binding.btnAdd.setOnClickListener {
                        binding.root.snacked("Add")
                        var pluss = itm.totalProduct ?: 1
                        if (totalProduct < 100) {
                            totalProduct++
                            viewModel.editCart(productId ?: 1, totalProduct)
                            holder.binding.etTotalProduct.setText(totalProduct.toString())
                        }
                    }

                    //Button Minus
                    holder.binding.btnRemove.setOnClickListener {
                        binding.root.snacked("Min")
//                        var min = itm.totalProduct ?: 1
                        if (totalProduct == 1) {
                            binding.root.snacked("test")
                        }
                        if (totalProduct != 1) {
                            totalProduct--
                            viewModel.editCart(productId ?: 1, totalProduct)
                            holder.binding.etTotalProduct.text = totalProduct.toString()
                        } else {

                        }

                    }


                }

                super.onBindViewHolder(holder, position)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
        getCart()

        binding.rvCart.adapter = adapterCart

        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.swipeLayout.setOnRefreshListener {
            getCart()
        }

    }

    private fun getCart() {
        viewModel.getCart()
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.SUCCESS -> {

                                it.flagView?.let {
                                    adapterCart.notifyItemRemoved(it)
                                }
                            }
                            else -> {

                            }
                        }
                    }
                }

                launch {
                    viewModel.responseProduct.collect { product ->
                        adapterCart.submitList(product)
                        binding.swipeLayout.isRefreshing = false

                        //Button Checkout
                        if (product.isEmpty()) {
                            binding.btnCheckout.background =
                                ContextCompat.getDrawable(this@CartActivity,R.drawable.button_drawable_grey)
                            binding.btnCheckout.setOnClickListener {
//                                binding.root.snacked("Tambahkan Produk ke Keranjang Terlebih Dahulu")
                            }
                        } else {
                            binding.btnCheckout.setOnClickListener {
                                openActivity<CheckoutActivity>()
                            }
                        }

                        //Text Data Kosong
                        if (product.isEmpty()) {
                            binding.tvkosong.visibility = View.VISIBLE
                        } else {
                            binding.tvkosong.visibility = View.GONE

                        }
                    }
                }
            }
        }
    }
}