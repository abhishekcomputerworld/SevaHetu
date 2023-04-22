package com.ritualkart.sevahetu.ui.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ritualkart.sevahetu.R
import com.ritualkart.sevahetu.databinding.ItemBannerDataBinding
import com.ritualkart.sevahetu.model.response.HomeFragmentViewPagerResponse

class BannerDataAdapter(private val context: Context, private val bannerDataList: ArrayList<HomeFragmentViewPagerResponse>
                        ) : PagerAdapter() {

    override fun getCount(): Int {
        return bannerDataList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding =
            ItemBannerDataBinding.inflate(LayoutInflater.from(context), container, false)

        Glide.with(context)
            .load(bannerDataList[position].imageUrl?:"")
            .apply(RequestOptions().placeholder(R.drawable.ic_add_cart))
            .into(binding!!.ivBannerImages)

        binding!!.ivBannerImages.setOnClickListener {
           // bannerClickListener.bannerClick(bannerDataList[position],position)
        }

        container?.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }
}