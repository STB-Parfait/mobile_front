package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import models.charClass.CharacterClasses
import models.character.AttributeDistribution
import models.character.AttributeDistributionType
import models.character.Character
import models.character.race.Races

class CharacterList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val characterHeader = intent.getParcelableExtra<CharacterHeader>("header")
        val statsType = intent.getParcelableExtra<CreationInfo>("creation")

        lateinit var character: Character

        when(statsType?.type){
            1 -> {
                character = Character(AttributeDistributionType.CLASSIC)
            }
            2 -> {
                character = Character(AttributeDistributionType.HEROIC)
            }
            3 -> {
                val distribution = AttributeDistribution()
                val custom = distribution.distributePointsAdventurer(statsType.atributeSheet)
                character = Character(custom)
            }
        }

        character.name = characterHeader!!.name
        character.race = Races.findByName(characterHeader.race.raceName)
        character.characterClass = CharacterClasses.findByName(characterHeader.charClass.className)

        enableEdgeToEdge()
        setContent {
            CharacterCard(character)
        }
    }
}
@Composable
fun CharacterCard(character: Character, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Name: ${character.name}", style = MaterialTheme.typography.titleLarge)
            Text("Class: ${character.characterClass?.className ?: "Desconhecida"}")
            Text("Race: ${character.race?.raceName ?: "Desconhecida"}")

            Spacer(modifier = Modifier.padding(8.dp))

            Text("Skills:", style = MaterialTheme.typography.titleMedium)
            character.skills.forEach { (attr, value) ->
                Text("$attr: $value")
            }
        }
    }
}
