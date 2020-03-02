package ru.arca.browser.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import ru.arca.browser.app

open class tab : RealmObject() {
    var domain : String = ""
    var title : String = ""
    var curUrl : String = ""
    var history = RealmList<String>()
    @Ignore
    var index = history.size - 1

    fun canForward() : Boolean = index < history.size - 1
    fun canBack() : Boolean = index < history.size && index > 0

    fun goForward() : String {
        if(canForward())
            return history[++index].toString()
        return ""
    }

    fun goBack() : String {
        if(canBack())
            return history[--index].toString()
        return ""
    }

    fun addUrl(url: String) {
        app.realm.beginTransaction()
        domain = url.split("/")[2]
        curUrl = url
        if (!history.contains(url)) {
            if(index != history.size - 1)
                for (i in index + 1 until history.size)
                    history.remove(history[i])
            index = history.size - 1
            history.add(url)
        }
        app.realm.commitTransaction()
    }

    fun getLastUrl(): String = history.last().toString()
    fun getCurrentUrl(): String = history[index].toString()
}