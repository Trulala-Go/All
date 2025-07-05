package go.all

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.os.Environment
import android.widget.*
import android.view.*
import android.content.Intent
import java.io.File

class Berkas : AppCompatActivity(){

  private lateinit var memo: File
  private var clipboard: File? = null
  private var modePotong: Boolean = false
  private var modeUrut = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.berkas)

    findViewById<TextView>(R.id.keluar).setOnClickListener {
      AlertDialog.Builder(this)
        .setMessage("Yakin Keluar")
        .setTitle("Keluar...")
        .setPositiveButton("Ya") { _, _ -> finish() }
        .setNegativeButton("Tidak", null)
        .show()
    }

    memo = Environment.getExternalStorageDirectory()
    JalurSekarang()
    AturGrid(memo)

    val liner = findViewById<LinearLayout>(R.id.liner)

    findViewById<ImageView>(R.id.nav).setOnClickListener {
      liner.visibility = if (liner.visibility == View.GONE) View.VISIBLE else View.GONE
    }

    findViewById<ImageView>(R.id.apk).setOnClickListener {
      liner.visibility = View.GONE
      memo = filesDir
      AturGrid(memo)
      JalurSekarang()
    }

    findViewById<ImageView>(R.id.sd).setOnClickListener {
      liner.visibility = View.GONE
      memo = Environment.getExternalStorageDirectory()
      AturGrid(memo)
      JalurSekarang()
    }
    
    findViewById<ImageView>(R.id.tambah).setOnClickListener{
      TambahBaru()
    }
    
    findViewById<ImageView>(R.id.urut).setOnClickListener{
      Mengurutkan()
    }
    
    findViewById<ImageView>(R.id.cari).setOnClickListener{
      Mencari()
    }
    
  }

  override fun onBackPressed() {
    val liner = findViewById<LinearLayout>(R.id.liner)
    if (liner.visibility == View.VISIBLE) {
      liner.visibility = View.GONE
    } else {
      Kembali()
    }
  }

  private fun Kembali() {
    val parent = memo.parentFile
    if (parent != null) {
      memo = parent
      AturGrid(memo)
      JalurSekarang()
    }
  }

  private fun JalurSekarang() {
    val jalur = findViewById<TextView>(R.id.jalur)
    jalur.text = memo.absolutePath
  }

  private fun AturGrid(dir: File) {
    val grid = findViewById<GridLayout>(R.id.grid)
    grid.removeAllViews()

    val files = dir.listFiles()?.toList() ?: return

    val terurut = when (modeUrut) {
      0 -> files.sortedBy { it.name.lowercase() }
      1 -> files.sortedByDescending { it.lastModified() }
      2 -> files.sortedByDescending { it.length() }
      else -> files
    }

    for (file in terurut) {
      val item = LayoutInflater.from(this).inflate(R.layout.item_vertical, grid, false)
      val gambar = item.findViewById<ImageView>(R.id.gambar)
      val nama = item.findViewById<TextView>(R.id.nama)

      nama.text = file.name
      gambar.setImageResource(if (file.isDirectory) R.drawable.folder else R.drawable.file)

      item.setOnClickListener {
          if (file.isDirectory) {
          memo = file
          AturGrid(memo)
          JalurSekarang()
          } else {
            BukaFile(file)
          }
      }
      
      item.setOnLongClickListener{
        TekanLama(file)
        true
      }

      grid.addView(item)
    }
  }
  
  private fun TekanLama(target:File){
    val lama = findViewById<LinearLayout>(R.id.lama)
    lama.visibility = View.VISIBLE
    
    val tempel = findViewById<ImageView>(R.id.tempel)
    
    findViewById<ImageView>(R.id.salin).setOnClickListener {
      clipboard = target
      modePotong = false
      tempel.visibility = View.VISIBLE
      Toast.makeText(this, "Disalin: ${target.name}", Toast.LENGTH_SHORT).show()
    }

    findViewById<ImageView>(R.id.potong).setOnClickListener {
      clipboard = target
      modePotong = true
      tempel.visibility = View.VISIBLE
      Toast.makeText(this, "Dipindah: ${target.name}", Toast.LENGTH_SHORT).show()
    }
    
    tempel.setOnClickListener {
      clipboard?.let { source ->
      val targetFile = File(memo, source.name)
      try {
        if (source.isDirectory) {
          source.copyRecursively(targetFile, true)
        } else {
          source.copyTo(targetFile, true)
        }
        if (modePotong) source.deleteRecursively()
        Toast.makeText(this, "Berhasil ditempel", Toast.LENGTH_SHORT).show()
      } catch (e: Exception) {
        Toast.makeText(this, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
        }
      }
      clipboard = null
      modePotong = false
      tempel.visibility = View.GONE
      lama.visibility = View.GONE
      AturGrid(memo)
    }
    
    findViewById<ImageView>(R.id.rename).setOnClickListener{
      lama.visibility = View.GONE
      NamaiUlang(target)
    }
    
    findViewById<ImageView>(R.id.hapus).setOnClickListener{
      lama.visibility = View.GONE
      Menghapus(target)
    }
    
    findViewById<ImageView>(R.id.lainya).setOnClickListener{
      lama.visibility = View.GONE
      KirimKeApk(target)
    }
    
    return
  }
  
  private fun NamaiUlang(target: File) {
  val tulis = EditText(this)
  tulis.setText(target.name)

  AlertDialog.Builder(this)
    .setTitle("Mengganti Nama")
    .setView(tulis) 
    .setPositiveButton("Simpan") { _, _ ->
      val namaBaru = tulis.text.toString()
      if (namaBaru.isNotEmpty()) {
        val fileBaru = File(target.parent, namaBaru)
        if (target.renameTo(fileBaru)) {
          Toast.makeText(this, "Berhasil mengganti nama", Toast.LENGTH_SHORT).show()
          AturGrid(memo)
        } else {
          Toast.makeText(this, "Gagal mengganti nama", Toast.LENGTH_SHORT).show()
        }
      }
    }
    .setNegativeButton("Batal", null)
    .show()
  }
  
  private fun Menghapus(target: File) {
  AlertDialog.Builder(this)
    .setTitle("Menghapus")
    .setMessage("Yakin menghapus ${target.name}?")
    .setNegativeButton("Tidak", null)
    .setPositiveButton("Yakin") { _, _ ->
      val berhasil = if (target.isDirectory) target.deleteRecursively() else target.delete()
      if (berhasil) {
        Toast.makeText(this, "${target.name} dihapus", Toast.LENGTH_SHORT).show()
        AturGrid(memo)
      } else {
        Toast.makeText(this, "Gagal menghapus ${target.name}", Toast.LENGTH_SHORT).show()
      }
    }
    .show()
  }
  
  private fun KirimKeApk(target: File) {
  val uri = androidx.core.content.FileProvider.getUriForFile(
    this,
    "$packageName.provider",
    target
  )

  val intent = Intent(Intent.ACTION_VIEW)
  intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
  intent.setDataAndType(uri, DapatkanMime(target))

  try {
    startActivity(Intent.createChooser(intent, "Buka dengan..."))
  } catch (e: Exception) {
    Toast.makeText(this, "Tidak bisa dibuka: ${e.message}", Toast.LENGTH_SHORT).show()
    }
  }
  
  private fun DapatkanMime(file: File): String {
  val ext = file.extension.lowercase()

  return when (ext) {
    "jpg", "jpeg", "png", "gif", "bmp", "webp" -> "image/*"
    "mp4", "mkv", "avi", "3gp", "webm"         -> "video/*"
    "mp3", "wav", "ogg", "m4a", "flac"         -> "audio/*"
    "txt", "log", "json", "xml", "html", "csv" -> "text/plain"
    "pdf"                                      -> "application/pdf"
    "doc", "docx"                              -> "application/msword"
    "xls", "xlsx"                              -> "application/vnd.ms-excel"
    "ppt", "pptx"                              -> "application/vnd.ms-powerpoint"
    "apk"                                      -> "application/vnd.android.package-archive"
    "zip", "rar", "7z"                         -> "application/zip"
    else                                       -> "*/*"
    }
  }
  
    private fun TambahBaru() {
    val opsi = arrayOf("FOLDER", "FILE")
    AlertDialog.Builder(this)
      .setTitle("Pilih Opsi")
      .setItems(opsi) { _, which ->
      Membuat(isFolder = which == 0)
      }
      .show()
  }
    
    private fun Membuat(isFolder: Boolean) {
    val tulis = EditText(this)
    tulis.hint = if (isFolder) "Nama folder..." else "Nama file..."

    AlertDialog.Builder(this)
    .setTitle(if (isFolder) "Buat Folder" else "Buat File")
    .setView(tulis)
    .setPositiveButton("Buat") { _, _ ->
      val nama = tulis.text.toString()
      if (nama.isNotEmpty()) {
        val baru = File(memo, nama)
        val berhasil = if (isFolder) baru.mkdir() else baru.createNewFile()

        if (berhasil) {
          Toast.makeText(this, "${if (isFolder) "Folder" else "File"} dibuat", Toast.LENGTH_SHORT).show()
          AturGrid(memo)
        } else {
          Toast.makeText(this, "Gagal membuat ${nama}", Toast.LENGTH_SHORT).show()
        }
      }
    }
    .setNegativeButton("Batal", null)
    .show()
  }
    
  private fun Mengurutkan() {
    val opsi = arrayOf("Nama", "Waktu", "Ukuran")
    AlertDialog.Builder(this)
    .setTitle("Berdasarkan..")
    .setItems(opsi) { _, which ->
      modeUrut = which
      AturGrid(memo)
    }
    .show()
  }
  
  private fun Mencari() {
  val tulis = EditText(this)
  tulis.hint = "Nama file/folder..."

  AlertDialog.Builder(this)
    .setTitle("Cari File/Folder")
    .setView(tulis)
    .setPositiveButton("Mulai") { _, _ ->
      val kata = tulis.text.toString().trim().lowercase()
      if (kata.isNotEmpty()) {
        val hasil = memo.listFiles()?.filter {
          it.name.lowercase().contains(kata)
        } ?: emptyList()

        if (hasil.isNotEmpty()) {
          TampilkanHasil(hasil)
        } else {
          Toast.makeText(this, "Tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
      }
    }
    .setNegativeButton("Batal", null)
    .show()
  }
  
  private fun TampilkanHasil(daftar: List<File>) {
  val grid = findViewById<GridLayout>(R.id.grid)
  grid.removeAllViews()

  for (file in daftar) {
    val item = LayoutInflater.from(this).inflate(R.layout.item_vertical, grid, false)
    val gambar = item.findViewById<ImageView>(R.id.gambar)
    val nama = item.findViewById<TextView>(R.id.nama)

    nama.text = file.name
    gambar.setImageResource(if (file.isDirectory) R.drawable.folder else R.drawable.file)

    item.setOnClickListener {
      if (file.isDirectory) {
        memo = file
        AturGrid(memo)
        JalurSekarang()
      } else {
        Toast.makeText(this, file.name, Toast.LENGTH_SHORT).show()
      }
    }

    item.setOnLongClickListener {
      TekanLama(file)
      true
    }

    grid.addView(item)
    }
  }
  
  private fun BukaFile(file: File) {
    val img = Intent(this, Gambar::class.java)
    img.putExtra("file", file.absolutePath) 
    
    val edit = Intent(this, Editor::class.java)
    edit.putExtra("file", file.absolutePath)
    
    AlertDialog.Builder(this)
        .setTitle("Pilih Opsi")
        .setItems(arrayOf("Text", "Foto", "Video", "Musik")) { _, which ->
            when (which) { 
                0 -> startActivity(edit)
                1 -> startActivity(img)
                2 -> Toast.makeText(this, "Belum Ada", Toast.LENGTH_SHORT).show()
                3 -> Toast.makeText(this, "Belum Ada", Toast.LENGTH_SHORT).show()
            }
        }.show()
  }

}