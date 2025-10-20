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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import android.os.Parcelable
import androidx.compose.material3.Button
import androidx.compose.ui.platform.LocalContext
import kotlinx.parcelize.Parcelize
import models.charClass.CharacterClass
import models.charClass.CharacterClasses
import models.character.race.Alignment
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

@Parcelize
data class ParHability(
    val habilityName: String,
    val description: String,
    val modType: String,
    val modStat: String,
    val modAmmount: Byte
): Parcelable

@Parcelize
class ParRace(
    val raceName: String,
    val infravision: Byte?,
    val movement: Byte,
    val preferredAlignment: Alignment,
    val racialHability: List<ParHability> = listOf<ParHability>()
) : Parcelable

@Parcelize
class ParCharacterClass(
    val className: String,
    val hitDie: Int,
    val primaryAttribute: String,
    val allowedArmor: List<String>,
    val allowedWeapons: List<String>
) : Parcelable

@Parcelize
class CharacterHeader(
    val name: String,
    val race: ParRace,
    val charClass: ParCharacterClass
) : Parcelable

@Composable
fun CharacterCreation(){
    val context = LocalContext.current
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
            dropdownLabel = "Race"
        )

        Button(
            onClick = {
                if (raceInput != null && classInput != null) {
                    val parRace = ParRace(
                        raceName = raceInput!!.raceName,
                        infravision = raceInput!!.infravision,
                        movement = raceInput!!.movement,
                        preferredAlignment = raceInput!!.preferredAlignment,
                        racialHability = raceInput!!.racialHability.map {
                            ParHability(
                                habilityName = it.habilityName,
                                description = it.description,
                                modType = it.modType,
                                modStat = it.modStat,
                                modAmmount = it.modAmmount
                            )
                        }
                    )

                    val parClass = ParCharacterClass(
                        className = classInput!!.className,
                        hitDie = classInput!!.hitDie,
                        primaryAttribute = classInput!!.primaryAttribute,
                        allowedArmor = classInput!!.allowedArmor,
                        allowedWeapons = classInput!!.allowedWeapons
                    )

                    if(nameInput==""){
                        nameInput = "Character"
                    }

                    val header = CharacterHeader(
                        name = nameInput,
                        race = parRace,
                        charClass = parClass
                    )

                    val intent = Intent(context, CharacterStats::class.java).apply {
                        putExtra("character", header)
                    }
                    context.startActivity(intent)
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Next")
        }


    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterRaceDropdown(
    modifier: Modifier = Modifier,
    selectedRace: Race?,
    onRaceSelected: (Race) -> Unit,
    dropdownLabel: String = "Race"
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
                value = selectedRace?.raceName ?: "Select a race",
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
                value = selectedClass?.className ?: "Select a class",
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