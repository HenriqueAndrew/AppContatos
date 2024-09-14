package br.edu.utfpr.appcontatos.ui.contact

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.utfpr.appcontatos.R
import br.edu.utfpr.appcontatos.data.Contact
import br.edu.utfpr.appcontatos.ui.theme.AppContatosTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun ContactListScreen(modifier: Modifier = Modifier, coroutineScope: CoroutineScope = rememberCoroutineScope()) {

    val isInitialComposition: MutableState<Boolean> = rememberSaveable { mutableStateOf(true) }
    val isLoading: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) }
    val hasError: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) }
    val contacts: MutableState<List<Contact>> = remember {mutableStateOf(generateContacts())}

    val loadContacts : () -> Unit = {
        isLoading.value = true
        hasError.value = false

        coroutineScope.launch {
            delay(3000)
            hasError.value = Random.nextBoolean()
            if (!hasError.value) {
                val isEmpty = Random.nextBoolean()
                if (isEmpty) {
                    contacts.value = listOf()
                } else{
                    contacts.value = generateContacts()
                }
            }
            isLoading.value = false
        }
    }

    if (isInitialComposition.value) {
        loadContacts()
        isInitialComposition.value = false
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {AppBar(onRefreshState = loadContacts) },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = {
                contacts.value = contacts.value.plus(
                    Contact(firstName = "Novo", lastName = "Contato")
                )
            }){
                Icon(imageVector = Icons.Filled.Add, contentDescription = stringResource(R.string.adicionar))
                Spacer(Modifier.size(8.dp))
                Text(text = "Novo Contato")
            }
        }
    ){ paddingValues ->
        val defaultModifier = Modifier.padding(paddingValues)
        if (isLoading.value) {
            LoadingContent(modifier = defaultModifier)
            } else if (hasError.value) {
            ErrorContent(
                modifier = defaultModifier,
                onTryAgainPressed = loadContacts
            )
        }else if (contacts.value.isEmpty()) {
             EmptyList(modifier = defaultModifier)
        }else{
            List(
                modifier = defaultModifier,
                contacts = contacts.value
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(modifier: Modifier = Modifier, onRefreshState: () -> Unit) {
    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            Text(stringResource(R.string.contacts))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.primary
        ),
        actions = {
            IconButton(onClick = onRefreshState) {
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = stringResource(R.string.atualizar))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun AppBarPreview() {
    AppContatosTheme {
        AppBar(onRefreshState = {})
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = stringResource(R.string.carregando_contatos),
            style = MaterialTheme.typography.titleLarge, //.copy(color = Color.Gray)
            color = MaterialTheme.colorScheme.primary,
            )
    }

}

@Preview(showBackground = true, heightDp = 400)
@Composable
private fun LoadingContentPreview() {
    AppContatosTheme {
        LoadingContent()
    }
}

@Composable
private fun ErrorContent(modifier: Modifier = Modifier, onTryAgainPressed: () -> Unit) {

    Column(modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
        {
            val defaultColor = MaterialTheme.colorScheme.primary
            Icon(
                //imageVector = Icons.Outlined.Warning,
                imageVector = Icons.Filled.CloudOff,
                contentDescription = stringResource(R.string.erro_ao_carregar),
                modifier = modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary //.error
            )
            val textPadding = PaddingValues(top = 8.dp, start = 8.dp, end = 8.dp)
            Text(
                modifier = Modifier.padding(textPadding),
                text = stringResource(R.string.erro_ao_carregar),
                style = MaterialTheme.typography.titleLarge,
                color = defaultColor
            )
            Text(
                modifier = Modifier.padding(textPadding),
                text = stringResource(R.string.aguarde_um_momento_e_tente_novamente),
                style = MaterialTheme.typography.titleSmall,
                color = defaultColor
            )
            ElevatedButton(
                onClick = onTryAgainPressed,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(stringResource(R.string.tentar_novamente))
        }
    }

}

@Preview(showBackground = true, heightDp = 400)
@Composable
private fun ErrorContentPreview() {
    AppContatosTheme {
        ErrorContent(onTryAgainPressed = {})
    }
}

@Composable
fun EmptyList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(all = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Image(
            painter = painterResource(id = R.drawable.nodata),
            contentDescription = stringResource(R.string.nada_por_aqui2)
        )
        Text(
            text = "Nada por aqui...",
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            stringResource(R.string.nenhum_contato),
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyListPreview() {
    AppContatosTheme {
        EmptyList()
    }
}


@Composable
private fun List(modifier: Modifier = Modifier, contacts: List<Contact>) {
    LazyColumn(modifier = modifier)
    {
        items(contacts){contact -> ContactListItem(contact = contact)}
    }
}

@Composable
private fun ContactListItem(modifier: Modifier = Modifier, contact: Contact) {
    val isFavorite : MutableState<Boolean> = rememberSaveable {mutableStateOf(contact.isFavorite) }
    ListItem(
        modifier = modifier,
        headlineContent = { Text(contact.fullName) },
        leadingContent = {},
        trailingContent = {
            IconButton(onClick = {
                isFavorite.value = !isFavorite.value
            }) {
                Icon(
                    imageVector = if (isFavorite.value) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Filled.FavoriteBorder
                    },
                    contentDescription = "Favoritar",
                    tint = if (isFavorite.value) {
                        Color.Red
                    } else {
                        LocalContentColor.current
                    }
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ListPreview() {
    AppContatosTheme {
        List(
            contacts = generateContacts()
        )
    }
}

private fun generateContacts(): List<Contact> {
    val firstNames = listOf(
        "João", "José", "Everton", "Marcos", "André", "Anderson", "Antônio",
        "Laura", "Ana", "Maria", "Joaquina", "Suelen"
    )
    val lastNames = listOf(
        "Do Carmo", "Oliveira", "Dos Santos", "Da Silva", "Brasil", "Pichetti",
        "Cordeiro", "Silveira", "Andrades", "Cardoso"
    )
    val contacts: MutableList<Contact> = mutableListOf()
    for (i in 0..19) {
        var generatedNewContact = false
        while (!generatedNewContact) {
            val firstNameIndex = Random.nextInt(firstNames.size)
            val lastNameIndex = Random.nextInt(lastNames.size)
            val newContact = Contact(
                id = i + 1,
                firstName = firstNames[firstNameIndex],
                lastName = lastNames[lastNameIndex]
            )
            if (!contacts.any { it.fullName == newContact.fullName }) {
                contacts.add(newContact)
                generatedNewContact = true
            }
        }
    }
    return contacts
}
