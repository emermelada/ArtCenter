package com.emermelada.artcenter.data.model.result

data class Result<T>(
    val data: T?,
    val msg: String?,
    val code:Int
)