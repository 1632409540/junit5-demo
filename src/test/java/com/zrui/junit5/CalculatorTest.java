package com.zrui.junit5;

import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.stream.Stream;

import static java.time.Duration.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class CalculatorTest {

    private Calculator calculator;

    @BeforeEach//在每次测试之前运行
    public void setCalculator() {
        calculator = new Calculator();
    }

    @AfterEach
    public void close(){
        System.out.println("finished");
    }

    @Test//定义测试方法
    @DisplayName("Calculator Test")//测试运行器显示的测试名称
    public void testMultiply() {
        assertEquals(20, calculator.multiply(4, 5));//assert 语句验证预期值和实际值
        assertEquals(20, calculator.multiply(4, 5), "Calculator multiply can`t use");//测试失败时的错误消息
    }

    @RepeatedTest(5)//多次运行的测试，在本例中为 5 次
    @DisplayName("Calculator Test more times")
    public void testMultiplyWithZero() {
        assertEquals(0, calculator.multiply(0, 10), "Multiple with zero should be zero");
        assertEquals(0, calculator.multiply(10, 0), "Multiple with zero should be zero");

    }

    @RepeatedTest(5)
    @DisplayName("Calculator Multiply used by not Linux")
    public void testMultiplyUseNotLinux() {
        Assumptions.assumeFalse(System.getProperty("os.name").contains("Linux")); //灵活禁用测试,测试运行者评估为跳过测试的结果
        assertEquals(0, calculator.multiply(0, 10), "Multiple with zero should be zero");
    }

    @Test
    @DisplayName("exceptionTesting")//期待异常
    public void exceptionTesting() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> calculator.setAge("123a"));
        assertEquals("For input string: \"123a\"", exception.getMessage());
    }

    @Test
    @Disabled("assertAll null")
    @DisplayName("assertAll Test")//使用 assertAll 分组断言
    void groupedAssertions() {
        Address address = new Address();
        assertAll("address name",
                () -> assertEquals("John", address.getFirstName()),
                () -> assertEquals("User", address.getLastName())
        );
    }

    @Test
    @Disabled("timeout")//超时测试
    @DisplayName("timeout Test")
    void timeoutNotExceeded() {
        assertTimeout(ofMillis(1), () -> calculator.doBackup());
    }

    @Test//超时测试，带返回结果
    @Disabled("timeout")
    @DisplayName("timeoutNotExceededWithResult")
    void timeoutNotExceededWithResult() {
        Integer actualResult = assertTimeout(ofSeconds(1), () -> {
            return calculator.multiply(10,20);
        });
        assertEquals(200, actualResult);
    }

    @Test//在超时期限过后取消测试
    @Disabled("timout")
    @DisplayName("assertTimeoutPreemptively test")
    void timeoutNotExceededWithResultWithConsole() {
        Integer actualResult = assertTimeoutPreemptively(ofSeconds(1), () -> {
            return calculator.multiply(10,20);
        });
        assertEquals(200, actualResult);
    }

    @TestFactory//在数据集上重复运行相同的测试
    public Stream<DynamicTest> testDifferentMultiplyOperations() {
        int[][] data = new int[][] { { 1, 2, 2 }, { 5, 3, 15 }, { 121, 4, 484 } };
        return Arrays.stream(data).map(entry -> {
            int m1 = entry[0];
            int m2 = entry[1];
            int expected = entry[2];
            return dynamicTest(m1 + " * " + m2 + " = " + expected, () -> {
                assertEquals(expected, calculator.multiply(m1, m2));
            });
        });
    }


}
