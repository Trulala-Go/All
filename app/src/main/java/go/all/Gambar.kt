package go.all

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.os.Environment
import android.os.Bundle
import android.view.*
import android.graphics.BitmapFactory
import android.util.Log // Import Log untuk debugging
import java.io.File

class Gambar : AppCompatActivity() {

    private lateinit var liner: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gambar)

        findViewById<TextView>(R.id.keluar).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Keluar")
                .setMessage("Yakin?")
                .setPositiveButton("Ya") { _, _ -> finish() }
                .setNegativeButton("Tidak", null)
                .show()
        }

        liner = findViewById(R.id.liner)

        val dariBerkas = intent.getStringExtra("file")
        val nav = findViewById<ImageView>(R.id.nav)

        if (dariBerkas != null) {
            nav.setImageResource(R.drawable.x)
            AturKhusus(File(dariBerkas))
        }

        nav.setOnClickListener {
            if (dariBerkas == null) {
                liner.visibility = if (liner.visibility == View.GONE) View.VISIBLE else View.GONE
            } else {
                finish()
            }
        }

        AturGrid()
    }

    override fun onBackPressed() {
        if (liner.visibility == View.VISIBLE) {
            liner.visibility = View.GONE
        } else {
            AlertDialog.Builder(this)
                .setTitle("Keluar")
                .setMessage("Yakin?")
                .setPositiveButton("Ya") { _, _ -> finish() }
                .setNegativeButton("Tidak", null)
                .show()
        }
    }

    private fun AturGrid() {
        val grid = findViewById<GridLayout>(R.id.grid)
        grid.removeAllViews()

        val externalStorageDir = Environment.getExternalStorageDirectory()
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val allPossibleDirs = listOf(externalStorageDir, picturesDir, dcimDir, downloadDir)
        val allFiles = mutableListOf<File>()

        for (dir in allPossibleDirs) {
            dir.listFiles()?.let { filesInDir ->
                allFiles.addAll(filesInDir.filter {
                    it.isFile && (it.name.endsWith(".jpg", true) ||
                                  it.name.endsWith(".png", true) ||
                                  it.name.endsWith(".webp", true) ||
                                  it.name.endsWith(".gif", true))
                })
            }
        }

        Log.d("GambarActivity", "Jumlah total file gambar yang ditemukan: ${allFiles.size}")

        if (allFiles.isEmpty()) {
            Toast.makeText(this, "Tidak ada file gambar yang ditemukan di penyimpanan lokal!", Toast.LENGTH_LONG).show()
            return
        }

        for (file in allFiles) {
            try {
                val foto = ImageView(this)
                val bmp = BitmapFactory.decodeFile(file.absolutePath)

                if (bmp != null) {
                    foto.setImageBitmap(bmp)

                    val params = GridLayout.LayoutParams()
                    params.width = 50 
                    params.height = 50 
                    params.setMargins(8, 8, 8, 8)
                    foto.layoutParams = params
                    foto.scaleType = ImageView.ScaleType.CENTER_CROP // Penting untuk tampilan yang baik

                    foto.setOnClickListener {
                        liner.visibility = View.GONE
                        val utama = findViewById<ImageView>(R.id.utama)
                        utama.setImageBitmap(bmp)
                    }
                    grid.addView(foto)
                    Log.d("GambarActivity", "Gambar ditambahkan: ${file.name}")
                } else {
                    Log.w("GambarActivity", "Gagal mendekode gambar: ${file.name}")
                }
            } catch (e: Exception) {
                Log.e("GambarActivity", "Error memproses file ${file.name}: ${e.message}", e)
            }
        }
    }

    private fun AturKhusus(terima: File) {
        val utama = findViewById<ImageView>(R.id.utama)
        val bmp = BitmapFactory.decodeFile(terima.absolutePath)
        utama.setImageBitmap(bmp)
    }
}
