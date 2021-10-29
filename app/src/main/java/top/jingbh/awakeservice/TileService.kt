package top.jingbh.awakeservice

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ProcessLifecycleOwner

class TileService : TileService() {
    private val serviceStarted = getServiceStarted()

    override fun onClick() {
        val intent = Intent(this, AwakeService::class.java)

        when (qsTile?.state) {
            Tile.STATE_ACTIVE -> stopService(intent)
            Tile.STATE_INACTIVE -> {
                if (checkForPermissions()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent)
                    } else {
                        startService(intent)
                    }
                }
            }
        }
    }

    override fun onStartListening() {
        serviceStarted.observe(ProcessLifecycleOwner.get(), observer)
        updateTile()
    }

    override fun onStopListening() {
        serviceStarted.removeObserver(observer)
    }

    private fun checkForPermissions(): Boolean {
        return if (Settings.canDrawOverlays(this)) {
            true
        } else {
            Toast.makeText(this, R.string.no_permission, Toast.LENGTH_LONG).show()

            val intent = if ("xiaomi" == Build.MANUFACTURER.lowercase()) {
                Intent("miui.intent.action.APP_PERM_EDITOR").apply {
                    setClassName("com.miui.securitycenter",
                        "com.miui.permcenter.permissions.PermissionsEditorActivity")
                    putExtra("extra_pkgname", packageName)
                }
            } else {
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                )
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

            false
        }
    }

    private fun updateTile() {
        qsTile?.apply {
            state = if (serviceStarted.value == true) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            updateTile()
        }
    }

    private val observer = Observer<Boolean> {
        updateTile()
    }
}
