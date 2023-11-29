package ja.insepector.base.bean

data class UpdateBean(
    val force: String,
    val state: String,
    val url: String,
    val version: String
)