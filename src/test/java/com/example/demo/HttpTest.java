package com.example.demo;

import cn.hutool.http.HttpUtil;
import org.junit.jupiter.api.Test;

public class HttpTest {
    @Test
    void test001(){
        String s = HttpUtil.get("http://img14.360buyimg.com/n7/jfs/t1/60094/30/4064/161405/5d23f5a5Ef44d1a93/aefb557792d90dce.jpg");
        System.out.println(s);
    }
}
