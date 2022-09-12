package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import com.group.libraryapp.dto.book.response.BookStatResponse
import com.group.libraryapp.repository.book.BookQuerydslRepository
import com.group.libraryapp.repository.user.loanhistory.UserLoanHistoryQuerydslRepository
import com.group.libraryapp.util.fail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val bookQuerydslRepository: BookQuerydslRepository,
    private val userRepository: UserRepository,
    //private val userLoanHistoryRepository: UserLoanHistoryRepository,
    private val userLoanHistoryQuerydslRepository: UserLoanHistoryQuerydslRepository,
) {

    @Transactional
    fun saveBook(request: BookRequest) {
        val book = Book(request.name, request.type)
        bookRepository.save(book)
    }

    @Transactional
    fun loanBook(request: BookLoanRequest) {
        val book = bookRepository.findByName(request.bookName) ?: fail()
        if (userLoanHistoryQuerydslRepository.find(request.bookName, UserLoanStatus.LOANED) != null) {
            throw IllegalArgumentException("진작 대출되어 있는 책입니다")
        }

        val user = userRepository.findByName(request.userName) ?: fail()
        user.loanBook(book)
    }

    @Transactional
    fun returnBook(request: BookReturnRequest) {
        val user = userRepository.findByName(request.userName) ?: fail()
        user.returnBook(request.bookName)
    }

    @Transactional(readOnly = true)
    fun countLoanedBook(): Int {
        // DB에 존재하는 UserLoanHistory 데이터를 모두 가져와서 애플리케이션에서 size를 계산
        // select * from user_loan_history where status = 'LOANED'
        //return userLoanHistoryRepository.findAllByStatus(UserLoanStatus.LOANED).size

        // DB에서 카운트하여 숫자만 받아옴
        // select count(*) from user_loan_history where status = 'LOANED'
        return userLoanHistoryQuerydslRepository.count(UserLoanStatus.LOANED).toInt()
    }

    @Transactional(readOnly = true)
    fun getBookStatistics(): List<BookStatResponse> {

        /*
        val results = mutableListOf<BookStatResponse>()
        val books = bookRepository.findAll()

        for (book in books) {
            results.firstOrNull { dto -> book.type == dto.type }?.plusOne()
                ?: results.add(BookStatResponse(book.type, 1))
        }
        return results
        */

        // 책 데이터를 메모리에 모두 가져온 뒤 리스트를 함수형 프로그래밍으로 카운트
        /*
        return bookRepository.findAll()                                  // List<Book>
            .groupBy { book -> book.type }                               // Map<BookType, List<Book>>
            .map { (type, books) -> BookStatResponse(type, books.size) } // List<BookStatResponse>
        */

        // DB에서 데이터를 가져올 때 Group By 쿼리를 사용하여 필요한 데이터만 가져옴(인덱스를 사용해 튜닝 할 여지가 있음)
        return bookQuerydslRepository.getStats()
    }

}