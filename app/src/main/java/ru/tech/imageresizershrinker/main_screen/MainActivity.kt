package ru.tech.imageresizershrinker.main_screen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DoorBack
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.PhotoSizeSelectSmall
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.getAppColorTuple
import dagger.hilt.android.AndroidEntryPoint
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.popUpTo
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.batch_resize.BatchResizeScreen
import ru.tech.imageresizershrinker.bytes_resize_screen.BytesResizeScreen
import ru.tech.imageresizershrinker.compare_screen.CompareScreen
import ru.tech.imageresizershrinker.crop_screen.CropScreen
import ru.tech.imageresizershrinker.generate_palette.GeneratePaletteScreen
import ru.tech.imageresizershrinker.main_screen.components.BatchResizePreference
import ru.tech.imageresizershrinker.main_screen.components.BytesResizePreference
import ru.tech.imageresizershrinker.main_screen.components.ComparePreference
import ru.tech.imageresizershrinker.main_screen.components.CropPreference
import ru.tech.imageresizershrinker.main_screen.components.GeneratePalettePreference
import ru.tech.imageresizershrinker.main_screen.components.HtmlText
import ru.tech.imageresizershrinker.main_screen.components.LocalAllowChangeColorByImage
import ru.tech.imageresizershrinker.main_screen.components.LocalAmoledMode
import ru.tech.imageresizershrinker.main_screen.components.LocalAppColorTuple
import ru.tech.imageresizershrinker.main_screen.components.LocalBorderWidth
import ru.tech.imageresizershrinker.main_screen.components.LocalDynamicColors
import ru.tech.imageresizershrinker.main_screen.components.LocalEditPresets
import ru.tech.imageresizershrinker.main_screen.components.LocalNightMode
import ru.tech.imageresizershrinker.main_screen.components.LocalPresetsProvider
import ru.tech.imageresizershrinker.main_screen.components.MainScreen
import ru.tech.imageresizershrinker.main_screen.components.PickColorPreference
import ru.tech.imageresizershrinker.main_screen.components.Screen
import ru.tech.imageresizershrinker.main_screen.components.SingleResizePreference
import ru.tech.imageresizershrinker.main_screen.components.alertDialog
import ru.tech.imageresizershrinker.main_screen.components.isNightMode
import ru.tech.imageresizershrinker.main_screen.viewModel.MainViewModel
import ru.tech.imageresizershrinker.pick_color_from_image.PickColorFromImageScreen
import ru.tech.imageresizershrinker.resize_screen.SingleResizeScreen
import ru.tech.imageresizershrinker.theme.ImageResizerTheme
import ru.tech.imageresizershrinker.theme.blend
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.APP_RELEASES
import ru.tech.imageresizershrinker.utils.ContextUtils.needToShowStoragePermissionRequest
import ru.tech.imageresizershrinker.utils.ContextUtils.requestStoragePermission
import ru.tech.imageresizershrinker.utils.IntentUtils.parcelable
import ru.tech.imageresizershrinker.utils.IntentUtils.parcelableArrayList
import ru.tech.imageresizershrinker.utils.getSavingFolder
import ru.tech.imageresizershrinker.utils.setContentWithWindowSizeClass
import ru.tech.imageresizershrinker.utils.toUiPath
import ru.tech.imageresizershrinker.widget.AutoSizeText
import ru.tech.imageresizershrinker.widget.LocalToastHost
import ru.tech.imageresizershrinker.widget.ToastHost
import ru.tech.imageresizershrinker.widget.activity.M3Activity
import java.util.concurrent.TimeUnit


@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : M3Activity() {

    private val viewModel by viewModels<MainViewModel>()


    @OptIn(ExperimentalAnimationApi::class, ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parseImageFromIntent(intent)

        setContentWithWindowSizeClass {
            var showConfetti by remember { mutableStateOf(false) }
            var showExitDialog by rememberSaveable { mutableStateOf(false) }
            val editPresetsState = rememberSaveable { mutableStateOf(false) }


            val saveFolderUri = viewModel.saveFolderUri

            CompositionLocalProvider(
                LocalToastHost provides viewModel.toastHostState,
                LocalNightMode provides viewModel.nightMode,
                LocalDynamicColors provides viewModel.dynamicColors,
                LocalAllowChangeColorByImage provides viewModel.allowImageMonet,
                LocalAmoledMode provides viewModel.amoledMode,
                LocalAppColorTuple provides viewModel.appColorTuple,
                LocalBorderWidth provides animateDpAsState(viewModel.borderWidth.dp).value,
                LocalPresetsProvider provides viewModel.localPresets,
                LocalEditPresets provides editPresetsState
            ) {
                ImageResizerTheme {
                    val themeState = LocalDynamicThemeState.current
                    val appColorTuple = getAppColorTuple(
                        defaultColorTuple = viewModel.appColorTuple,
                        dynamicColor = viewModel.dynamicColors,
                        darkTheme = viewModel.nightMode.isNightMode()
                    )
                    val onGoBack: () -> Unit = {
                        viewModel.updateUris(null)
                        themeState.updateColorTuple(appColorTuple)
                        viewModel.navController.apply {
                            if (backstack.entries.size > 1) pop()
                        }
                    }
                    BackHandler {
                        if (viewModel.shouldShowDialog) showExitDialog = true
                        else finishAffinity()
                    }

                    Surface(Modifier.fillMaxSize()) {
                        AnimatedNavHost(
                            controller = viewModel.navController,
                            transitionSpec = { _, _, to ->
                                if (to != Screen.Main) {
                                    slideInHorizontally { it / 2 } + fadeIn() with fadeOut()
                                } else {
                                    fadeIn() with fadeOut() + slideOutHorizontally { it / 2 }
                                }
                            }
                        ) { screen ->
                            when (screen) {
                                is Screen.Main -> {
                                    MainScreen(
                                        navController = viewModel.navController,
                                        currentFolderUri = saveFolderUri,
                                        onGetNewFolder = {
                                            viewModel.updateSaveFolderUri(it)
                                        },
                                        showConfetti = { showConfetti = true },
                                        viewModel = viewModel
                                    )
                                }

                                is Screen.SingleResize -> {
                                    SingleResizeScreen(
                                        uriState = viewModel.uris?.firstOrNull(),
                                        onGoBack = onGoBack,
                                        pushNewUri = viewModel::updateUri,
                                        getSavingFolder = { name, ext ->
                                            getSavingFolder(
                                                treeUri = saveFolderUri,
                                                filename = name,
                                                extension = ext
                                            )
                                        },
                                        savingPathString = saveFolderUri.toUiPath(
                                            context = this@MainActivity,
                                            default = stringResource(R.string.default_folder)
                                        ),
                                        showConfetti = { showConfetti = true }
                                    )
                                }

                                is Screen.BatchResize -> {
                                    BatchResizeScreen(
                                        uriState = viewModel.uris,
                                        onGoBack = onGoBack,
                                        pushNewUris = viewModel::updateUris,
                                        getSavingFolder = { name, ext ->
                                            getSavingFolder(
                                                treeUri = saveFolderUri,
                                                filename = name,
                                                extension = ext
                                            )
                                        },
                                        savingPathString = saveFolderUri.toUiPath(
                                            context = this@MainActivity,
                                            default = stringResource(R.string.default_folder)
                                        ),
                                        showConfetti = { showConfetti = true }
                                    )
                                }

                                is Screen.ResizeByBytes -> {
                                    BytesResizeScreen(
                                        uriState = viewModel.uris,
                                        onGoBack = onGoBack,
                                        pushNewUris = viewModel::updateUris,
                                        getSavingFolder = { name, ext ->
                                            getSavingFolder(
                                                treeUri = saveFolderUri,
                                                filename = name,
                                                extension = ext
                                            )
                                        },
                                        savingPathString = saveFolderUri.toUiPath(
                                            context = this@MainActivity,
                                            default = stringResource(R.string.default_folder)
                                        ),
                                        showConfetti = { showConfetti = true }
                                    )
                                }

                                is Screen.Crop -> {
                                    CropScreen(
                                        uriState = viewModel.uris?.firstOrNull(),
                                        onGoBack = onGoBack,
                                        pushNewUri = viewModel::updateUri,
                                        getSavingFolder = { name, ext ->
                                            getSavingFolder(
                                                treeUri = saveFolderUri,
                                                filename = name,
                                                extension = ext
                                            )
                                        },
                                        savingPathString = saveFolderUri.toUiPath(
                                            context = this@MainActivity,
                                            default = stringResource(R.string.default_folder)
                                        ),
                                        showConfetti = { showConfetti = true }
                                    )
                                }

                                is Screen.PickColorFromImage -> {
                                    PickColorFromImageScreen(
                                        uriState = viewModel.uris?.firstOrNull(),
                                        navController = viewModel.navController,
                                        onGoBack = onGoBack,
                                        pushNewUri = viewModel::updateUri
                                    )
                                }

                                is Screen.GeneratePalette -> {
                                    GeneratePaletteScreen(
                                        uriState = viewModel.uris?.firstOrNull(),
                                        navController = viewModel.navController,
                                        onGoBack = onGoBack,
                                        pushNewUri = viewModel::updateUri
                                    )
                                }

                                is Screen.Compare -> {
                                    CompareScreen(
                                        comparableUris = viewModel.uris
                                            ?.takeIf { it.size == 2 }
                                            ?.let { it[0] to it[1] },
                                        pushNewUris = viewModel::updateUris,
                                        onGoBack = onGoBack,
                                    )
                                }
                            }
                        }

                        if (showConfetti) {
                            val primary = MaterialTheme.colorScheme.primary
                            KonfettiView(
                                modifier = Modifier.fillMaxSize(),
                                parties = remember { particles(primary) },
                                updateListener = object : OnParticleSystemUpdateListener {
                                    override fun onParticleSystemEnded(
                                        system: PartySystem,
                                        activeSystems: Int
                                    ) {
                                        if (activeSystems == 0) showConfetti = false
                                    }
                                }
                            )
                        }
                    }

                    if (showExitDialog) {
                        AlertDialog(
                            modifier = Modifier.alertDialog(),
                            onDismissRequest = { showExitDialog = false },
                            dismissButton = {
                                OutlinedButton(
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    ),
                                    border = BorderStroke(
                                        LocalBorderWidth.current,
                                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                                    ),
                                    onClick = {
                                        finishAffinity()
                                    }
                                ) {
                                    Text(stringResource(R.string.close))
                                }
                            },
                            confirmButton = {
                                OutlinedButton(
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                                    border = BorderStroke(
                                        LocalBorderWidth.current,
                                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                                    ), onClick = { showExitDialog = false }) {
                                    Text(stringResource(R.string.stay))
                                }
                            },
                            title = { Text(stringResource(R.string.app_closing)) },
                            text = {
                                Text(
                                    stringResource(R.string.app_closing_sub),
                                    textAlign = TextAlign.Center
                                )
                            },
                            icon = { Icon(Icons.Outlined.DoorBack, null) }
                        )
                    } else if (viewModel.showSelectDialog) {
                        AlertDialog(
                            modifier = Modifier.alertDialog(),
                            onDismissRequest = {},
                            title = { stringResource(R.string.image) },
                            confirmButton = {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.apply {
                                            hideSelectDialog()
                                            updateUris(null)
                                        }
                                    },
                                    border = BorderStroke(
                                        LocalBorderWidth.current,
                                        MaterialTheme.colorScheme.outlineVariant()
                                    )
                                ) {
                                    Text(stringResource(id = R.string.cancel))
                                }
                            },
                            text = {
                                val navigate: (Screen) -> Unit = { screen ->
                                    viewModel.apply {
                                        navController.apply {
                                            popUpTo { it == Screen.Main }
                                            navigate(screen)
                                        }
                                        hideSelectDialog()
                                    }
                                }
                                val color = MaterialTheme.colorScheme.secondaryContainer
                                if ((viewModel.uris?.size ?: 0) <= 1) {
                                    Column(Modifier.verticalScroll(rememberScrollState())) {
                                        SingleResizePreference(
                                            onClick = { navigate(Screen.SingleResize) },
                                            color = color
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        BytesResizePreference(
                                            onClick = { navigate(Screen.ResizeByBytes) },
                                            color = color
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        CropPreference(
                                            onClick = { navigate(Screen.Crop) },
                                            color = color
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        PickColorPreference(
                                            onClick = { navigate(Screen.PickColorFromImage) },
                                            color = color
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        GeneratePalettePreference(
                                            onClick = { navigate(Screen.GeneratePalette) },
                                            color = color
                                        )
                                    }
                                } else {
                                    Column(Modifier.verticalScroll(rememberScrollState())) {
                                        BatchResizePreference(
                                            onClick = { navigate(Screen.BatchResize) },
                                            color = color
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        BytesResizePreference(
                                            onClick = { navigate(Screen.ResizeByBytes) },
                                            color = color
                                        )
                                        if (viewModel.uris?.size == 2) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            ComparePreference(
                                                onClick = { navigate(Screen.Compare) },
                                                color = color
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    } else if (viewModel.showUpdateDialog) {
                        AlertDialog(
                            modifier = Modifier.alertDialog(),
                            onDismissRequest = { },
                            icon = {
                                Icon(Icons.Rounded.Download, null)
                            },
                            title = { Text(stringResource(R.string.new_version, viewModel.tag)) },
                            text = {
                                Box {
                                    Divider(Modifier.align(Alignment.TopCenter))
                                    Column(Modifier.verticalScroll(rememberScrollState())) {
                                        Spacer(Modifier.height(16.dp))
                                        HtmlText(
                                            viewModel.nightMode.isNightMode(),
                                            viewModel.changelog
                                        )
                                    }
                                    Divider(Modifier.align(Alignment.BottomCenter))
                                }
                            },
                            confirmButton = {
                                OutlinedButton(
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                                    border = BorderStroke(
                                        LocalBorderWidth.current,
                                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                                    ),
                                    onClick = {
                                        startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("$APP_RELEASES/tag/${viewModel.tag}")
                                            )
                                        )
                                    }
                                ) {
                                    Text(stringResource(id = R.string.update))
                                }
                            },
                            dismissButton = {
                                OutlinedButton(
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    ),
                                    border = BorderStroke(
                                        LocalBorderWidth.current,
                                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                                    ), onClick = { viewModel.cancelledUpdate() }
                                ) {
                                    Text(stringResource(id = R.string.close))
                                }
                            }
                        )
                    }

                    ToastHost(hostState = LocalToastHost.current)

                    SideEffect { viewModel.tryGetUpdate() }

                    val showPermission = needToShowStoragePermissionRequest()

                    if (showPermission) {
                        AlertDialog(
                            modifier = Modifier.alertDialog(),
                            onDismissRequest = { },
                            icon = {
                                Icon(Icons.Rounded.Storage, null)
                            },
                            title = { Text(stringResource(R.string.permission)) },
                            text = {
                                Text(stringResource(R.string.permission_sub))
                            },
                            confirmButton = {
                                OutlinedButton(
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                                    border = BorderStroke(
                                        LocalBorderWidth.current,
                                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                                    ),
                                    onClick = {
                                        requestStoragePermission()
                                    }
                                ) {
                                    Text(stringResource(id = R.string.grant))
                                }
                            }
                        )
                    }

                    if (editPresetsState.value) {
                        AlertDialog(
                            modifier = Modifier
                                .alertDialog()
                                .width(320.dp),
                            properties = DialogProperties(usePlatformDefaultWidth = false),
                            onDismissRequest = { editPresetsState.value = false },
                            title = { Text(stringResource(R.string.presets)) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Rounded.PhotoSizeSelectSmall,
                                    contentDescription = null
                                )
                            },
                            text = {
                                val data = LocalPresetsProvider.current
                                Box {
                                    Divider(Modifier.align(Alignment.TopCenter))
                                    AnimatedContent(
                                        targetState = data,
                                        transitionSpec = { fadeIn() with fadeOut() },
                                        modifier = Modifier.verticalScroll(rememberScrollState())
                                    ) { list ->
                                        FlowRow(
                                            Modifier
                                                .align(Alignment.Center)
                                                .padding(start = 14.dp, top = 8.dp, bottom = 8.dp)
                                        ) {
                                            list.forEach {
                                                OutlinedIconButton(
                                                    shape = RoundedCornerShape(12.dp),
                                                    onClick = {
                                                        if (list.size > 7) {
                                                            viewModel.updatePresets(list - it)
                                                        }
                                                    },
                                                    border = BorderStroke(
                                                        androidx.compose.ui.unit.max(
                                                            LocalBorderWidth.current,
                                                            1.dp
                                                        ),
                                                        MaterialTheme.colorScheme.outlineVariant
                                                    ),
                                                    colors = IconButtonDefaults.outlinedIconButtonColors(
                                                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                                            alpha = 0.3f
                                                        ),
                                                        contentColor = MaterialTheme.colorScheme.onSurface
                                                    )
                                                ) {
                                                    AutoSizeText(it.toString())
                                                }
                                            }
                                            var expanded by remember { mutableStateOf(false) }
                                            OutlinedIconButton(
                                                shape = RoundedCornerShape(12.dp),
                                                onClick = {
                                                    expanded = true
                                                },
                                                border = BorderStroke(
                                                    androidx.compose.ui.unit.max(
                                                        LocalBorderWidth.current,
                                                        1.dp
                                                    ),
                                                    MaterialTheme.colorScheme.outlineVariant
                                                ),
                                                colors = IconButtonDefaults.outlinedIconButtonColors(
                                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                                        alpha = 0.3f
                                                    ),
                                                    contentColor = MaterialTheme.colorScheme.onSurface
                                                )
                                            ) {
                                                Icon(Icons.Rounded.AddCircle, null)
                                            }
                                            MaterialTheme(
                                                shapes = MaterialTheme.shapes.copy(
                                                    extraSmall = MaterialTheme.shapes.extraLarge
                                                )
                                            ) {
                                                DropdownMenu(
                                                    modifier = Modifier
                                                        .width(240.dp)
                                                        .alertDialog(),
                                                    expanded = expanded,
                                                    onDismissRequest = { expanded = false },
                                                    offset = DpOffset(6.dp, 0.dp)
                                                ) {
                                                    var value by remember { mutableStateOf(50f) }
                                                    Column(
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        Spacer(Modifier.height(12.dp))
                                                        Text(
                                                            "${value.toInt()}%",
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Slider(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(horizontal = 16.dp),
                                                            value = animateFloatAsState(targetValue = value).value,
                                                            onValueChange = { value = it },
                                                            steps = 490,
                                                            valueRange = 10f..500f
                                                        )
                                                        OutlinedButton(
                                                            onClick = {
                                                                viewModel.updatePresets(list + value.toInt())
                                                                expanded = false
                                                            },
                                                            colors = ButtonDefaults.outlinedButtonColors(
                                                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                                                    alpha = if (LocalNightMode.current.isNightMode()) 0.5f
                                                                    else 1f
                                                                ),
                                                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                                            ),
                                                            border = BorderStroke(
                                                                LocalBorderWidth.current,
                                                                MaterialTheme.colorScheme.outlineVariant(
                                                                    onTopOf = MaterialTheme.colorScheme.secondaryContainer
                                                                )
                                                            ),
                                                        ) {
                                                            Text(stringResource(R.string.add))
                                                        }
                                                        Spacer(Modifier.height(8.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Divider(Modifier.align(Alignment.BottomCenter))
                                }
                            },
                            confirmButton = {
                                OutlinedButton(
                                    onClick = { editPresetsState.value = false },
                                    border = BorderStroke(
                                        LocalBorderWidth.current,
                                        MaterialTheme.colorScheme.outlineVariant()
                                    )
                                ) {
                                    Text(stringResource(R.string.close))
                                }
                            }
                        )
                    }
                }
            }
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        parseImageFromIntent(intent)
    }

    private fun parseImageFromIntent(intent: Intent?) {
        if (intent?.type != null && viewModel.uris == null) {
            viewModel.shouldShowExitDialog(false)
        }
        if (intent?.type?.startsWith("image/") == true) {
            when (intent.action) {
                Intent.ACTION_SEND -> {
                    intent.parcelable<Uri>(Intent.EXTRA_STREAM)?.let {
                        viewModel.updateUris(listOf(it))
                    }
                }

                Intent.ACTION_SEND_MULTIPLE -> {
                    intent.parcelableArrayList<Uri>(Intent.EXTRA_STREAM)?.let {
                        viewModel.updateUris(it)
                    }
                }

                else -> {
                    intent.data?.let { viewModel.updateUris(listOf(it)) }
                }
            }
        } else if (intent?.type != null) {
            viewModel.showToast(
                message = getString(R.string.unsupported_type, intent.type),
                icon = Icons.Rounded.ErrorOutline
            )
        }
    }
}

private fun particles(primary: Color) = listOf(
    Party(
        speed = 0f,
        maxSpeed = 15f,
        damping = 0.9f,
        angle = Angle.BOTTOM,
        spread = Spread.ROUND,
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def).map {
            it.blend(primary)
        },
        emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100),
        position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0))
    ),
    Party(
        speed = 10f,
        maxSpeed = 30f,
        damping = 0.9f,
        angle = Angle.RIGHT - 45,
        spread = 60,
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def).map {
            it.blend(primary)
        },
        emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100),
        position = Position.Relative(0.0, 1.0)
    ),
    Party(
        speed = 10f,
        maxSpeed = 30f,
        damping = 0.9f,
        angle = Angle.RIGHT - 135,
        spread = 60,
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def).map {
            it.blend(primary)
        },
        emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100),
        position = Position.Relative(1.0, 1.0)
    )
)