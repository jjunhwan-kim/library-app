package com.group.libraryapp.calculator

fun main() {
    val calculatorTest = CalculatorTest()
    calculatorTest.addTest()
    calculatorTest.minusTest()
    calculatorTest.multiplyTest()
    calculatorTest.divideTest()
    calculatorTest.divideExceptionTest()
}

class CalculatorTest {

    fun addTest() {
        // given
        // 1. 테스트 대상 생성
        val calculator = Calculator(5)

        // when
        // 2. 테스트 하고싶은 기능 호출
        calculator.add(3)

        /*
        val expectedCalculator = Calculator(8)
        // data class는 equals가 자동으로 구현되므로 비교 연산자로 객체 자체를 비교할 수 있음
        if (calculator != expectedCalculator) {
            throw IllegalArgumentException()
        }
        */

        // then
        // 3. 테스트 결과 확인
        if (calculator.number != 8) {
            throw IllegalArgumentException()
        }
    }

    fun minusTest() {
        // given
        val calculator = Calculator(5)

        // when
        calculator.minus(3)

        // then
        if (calculator.number != 2) {
            throw IllegalArgumentException()
        }
    }

    fun multiplyTest() {
        // given
        val calculator = Calculator(5)

        // when
        calculator.multiply(3)

        // then
        if (calculator.number != 15) {
            throw IllegalArgumentException()
        }
    }

    fun divideTest() {
        // given
        val calculator = Calculator(5)

        // when
        calculator.divide(2)

        // then
        if (calculator.number != 2) {
            throw IllegalArgumentException()
        }
    }

    fun divideExceptionTest() {
        // given
        val calculator = Calculator(5)

        // when
        try {
            calculator.divide(0)
        } catch (e: IllegalArgumentException) {
            if (e.message != "0으로 나눌 수 없습니다") {
                throw IllegalStateException("메시지가 다릅니다")
            }
            // 테스트 성공
            return
        } catch (e: Exception) {
            // 테스트 실패
            throw IllegalStateException()
        }

        throw IllegalStateException("기대하는 예외가 발생하지 않았습니다")
    }
}



