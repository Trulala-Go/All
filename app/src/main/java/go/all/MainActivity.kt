
package go.all

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MintaIjin()
        AturHome()

        findViewById<ImageView>(R.id.ijin).setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED )
            {
                MintaIjin()
                MulaiIjin()
            } else {
                Toast.makeText(this, "Ijin Siap", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.keluar).setOnClickListener {
            onBackPressed()
        }

        findViewById<LinearLayout>(R.id.berkas).setOnClickListener {
            startActivity(Intent(this, Berkas::class.java))
        }
        
        findViewById<LinearLayout>(R.id.browser).setOnClickListener {
            startActivity(Intent(this, Browser::class.java))
        }
        
        findViewById<LinearLayout>(R.id.gambar).setOnClickListener {
            startActivity(Intent(this, Gambar::class.java))
        }
        
        findViewById<LinearLayout>(R.id.terminal).setOnClickListener {
            startActivity(Intent(this, Terminal::class.java))
        }
        
        findViewById<LinearLayout>(R.id.editor).setOnClickListener {
            startActivity(Intent(this, Editor::class.java))
        }
        
        findViewById<LinearLayout>(R.id.video).setOnClickListener {
            startActivity(Intent(this, Video::class.java))
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Keluar Aplikasi")
            .setMessage("Yakin Ingin Keluar?")
            .setPositiveButton("Ya") { _, _ -> finish() }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun MintaIjin() {
        val ijin = findViewById<ImageView>(R.id.ijin)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                ijin.setImageResource(R.drawable.sd_terima)
            } else {
                ijin.setImageResource(R.drawable.sd_tolak)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                ijin.setImageResource(R.drawable.sd_terima)
            } else {
                ijin.setImageResource(R.drawable.sd_tolak)
            }
        }
    }

    private fun MulaiIjin() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + packageName)
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            val ijin = findViewById<ImageView>(R.id.ijin)
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ijin.setImageResource(R.drawable.sd_terima)
            } else {
                ijin.setImageResource(R.drawable.sd_tolak)
            }
        }
    }
    
    private fun AturHome() {
    val aktif = findViewById<Switch>(R.id.home)

    aktif.setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) {
            Toast.makeText(this, "Kembali ke Home Launcher", Toast.LENGTH_SHORT).show()
            HomeScreen()
        } else {
            Toast.makeText(this, "Tidak Aktif", Toast.LENGTH_SHORT).show()
        }
      }
    }
  
  private fun HomeScreen() {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_HOME)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
  }
  
}
