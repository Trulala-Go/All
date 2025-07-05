package go.all

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.os.Bundle
import android.view.*
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import java.io.File

class Editor : AppCompatActivity(){

  private lateinit var liner:LinearLayout
  private lateinit var tulis:EditText
  private lateinit var file:File

  override fun onCreate(savedInstanceState:Bundle?){
    super.onCreate(savedInstanceState)
    setContentView(R.layout.editor)

    findViewById<TextView>(R.id.keluar).setOnClickListener{
      AlertDialog.Builder(this)
        .setTitle("Keluar")
        .setMessage("Yakin Ingin Keluar?")
        .setPositiveButton("Ya"){_,_-> finish()}
        .setNegativeButton("Tidak", null)
        .show()
    }

    liner = findViewById(R.id.liner)
    tulis = findViewById(R.id.tulis)

    val terima = intent.getStringExtra("file")
    if(terima != null){AturDariBerkas(File(terima))}
    else{MuatNormal()}

    findViewById<ImageView>(R.id.simpan).setOnClickListener{
      if(::file.isInitialized){
        Toast.makeText(this, "${file.name} tersimpan", Toast.LENGTH_SHORT).show()
      }else{
        Toast.makeText(this, "Belum ada file", Toast.LENGTH_SHORT).show()
      }
    }
  }

  override fun onBackPressed(){
    if(liner.visibility == View.VISIBLE){liner.visibility = View.GONE}
    else{
      AlertDialog.Builder(this)
        .setTitle("Keluar")
        .setMessage("Yakin Ingin Keluar?")
        .setPositiveButton("Ya"){_,_-> finish()}
        .setNegativeButton("Tidak", null)
        .show()
    }
  }

  private fun AturDariBerkas(terima:File){
    val nav = findViewById<ImageView>(R.id.nav)
    file = terima

    nav.setImageResource(R.drawable.x)
    nav.setOnClickListener{
      AlertDialog.Builder(this)
        .setTitle("Keluar")
        .setMessage("Yakin Ingin Keluar?")
        .setPositiveButton("Ya"){_,_-> finish()}
        .setNegativeButton("Tidak", null)
        .show()
    }

    tulis.setText(terima.readText())
  }

  private fun MuatNormal(){
    tulis.setText("")
    val nav = findViewById<ImageView>(R.id.nav)
    nav.setOnClickListener{
      liner.visibility = if(liner.visibility == View.GONE)View.VISIBLE else View.GONE
    }

    file = Environment.getExternalStorageDirectory()
    AturGrid(file)

    findViewById<ImageView>(R.id.apk).setOnClickListener{
      AturGrid(filesDir)
    }

    findViewById<ImageView>(R.id.sd).setOnClickListener{
      file = Environment.getExternalStorageDirectory()
      AturGrid(file)
    }
  }

  private fun AturGrid(folder:File){
  val grid = findViewById<GridLayout>(R.id.grid)
  grid.removeAllViews()

  if(folder.isDirectory){
    val daftar = folder.listFiles() ?: return

    for(file in daftar){
      val item = LayoutInflater.from(this).inflate(R.layout.item_horizontal, grid, false)
      val gambar = item.findViewById<ImageView>(R.id.gambar)
      val nama = item.findViewById<TextView>(R.id.nama)

      nama.text = file.name

      if(file.isDirectory){
        gambar.setImageResource(R.drawable.folder)
      }
      else{
        gambar.setImageResource(R.drawable.file)
      }

      item.setOnClickListener{
        if(file.isDirectory){
          AturGrid(file)
        }
        else{
          tulis.setText(file.readText())
          this.file = file
          liner.visibility = View.GONE
        }
      }

      grid.addView(item)
      }
    }
  }
}