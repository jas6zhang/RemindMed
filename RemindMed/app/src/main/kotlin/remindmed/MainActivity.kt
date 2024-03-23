package remindmed

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.gradle.models.LoginModel
import com.gradle.ui.views.shared.Login
import com.example.remindmed.databinding.ActivityMainBinding
import com.gradle.constants.CHANNEL_ID
import com.gradle.ui.views.RemindMedApp


class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    private val PERMISSION_NOTIFICATION_CODE: Int = 100
    private val PERMISSION_SCHEDULE_CODE: Int = 100

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainViewModel: LoginModel by viewModels()
        mainViewModel.setContext(this)

        createNotificationChannel()

        setContent {
            Login(mainViewModel, applicationContext)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "medication reminders"
            val description = "Reminds Patients to take medications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = description
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_NOTIFICATION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
