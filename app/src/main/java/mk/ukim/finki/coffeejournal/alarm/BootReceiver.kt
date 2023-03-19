package mk.ukim.finki.coffeejournal.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import mk.ukim.finki.coffeejournal.MainActivity

class BootReceiver : BroadcastReceiver() {
    /*
    * restart reminders alarms when user's device reboots
    * */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            MainActivity.RemindersManager.startReminder(context)
        }
    }
}