package ru.arca.browser.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.arca.browser.R
import ru.arca.browser.app
import ru.arca.browser.models.tab

class tab_adapter(lis : adapterClickListener): baseRAdapter<tab>(R.layout.temp_tab, ViewHolder::class.java) {
    init { listener = lis }

    inner class ViewHolder(private val view : View): Holder<tab>(view), View.OnClickListener {
        private val title = view.findViewById<TextView>(R.id.title)
        private val url = view.findViewById<TextView>(R.id.url)
        private var t: tab? = null

        init {
            view.findViewById<ImageView>(R.id.close).setOnClickListener(this)
            view.setOnClickListener(this)
        }

        override fun update(item: tab) {
            t = item
            title.text = item.title
            url.text = item.domain
        }

        override fun onClick(v: View) {
            if (t != null)
                when {
                    v == view -> listener?.onItemClick(this@tab_adapter, t!!, 0)
                    v.id == R.id.close -> listener?.onItemClick(this@tab_adapter, t!!, 1)
                }
        }
    }
}
