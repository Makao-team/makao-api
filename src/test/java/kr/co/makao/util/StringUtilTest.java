package kr.co.makao.util;

import org.junit.jupiter.api.Test;

class StringUtilTest {
    @Test
    void generateEngDigit_문자열_길이_성공() {
        String result = StringUtil.random(6);

        assert result.length() == 6;
    }

    @Test
    void generateEngDigit_문자열_영어_숫자_성공() {
        String result = StringUtil.random(6);

        assert result.matches("^[A-Z0-9]*$");
    }
}