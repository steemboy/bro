package ru.arca.browser.adapters

import android.view.View
import ru.arca.browser.R
import ru.arca.browser.models.favorite

class favorites_adapter(lis : adapterClickListener): baseRAdapter<favorite>(R.layout.temp_favorite, ViewHolder::class.java) {
    init { listener = lis }
    inner class ViewHolder(val view : View): Holder<favorite>(view) {
        override fun update(item: favorite) {

        }
    }
}
