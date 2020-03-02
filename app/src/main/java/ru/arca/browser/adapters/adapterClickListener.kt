package ru.arca.browser.adapters

import androidx.recyclerview.widget.RecyclerView

interface adapterClickListener {
    fun onItemClick(adapter : Any, obj : Any, type : Int)
}