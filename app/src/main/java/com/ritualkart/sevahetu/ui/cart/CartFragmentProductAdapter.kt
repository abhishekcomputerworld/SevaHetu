package com.ritualkart.sevahetu.ui.cart

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ritualkart.sevahetu.R
import com.ritualkart.sevahetu.databinding.ItemCartProductBinding
import com.ritualkart.sevahetu.model.SelectedProductItems
import com.ritualkart.sevahetu.model.response.ProductItemResponse
import com.ritualkart.sevahetu.utiliy.Preference

class CartFragmentProductAdapter(
    private val context: Context,
    private var orderList: List<ProductItemResponse>,
    private var sharedPreference: Preference,
    var categoryClickListener: CartFragmentProductInterface
) : RecyclerView.Adapter<CartFragmentProductAdapter.ProductItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductItemViewHolder {
        val binding = ItemCartProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductItemViewHolder, position: Int) {
        with(holder) {
            binding.tvName.text = orderList[position].name ?: ""
            binding.tvQuantity.text = orderList[position].quantity ?: ""
            binding.tvActualPrice.text = "₹ " + orderList[position].price ?: ""
            binding.tvMarketPrice.text = "₹ " + orderList[position].price ?: ""
            binding!!.tvMarketPrice.setPaintFlags(binding!!.tvMarketPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
            Glide.with(context).load(orderList[position]?.image_url).apply(
                RequestOptions().placeholder(R.drawable.avupanchaka)
            ).into(binding!!.ivImage)
            if(sharedPreference.packageAvailable(orderList[position].item_id.toString())){
                   binding.tvNoOfAddedItem.text =sharedPreference.getSelectedProductItemModel(orderList[position]?.item_id)?.productItemQuantity.toString()//"Remove from Cart"
              //  binding.tvNoOfAddedItem.text ="Remove from Cart"
                binding.llAddToCart.background = context.getDrawable(R.drawable.bg_button_rounded_green)
            }else{
                binding.tvNoOfAddedItem.text ="Add to Cart"
                binding.llAddToCart.background = context.getDrawable(R.drawable.bg_button_rounded_sky_blue)
            }

//            binding.clProductItem.setOnClickListener {
//                if (binding.tvNoOfAddedItem.text == "Add to Cart") {
//                    binding.tvNoOfAddedItem.text ="Remove from Cart"
//                    sharedPreference.addedPackageList((orderList[position].item_id).toString())
//                    binding.llAddToCart.background = context.getDrawable(R.drawable.bg_button_rounded_green)
//                    categoryClickListener.onProductItemClick(orderList[position],position)
//                } else {
//                    sharedPreference.removeFromPackageList((orderList[position].item_id).toString())
//                    categoryClickListener.onProductItemClick(orderList[position],position)
//                    binding.tvNoOfAddedItem.text ="Add to Cart"
//                    binding.llAddToCart.background = context.getDrawable(R.drawable.bg_button_rounded_sky_blue)
//                }
//            }
            binding.ivRemove.setOnClickListener {
                if((sharedPreference.getSelectedProductItemModel(orderList[position].item_id.toString())!!.productItemQuantity).toInt()==1){
                    binding.ivDelete.callOnClick()
                    return@setOnClickListener
                }
                sharedPreference.addSelectedProductItemModel(orderList[position]?.item_id.toString(),
                    SelectedProductItems( orderList[position]?.item_id.toString(),
                        (orderList[position]?.price ?: 0.0) as Int,
                        (sharedPreference.getSelectedProductItemModel(orderList[position].item_id.toString())!!.productItemQuantity).toInt()!!.minus(1)
                    )
                )
                binding.tvNoOfAddedItem.text =sharedPreference.getSelectedProductItemModel(orderList[position]?.item_id)?.productItemQuantity.toString()//"Remove from Cart"
                categoryClickListener.onProductItemRemove(orderList[position],position)

            }
            binding.ivAdd.setOnClickListener {
                sharedPreference.addSelectedProductItemModel(orderList[position]?.item_id.toString(),
                    SelectedProductItems( orderList[position]?.item_id.toString(),
                        (orderList[position]?.price ?: 0.0) as Int,
                        (sharedPreference.getSelectedProductItemModel(orderList[position].item_id.toString())!!.productItemQuantity).toInt()!!.plus(1)
                    )
                )
                binding.tvNoOfAddedItem.text =sharedPreference.getSelectedProductItemModel(orderList[position]?.item_id)?.productItemQuantity.toString()//"Remove from Cart"
                categoryClickListener.onProductItemAdded(orderList[position],position)

            }

            binding.ivDelete.setOnClickListener {
                sharedPreference.removeSelectedProductItemModel(orderList[position]?.item_id.toString())
                categoryClickListener.onProductItemDelete(orderList[position],position)
            }
        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    inner class ProductItemViewHolder(val binding: ItemCartProductBinding) :
        RecyclerView.ViewHolder(binding.root)

}