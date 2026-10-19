package uz.murotxonov.sms_provider

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import uz.murotxonov.sms_provider.repo.SmsStatusReceiver
import uz.murotxonov.sms_provider.screen.home.HomeScreen
import uz.murotxonov.sms_provider.screen.home.SMS_DELIVERED_ACTION
import uz.murotxonov.sms_provider.screen.home.SMS_SENT_ACTION
import uz.murotxonov.sms_provider.ui.theme.Sms_providerTheme

class MainActivity : ComponentActivity() {
    private val smsReceiver = SmsStatusReceiver()

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter().apply {
            addAction(SMS_SENT_ACTION)
            addAction(SMS_DELIVERED_ACTION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(smsReceiver, intentFilter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(smsReceiver, intentFilter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SEND_SMS),
                101
            )
        }
        setContent {
            Sms_providerTheme {
                HomeScreen(context = applicationContext)
            }
        }
    }
}
