package com.hmproductions.itsmclient.data


data class TicketResponse(val result: Result)

data class Result(val count: Int, val tickets: List<Ticket>)

data class Ticket(val sysId: String, val number: String, val short_description: String, val description: String, val priority: Int,
                  val state: Int, val severity: Int, val category: String, val createdOn: Long, val active: Boolean,
                  val sysModeCount: Int, val companyId: String)

data class LoginDetails(val email: String, val password: String)

data class SignUpDetails(val email: String, val password: String, val company: String, val tier: Int, val designation: String)

data class LoginResponse(val statusCode: Int, val message: String, val token: String, val isAdmin: Boolean)

data class ErrorMessage(val statusCode: Int, val message: String)
