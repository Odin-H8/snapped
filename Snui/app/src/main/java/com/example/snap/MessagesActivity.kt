package com.example.snap

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun MessagesActivity(modifier: Modifier) {
    val messages: MutableList<Message> = remember { mutableStateListOf()}
    var msg by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        Messages.updateMsgs()
        messages.addAll(Messages.msgs)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        LazyColumn(
            modifier = modifier
                .weight(0.75f)
                .fillMaxWidth()
                .padding(8.dp),
            reverseLayout = false,
        ) {
            items(messages) { message ->

                if (Messages.CurrentUser == message.sender) {
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Text(
                            textAlign = TextAlign.End,
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Gray)
                                ) {
                                    append(message.sender + "\n")
                                }
                                append(message.msg)
                            }
                        )
                    }
                } else {
                    Row (
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Text(
                            textAlign = TextAlign.Start,
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Gray)
                                ) {
                                    append(message.sender + "\n")
                                }
                                append(message.msg)
                            }
                        )
                    }
                }
            }
        }
        Row(
            modifier = modifier
                .weight(0.10f)
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                modifier = modifier
                    .fillMaxWidth(),
                value = msg,
                onValueChange = { msgText -> msg = msgText },
                label = { Text("Enter message") }
            )
        }
        Row(
            modifier = modifier,
        ) {
            Button(
                modifier = modifier
                    .fillMaxWidth(),
                onClick = { ->
                    coroutineScope.launch {
                        Messages.sendMsg(Message(Messages.CurrentUser, "Martyna", msg))
                        Messages.updateMsgs()
                        messages.clear()
                        messages.addAll(Messages.msgs)

                        msg = ""
                    }
                }
            ) {
                Text("Send msg")
            }
        }
    }
}
