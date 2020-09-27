package com.example.anlosia.model

data class ListVacationResponse(val count: Int, val next: Int, val previous: Int, val results: List<VacationResponse>)