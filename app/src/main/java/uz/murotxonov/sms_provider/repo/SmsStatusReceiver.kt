package uz.murotxonov.sms_provider.repo

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.widget.Toast
import uz.murotxonov.sms_provider.screen.home.SMS_DELIVERED_ACTION
import uz.murotxonov.sms_provider.screen.home.SMS_SENT_ACTION

class SmsStatusReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            SMS_SENT_ACTION -> {
                when (resultCode) {
                    Activity.RESULT_OK -> Toast.makeText(context, "SMS Sent Successfully!", Toast.LENGTH_SHORT).show()
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(context, "Generic Failure", Toast.LENGTH_SHORT).show()
                    SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(context, "No Service", Toast.LENGTH_SHORT).show()
                    SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(context, "Null PDU Error", Toast.LENGTH_SHORT).show()
                    SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(context, "Radio Off", Toast.LENGTH_SHORT).show()
                }
            }
            SMS_DELIVERED_ACTION -> {
                when (resultCode) {
                    Activity.RESULT_OK -> Toast.makeText(context, "SMS Delivered Successfully!", Toast.LENGTH_SHORT).show()
                    Activity.RESULT_CANCELED -> Toast.makeText(context, "SMS Delivery Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
