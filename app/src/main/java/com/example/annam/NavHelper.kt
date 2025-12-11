package com.example.annam

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigator(navController: NavHostController, networkService: NetworkService) {
    val navController = rememberNavController()
    val navBack = fun () {
        navController.navigateUp()
    }
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                colors = topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.primary,
//                ),
//                title = {
//                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
//                        Text(modifier = Modifier.semantics {
//                            contentDescription = "user"
//                        },
//                            text = "An Nam")
//                    }
//                },
//                navigationIcon = {
//                    val currentRoute =
//                        navController.currentBackStackEntryAsState().value?.destination?.route
//                    if (currentRoute != "home") {
//                        IconButton(onClick = {
//                            navController.navigateUp()
//                        }){
//                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
//                        }
//                    } else {
//    // Show drawer icon or nothing on home
//                   }
//               }
//           )
//       },
//    ) { innerPadding ->
    NavHost(
        //modifier = Modifier.padding(innerPadding),
        navController = navController,
        startDestination = "menu"
    ) {
        // HOME
        composable(route = "menu") {
            Menu(
                navigator = navController
            )
        }

        composable(route = "search_card") {
            SearchCard(
                navigator = navController
            )
        }
        composable(route = "add_card") {
            AddCard(
                navBack =navBack
            )
        }
        composable(route = "loginPage") {
            LoginPage(
                networkService= networkService
            )
        }
    }
}
//}
//}
