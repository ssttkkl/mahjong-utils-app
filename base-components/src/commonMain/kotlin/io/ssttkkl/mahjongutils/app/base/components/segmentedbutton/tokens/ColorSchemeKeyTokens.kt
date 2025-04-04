package io.ssttkkl.mahjongutils.app.base.components.segmentedbutton.tokens

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// VERSION: v0_162
// GENERATED CODE - DO NOT MODIFY BY HAND
internal enum class ColorSchemeKeyTokens {
    OnSecondaryContainer,
    OnSurface,
    Outline,
    SecondaryContainer,
}

internal val ColorSchemeKeyTokens.value: Color
    @Composable
    get() = when (this) {
        ColorSchemeKeyTokens.OnSecondaryContainer -> MaterialTheme.colorScheme.onSecondaryContainer
        ColorSchemeKeyTokens.OnSurface -> MaterialTheme.colorScheme.onSurface
        ColorSchemeKeyTokens.Outline -> MaterialTheme.colorScheme.outline
        ColorSchemeKeyTokens.SecondaryContainer -> MaterialTheme.colorScheme.secondaryContainer
    }