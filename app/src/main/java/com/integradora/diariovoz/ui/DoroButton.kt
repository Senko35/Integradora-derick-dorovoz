package com.integradora.diariovoz.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.integradora.diariovoz.theme.*

@Composable
fun DoroButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PinkLight
        ),
        modifier = modifier
            .height(55.dp)
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    listOf(PinkLight, PurpleLight)
                ),
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Text(
            text = text,
            color = SoftBlack,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
