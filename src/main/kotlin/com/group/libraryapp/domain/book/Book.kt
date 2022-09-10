package com.group.libraryapp.domain.book

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id

@Entity
class Book(
    val name: String,

    val type: String,

    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long? = null,
) {

    init {
        if (name.isBlank()) {
            throw IllegalArgumentException("이름은 비어 있을 수 없습니다")
        }
    }

    /**
     * 정적 팩토리 메서드를 사용할 경우,
     * 프로퍼티가 추가되었을 때,
     * 프로퍼티 변경이 전파되지 않음
     * 왜냐하면 외부 코드에서는 생성자를 직접적으로 호출하지 않기 때문에
     * 정적 팩토리 메서드의 내용만 수정하면 됨
     */
    companion object {
        fun fixture(
            name: String = "책 이름",
            type: String = "COMPUTER",
            id: Long? = null,
        ): Book {
            return Book(
                name = name,
                type = type,
                id = id,
            )
        }
    }

}