package uz.murotxonov.sms_provider.screen.home

import android.content.Context
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.murotxonov.sms_provider.repo.WebSocketRepository

class HomeViewModel(
    private val repository: WebSocketRepository = WebSocketRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
//        repository.connect("wss://ws-fapi.binance.com/ws-fapi/v1")
        repository.connect("wss://echo.websocket.org")
        observeMessages()
    }

    fun sendLocalizedSms(context: Context, phoneNumber: String, messageBody: String) {
        val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }

       var response= smsManager.sendTextMessage(
            phoneNumber,
            null,
            messageBody,
            null,
            null
        )

        Log.d("Response", "sendLocalizedSms: ${response.toString()}")
    }

    private fun observeMessages() {
        viewModelScope.launch {
            repository.messages.collect { message ->
                _uiState.update { currentState->
                    currentState.copy(status = Status.Loading)
                }
                _uiState.update { currentState ->
                    currentState.copy(
                        receivedMessages = currentState.receivedMessages + message
                    )
                }
            }
        }
    }

    fun sendMessage(message: String) {
        if (message.isNotBlank()) {
            repository.sendMessage(message)
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnect()
    }
}


