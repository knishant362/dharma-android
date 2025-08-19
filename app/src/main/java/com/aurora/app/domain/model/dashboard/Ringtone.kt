package com.aurora.app.domain.model.dashboard

data class Ringtone(
    val name: String,
    val dname: String,
    val url: String,
    val extension: String,
    val meta: MetaData,
    val ename: String
)