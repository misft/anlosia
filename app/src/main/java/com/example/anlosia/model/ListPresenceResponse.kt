package com.example.anlosia.model

data class ListPresenceResponse(val count: Int, val next: Int, val previous: Int, val results: List<PresenceResponse>)