package go.all

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.webkit.*
import android.widget.*

class Browser : AppCompatActivity() {

  private lateinit var liner: LinearLayout
  private lateinit var web: WebView
  private lateinit var link: EditText

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.browser)

    findViewById<TextView>(R.id.keluar).setOnClickListener {
      AlertDialog.Builder(this)
        .setTitle("Keluar")
        .setMessage("Yakin?")
        .setPositiveButton("Yakin") { _, _ -> finish() }
        .setNegativeButton("Batal", null)
        .show()
    }

    liner = findViewById(R.id.liner)
    web = findViewById(R.id.web)
    link = findViewById(R.id.link)

    web.settings.javaScriptEnabled = true
    web.webViewClient = WebViewClient()

    findViewById<ImageView>(R.id.nav).setOnClickListener {
      liner.visibility = if (liner.visibility == View.GONE) View.VISIBLE else View.GONE
    }

    findViewById<ImageView>(R.id.duck).setOnClickListener {
      web.loadUrl("https://duckduckgo.com/")
      link.setText("https://duckduckgo.com/")
      liner.visibility = View.GONE
    }

    findViewById<ImageView>(R.id.google).setOnClickListener {
      web.loadUrl("https://google.com/")
      link.setText("https://google.com/")
      liner.visibility = View.GONE
    }

    findViewById<ImageView>(R.id.jelajah).setOnClickListener {
      var target = link.text.toString()
      if (!target.startsWith("http")) {
        target = "http://$target"
      }
      web.loadUrl(target)
      link.setText(target)
      liner.visibility = View.GONE
    }
  }

  override fun onBackPressed() {
    if (liner.visibility == View.VISIBLE) {
      liner.visibility = View.GONE
    } else if (web.canGoBack()) {
      web.goBack()
    } else {
      Toast.makeText(this, "Tidak ada riwayat", Toast.LENGTH_SHORT).show()
    }
  }
}