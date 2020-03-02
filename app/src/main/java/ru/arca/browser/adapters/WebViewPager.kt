package ru.arca.browser.adapters

import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.viewpager.widget.PagerAdapter
import ru.arca.browser.models.WebViewPagerItem
import ru.arca.browser.app
import ru.arca.browser.models.tab


class WebViewPager(val wvClient : WebViewClient, val wcClient : WebChromeClient) : PagerAdapter() {
    var views: ArrayList<WebViewPagerItem> = ArrayList()

    override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj
    override fun getCount(): Int = views.size
    fun getView(position: Int): View? = views[position].wv

    override fun getItemPosition(obj : Any): Int {
        for (index in 0 until count)
            if (obj as View == views[index].wv)
                return index
        return POSITION_NONE
    }

    fun add(t: tab) {
        val v = WebView(app)
        v.webViewClient = wvClient
        v.webChromeClient = wcClient
        v.settings.javaScriptEnabled = true
        views.add(WebViewPagerItem(t, v))
        notifyDataSetChanged()
    }

    fun remove(pos : Int) {
        if(pos > -1 && pos < views.size)
            views.removeAt(pos)
    }

    fun remove(v : View) {
        //if(views.contains(v))
        //    views.remove(v)
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(views[position].wv)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = views[position].wv
        container.addView(view)
        return view
    }
}