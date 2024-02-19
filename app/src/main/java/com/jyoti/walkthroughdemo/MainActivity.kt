package com.jyoti.walkthroughdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jyoti.spotlight.RenderSpotlight
import com.jyoti.spotlight.SpotlightActions
import com.jyoti.spotlight.TargetScreen
import com.jyoti.spotlight.getRect
import com.jyoti.walkthroughdemo.ui.theme.WalkthroughDemoTheme

class MainActivity : ComponentActivity() {
    private val spotlightViewModel: SpotlightViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WalkthroughDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val scrollState = rememberScrollState()
                    AppContent(scrollState, cordsUpdated = { targetScreen, layoutCords ->
                        spotlightViewModel.onIntent(
                            SpotlightMviIntent.TargetCordsUpdated(
                                targetScreen,
                                layoutCords.getRect()
                            )
                        )
                    })
                    val spotlightState by spotlightViewModel.state.collectAsStateWithLifecycle()
                    if (spotlightState.showSpotlight) {
                        RenderSpotlight(
                            spotLightIndex = spotlightState.currentTargetIndex,
                            targets = spotlightState.targets,
                            scrollState = scrollState,
                            spotlightActions = SpotlightActions(
                                removeTarget = {
                                    spotlightViewModel.onIntent(
                                        SpotlightMviIntent.RemoveTarget(it)
                                    )
                                },
                                showNextTarget = {
                                    spotlightViewModel.onIntent(
                                        SpotlightMviIntent.ShowNextTarget(it)
                                    )
                                },
                                showPrevTarget = {
                                    spotlightViewModel.onIntent(
                                        SpotlightMviIntent.ShowPrevTarget(it)
                                    )
                                }
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppContent(scrollState: ScrollState, cordsUpdated: (TargetScreen, LayoutCoordinates) -> Unit = { _: TargetScreen, _: LayoutCoordinates -> }) {
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)) {
        for (i in 1..10) {
            Greeting(
                "Android $i",
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        when (i) {
                            1 -> cordsUpdated(
                                WalkthroughScreen
                                    .Home(TargetHome.TARGET_1)
                                    .getTarget(),
                                coordinates
                            )

                            5 -> cordsUpdated(
                                WalkthroughScreen
                                    .Home(TargetHome.TARGET_5)
                                    .getTarget(),
                                coordinates
                            )

                            10 -> cordsUpdated(
                                WalkthroughScreen
                                    .Home(TargetHome.TARGET_10)
                                    .getTarget(),
                                coordinates
                            )
                        }
                    }
                    .padding(16.dp))
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WalkthroughDemoTheme {
        Greeting("Android")
    }
}