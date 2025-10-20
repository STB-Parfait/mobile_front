package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.CharacterEntity
import kotlinx.coroutines.launch
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

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "characters_db"
        ).build()

        val dao = db.characterDao()

        lifecycleScope.launch {
            val entity = CharacterEntity(
                name = character.name,
                race = character.race?.raceName ?: "Desconhecida",
                charClass = character.characterClass?.className ?: "Desconhecida",
                strength = character.skills.get("str")!!.toInt(),
                dexterity = character.skills.get("dex")!!.toInt(),
                constitution = character.skills.get("con")!!.toInt(),
                intelligence = character.skills.get("int")!!.toInt(),
                wisdom = character.skills.get("wis")!!.toInt(),
                charisma = character.skills.get("cha")!!.toInt()
            )
            dao.insert(entity)
        }

        enableEdgeToEdge()
        setContent {
            CharacterListScreen(db)
        }
    }
}
@Composable
fun CharacterListScreen(db: AppDatabase) {
    val dao = db.characterDao()
    val coroutineScope = rememberCoroutineScope()
    var characters by remember { mutableStateOf<List<CharacterEntity>>(emptyList()) }

    // Carrega os personagens ao abrir a tela
    LaunchedEffect(Unit) {
        characters = dao.getAll()
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(characters) { character ->
            CharacterEntityCard(character)
        }
    }
}
@Composable
fun CharacterEntityCard(character: CharacterEntity, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nome: ${character.name}", style = MaterialTheme.typography.titleLarge)
            Text("Classe: ${character.charClass}")
            Text("Raça: ${character.race}")

            Spacer(modifier = Modifier.padding(8.dp))

            Text("Atributos:", style = MaterialTheme.typography.titleMedium)
            Text("STR: ${character.strength}")
            Text("DEX: ${character.dexterity}")
            Text("CON: ${character.constitution}")
            Text("INT: ${character.intelligence}")
            Text("WIS: ${character.wisdom}")
            Text("CHA: ${character.charisma}")
        }
    }
}
