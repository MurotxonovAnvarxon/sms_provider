package uz.murotxonov.sms_provider.screen.home

import uz.murotxonov.sms_provider.repo.Message

data class HomeUiState(
    val receivedMessages: List<Message> = emptyList(),
    val status: Status=Status.Loading
)


enum class Status{
    Loading,
    Error,
    Success,
    Failure
}