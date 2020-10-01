package com.example.anlosia.model

data class ListVacationResponse(val count: Int, val next: String, val previous: String, val results: List<VacationResponse>)