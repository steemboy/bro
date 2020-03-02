package ru.arca.browser

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_DPAD_CENTER
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import ru.arca.browser.adapters.adapterClickListener
import ru.arca.browser.adapters.favorites_adapter
import ru.arca.browser.adapters.tab_adapter
import ru.arca.browser.models.favorite
import ru.arca.browser.models.tab

class MainActivity : AppCompatActivity(), View.OnClickListener, View.OnKeyListener, KeyboardVisibilityEventListener, adapterClickListener {
    private var keyIsOpen = false
    private var started: Boolean = false
    private val tadapter = tab_adapter(this)
    private val fadapter = favorites_adapter(this)
    private var selected : tab? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        go_back.isEnabled = false
        go_forward.isEnabled = false

        webview.webViewClient = wvClient
        webview.webChromeClient = wcClient
        webview.settings.javaScriptEnabled = true
        webview.settings.setSupportZoom(true)
        webview.settings.builtInZoomControls = true
        webview.settings.displayZoomControls = false

        url.setOnKeyListener(this)

        list.layoutManager = LinearLayoutManager(this)
        list.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        list.adapter = tadapter

        favorites.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        favorites.adapter = fadapter

        tadapter.addAll(app.realm.where(tab::class.java).findAll())

        if(tadapter.itemCount > 0) {
            count.text = "${tadapter.itemCount}"
            goToTab(tadapter.getItem(0))
        }
        setEventListener(this, getLifecycleOwner(this), this)
    }

    override fun onClick(view: View) {
        when (view) {
            clear_url -> url.setText("")
            share -> ShareCompat.IntentBuilder.from(this).setType("text/plain").setText(url.text).startChooser()
            go_forward -> goForward()
            go_back -> goBack()
            count_lay -> if (tadapter.itemCount > 0)
                if(list_lay.visibility == GONE)
                    switchToList()
                else
                    switchToTitle()
            list_lay -> switchToTitle()
            add_tab -> newTab()
            clear_tabs -> newTab(true)
            switch_agent -> switchAgent(true)
            etitle -> switchToSearch()
            refresh -> webview.reload()
        }
    }

    override fun onBackPressed() {
        when {
            keyIsOpen -> hideKeyBoard(url)
            list_lay.visibility == VISIBLE -> switchToTitle()
            goBack() -> super.onBackPressed()
        }
    }

    var wvClient: WebViewClient = object : WebViewClient() {
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            started = true
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            if(started) {
                started = false
                progressBar.progress = 0
                if (selected != null) {
                    selected?.addUrl(view.url)
                    if (selected?.title != view.title) {
                        app.realm.executeTransaction {
                            selected?.title = view.title
                        }
                        tadapter.update(selected!!)
                    }
                    setEnabled(go_back, selected?.canBack() == true)
                    setEnabled(go_forward, selected?.canForward() == true)
                    switchToTitle()
                    updTitle(selected!!.domain)
                }
            }
        }

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            return false
        }
    }

    var wcClient: WebChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            if (started)
                progressBar.progress = newProgress
        }

        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
            return super.onJsAlert(view, url, message, result)
        }
    }

    private fun goBack(): Boolean {
        val s = selected?.goBack() ?: ""
        if(s.isNotEmpty()) {
            webview.loadUrl(s)
            return false
        }
        return true
    }

    private fun goForward() {
        val s = selected?.goForward() ?: ""
        if(s.isNotEmpty())
            webview.loadUrl(s)
    }

    private fun setImage(id : Int) = (switch_agent.getChildAt(0) as ImageView).setImageResource(id)
    private fun updTitle(url : String) = (lay_title.getChildAt(0) as TextView).setText(url)

    private fun switchToSearch() {
        counter_border.setBackgroundColor(Color.WHITE)
        holder.setBackgroundColor(Color.WHITE)
        lay_search.visibility = VISIBLE
        list_lay.visibility = GONE
        lay_tabs.visibility = GONE
        lay_title.visibility = GONE
        url.requestFocus()
        showKeyBoard()
    }

    private fun switchToList() {
        val c = getColor(R.color.lgray)
        counter_border.setBackgroundColor(c)
        holder.setBackgroundColor(c)
        lay_search.visibility = GONE
        lay_title.visibility = GONE
        list_lay.visibility = VISIBLE
        lay_tabs.visibility = VISIBLE
        hideKeyBoard(url)
    }

    private fun switchToTitle() {
        counter_border.setBackgroundColor(Color.WHITE)
        holder.setBackgroundColor(Color.WHITE)
        lay_title.visibility = VISIBLE
        lay_search.visibility = GONE
        list_lay.visibility = GONE
        lay_tabs.visibility = GONE
    }

    override fun onKey(v: View?, k: Int, e: KeyEvent): Boolean {
        if (v == url && e.action == KeyEvent.ACTION_DOWN)
            return when (k) {
                KEYCODE_DPAD_CENTER,
                KEYCODE_ENTER -> {
                    if (selected == null) {
                        selected = tab()
                        tadapter.add(selected!!)
                        count.text = "${tadapter.itemCount}"
                    } else
                        tadapter.notifyDataSetChanged()
                    switchAgent()
                    selected!!.addUrl(createUrl("${url.text}"))
                    updTitle(selected!!.domain)
                    app.realm.executeTransaction {
                        it.copyToRealm(selected!!)
                    }
                    webview.loadUrl(selected!!.curUrl)
                    true
                }
                else -> false
            }
        return false
    }

    override fun onVisibilityChanged(isOpen: Boolean) {
        keyIsOpen = isOpen
        if (!isOpen && lay_search.visibility == VISIBLE && selected != null)
            if (selected!!.history.size > 0)
                switchToTitle()
    }

    private fun switchAgent(switch : Boolean = false) {
        if(switch) {
            if(webview.settings.userAgentString != newUA) {
                webview.settings.userAgentString = newUA
                setImage(R.drawable.ic_phone)
            } else {
                webview.settings.userAgentString = ""
                setImage(R.drawable.ic_computer)
            }
            webview.reload()
        } else {
            setImage(R.drawable.ic_computer)
            webview.settings.userAgentString = ""
        }
    }

    private fun newTab(clear : Boolean = false) {
        if(clear) {
            etitle.text = ""
            count.text = ""
            url.setText("")
            setEnabled(go_back, false)
            setEnabled(go_forward, false)
        }
        selected = null
        webview.loadUrl("about:blank")
        switchToSearch()
    }

    private fun goToTab(t : tab?) {
        if(t == null)
            return
        if (t != selected) {
            selected = t
            url.setText(selected?.curUrl)
            updTitle(selected!!.domain)
            webview.clearHistory()
            webview.loadUrl(t.getLastUrl())
        }
        switchToTitle()
    }

    private fun switchFavorite(add : Boolean) {
        if(selected != null) {
            app.realm.beginTransaction()
            if (add) {
                val f = favorite(selected!!.curUrl, selected!!.title)
                app.realm.copyToRealm(f)
            } else {
                val f = app.realm.where(favorite::class.java).equalTo("url", selected!!.curUrl).equalTo("title", selected!!.title).findFirst()
            }
            app.realm.commitTransaction()
        }
    }

    override fun onItemClick(adapter: Any, obj: Any, event: Int) {
        if(adapter == tadapter) {
            when(event) {
                0 -> goToTab(obj as tab)
                1 -> {
                    app.realm.executeTransaction {
                        (obj as tab).deleteFromRealm()
                    }
                    tadapter.remove(obj as tab)
                    if(tadapter.itemCount == 0)
                        newTab(true)
                    else {
                        count.text = "${tadapter.itemCount}"
                        goToTab(tadapter.getItem(0))
                    }
                }
            }
        }
    }
}
