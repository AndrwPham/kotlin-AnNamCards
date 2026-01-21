package com.example.annam

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

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
    NavHost(
        //modifier = Modifier.padding(innerPadding),
        navController = navController,
        startDestination = HomeRoute
    ) {
        // HOME
        composable<HomeRoute> {
            Menu(
                navigator = navController
            )
        }

        composable<SearchScreenRoute> {
            SearchCard(
                navBack= navBack,
                navigateToResults = { args ->
                    navController.navigate(args)
                }
            )
        }
        composable<SearchResultsRoute> { backStackEntry ->
            val searchArgs = backStackEntry.toRoute<SearchResultsRoute>()
            SearchResults(
                navBack = navBack,
                flashCardDao = flashCardDao,
                englishQuery = searchArgs.en,
                vietnameseQuery = searchArgs.vn,
                englishExact = searchArgs.exactByEnglish,
                vietnameseExact = searchArgs.exactByVietnamese,
                navigateToEdit = { args ->
                    navController.navigate(args)
                }
            )
        }
        composable<AddCardRoute> {
            AddCard(
                navBack =navBack,
                flashCardDao = flashCardDao
            )
        }
        composable<LoginRoute> {
            LoginPage(
                networkService= networkService,
                navigateToToken = { args ->
                    navController.navigate(args)
                },
                navBack = navBack

            )
        }
        composable<TokenRoute> { backStackEntry ->
            val emailArg = backStackEntry.toRoute<TokenRoute>()
            TokenScreen(
                email = emailArg.email,
                navBack = navBack,
                navigateToHome = { args ->
                    navController.navigate(args)
                }
            )
        }
        composable<StudyCardsRoute> {
            StudyCard(
                navBack = navBack,
                flashCardDao = flashCardDao,
                networkService = networkService
            )
        }
        composable<EditCardRoute>{ backStackEntry ->

            EditCard(
                navBack = navBack,
                flashCardDao = flashCardDao,
                english = backStackEntry.toRoute<EditCardRoute>().english,
                vietnamese = backStackEntry.toRoute<EditCardRoute>().vietnamese,
                networkService = networkService
            )
        }

    }
}
