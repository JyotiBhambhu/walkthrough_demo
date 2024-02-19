package com.jyoti.walkthroughdemo

import androidx.annotation.StringRes
import com.jyoti.spotlight.TargetScreen
import com.jyoti.walkthroughdemo.WalkthroughScreen.Constants.SCREEN_WALKTHROUGH_HOME

sealed class WalkthroughScreen {
    data class Home(val targets: TargetHome) : WalkthroughScreen()
    object NONE : WalkthroughScreen()

    fun getScreenName() = when (this) {
        is Home -> SCREEN_WALKTHROUGH_HOME
        NONE -> "NA"
    }

    fun getId() = when (this) {
        is Home -> targets.targetId
        NONE -> -1
    }

    fun getLabel() = when (this) {
        is Home -> targets.targetLabel
        NONE -> R.string.na
    }

    fun getDescription() = when (this) {
        is Home -> targets.targetDescription
        NONE -> R.string.na
    }

    fun getTarget() =
        TargetScreen(
            targetId = getId(),
            targetLabel = getLabel(),
            targetDescription = getDescription()
        )

    object Constants {
        const val SCREEN_WALKTHROUGH_HOME = "screen_walkthrough_home"
        const val SCREEN_WALKTHROUGH_NONE = "screen_walkthrough_none"
    }
}

enum class TargetHome(
    val targetId: Int,
    @StringRes val targetLabel: Int,
    @StringRes val targetDescription: Int
) {
    TARGET_1(101, R.string.target_1, R.string.put_description_for_target_1_here),
    TARGET_5(105, R.string.target_5, R.string.put_description_for_target_5_here),
    TARGET_10(110, R.string.target_10, R.string.put_description_for_target_10_here)
}