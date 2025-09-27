package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme


class CharacterStats : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val character = intent.getParcelableExtra<CharacterHeader>("character")

        setContent {
            MyApplicationTheme {
                MainMenu()
            }
        }
    }
}
@Composable
fun MainMenu(){
    var characterCreationType by remember { mutableStateOf(0) }
    var customAttributes by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var pointsLeft by remember { mutableStateOf(27) }
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 70.dp)
    ){
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                CustomButton(
                    text = "Classic",
                    isSelected = characterCreationType == 1,
                    onClick = {characterCreationType = 1}
                )
                Spacer(Modifier.width(8.dp))
                CustomButton(
                    text = "Heroic",
                    isSelected = characterCreationType == 2,
                    onClick = {characterCreationType = 2}
                )
                Spacer(Modifier.width(8.dp))
                CustomButton(
                    text = "Custom",
                    isSelected = characterCreationType == 3,
                    onClick = {characterCreationType = 3}
                )
            }
            if (characterCreationType == 3) {
                CustomAttributesMenu(
                    totalPoints = 27,
                    onUpdate = { attrs, left ->
                        customAttributes = attrs
                        pointsLeft = left
                    }
                )
            }
        }
        Button(
            onClick = {
                when (characterCreationType) {
                    1 -> println("Confirmou Classic")
                    2 -> println("Confirmou Heroic")
                    3 -> println("Confirmou Custom: $customAttributes")
                }
            },
            enabled = characterCreationType != 0 && (characterCreationType != 3 || pointsLeft == 0), // só bloqueia se Custom não terminou
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Confirmar")
        }
    }
}
@Composable
fun CustomButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if(isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
            contentColor = if(isSelected) Color.White else Color.Black
        )
    ) { Text(text)}
}
@Composable
fun CustomAttributesMenu(
    totalPoints: Int = 27,
    onUpdate: (Map<String, Int>, Int) -> Unit
) {
    var attributes by remember {
        mutableStateOf(
            mutableMapOf(
                "str" to 0,
                "dex" to 0,
                "con" to 0,
                "int" to 0,
                "wis" to 0,
                "cha" to 0
            )
        )
    }

    val pointsSpent = attributes.values.sum()
    val pointsLeft = totalPoints - pointsSpent

    // Sempre envia atualização pro pai
    LaunchedEffect(attributes) {
        onUpdate(attributes, pointsLeft)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Pontos restantes: $pointsLeft")

        attributes.forEach { (attr, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(attr.uppercase())
                Row {
                    Button(
                        onClick = {
                            if (value > 0) attributes = attributes.toMutableMap().also {
                                it[attr] = value - 1
                            }
                        },
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("-") }

                    Text(value.toString(), modifier = Modifier.padding(horizontal = 8.dp))

                    Button(
                        onClick = {
                            if (pointsLeft > 0) attributes = attributes.toMutableMap().also {
                                it[attr] = value + 1
                            }
                        },
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("+") }
                }
            }
        }
    }
}
