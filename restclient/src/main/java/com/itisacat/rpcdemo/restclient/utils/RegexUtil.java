package com.itisacat.rpcdemo.restclient.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
    private RegexUtil(){}

    public static boolean check(String regEx, String checkData) {
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        Matcher matcher = pattern.matcher(checkData);
        // 字符串是否与正则表达式相匹配
        return matcher.matches();
    }
}
