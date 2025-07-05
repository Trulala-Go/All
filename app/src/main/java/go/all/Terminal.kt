package go.all

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import java.net.URL
import java.net.HttpURLConnection
import java.util.zip.GZIPInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream

class Terminal : AppCompatActivity() {

    external fun JalankanPerintah(perintah: String): String

    init {
        System.loadLibrary("proses")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.terminal)
        
        val proot = File(filesDir, "logika/proot")
          proot.setExecutable(true)

        findViewById<TextView>(R.id.keluar).setOnClickListener {
            onBackPressed()
        }

        findViewById<TextView>(R.id.kirim).setOnClickListener {
            JalankanPerintahDariInput()
            findViewById<EditText>(R.id.perintah).setText("")
        }
        
        LihatAlat()
    }

    private fun JalankanPerintahDariInput() {
    val hasil = findViewById<TextView>(R.id.hasil)
    val perintah = findViewById<EditText>(R.id.perintah).text.toString().trim()

    if (perintah == "clear") {
        hasil.text = ""
        return
    }

    val prootPath = File(applicationInfo.nativeLibraryDir, "libproses.so").absolutePath
    val rootfs = File(filesDir, "logika/rootfs").absolutePath
    val cmd = "$prootPath -S $rootfs -w /root -b /dev -b /proc -b /sys /bin/sh -c \"$perintah\""

    Log.d("Terminal", "Manggil JNI: $cmd")

    val output = try {
        JalankanPerintah(cmd)
    } catch (e: UnsatisfiedLinkError) {
        "Gagal load native lib: ${e.message}"
    } catch (e: Exception) {
        "Error saat eksekusi: ${e.message}"
    }

    Log.d("Terminal", "Hasil JNI: $output")
    hasil.append("\n$ $perintah\n$output\n")
  }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Keluar")
            .setMessage("Yakin Mau Keluar?")
            .setPositiveButton("Ya") { _, _ -> finish() }
            .setNegativeButton("Tidak", null)
            .show()
    }
    
    private fun LihatAlat() {
    val logika = File(filesDir, "logika")
    val hasil = findViewById<TextView>(R.id.hasil)

    if (!logika.exists()) logika.mkdirs()

    val rootfs = File(logika, "rootfs")
    if (rootfs.exists()) {
        hasil.append("\n$ rootfs sudah tersedia\n").also{EkstrakAlat()}
        return
    }

    hasil.append("\n$ Mengunduh rootfs ubuntu...\n")

    val url = URL("https://dl-cdn.alpinelinux.org/alpine/v3.19/releases/aarch64/alpine-minirootfs-3.19.0-aarch64.tar.gz")
    val rootTar = File(logika, "ubuntu.tar.gz")

    Thread {
        try {
            val connection = url.openConnection()
            val size = connection.contentLength
            val input = connection.getInputStream()
            val output = FileOutputStream(rootTar)

            val buffer = ByteArray(4096)
            var total = 0
            var count: Int

            while (input.read(buffer).also { count = it } != -1) {
                output.write(buffer, 0, count)
                total += count
                val persen = (total * 100 / size).coerceAtMost(100)
                runOnUiThread {
                    hasil.text = "\n$ Mengunduh... $persen%\n"
                }
            }

            output.close()
            input.close()

            runOnUiThread {
                hasil.append("$\nUnduh selesai: ${rootTar.name}\nSiap untuk ekstrak...\n").also{EkstrakAlat()}
            }

        } catch (e: Exception) {
            runOnUiThread {
                hasil.append("\n$ Gagal unduh rootfs: ${e.message}\n")
            }
        }
    }.start()
  }
  
  private fun EkstrakAlat() {
    val hasil = findViewById<TextView>(R.id.hasil)
    val alat = File(filesDir, "logika/ubuntu.tar.gz")
    val tempat = File(filesDir, "logika/rootfs")

    if (tempat.exists()) {
        hasil.append("\n$ ALAT UNTUK TERMINAL SUDAH SIAP\n")
    } else {
        hasil.append("\n$ Mengekstrak rootfs...\n")
        ProsesEkstraksi(alat, tempat, hasil)
    }
  }
  
  private fun ProsesEkstraksi(alat: File, tempat: File, hasil: TextView) {
    Thread {
        try {
            tempat.mkdirs()
            val gzipIn = GZIPInputStream(FileInputStream(alat))
            val tarIn = TarArchiveInputStream(gzipIn)

            var entry = tarIn.nextTarEntry
            while (entry != null) {
                val outFile = File(tempat, entry.name)
                if (entry.isDirectory) {
                    outFile.mkdirs()
                } else {
                    outFile.parentFile?.mkdirs()
                    val outStream = FileOutputStream(outFile)
                    tarIn.copyTo(outStream)
                    outStream.close()
                }
                entry = tarIn.nextTarEntry
            }

            tarIn.close()
            runOnUiThread {
                hasil.append("\n$ Ekstraksi selesai.\n")
            }
        } catch (e: Exception) {
            runOnUiThread {
                hasil.append("\n$ Gagal ekstrak: ${e.message}\n")
            }
        }
    }.start()
  }

}