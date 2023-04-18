package mk.ukim.finki.coffeejournal

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import mk.ukim.finki.coffeejournal.alarm.AlarmReceiver
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fragmentContainerView: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO: TEMP
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, 1)
        val timeString = SimpleDateFormat("hh:mm").format(calendar.time)
        createNotificationsChannels()
        RemindersManager.startReminder(this, timeString)

        bottomNavigationView = findViewById(R.id.bottom_nav_view)
        fragmentContainerView = findViewById(R.id.fragmentContainerView)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.addJournalEntryFragment,
                R.id.viewJournalEntriesFragment,
                R.id.backupFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavigationView.setupWithNavController(navController)
    }

    private fun createNotificationsChannels() {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                val channel = NotificationChannel(
                    getString(R.string.reminders_notification_channel_id),
                    getString(R.string.reminders_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
                )
                ContextCompat.getSystemService(this, NotificationManager::class.java)
                    ?.createNotificationChannel(channel)
            }
        }

        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    object RemindersManager {
        private const val REMINDER_NOTIFICATION_REQUEST_CODE = 123

        fun startReminder(
            context: Context,
            reminderTime: String = "09:00",
            reminderId: Int = REMINDER_NOTIFICATION_REQUEST_CODE
        ) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val (hours, min) = reminderTime.split(":").map { it.toInt() }
            val intent =
                Intent(context.applicationContext, AlarmReceiver::class.java).let { intent ->
                    PendingIntent.getBroadcast(
                        context.applicationContext,
                        reminderId,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                }

            val calendar: Calendar = Calendar.getInstance(Locale.ENGLISH).apply {
                set(Calendar.HOUR_OF_DAY, hours)
                set(Calendar.MINUTE, min)
            }

            // If the trigger time you specify is in the past, the alarm triggers immediately.
            // if soo just add one day to required calendar
            // Note: i'm also adding one min cuz if the user clicked on the notification as soon as
            // he receive it it will reschedule the alarm to fire another notification immediately
            if (Calendar.getInstance(Locale.ENGLISH)
                    .apply { add(Calendar.MINUTE, 1) }.timeInMillis - calendar.timeInMillis > 0
            ) {
                calendar.add(Calendar.DATE, 1)
            }

            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(calendar.timeInMillis, intent), intent
            )
        }

        fun stopReminder(
            context: Context, reminderId: Int = REMINDER_NOTIFICATION_REQUEST_CODE
        ) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(
                    context, reminderId, intent, 0 or PendingIntent.FLAG_IMMUTABLE
                )
            }
            alarmManager.cancel(intent)
        }
    }
}