@file:JvmName("static")
package ru.arca.browser

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.LifecycleOwner

lateinit var app: BroApp

const val newUA  = "Mozilla/5.0+ (Windows;+WOW64;+x64;rv:52.0) Gecko/20100101+Firefox/52.0"

fun showKeyBoard() = app.imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
fun hideKeyBoard(v : View) = app.imm.hideSoftInputFromWindow(v.windowToken, 0)

fun getLifecycleOwner(a : Activity): LifecycleOwner {
    var context: Context = a
    while (context !is LifecycleOwner)
        context = (context as ContextWrapper).baseContext
    return context
}

fun setEnabled(v: View?, enabled: Boolean) {
    v!!.isEnabled = enabled
    if (enabled)
        v.alpha = 1f else v.alpha = 0.5f
}

fun createUrl(url : String) : String {
    var u = ""
    if(url.split(".").size > 1) {
        if(!url.contains("http") && !url.contains("://"))
            u = "https://$url"
    } else
        u = "https://yandex.ru/search/touch/?text=${url.replace(" ", "%20")}"
    return u
}