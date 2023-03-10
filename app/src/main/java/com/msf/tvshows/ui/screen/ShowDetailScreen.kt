package com.msf.tvshows.ui.screen // ktlint-disable filename

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.msf.tvshows.BuildConfig
import com.msf.tvshows.R
import com.msf.tvshows.extensions.divideHalf
import com.msf.tvshows.model.detail.DetailResponse
import com.msf.tvshows.ui.components.Loading
import com.msf.tvshows.ui.components.Message
import com.msf.tvshows.ui.components.RatingBar
import com.msf.tvshows.ui.components.SeasonCard
import com.msf.tvshows.viewmodel.DetailViewModel
import com.msf.tvshows.viewmodel.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun ShowDetail(
    id: Long,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    detailViewModel: DetailViewModel = koinViewModel()
) {
    var show: DetailResponse? by remember {
        mutableStateOf(null)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    show?.let {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = it.name,
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = colorResource(id = R.color.white)
                            )
                        }
                    } ?: CircularProgressIndicator(color = colorResource(id = R.color.white))
                },
                backgroundColor = colorResource(id = R.color.primary),
                navigationIcon = if (navController.previousBackStackEntry != null) {
                    {
                        IconButton(
                            onClick = {
                                navController.navigateUp()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = colorResource(id = R.color.white)
                            )
                        }
                    }
                } else {
                    null
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = modifier.padding(innerPadding)) {
            val uiState by detailViewModel.uiState.collectAsState()
            detailViewModel.getDetail(id)
            when (uiState) {
                is UiState.Loading -> Loading()
                is UiState.Loaded<*> -> {
                    val detailResponse = (uiState as UiState.Loaded<*>).value as DetailResponse
                    show = detailResponse
                    BodyDetail(detailResponse)
                }
                is UiState.Error -> Message(message = (uiState as UiState.Error).message, true)
                else -> Message(message = "Please, try again", true)
            }
        }
    }
}

@Composable
fun BodyDetail(detailResponse: DetailResponse) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(
                top = 24.dp,
                bottom = 32.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {
        Text(
            text = "Summary",
            color = colorResource(id = R.color.primary),
            fontWeight = FontWeight(500),
            fontSize = 20.sp,
            lineHeight = 23.44.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = detailResponse.overview,
            color = colorResource(id = R.color.text),
            fontWeight = FontWeight(400),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        Column(
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            detailResponse.seasons.forEach { season ->
                SeasonCard(season) {}
            }
        }
    }
}
