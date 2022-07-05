package com.stetsiuk.cameraapp.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder("label", "points", "group_id", "shape_type", "flags")
data class Shape(
    val label: String,
    val points: List<List<Double>>,
    val group_id: Int?,
    val shape_type: String,
    val flags: Flags?,
)