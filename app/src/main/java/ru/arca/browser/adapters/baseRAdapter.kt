package ru.arca.browser.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.arca.browser.app

abstract class baseRAdapter<E>(val id : Int, val clasz : Class<*>): RecyclerView.Adapter<Holder<E>>() {
    protected val items = ArrayList<E>()
    var listener: adapterClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder<E> = (
            if (clasz.kotlin.isInner)
                clasz.declaredConstructors[0].newInstance(this,app.inflater.inflate(id, parent, false))
            else
                clasz.declaredConstructors[0].newInstance(app.inflater.inflate(id, parent, false))
            ) as Holder<E>

    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: Holder<E>, position: Int) = holder.update(items[position])

    fun update(item: E) {
        if(items.contains(item))
            notifyItemChanged(items.indexOf(item))
    }

    fun addAll(list: Collection<E>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun add(item: E) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun remove(item: E) {
        if (items.contains(item)) {
            notifyItemRemoved(items.indexOf(item))
            items.remove(item)
        }
    }

    fun getItem(i: Int): E? =
        if (i > -1 && i < items.size)
            items[i]
        else
            null

    fun addListener(lis: adapterClickListener) {
        listener = lis
    }
}