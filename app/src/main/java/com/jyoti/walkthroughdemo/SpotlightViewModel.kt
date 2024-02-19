package com.jyoti.walkthroughdemo

import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.viewModelScope
import com.jyoti.spotlight.TargetScreen
import kotlinx.coroutines.launch

class SpotlightViewModel : MviViewModel<SpotlightState, SpotlightMviIntent, SpotlightReduceAction>(
    initialState = SpotlightState.initial
) {

    private fun validateNewTarget(
        target: com.jyoti.spotlight.Target,
        updateCords: (Rect) -> Unit,
        updateTarget: () -> Unit
    ) {
        target.rect.let { currentRect ->
            val rectNotContains = state.value.targets.none { it.rect == currentRect }
            if (rectNotContains) {
                updateCords(currentRect)
            }
            val targetNotContains =
                state.value.targets.none { it.targetScreen.targetId == target.targetScreen.targetId }
            if (targetNotContains) {
                updateTarget()
            }
        }
    }

    private fun fetchWalkthroughForTarget(
        targetScreen: TargetScreen,
        rect: Rect
    ) {
        viewModelScope.launch {
            val newTarget = com.jyoti.spotlight.Target(
                rect = rect,
                targetScreen = targetScreen
            )
            validateNewTarget(
                newTarget,
                updateCords = { rect ->
                    handle(SpotlightReduceAction.UpdateCoordinates(newTarget, rect))
                },
                updateTarget = {
                    handle(SpotlightReduceAction.UpdateTarget(newTarget))
                }
            )
        }
    }

    private fun getUpdatedTargetsAfterCoordinateUpdate(
        state: SpotlightState,
        newTarget: com.jyoti.spotlight.Target,
        rect: Rect
    ) = state.targets.map { target ->
        if (target.targetScreen.targetId == newTarget.targetScreen.targetId) {
            target.copy(rect = rect)
        } else {
            target
        }
    }

    private fun updateIsShow(
        currentTargetIndex: Int,
        targets: List<com.jyoti.spotlight.Target>,
    ) =
        currentTargetIndex in targets.indices

    override fun executeIntent(mviIntent: SpotlightMviIntent) {
        when (mviIntent) {
            is SpotlightMviIntent.TargetCordsUpdated -> {
                fetchWalkthroughForTarget(mviIntent.targetScreen, mviIntent.rect)
            }

            is SpotlightMviIntent.RemoveTarget -> handle(
                SpotlightReduceAction.RemoveTarget(
                    mviIntent.targetIndex
                )
            )

            is SpotlightMviIntent.ShowNextTarget -> {
                handle(
                    SpotlightReduceAction.ShowNextTarget(mviIntent.targetIndex)
                )
            }

            is SpotlightMviIntent.ShowPrevTarget -> handle(
                SpotlightReduceAction.ShowPrevTarget(mviIntent.targetIndex)
            )
        }
    }

    override fun reduce(
        state: SpotlightState,
        reduceAction: SpotlightReduceAction
    ): SpotlightState =
        when (reduceAction) {

            is SpotlightReduceAction.UpdateCoordinates -> {
                val updatedTargets = getUpdatedTargetsAfterCoordinateUpdate(
                    state,
                    newTarget = reduceAction.newTarget,
                    rect = reduceAction.rect
                )
                state.copy(
                    targets = updatedTargets,
                    progressState = SpotlightProgressState.STARTED,
                    showSpotlight = updateIsShow(
                        state.currentTargetIndex,
                        updatedTargets
                    )
                )
            }

            is SpotlightReduceAction.UpdateTarget -> {
                val updatedTargets = state.targets.toMutableList().apply {
                    add(reduceAction.target)
                }
                state.copy(
                    targets = updatedTargets,
                    showSpotlight = updateIsShow(
                        state.currentTargetIndex,
                        updatedTargets
                    )
                )
            }

            is SpotlightReduceAction.RemoveTarget -> {
                state.copy(
                    showSpotlight = false,
                    targets = state.targets.toMutableList().apply {
                        state.targets.getOrNull(state.currentTargetIndex)?.apply {
                            rect = Rect(0f, 0f, 0f, 0f)
                        }
                    },
                )
            }

            is SpotlightReduceAction.ShowNextTarget -> {
                state.copy(
                    currentTargetIndex = reduceAction.targetIndex,
                )
            }

            is SpotlightReduceAction.ShowPrevTarget -> state.copy(
                currentTargetIndex = reduceAction.targetIndex,
            )

        }
}

data class SpotlightState(
    val targets: List<com.jyoti.spotlight.Target>,
    var currentTargetIndex: Int,
    var showSpotlight: Boolean = false,
    var progressState: SpotlightProgressState
) : State {
    companion object {
        val initial = SpotlightState(
            targets = listOf(),
            currentTargetIndex = 0,
            showSpotlight = false,
            progressState = SpotlightProgressState.NOT_STARTED
        )
    }
}

enum class SpotlightProgressState() {
    NOT_STARTED, STARTED, DONE
}

sealed class SpotlightMviIntent : MviIntent {
    data class TargetCordsUpdated(
        val targetScreen: TargetScreen,
        val rect: Rect
    ) : SpotlightMviIntent()

    data class ShowNextTarget(val targetIndex: Int) : SpotlightMviIntent()

    data class ShowPrevTarget(val targetIndex: Int) : SpotlightMviIntent()

    data class RemoveTarget(val targetIndex: Int) : SpotlightMviIntent()

}

sealed class SpotlightReduceAction : ReduceAction {

    data class UpdateCoordinates(val newTarget: com.jyoti.spotlight.Target, val rect: Rect) :
        SpotlightReduceAction()

    data class UpdateTarget(val target: com.jyoti.spotlight.Target) : SpotlightReduceAction()

    data class RemoveTarget(val targetIndex: Int) : SpotlightReduceAction()

    data class ShowNextTarget(val targetIndex: Int) : SpotlightReduceAction()
    data class ShowPrevTarget(val targetIndex: Int) : SpotlightReduceAction()

}
