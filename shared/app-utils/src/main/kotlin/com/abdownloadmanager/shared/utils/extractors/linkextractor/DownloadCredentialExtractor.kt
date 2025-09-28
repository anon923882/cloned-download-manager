package com.abdownloadmanager.shared.utils.extractors.linkextractor

import com.abdownloadmanager.shared.utils.extractors.Extractor
import com.abdownloadmanager.shared.utils.extractors.multporn.MultpornGalleryExtractor
import ir.amirab.downloader.downloaditem.DownloadCredentials


interface DownloadCredentialExtractor<T> : Extractor<T, List<DownloadRequest>> {
    override fun extract(input: T): List<DownloadRequest>
}


object DownloadCredentialFromStringExtractor : DownloadCredentialExtractor<String> {
    override fun extract(input: String): List<DownloadRequest> {
        return StringUrlExtractor.extract(input)
            .flatMap { link ->
                val gallery = MultpornGalleryExtractor.extract(link)
                if (gallery != null) {
                    gallery.imageUrls.map { imageUrl ->
                        DownloadRequest(
                            credentials = DownloadCredentials(
                                link = imageUrl,
                                downloadPage = link,
                            ),
                            suggestedSubfolder = gallery.folderName,
                        )
                    }
                } else {
                    listOf(
                        DownloadRequest(
                            credentials = DownloadCredentials(link = link),
                        )
                    )
                }
            }
    }
}