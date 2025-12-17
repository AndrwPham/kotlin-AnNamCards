package com.example.annam

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigator(
    navController: NavHostController,
    networkService: NetworkService,
    flashCardDao: FlashCardDao
) {
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
                navBack =navBack,
                flashCardDao = flashCardDao
            )
        }
        composable(route = "loginPage") {
            LoginPage(
                networkService= networkService,
                navigator = navController
            )
        }
        composable(
            route = "tokenPage/{email}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val emailArg = backStackEntry.arguments?.getString("email") ?: ""
            TokenScreen(
                email = emailArg,
                navBack = navBack,
                navigateToHome = {
                    navController.navigate("menu") {
                        popUpTo("menu") { inclusive = false }
                    }
                }
            )
        }
        composable(route = "study_card") {
            StudyCard(
                navBack = navBack,
                flashCardDao = flashCardDao
            )
        }
        
    }
}
