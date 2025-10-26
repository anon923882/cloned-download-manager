package com.abdownloadmanager.shared.utils.extractors.multporn

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * Extracts gallery information from multporn.net pages.
 */
object MultpornGalleryExtractor {
    data class GalleryInfo(
        val title: String,
        val folderName: String,
        val imageUrls: List<String>,
    )

    private val galleryUrlRegex = Regex(
        pattern = """^https?://(?:www\.)?multporn\.net/comics/[^#?]+(?:\?[^#]*)?$""",
        options = setOf(RegexOption.IGNORE_CASE),
    )
    private val titleRegex = Regex(
        pattern = """<h1[^>]*id=[\"']page-title[\"'][^>]*>(.*?)</h1>""",
        options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL),
    )
    private val imageRegex = Regex(
        pattern = """<p\s+class=[\"']jb-image[\"'][^>]*>\s*<img\s+[^>]*src=[\"']([^\"']+)[\"']""",
        options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL),
    )
    private val styledPathRegex = Regex("""/styles/[^/]+/public""", RegexOption.IGNORE_CASE)

    fun extract(url: String): GalleryInfo? {
        val normalizedUrl = url.trim()
        if (!galleryUrlRegex.matches(normalizedUrl)) {
            return null
        }
        val html = fetch(normalizedUrl) ?: return null
        return parseGalleryDocument(html)
    }

    internal fun parseGalleryDocument(html: String): GalleryInfo? {
        val rawTitle = titleRegex.find(html)?.groupValues?.getOrNull(1)?.let(::cleanTitle)?.takeIf { it.isNotBlank() }
            ?: return null
        val folderName = sanitizeFolderName(rawTitle)
        val images = imageRegex.findAll(html)
            .mapNotNull { match ->
                match.groupValues.getOrNull(1)?.let(::toOriginalImageUrl)
            }
            .distinct()
            .toList()
        if (images.isEmpty()) {
            return null
        }
        return GalleryInfo(
            title = rawTitle,
            folderName = folderName,
            imageUrls = images,
        )
    }

    private fun fetch(url: String): String? {
        return runCatching {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.instanceFollowRedirects = true
            connection.connectTimeout = 15_000
            connection.readTimeout = 30_000
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MultpornGalleryExtractor/1.0)")
            connection.inputStream.use { input ->
                BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).use { reader ->
                    val builder = StringBuilder()
                    val buffer = CharArray(8 * 1024)
                    while (true) {
                        val read = reader.read(buffer)
                        if (read < 0) break
                        builder.append(buffer, 0, read)
                        if (builder.length > 2_000_000) {
                            break
                        }
                    }
                    builder.toString()
                }
            }
        }.getOrNull()
    }

    private fun cleanTitle(raw: String): String {
        val withoutTags = raw.replace(Regex("""<[^>]+>"""), "")
        val decoded = decodeHtmlEntities(withoutTags)
        return decoded
            .replace("- Porn Comics", "", ignoreCase = true)
            .replace("\n", " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun toOriginalImageUrl(url: String): String {
        val withoutQuery = url.substringBefore("?")
        return styledPathRegex.replace(withoutQuery) { "" }
    }

    private fun sanitizeFolderName(name: String): String {
        val cleaned = name.replace(Regex("""[\\/:*?"<>|]"""), "_").trim('.', ' ')
        return if (cleaned.isNotBlank()) cleaned else "gallery"
    }

    private fun decodeHtmlEntities(text: String): String {
        val entities = mapOf(
            "&amp;" to "&",
            "&quot;" to "\"",
            "&apos;" to "'",
            "&#39;" to "'",
            "&lt;" to "<",
            "&gt;" to ">",
        )
        var result = text
        entities.forEach { (entity, value) ->
            result = result.replace(entity, value)
        }
        val numericRegex = Regex("""&#(\d+);""")
        result = numericRegex.replace(result) { match ->
            val code = match.groupValues.getOrNull(1)?.toIntOrNull() ?: return@replace match.value
            if (code in 32..0x10FFFF) {
                String(intArrayOf(code), 0, 1)
            } else {
                match.value
            }
        }
        val hexRegex = Regex("""&#x([0-9a-fA-F]+);""")
        result = hexRegex.replace(result) { match ->
            val code = match.groupValues.getOrNull(1)?.toIntOrNull(16) ?: return@replace match.value
            if (code in 32..0x10FFFF) {
                String(intArrayOf(code), 0, 1)
            } else {
                match.value
            }
        }
        return result
    }
}
