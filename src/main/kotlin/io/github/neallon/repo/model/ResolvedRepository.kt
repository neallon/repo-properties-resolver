package io.github.neallon.repo.model

data class ResolvedRepository(
    val id: String,
    val name: String,
    val url: String,
    val user: String? = null,
    val password: String? = null,
    val allowInsecureProtocol: Boolean = false
)