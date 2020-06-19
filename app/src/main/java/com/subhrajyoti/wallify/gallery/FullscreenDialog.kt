package com.subhrajyoti.wallify.gallery

import android.R.style
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.subhrajyoti.wallify.databinding.FragmentImageSliderBinding
import com.subhrajyoti.wallify.databinding.ImageFullscreenPreviewBinding
import java.io.File
import java.util.ArrayList

class FullscreenDialog : DialogFragment() {

    private var _binding: FragmentImageSliderBinding? = null
    private val binding get() = _binding!!

    private var images: ArrayList<String>? = null
    var viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            displayMetaInfo(position)
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }
    private var selectedPosition = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentImageSliderBinding.inflate(inflater)

        images = arguments!!.getSerializable("images") as ArrayList<String>
        selectedPosition = arguments!!.getInt("position")
        val viewPagerAdapter = MyViewPagerAdapter()
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.addOnPageChangeListener(viewPagerPageChangeListener)
        setCurrentItem(selectedPosition)
        return binding.root
    }

    private fun setCurrentItem(position: Int) {
        binding.viewPager.setCurrentItem(position, false)
        displayMetaInfo(selectedPosition)
    }

    private fun displayMetaInfo(position: Int) {
        binding.countView.text = (position + 1).toString() + " of " + images!!.size
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, style.Theme_Black_NoTitleBar_Fullscreen)
    }

    inner class MyViewPagerAdapter : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val layoutInflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val binding = ImageFullscreenPreviewBinding.inflate(layoutInflater)
            binding.imageView.setImageURI(Uri.fromFile(File(images!![position])))
            container.addView(binding.root)
            return binding.root
        }

        override fun getCount(): Int {
            return images!!.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    companion object {
        fun newInstance(): FullscreenDialog {
            return FullscreenDialog()
        }
    }
}