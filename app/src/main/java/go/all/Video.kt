package go.all

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.os.Bundle
import android.content.Intent
import java.io.File
import android.view.*

class Video : AppCompatActivity() {

  private lateinit var liner: LinearLayout

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.video)

    liner = findViewById(R.id.liner)

    val nav = findViewById<ImageView>(R.id.nav)
    val dariBerkas = intent.getStringExtra("file")
    
    if (dariBerkas == null) {
      AturNormal()
    } else {
      AturKusus(File(dariBerkas))
      nav.setImageResource(R.drawable.sd_tolak)
    }

    nav.setOnClickListener {
      if (dariBerkas != null) {
        DialogKeluar()
      } else {
        liner.visibility = if (liner.visibility == View.GONE) View.VISIBLE else View.GONE
      }
    }

    findViewById<TextView>(R.id.keluar).setOnClickListener {
      DialogKeluar()
    }
  }

  override fun onBackPressed() {
    if (liner.visibility == View.VISIBLE) {
      liner.visibility = View.GONE
    } else {
      DialogKeluar()
    }
  }

  private fun DialogKeluar() {
    AlertDialog.Builder(this)
      .setTitle("Keluar")
      .setMessage("Yakin Meninggalkan Video?")
      .setPositiveButton("Ya") { _, _ -> finish() }
      .setNegativeButton("Tidak", null)
      .show()
  }

  private fun AturKusus(isi: File) {
    // TODO: Isi sesuai logika
  }

  private fun AturNormal() {
    // TODO: Isi sesuai logika
  }
}