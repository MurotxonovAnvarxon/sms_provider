package uz.murotxonov.sms_provider.screen.home

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel(), context: Context) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 100.dp)
                .fillMaxWidth(),
            reverseLayout = false
        ) {
            items(uiState.receivedMessages) { message ->
                if(message.type=="sms"){
                    viewModel.sendLocalizedSms(
                        context = context,
                        phoneNumber = message.to,
                        messageBody = message.content
                    )
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "type: " + message.type,
                        modifier = Modifier.padding(12.dp)
                    )
                    Text(
                        text = "to: " + message.to,
                        modifier = Modifier.padding(12.dp)
                    )
                    Text(
                        text = "content: " + message.content,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 56.dp),
            horizontalArrangement = Arrangement.Center
        ) {

//            Button(
//                onClick = {
//                    sendLocalizedSms(
//                        context = context,
//                        phoneNumber = "+998 90 585 28 56",
//                        messageBody = "harry nima gap \n bordimi?"
//                    )

//                    textInput = ""
//                }
//            ) {
//                Text("Send")
//            }
        }
    }
}
