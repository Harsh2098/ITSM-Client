package com.hmproductions.itsmclient.data

data class Result(val tickets: List<Ticket>)

data class Ticket(val sysId: String, val number: String, val shortDescription: String, val description: String, val priority: Int,
                  val state: Int, val severity: Int, val category: String, val createdOn: Long, val active: Boolean,
                  val sysModeCount: Int, val companyId: String)

data class LoginDetails(val email: String, val password: String)

data class SignUpDetails(val email: String, val password: String, val company: String, val tier: Int, val designation: String,
                         val admin: Boolean)

data class LoginResponse(val statusCode: Int, val message: String, val token: String)
