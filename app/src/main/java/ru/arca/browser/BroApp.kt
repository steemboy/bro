package ru.arca.browser

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import io.realm.Realm

class BroApp: Application() {
    lateinit var inflater: LayoutInflater
    lateinit var realm: Realm
    lateinit var imm: InputMethodManager

    override fun onCreate() {
        super.onCreate()
        app = this

        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inflater = LayoutInflater.from(this)

        Realm.init(this)
        realm = Realm.getDefaultInstance()

    }
}