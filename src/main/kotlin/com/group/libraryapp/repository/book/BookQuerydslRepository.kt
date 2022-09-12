package com.group.libraryapp.repository.book

import com.group.libraryapp.domain.book.QBook.book
import com.group.libraryapp.dto.book.response.BookStatResponse
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component

@Component
class BookQuerydslRepository(
    private val queryFactory: JPAQueryFactory,
) {

//  select new com.group.libraryapp.dto.book.response.BookStatResponse(b.type, count(b.id))
//  from Book b group by b.type
    fun getStats(): List<BookStatResponse> {
        return queryFactory.select(
            Projections.constructor(
                BookStatResponse::class.java,
                book.type,
                book.id.count()
            ))
            .from(book)
            .groupBy(book.type)
            .fetch()
    }

}