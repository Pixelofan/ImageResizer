package com.smarttoolfactory.colordetector

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.smarttoolfactory.colordetector.util.ColorUtil
import com.smarttoolfactory.colordetector.util.ColorUtil.fractionToIntPercent

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
    borderWidth: Dp,
    onEmpty: @Composable ColumnScope.() -> Unit,
    onColorChange: (ColorData) -> Unit
) {
    val paletteData: List<PaletteData> by remember(imageBitmap) {
        derivedStateOf {
            val paletteData = mutableListOf<PaletteData>()
            val palette = Palette
                .from(imageBitmap.asAndroidBitmap())
                .maximumColorCount(maximumColorCount)
                .generate()


            val numberOfPixels: Float = palette.swatches.sumOf {
                it.population
            }.toFloat()

            palette.swatches.forEach { paletteSwatch ->
                paletteSwatch?.let { swatch ->
                    val color = Color(swatch.rgb)
                    val name = ""
                    val colorData = ColorData(color, name)
                    val percent: Float = swatch.population / numberOfPixels
                    paletteData.add(PaletteData(colorData = colorData, percent))
                }
            }
            paletteData.sortedWith(
                compareBy(
                    { ColorUtil.colorToHSV(it.colorData.color)[0] },
                    { it.colorData.color.luminance() },
                )
            )
        }
    }

    ColorProfileList(
        modifier = modifier,
        paletteDataList = paletteData,
        onColorChange = onColorChange,
        onEmpty = onEmpty,
        borderWidth = borderWidth
    )
}

@Composable
private fun ColorProfileList(
    modifier: Modifier,
    borderWidth: Dp,
    paletteDataList: List<PaletteData>,
    onEmpty: @Composable ColumnScope.() -> Unit,
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
                    .border(
                        width = borderWidth,
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    .then(
                        if (borderWidth < 0.dp) {
                            Modifier.shadow(.5.dp, RoundedCornerShape(50))
                        } else Modifier
                    )
                    .background(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
                        RoundedCornerShape(50)
                    )
                    .fillMaxWidth(),
                colorData = colorData,
                populationPercent = "$percent%",
                onClick = onColorChange,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        }
        if (paletteDataList.isEmpty()) onEmpty()
    }
}

private data class PaletteData(val colorData: ColorData, val percent: Float)