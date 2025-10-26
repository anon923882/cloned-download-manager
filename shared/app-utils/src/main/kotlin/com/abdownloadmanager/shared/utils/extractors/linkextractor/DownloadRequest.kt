package com.abdownloadmanager.shared.utils.extractors.linkextractor

import ir.amirab.downloader.downloaditem.DownloadCredentials

/**
 * Represents a download request extracted from a user supplied source.
 *
 * @property credentials download information required by the downloader core.
 * @property suggestedSubfolder optional relative subfolder (under the user's
 *   chosen download directory) suggested by the extractor.
 */
data class DownloadRequest(
    val credentials: DownloadCredentials,
    val suggestedSubfolder: String? = null,
)
