package io.legado.app.domain.usecase

import io.legado.app.domain.gateway.BookCacheCleanupGateway
import io.legado.app.domain.gateway.BookSourceCallbackGateway
import io.legado.app.domain.gateway.LocalBookGateway
import io.legado.app.domain.model.BookGroupAssignment
import io.legado.app.domain.model.CacheableBook
import io.legado.app.domain.model.DeletableBook
import io.legado.app.domain.repository.BookDomainRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeleteBooksUseCaseTest {

    @Test
    fun `deletes local source and online cache only when requested`() = runBlocking {
        val repository = RecordingBookRepository(
            listOf(
                DeletableBook("local", "", true),
                DeletableBook("online", "source", false),
            )
        )
        val localGateway = RecordingLocalBookGateway()
        val sourceGateway = RecordingSourceCallbackGateway()
        val cacheGateway = RecordingCacheCleanupGateway()
        val useCase = DeleteBooksUseCase(
            repository,
            localGateway,
            sourceGateway,
            cacheGateway,
        )

        val deleted = useCase.execute(
            bookUrls = setOf("local", "online"),
            deleteOriginal = true,
            deleteCache = true,
        )

        assertEquals(listOf("local" to true), localGateway.deletedBooks)
        assertEquals(listOf("online"), sourceGateway.deletedBooks)
        assertEquals(listOf("online"), cacheGateway.clearedBooks)
        assertEquals(setOf("local", "online"), repository.deletedChapters)
        assertEquals(setOf("local", "online"), repository.deletedBooks)
        assertEquals(listOf("local", "online"), deleted)
    }

    @Test
    fun `keeps online cache when cache deletion is not requested`() = runBlocking {
        val repository = RecordingBookRepository(
            listOf(DeletableBook("online", "source", false))
        )
        val cacheGateway = RecordingCacheCleanupGateway()
        val useCase = DeleteBooksUseCase(
            repository,
            RecordingLocalBookGateway(),
            RecordingSourceCallbackGateway(),
            cacheGateway,
        )

        useCase.execute(
            bookUrls = setOf("online"),
            deleteOriginal = false,
            deleteCache = false,
        )

        assertTrue(cacheGateway.clearedBooks.isEmpty())
    }

    private class RecordingBookRepository(
        private val deletableBooks: List<DeletableBook>,
    ) : BookDomainRepository {
        val deletedChapters = mutableSetOf<String>()
        var deletedBooks = emptySet<String>()

        override suspend fun getCacheableBooks(bookUrls: Set<String>): List<CacheableBook> =
            emptyList()

        override suspend fun getDeletableBooks(bookUrls: Set<String>): List<DeletableBook> =
            deletableBooks.filter { it.bookUrl in bookUrls }

        override suspend fun getBookGroupAssignments(
            bookUrls: Set<String>
        ): List<BookGroupAssignment> = emptyList()

        override suspend fun updateBookGroups(assignments: List<BookGroupAssignment>) = Unit

        override suspend fun removeGroupFromBooks(groupId: Long) = Unit

        override suspend fun deleteBooks(bookUrls: Set<String>) {
            deletedBooks = bookUrls
        }

        override suspend fun deleteChaptersByBook(bookUrl: String) {
            deletedChapters += bookUrl
        }
    }

    private class RecordingLocalBookGateway : LocalBookGateway {
        val deletedBooks = mutableListOf<Pair<String, Boolean>>()

        override suspend fun deleteBook(bookUrl: String, deleteOriginal: Boolean) {
            deletedBooks += bookUrl to deleteOriginal
        }
    }

    private class RecordingSourceCallbackGateway : BookSourceCallbackGateway {
        val deletedBooks = mutableListOf<String>()

        override suspend fun onDeleteFromShelf(bookUrl: String) {
            deletedBooks += bookUrl
        }
    }

    private class RecordingCacheCleanupGateway : BookCacheCleanupGateway {
        val clearedBooks = mutableListOf<String>()

        override fun clearAll() = Unit

        override suspend fun clear(bookUrl: String): Boolean {
            clearedBooks += bookUrl
            return true
        }
    }
}
