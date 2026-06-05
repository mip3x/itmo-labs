package ru.mip3x.selenium.config

enum class Browser {
    CHROME,
    FIREFOX;

    companion object {
        fun from(value: String): Browser =
            entries.firstOrNull { it.name.equals(value.trim(), ignoreCase = true) }
                ?: error("Unsupported browser '$value'. Use chrome or firefox.")
    }
}
