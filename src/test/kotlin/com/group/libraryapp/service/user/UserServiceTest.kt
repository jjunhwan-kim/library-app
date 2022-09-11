package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {

    @AfterEach
    fun clean() {
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("유저 저장이 정상 동작한다")
    fun saveUserTest() {

        // given
        val request = UserCreateRequest("최태현", null)

        // when
        userService.saveUser(request)

        // then
        val results = userRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].name).isEqualTo(request.name)
        assertThat(results[0].age).isNull() // 플랫폼 타입
    }

    @Test
    @DisplayName("유저 조회가 정상 동작한다")
    fun getUsersTest() {

        // given
        userRepository.saveAll(
            listOf(
                User("A", 20),
                User("B", null)
            )
        );

        // when
        val results = userService.getUsers()

        // then
        assertThat(results).hasSize(2)         // [UserResponse(), UserResponse()]
        assertThat(results).extracting("name").containsExactlyInAnyOrder("A", "B") // ["A", "B"]
        assertThat(results).extracting("age").containsExactlyInAnyOrder(20, null)

    }

    @Test
    @DisplayName("유저 업데이트가 정상 동작한다")
    fun updateUserNameTest() {

        // given
        val savedUser = userRepository.save(User("A", null))
        val request = UserUpdateRequest(savedUser.id!!, "B") // null 아님 단언

        // when
        userService.updateUserName(request)

        // then
        val result = userRepository.findAll()[0]
        assertThat(result.name).isEqualTo("B")
    }

    @Test
    @DisplayName("유저 삭제가 정상 동작한다")
    fun deleteUserTest() {

        // given
        val savedUser = userRepository.save(User("A", null))

        // when
        userService.deleteUser(savedUser.name)

        // then
        assertThat(userRepository.findAll()).isEmpty()
    }

    @Test
    @DisplayName("대출 기록이 없는 유저도 응답에 포함된다")
    fun getUserLoanHistoriesTest1() {

        // given
        val savedUser = userRepository.save(User("A", null))

        // when
        val results = userService.getUserLoanHistories()

        // then
        assertThat(results).hasSize(1)
        assertThat(results[0].name).isEqualTo(savedUser.name)
        assertThat(results[0].books).isEmpty()
    }

    @Test
    @DisplayName("대출 기록이 많은 유저의 응답이 정상 동작한다")
    fun getUserLoanHistoriesTest2() {

        // given
        val savedUser = userRepository.save(User("A", null))

        userLoanHistoryRepository.saveAll(
            listOf(
                UserLoanHistory.fixture(savedUser, "책1", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(savedUser, "책2", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(savedUser, "책3", UserLoanStatus.RETURNED),
            )
        )

        // when
        val results = userService.getUserLoanHistories()

        // then
        assertThat(results).hasSize(1)
        assertThat(results[0].name).isEqualTo(savedUser.name)
        assertThat(results[0].books).hasSize(3)

        assertThat(results[0].books).extracting("name")
            .containsExactlyInAnyOrder("책1", "책2", "책3")

        assertThat(results[0].books).extracting("isReturn")
            .containsExactlyInAnyOrder(false, false, true)
    }

    @Test
    @DisplayName("방금 두 경우가 합쳐진 테스트")
    fun getUserLoanHistoriesTest3() {

        // 큰 테스트 코드 1개보다 작은 테스트 코드 2개를 권장
        // 복잡한 테스트 1개보다, 간단한 테스트 2개가 유지보수하기 용이함
        // 앞 부분에서 실패가 나는 경우 뒷 부분은 아예 검증되지 않음

        // given
        val savedUsers = userRepository.saveAll(
            listOf(
                User("A", null),
                User("B", null),
            )
        )

        userLoanHistoryRepository.saveAll(
            listOf(
                UserLoanHistory.fixture(savedUsers[0], "책1", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(savedUsers[0], "책2", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(savedUsers[0], "책3", UserLoanStatus.RETURNED),
            )
        )

        // when
        val results = userService.getUserLoanHistories()

        // then
        assertThat(results).hasSize(2)

        val resultOfUserA = results.first { it.name == "A" }

        assertThat(resultOfUserA.name).isEqualTo(savedUsers[0].name)
        assertThat(resultOfUserA.books).hasSize(3)

        assertThat(resultOfUserA.books).extracting("name")
            .containsExactlyInAnyOrder("책1", "책2", "책3")

        assertThat(resultOfUserA.books).extracting("isReturn")
            .containsExactlyInAnyOrder(false, false, true)

        val resultOfUserB = results.first { it.name == "B" }

        assertThat(resultOfUserB.name).isEqualTo(savedUsers[1].name)
        assertThat(resultOfUserB.books).isEmpty()
    }
}