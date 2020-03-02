package ru.arca.browser.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class Holder<E>(view : View) : RecyclerView.ViewHolder(view) {
    abstract fun update(item : E)
}