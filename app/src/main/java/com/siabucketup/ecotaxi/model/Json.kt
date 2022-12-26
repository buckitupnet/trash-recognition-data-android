package com.siabucketup.ecotaxi.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder("version", "flags", "shapes", "imagePath", "imageData", "imageHeight", "imageWidth")
data class Json(
    val version: String,
    val flags: Flags?,
    val shapes: List<Shape>,
    val imagePath: String,
    val imageData: String,
    val imageHeight: Int,
    val imageWidth: Int,
)