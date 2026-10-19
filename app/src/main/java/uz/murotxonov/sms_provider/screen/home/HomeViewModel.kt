package uz.murotxonov.sms_provider.screen.home

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.murotxonov.sms_provider.repo.Message
import uz.murotxonov.sms_provider.repo.WebSocketRepository

const val SMS_SENT_ACTION = "uz.murotxonov.sms_provider.SMS_SENT"
const val SMS_DELIVERED_ACTION = "uz.murotxonov.sms_provider.SMS_DELIVERED"

const val wsUrl="wss://echo.websocket.org"
class HomeViewModel(
    private val repository: WebSocketRepository = WebSocketRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        repository.connect(wsUrl, wbStatus = {statusWB ->
           viewModelScope.launch {
               _uiState.update { currentState->
                   currentState.copy(status =statusWB )
               }
           }
        })
        observeMessages()
    }

    fun clearItems(){
        viewModelScope.launch {
            _uiState.update { currentState->
                currentState.copy(receivedMessages = emptyList())
            }
        }
    }
    fun reconnect(){
        repository.connect(wsUrl, wbStatus = {statusWB ->
            viewModelScope.launch {
                _uiState.update { currentState->
                    currentState.copy(status =statusWB )
                }
            }
        })

        observeMessages()
    }
    fun sendLocalizedSms(context: Context, phoneNumber: String, messageBody: String) {
        val smsManager = context.getSystemService(SmsManager::class.java)

        val sentIntent = PendingIntent.getBroadcast(
            context, 0, Intent(SMS_SENT_ACTION), PendingIntent.FLAG_IMMUTABLE
        )
        val deliveredIntent = PendingIntent.getBroadcast(
            context, 0, Intent(SMS_DELIVERED_ACTION), PendingIntent.FLAG_IMMUTABLE
        )

        try{
            smsManager.sendTextMessage(phoneNumber, null, messageBody, sentIntent, deliveredIntent)
        }catch (e: Exception){
            _uiState.update { currentState ->
                currentState.copy(smsStatus = SmsStatus.Error, smsErrorMessage = e.message.toString())
            }
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            repository.messages.collect { message ->
                _uiState.update { currentState ->
                    var list: List<Message> =currentState.receivedMessages
                    list+=message
                    currentState.copy(
                        receivedMessages = list
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


