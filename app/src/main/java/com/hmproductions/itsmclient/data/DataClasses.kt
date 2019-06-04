package com.hmproductions.itsmclient.data

// Tickets data classes
data class TicketResponse(val ticketsResult: TicketsResult)

data class TicketsResult(val count: Int, val tickets: List<Ticket>)

data class Ticket(val sysId: String, val number: String, val short_description: String, val description: String, val priority: Int,
                  val state: Int, val severity: Int, val category: String, val createdOn: Long, val active: Boolean,
                  val sysModeCount: Int, val companyId: String)

// User authentication data classes
data class LoginDetails(val email: String, val password: String)

data class SignUpDetails(val email: String, val password: String, val company: String, val tier: Int, val designation: String)

data class LoginResponse(val statusCode: Int, val message: String, val token: String, val isAdmin: Boolean)

// Configuration data classes
data class ConfigurationResponse(val result: ConfigurationResult)

data class ConfigurationResult(val count: Int, val configurations: List<Configuration>)

data class Configuration(val tier: Int, val fields: List<String>)

/// Field data classes
data class FieldResponse(val configuration: List<ConfigurationField>)

data class ConfigurationField(val field: String, var checked: Boolean = false)

// Generic data classes
data class ErrorMessage(val statusCode: Int, val message: String)
