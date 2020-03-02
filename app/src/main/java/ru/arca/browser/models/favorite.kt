package ru.arca.browser.models

import io.realm.RealmObject

open class favorite(var url : String = "", var title : String = "") : RealmObject()