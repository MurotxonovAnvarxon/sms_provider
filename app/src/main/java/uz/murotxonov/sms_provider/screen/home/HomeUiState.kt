package uz.murotxonov.sms_provider.screen.home

import uz.murotxonov.sms_provider.repo.Message

data class HomeUiState(
    val receivedMessages: List<Message> = emptyList(),
    val status: Status=Status.Init,
    val smsStatus: SmsStatus=SmsStatus.Init,
    val smsErrorMessage:String=""
)


enum class Status{
    Init,
    Connect,
    Disconnect,
    Failure,
}

enum class SmsStatus{
    Init,
    Ok,
    Error,
    Failure,

}

