package com.abdownloadmanager.shared.utils.extractors.multporn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MultpornGalleryExtractorTest {

    @Test
    fun `parseGalleryDocument extracts title folder and original image urls`() {
        val gallery = MultpornGalleryExtractor.parseGalleryDocument(sampleHtml)

        assertNotNull(gallery, "Expected gallery metadata to be extracted")
        assertEquals("Boiled", gallery.title)
        assertEquals("Boiled", gallery.folderName)
        assertEquals(3, gallery.imageUrls.size)
        assertEquals(
            "https://multporn.net/sites/default/files/comics/avatar_the_last_airbender/spageta_boiled/01_avatar_pg_1.jpg",
            gallery.imageUrls.first(),
        )
        assertTrue(gallery.imageUrls.none { it.contains("/styles/") })
    }

    @Test
    fun `parseGalleryDocument removes duplicate image sources`() {
        val gallery = MultpornGalleryExtractor.parseGalleryDocument(sampleHtmlWithDuplicates)

        assertNotNull(gallery)
        assertEquals(2, gallery.imageUrls.size)
    }

    private val sampleHtml = """
        <html>
          <body>
            <h1 class="title" id="page-title">
              Boiled - Porn Comics
            </h1>
            <div>
              <p class="jb-image">
                <img src="https://multporn.net/sites/default/files/styles/juicebox_medium/public/comics/avatar_the_last_airbender/spageta_boiled/01_avatar_pg_1.jpg?itok=Xa9_55dJ" />
              </p>
              <p class="jb-image">
                <img src="https://multporn.net/sites/default/files/styles/juicebox_medium/public/comics/avatar_the_last_airbender/spageta_boiled/02_avatar_pg_2.jpg?itok=abcdef" />
              </p>
              <p class="jb-image">
                <img src="https://multporn.net/sites/default/files/styles/juicebox_medium/public/comics/avatar_the_last_airbender/spageta_boiled/03_avatar_pg_3.jpg?itok=ghijkl" />
              </p>
            </div>
          </body>
        </html>
    """.trimIndent()

    private val sampleHtmlWithDuplicates = """
        <html>
          <body>
            <h1 class="title" id="page-title">Duplicate Test - Porn Comics</h1>
            <div>
              <p class="jb-image">
                <img src="https://multporn.net/sites/default/files/styles/juicebox_medium/public/comics/test/001.jpg?itok=1" />
              </p>
              <p class="jb-image">
                <img src="https://multporn.net/sites/default/files/styles/juicebox_medium/public/comics/test/001.jpg?itok=2" />
              </p>
              <p class="jb-image">
                <img src="https://multporn.net/sites/default/files/styles/juicebox_medium/public/comics/test/002.jpg" />
              </p>
            </div>
          </body>
        </html>
    """.trimIndent()
}
