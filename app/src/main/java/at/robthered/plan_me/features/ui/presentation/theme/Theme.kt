package at.robthered.plan_me.features.ui.presentation.theme


import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Immutable
data class ExtendedColorScheme(
    val success: ColorFamily,
    val project: ColorFamily,
    val section: ColorFamily,
    val task: ColorFamily,
    val priorityLow: ColorFamily,
    val priorityNormal: ColorFamily,
    val priorityMedium: ColorFamily,
    val priorityHigh: ColorFamily,
    val scheduleEvent: ColorFamily,
    val hashtag: ColorFamily,
)

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

val extendedLight = ExtendedColorScheme(
    success = ColorFamily(
        successLight,
        onSuccessLight,
        successContainerLight,
        onSuccessContainerLight,
    ),
    project = ColorFamily(
        projectLight,
        onProjectLight,
        projectContainerLight,
        onProjectContainerLight,
    ),
    section = ColorFamily(
        sectionLight,
        onSectionLight,
        sectionContainerLight,
        onSectionContainerLight,
    ),
    task = ColorFamily(
        taskLight,
        onTaskLight,
        taskContainerLight,
        onTaskContainerLight,
    ),
    priorityLow = ColorFamily(
        priorityLowLight,
        onPriorityLowLight,
        priorityLowContainerLight,
        onPriorityLowContainerLight,
    ),
    priorityNormal = ColorFamily(
        priorityNormalLight,
        onPriorityNormalLight,
        priorityNormalContainerLight,
        onPriorityNormalContainerLight,
    ),
    priorityMedium = ColorFamily(
        priorityMediumLight,
        onPriorityMediumLight,
        priorityMediumContainerLight,
        onPriorityMediumContainerLight,
    ),
    priorityHigh = ColorFamily(
        priorityHighLight,
        onPriorityHighLight,
        priorityHighContainerLight,
        onPriorityHighContainerLight,
    ),
    scheduleEvent = ColorFamily(
        scheduleEventLight,
        onScheduleEventLight,
        scheduleEventContainerLight,
        onScheduleEventContainerLight,
    ),
    hashtag = ColorFamily(
        hashtagLight,
        onHashtagLight,
        hashtagContainerLight,
        onHashtagContainerLight,
    ),
)

val extendedDark = ExtendedColorScheme(
    success = ColorFamily(
        successDark,
        onSuccessDark,
        successContainerDark,
        onSuccessContainerDark,
    ),
    project = ColorFamily(
        projectDark,
        onProjectDark,
        projectContainerDark,
        onProjectContainerDark,
    ),
    section = ColorFamily(
        sectionDark,
        onSectionDark,
        sectionContainerDark,
        onSectionContainerDark,
    ),
    task = ColorFamily(
        taskDark,
        onTaskDark,
        taskContainerDark,
        onTaskContainerDark,
    ),
    priorityLow = ColorFamily(
        priorityLowDark,
        onPriorityLowDark,
        priorityLowContainerDark,
        onPriorityLowContainerDark,
    ),
    priorityNormal = ColorFamily(
        priorityNormalDark,
        onPriorityNormalDark,
        priorityNormalContainerDark,
        onPriorityNormalContainerDark,
    ),
    priorityMedium = ColorFamily(
        priorityMediumDark,
        onPriorityMediumDark,
        priorityMediumContainerDark,
        onPriorityMediumContainerDark,
    ),
    priorityHigh = ColorFamily(
        priorityHighDark,
        onPriorityHighDark,
        priorityHighContainerDark,
        onPriorityHighContainerDark,
    ),
    scheduleEvent = ColorFamily(
        scheduleEventDark,
        onScheduleEventDark,
        scheduleEventContainerDark,
        onScheduleEventContainerDark,
    ),
    hashtag = ColorFamily(
        hashtagDark,
        onHashtagDark,
        hashtagContainerDark,
        onHashtagContainerDark,
    ),
)

val extendedLightMediumContrast = ExtendedColorScheme(
    success = ColorFamily(
        successLightMediumContrast,
        onSuccessLightMediumContrast,
        successContainerLightMediumContrast,
        onSuccessContainerLightMediumContrast,
    ),
    project = ColorFamily(
        projectLightMediumContrast,
        onProjectLightMediumContrast,
        projectContainerLightMediumContrast,
        onProjectContainerLightMediumContrast,
    ),
    section = ColorFamily(
        sectionLightMediumContrast,
        onSectionLightMediumContrast,
        sectionContainerLightMediumContrast,
        onSectionContainerLightMediumContrast,
    ),
    task = ColorFamily(
        taskLightMediumContrast,
        onTaskLightMediumContrast,
        taskContainerLightMediumContrast,
        onTaskContainerLightMediumContrast,
    ),
    priorityLow = ColorFamily(
        priorityLowLightMediumContrast,
        onPriorityLowLightMediumContrast,
        priorityLowContainerLightMediumContrast,
        onPriorityLowContainerLightMediumContrast,
    ),
    priorityNormal = ColorFamily(
        priorityNormalLightMediumContrast,
        onPriorityNormalLightMediumContrast,
        priorityNormalContainerLightMediumContrast,
        onPriorityNormalContainerLightMediumContrast,
    ),
    priorityMedium = ColorFamily(
        priorityMediumLightMediumContrast,
        onPriorityMediumLightMediumContrast,
        priorityMediumContainerLightMediumContrast,
        onPriorityMediumContainerLightMediumContrast,
    ),
    priorityHigh = ColorFamily(
        priorityHighLightMediumContrast,
        onPriorityHighLightMediumContrast,
        priorityHighContainerLightMediumContrast,
        onPriorityHighContainerLightMediumContrast,
    ),
    scheduleEvent = ColorFamily(
        scheduleEventLightMediumContrast,
        onScheduleEventLightMediumContrast,
        scheduleEventContainerLightMediumContrast,
        onScheduleEventContainerLightMediumContrast,
    ),
    hashtag = ColorFamily(
        hashtagLightMediumContrast,
        onHashtagLightMediumContrast,
        hashtagContainerLightMediumContrast,
        onHashtagContainerLightMediumContrast,
    ),
)

val extendedLightHighContrast = ExtendedColorScheme(
    success = ColorFamily(
        successLightHighContrast,
        onSuccessLightHighContrast,
        successContainerLightHighContrast,
        onSuccessContainerLightHighContrast,
    ),
    project = ColorFamily(
        projectLightHighContrast,
        onProjectLightHighContrast,
        projectContainerLightHighContrast,
        onProjectContainerLightHighContrast,
    ),
    section = ColorFamily(
        sectionLightHighContrast,
        onSectionLightHighContrast,
        sectionContainerLightHighContrast,
        onSectionContainerLightHighContrast,
    ),
    task = ColorFamily(
        taskLightHighContrast,
        onTaskLightHighContrast,
        taskContainerLightHighContrast,
        onTaskContainerLightHighContrast,
    ),
    priorityLow = ColorFamily(
        priorityLowLightHighContrast,
        onPriorityLowLightHighContrast,
        priorityLowContainerLightHighContrast,
        onPriorityLowContainerLightHighContrast,
    ),
    priorityNormal = ColorFamily(
        priorityNormalLightHighContrast,
        onPriorityNormalLightHighContrast,
        priorityNormalContainerLightHighContrast,
        onPriorityNormalContainerLightHighContrast,
    ),
    priorityMedium = ColorFamily(
        priorityMediumLightHighContrast,
        onPriorityMediumLightHighContrast,
        priorityMediumContainerLightHighContrast,
        onPriorityMediumContainerLightHighContrast,
    ),
    priorityHigh = ColorFamily(
        priorityHighLightHighContrast,
        onPriorityHighLightHighContrast,
        priorityHighContainerLightHighContrast,
        onPriorityHighContainerLightHighContrast,
    ),
    scheduleEvent = ColorFamily(
        scheduleEventLightHighContrast,
        onScheduleEventLightHighContrast,
        scheduleEventContainerLightHighContrast,
        onScheduleEventContainerLightHighContrast,
    ),
    hashtag = ColorFamily(
        hashtagLightHighContrast,
        onHashtagLightHighContrast,
        hashtagContainerLightHighContrast,
        onHashtagContainerLightHighContrast,
    ),
)

val extendedDarkMediumContrast = ExtendedColorScheme(
    success = ColorFamily(
        successDarkMediumContrast,
        onSuccessDarkMediumContrast,
        successContainerDarkMediumContrast,
        onSuccessContainerDarkMediumContrast,
    ),
    project = ColorFamily(
        projectDarkMediumContrast,
        onProjectDarkMediumContrast,
        projectContainerDarkMediumContrast,
        onProjectContainerDarkMediumContrast,
    ),
    section = ColorFamily(
        sectionDarkMediumContrast,
        onSectionDarkMediumContrast,
        sectionContainerDarkMediumContrast,
        onSectionContainerDarkMediumContrast,
    ),
    task = ColorFamily(
        taskDarkMediumContrast,
        onTaskDarkMediumContrast,
        taskContainerDarkMediumContrast,
        onTaskContainerDarkMediumContrast,
    ),
    priorityLow = ColorFamily(
        priorityLowDarkMediumContrast,
        onPriorityLowDarkMediumContrast,
        priorityLowContainerDarkMediumContrast,
        onPriorityLowContainerDarkMediumContrast,
    ),
    priorityNormal = ColorFamily(
        priorityNormalDarkMediumContrast,
        onPriorityNormalDarkMediumContrast,
        priorityNormalContainerDarkMediumContrast,
        onPriorityNormalContainerDarkMediumContrast,
    ),
    priorityMedium = ColorFamily(
        priorityMediumDarkMediumContrast,
        onPriorityMediumDarkMediumContrast,
        priorityMediumContainerDarkMediumContrast,
        onPriorityMediumContainerDarkMediumContrast,
    ),
    priorityHigh = ColorFamily(
        priorityHighDarkMediumContrast,
        onPriorityHighDarkMediumContrast,
        priorityHighContainerDarkMediumContrast,
        onPriorityHighContainerDarkMediumContrast,
    ),
    scheduleEvent = ColorFamily(
        scheduleEventDarkMediumContrast,
        onScheduleEventDarkMediumContrast,
        scheduleEventContainerDarkMediumContrast,
        onScheduleEventContainerDarkMediumContrast,
    ),
    hashtag = ColorFamily(
        hashtagDarkMediumContrast,
        onHashtagDarkMediumContrast,
        hashtagContainerDarkMediumContrast,
        onHashtagContainerDarkMediumContrast,
    ),
)

val extendedDarkHighContrast = ExtendedColorScheme(
    success = ColorFamily(
        successDarkHighContrast,
        onSuccessDarkHighContrast,
        successContainerDarkHighContrast,
        onSuccessContainerDarkHighContrast,
    ),
    project = ColorFamily(
        projectDarkHighContrast,
        onProjectDarkHighContrast,
        projectContainerDarkHighContrast,
        onProjectContainerDarkHighContrast,
    ),
    section = ColorFamily(
        sectionDarkHighContrast,
        onSectionDarkHighContrast,
        sectionContainerDarkHighContrast,
        onSectionContainerDarkHighContrast,
    ),
    task = ColorFamily(
        taskDarkHighContrast,
        onTaskDarkHighContrast,
        taskContainerDarkHighContrast,
        onTaskContainerDarkHighContrast,
    ),
    priorityLow = ColorFamily(
        priorityLowDarkHighContrast,
        onPriorityLowDarkHighContrast,
        priorityLowContainerDarkHighContrast,
        onPriorityLowContainerDarkHighContrast,
    ),
    priorityNormal = ColorFamily(
        priorityNormalDarkHighContrast,
        onPriorityNormalDarkHighContrast,
        priorityNormalContainerDarkHighContrast,
        onPriorityNormalContainerDarkHighContrast,
    ),
    priorityMedium = ColorFamily(
        priorityMediumDarkHighContrast,
        onPriorityMediumDarkHighContrast,
        priorityMediumContainerDarkHighContrast,
        onPriorityMediumContainerDarkHighContrast,
    ),
    priorityHigh = ColorFamily(
        priorityHighDarkHighContrast,
        onPriorityHighDarkHighContrast,
        priorityHighContainerDarkHighContrast,
        onPriorityHighContainerDarkHighContrast,
    ),
    scheduleEvent = ColorFamily(
        scheduleEventDarkHighContrast,
        onScheduleEventDarkHighContrast,
        scheduleEventContainerDarkHighContrast,
        onScheduleEventContainerDarkHighContrast,
    ),
    hashtag = ColorFamily(
        hashtagDarkHighContrast,
        onHashtagDarkHighContrast,
        hashtagContainerDarkHighContrast,
        onHashtagContainerDarkHighContrast,
    ),
)


@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color,
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

val LocalExtendedColorScheme = staticCompositionLocalOf<ExtendedColorScheme> {
    error("No ExtendedColorScheme provided")
}

object PlanMeTheme {
    val extendedColors: ExtendedColorScheme
        @Composable
        get() = LocalExtendedColorScheme.current
}

@Composable
fun PlanMeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable() () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme
        else -> lightScheme

    }

    val extendedColors = when {
        darkTheme -> extendedDark
        else -> extendedLight
    }

    CompositionLocalProvider(LocalExtendedColorScheme provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}