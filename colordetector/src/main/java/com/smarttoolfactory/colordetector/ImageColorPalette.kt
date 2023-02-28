package com.smarttoolfactory.colordetector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.smarttoolfactory.extendedcolors.util.fractionToIntPercent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map

/**
 * Composable that creates color lists [Palette.Swatch] using **Palette API**
 * @param imageBitmap is the image that colors are generated from
 * should be displayed
 * @param maximumColorCount maximum number of [Palette.Swatch]es that should be generated
 * from [imageBitmap]. Maximum number might not be achieved based on image color composition
 * @param onColorChange callback to notify that user moved and picked a color
 */
@Composable
fun ImageColorPalette(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    maximumColorCount: Int = 32,
    onColorChange: (ColorData) -> Unit
) {
    val paletteData = remember {
        mutableStateListOf<PaletteData>()
    }

    LaunchedEffect(key1 = imageBitmap) {
        snapshotFlow {
            imageBitmap
        }.map {
            val palette = Palette
                .from(imageBitmap.asAndroidBitmap())
                .maximumColorCount(maximumColorCount)
                .generate()

            paletteData.clear()

            val numberOfPixels: Float = palette.swatches.sumOf {
                it.population
            }.toFloat()

            palette.swatches.forEach { paletteSwatch: Palette.Swatch? ->
                paletteSwatch?.let { swatch: Palette.Swatch ->
                    val color = Color(swatch.rgb)
                    val name = ""
                    val colorData = ColorData(color, name)
                    val percent: Float = swatch.population / numberOfPixels
                    paletteData.add(PaletteData(colorData = colorData, percent))
                }
            }
        }
            .flowOn(Dispatchers.Default)
            .launchIn(this)
    }

    ColorProfileList(
        modifier = modifier,
        paletteDataList = remember(paletteData) {
            derivedStateOf {
                paletteData.sortedByDescending { it.percent }
            }
        }.value,
        onColorChange = onColorChange
    )
}

@Composable
private fun ColorProfileList(
    modifier: Modifier,
    paletteDataList: List<PaletteData>,
    onColorChange: (ColorData) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        paletteDataList.forEach { paletteData: PaletteData ->
            val percent = paletteData.percent.fractionToIntPercent()
            val colorData = paletteData.colorData

            ColorItemRow(
                modifier = Modifier
                    .shadow(.5.dp, RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .fillMaxWidth(),
                colorData = colorData,
                populationPercent = "$percent%",
                onClick = onColorChange,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private data class PaletteData(val colorData: ColorData, val percent: Float)