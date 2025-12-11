package com.example.annam

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun AddCard(navBack: () -> Unit){
//        var enWord = ""
//
//        var vnWord = ""

        var enWord by remember { mutableStateOf("") }

        var vnWord by remember { mutableStateOf("") }

        //var enWord by rememberSaveable { mutableStateOf("") }

        //var vnWord by rememberSaveable { mutableStateOf("") }

        Column() {

            TextField(

                value = enWord,

                onValueChange = { enWord = it },

                modifier = Modifier.semantics{contentDescription = "English String"},

                label = { Text("en") }

            )

            TextField(

                value = vnWord,

                onValueChange = { vnWord = it },

                label = { Text("vn") }

            )

            Button(onClick = {
                enWord="hello"

                Log.d(

                    "TEST", "Adding a card with words: "

                            + enWord + " and " + vnWord

                )

            }) {

                Text("Add")

            }

        }

    }
