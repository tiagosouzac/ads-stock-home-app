package com.example.stockhome.ui.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Ícones de linha 24x24, currentColor — porta fiel do componente <Icon> de sh-ui.jsx.
 * Cada ícone é uma lista de primitivas (path SVG, círculo ou retângulo arredondado),
 * desenhadas com traço de pontas/junções arredondadas.
 */
private sealed interface Prim {
    data class P(val d: String) : Prim                                   // path SVG
    data class C(val cx: Float, val cy: Float, val r: Float) : Prim       // círculo
    data class R(val x: Float, val y: Float, val w: Float, val h: Float, val rx: Float) : Prim // retângulo arredondado
}

private fun shapes(name: String): List<Prim> = when (name) {
    "home" -> listOf(Prim.P("M3.5 10.5 12 4l8.5 6.5"), Prim.P("M5.5 9.5V20h13V9.5"))
    "list" -> listOf(Prim.R(3.5f, 5f, 17f, 14f, 2.5f), Prim.P("M7.5 9.5h9M7.5 14.5h6"))
    "bell" -> listOf(Prim.P("M6 9a6 6 0 0 1 12 0c0 5 1.5 6 1.5 6h-15S6 14 6 9Z"), Prim.P("M10 19a2 2 0 0 0 4 0"))
    "user" -> listOf(Prim.C(12f, 8.5f, 3.5f), Prim.P("M5.5 19.5a6.5 6.5 0 0 1 13 0"))
    "search" -> listOf(Prim.C(11f, 11f, 6f), Prim.P("m20 20-3.5-3.5"))
    "filter" -> listOf(Prim.P("M5 7h14M8 12h8M10.5 17h3"))
    "back" -> listOf(Prim.P("M15 5l-7 7 7 7"))
    "chevR" -> listOf(Prim.P("M9 5l7 7-7 7"))
    "chevD" -> listOf(Prim.P("M5 9l7 7 7-7"))
    "plus" -> listOf(Prim.P("M12 5v14M5 12h14"))
    "edit" -> listOf(Prim.P("M15.5 5.5l3 3M4 20l1-4L16 5l3 3L8 19l-4 1Z"))
    "trash" -> listOf(Prim.P("M5 7h14M9 7V5h6v2M7 7l1 13h8l1-13"))
    "gear" -> listOf(Prim.C(12f, 12f, 3f), Prim.P("M12 3v2.5M12 18.5V21M4.2 7l2.2 1.3M17.6 15.7l2.2 1.3M4.2 17l2.2-1.3M17.6 8.3l2.2-1.3"))
    "lock" -> listOf(Prim.R(5.5f, 10.5f, 13f, 9f, 2f), Prim.P("M8.5 10.5V8a3.5 3.5 0 0 1 7 0v2.5"))
    "mail" -> listOf(Prim.R(3.5f, 6f, 17f, 12f, 2.5f), Prim.P("m4 8 8 5 8-5"))
    "eye" -> listOf(Prim.P("M2.5 12S6 6 12 6s9.5 6 9.5 6-3.5 6-9.5 6-9.5-6-9.5-6Z"), Prim.C(12f, 12f, 2.5f))
    "check" -> listOf(Prim.P("M5 12.5l4.5 4.5L19 7"))
    "calendar" -> listOf(Prim.R(4f, 5.5f, 16f, 14f, 2.5f), Prim.P("M4 9.5h16M8.5 3.5v4M15.5 3.5v4"))
    "box" -> listOf(Prim.P("m12 3 8 4.5v9L12 21l-8-4.5v-9L12 3Z"), Prim.P("m4 7.5 8 4.5 8-4.5M12 12v9"))
    "logout" -> listOf(Prim.P("M14 5H6.5A1.5 1.5 0 0 0 5 6.5v11A1.5 1.5 0 0 0 6.5 19H14M16 8.5 19.5 12 16 15.5M9.5 12h10"))
    "alert" -> listOf(Prim.P("M12 4 2.5 20h19L12 4Z"), Prim.P("M12 10v4.5M12 17.2v.2"))
    "minus" -> listOf(Prim.P("M5 12h14"))
    "key" -> listOf(Prim.C(8f, 13f, 3.5f), Prim.P("m10.5 11 8-8M16 5l2 2M14 7l2 2"))
    "clock" -> listOf(Prim.C(12f, 12f, 7.5f), Prim.P("M12 8v4.5l3 2"))
    "tag" -> listOf(Prim.P("M4 4h7l9 9-7 7-9-9V4Z"), Prim.C(8f, 8f, 1.4f))
    else -> emptyList()
}

@Composable
fun Icon(
    name: String,
    size: Dp = 24.dp,
    stroke: Float = 1.8f,
    color: Color = Color.Unspecified,
    modifier: Modifier = Modifier,
) {
    val c = if (color == Color.Unspecified) androidx.compose.material3.LocalContentColor.current else color
    Canvas(modifier = modifier.then(Modifier.size(size))) {
        val scale = this.size.minDimension / 24f
        val style = Stroke(width = stroke * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
        shapes(name).forEach { prim ->
            when (prim) {
                is Prim.P -> {
                    val path = PathParser().parsePathString(prim.d).toPath()
                    val scaled = androidx.compose.ui.graphics.Path().apply {
                        addPath(path)
                    }
                    // aplica escala via matriz
                    scaled.transform(androidx.compose.ui.graphics.Matrix().apply { scale(scale, scale) })
                    drawPath(scaled, c, style = style)
                }
                is Prim.C -> drawCircle(
                    color = c,
                    radius = prim.r * scale,
                    center = androidx.compose.ui.geometry.Offset(prim.cx * scale, prim.cy * scale),
                    style = style,
                )
                is Prim.R -> drawRoundRect(
                    color = c,
                    topLeft = androidx.compose.ui.geometry.Offset(prim.x * scale, prim.y * scale),
                    size = androidx.compose.ui.geometry.Size(prim.w * scale, prim.h * scale),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(prim.rx * scale, prim.rx * scale),
                    style = style,
                )
            }
        }
    }
}
