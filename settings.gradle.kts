listOf(
    "cli",
    "common"
).forEach {
    include(it)
    findProject(":${it}")?.name = "reconstruct-${it}"
}