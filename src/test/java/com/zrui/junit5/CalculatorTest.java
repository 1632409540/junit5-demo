package com.zrui.junit5;

import com.sun.security.jgss.GSSUtil;
import com.sun.source.tree.AssertTree;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.*;

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

//    @AfterEach
//    public void close() {
//        System.out.println("finished");
//    }

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
    @DisplayName("assertAll Test")
//使用 assertAll 分组断言
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
            return calculator.multiply(10, 20);
        });
        assertEquals(200, actualResult);
    }

    @Test//在超时期限过后取消测试
    @Disabled("timout")
    @DisplayName("assertTimeoutPreemptively test")
    void timeoutNotExceededWithResultWithConsole() {
        Integer actualResult = assertTimeoutPreemptively(ofSeconds(1), () -> {
            return calculator.multiply(10, 20);
        });
        assertEquals(200, actualResult);
    }

    @TestFactory//在数据集上重复运行相同的测试
    public Stream<DynamicTest> testDifferentMultiplyOperations() {
        int[][] data = new int[][]{{1, 2, 2}, {5, 3, 15}, {121, 4, 484}};
        return Arrays.stream(data).map(entry -> {
            int m1 = entry[0];
            int m2 = entry[1];
            int expected = entry[2];
            return dynamicTest(m1 + " * " + m2 + " = " + expected, () -> {
                assertEquals(expected, calculator.multiply(m1, m2));
            });
        });
    }

    public static int[][] data() {
        return new int[][]{{1, 2, 2}, {5, 3, 15}, {121, 4, 484}};
    }

    @ParameterizedTest//使用参数化测试,
    @MethodSource(value = "data")//命名方法的结果作为参数传递给测试。
    void testWithStringParameter(int[] data) {
        int m1 = data[0];
        int m2 = data[1];
        int expected = data[2];
        assertEquals(expected, calculator.multiply(m1, m2));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 2})//允许您定义一组测试值。允许的类型是String，int，long，或double。
    void testValueSource(int ints) {
        Assertions.assertTrue(ints > 0,"data need > 0");
    }

    @DisplayName("String Source Test")
    @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
    @ValueSource(strings = {"zs", "ls", "ww"})
    public void testStringValueSource(String name){
        System.out.println(name);
    }

    @DisplayName("Enum Source Test")
    @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
    @EnumSource(OwnerType.class)//允许您将枚举常量作为测试类传递。使用 optional 属性，names您可以选择应该使用哪些常量。否则将使用所有属性。
    void enumTest(OwnerType ownerType) {
        System.out.println(ownerType);
    }

    @DisplayName("CsvSource Test")
    @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
    @CsvSource({ "foo, 1", "'baz, qux', 3" })//期望将字符串解析为 Csv。分隔符是','。
    void testMethod(String first, int second) {
        System.out.println(first+" -> "+second);
    }

    @DisplayName("CSV Input Test")
    @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
    @CsvSource({
            "FL, 1, 1",
            "OH, 2, 2",
            "MI, 3, 1"
    })
    void csvInputTest(String stateName, int val1, int val2) {
        System.out.println(stateName + " = " + val1 + ":" + val2);
    }

    @DisplayName("CSV From File Test")
    @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
    @CsvFileSource(resources = "/input.csv", numLinesToSkip = 0)
    void csvFromFileTest(String stateName, int val1, int val2) {
        System.out.println(stateName + " = " + val1 + ":" + val2);
    }

    @DisplayName("Method Provider Test")
    @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
    @MethodSource("getargs")
    void fromMethodTest(String stateName, int val1, int val2) {
        System.out.println(stateName + " = " + val1 + ":" + val2);
    }

    static Stream<Arguments> getargs() {
        return Stream.of(
                Arguments.of("FL", 5, 1),
                Arguments.of("OH", 2, 8),
                Arguments.of("MI", 3, 5));
    }

    @DisplayName("Custom Provider Test")
    @ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
    @ArgumentsSource(CustomArgsProvider.class)
    void fromCustomProviderTest(String stateName, int val1, int val2) {
        System.out.println(stateName + " = " + val1 + ":" + val2);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 12, 42})
    void testWithExplicitArgumentConversion(@ConvertWith(ToOctalStringArgumentConverter.class) String argument) {
        System.err.println(argument);
        assertNotNull(argument);
    }

    static class ToOctalStringArgumentConverter extends SimpleArgumentConverter {//JUnit 尝试自动转换源字符串以匹配测试方法的预期参数。
        @Override
        protected Object convert(Object source, Class<?> targetType) {
            assertEquals(Integer.class, source.getClass(), "Can only convert from Integers.");
            assertEquals(String.class, targetType, "Can only convert to String");
            return Integer.toOctalString((Integer) source);
        }
    }
}
