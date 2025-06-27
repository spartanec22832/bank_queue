package com.sfedu.bank_queue_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sfedu.bank_queue_android.ui.AppNavHost
import com.sfedu.bank_queue_android.ui.theme.Bank_queue_androidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Bank_queue_androidTheme {
                AppNavHost()
            }
        }
    }
}