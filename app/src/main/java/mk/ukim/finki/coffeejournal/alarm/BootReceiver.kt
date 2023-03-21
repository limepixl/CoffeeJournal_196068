package mk.ukim.finki.coffeejournal.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import mk.ukim.finki.coffeejournal.MainActivity
import java.text.SimpleDateFormat
import java.util.*

class BootReceiver : BroadcastReceiver() {
    /*
    * restart reminders alarms when user's device reboots
    * */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            // TODO: TEMP
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MINUTE, 1)
            val timeString = SimpleDateFormat("hh:mm").format(calendar.time)
            MainActivity.RemindersManager.startReminder(context, timeString)
        }
    }
}