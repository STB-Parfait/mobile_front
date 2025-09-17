package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

import models.charClass.CharacterClass
import models.charClass.CharacterClasses
import models.character.race.Race
import models.character.race.Races

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CharacterCreation()
            }
        }
    }
}

@Composable
fun CharacterCreation(){
    Column(modifier = Modifier.padding(16.dp)){

        var nameInput by remember { mutableStateOf("") }
        var classInput by remember { mutableStateOf<CharacterClass?>(null) }
        var raceInput by remember { mutableStateOf<Race?>(null) }

        //nome do personagem
        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Character name")}
        )

        //classe do personagem
        CharacterClassDropdown(
            selectedClass = classInput,
            onClassSelected = { selectedClass ->
                classInput = selectedClass
            },
            dropdownLabel = "Classe"
        )

        //raça do personagem
        CharacterRaceDropdown(
            selectedRace = raceInput,
            onRaceSelected = { selectedRace ->
                raceInput = selectedRace
            },
            dropdownLabel = "Raça"
        )


    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterRaceDropdown(
    modifier: Modifier = Modifier,
    selectedRace: Race?,
    onRaceSelected: (Race) -> Unit,
    dropdownLabel: String = "Raça"
){
    var expanded by remember { mutableStateOf(false) }
    var availableRaces = remember { Races.listAll() }
    Box(modifier = modifier.fillMaxWidth()){
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = selectedRace?.raceName ?: "Selecione uma raça",
                onValueChange = {},
                readOnly = true,
                label = { Text(dropdownLabel) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = !expanded }
            ) {
                availableRaces.forEach { availableRace ->
                    DropdownMenuItem(
                        text = { Text(availableRace.raceName) },
                        onClick = {
                            onRaceSelected(availableRace)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterClassDropdown(
    modifier: Modifier = Modifier,
    selectedClass: CharacterClass?,
    onClassSelected: (CharacterClass) -> Unit,
    dropdownLabel: String = "Classe"
    ){
    var expanded by remember { mutableStateOf(false) }
    val availableClasses = remember { CharacterClasses.listAll() }
    Box(modifier = modifier.fillMaxWidth()){
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ){
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = selectedClass?.className ?: "Selecione uma classe",
                onValueChange = {},
                readOnly = true,
                label = { Text(dropdownLabel) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ){
                availableClasses.forEach { availableClass ->
                    DropdownMenuItem(
                        text = { Text(availableClass.className) },
                        onClick = {
                            onClassSelected(availableClass)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}