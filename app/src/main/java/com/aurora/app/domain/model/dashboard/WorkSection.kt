package com.aurora.app.domain.model.dashboard

import com.aurora.app.data.model.WorkDto

data class WorkSection(
    val id: String,
    val categoryName: String,
    val works: List<WorkDto>
)