package com.example.anlosia.model

data class TodoListResponse(val api_status : Int?, val api_message : String?, val data : List<TodoListRequest>)
