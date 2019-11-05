package com.itheima.test;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;

/**
 * @ClassName TestPOI
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2019/10/28 14:55
 * @Version V1.0
 */
public class TestPassword {

    // 使用SpringSecurity的加密方式
    /**
     * （1）加密(encode)：注册用户时，使用SHA-256+随机盐+密钥把用户输入的密码进行hash处理，得到密码的hash值，然后将其存入数据库中。
     （2）密码匹配(matches)：用户登录时，密码匹配阶段并没有进行密码解密（因为密码经过Hash处理，是不可逆的），而是使用相同的算法把用户输入的密码进行hash处理，得到密码的hash值，然后将其与从数据库中查询到的密码hash值进行比较。如果两者相同，说明用户输入的密码正确。
     * @throws IOException
     */
    @Test
    public void testPassword() throws IOException {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // $2a$10$KG3A4PWn/KYWhzfq9cmf8e7RM0wkpmScsJnFSe3WuUqwbe0cKt1nK
        String pasword1 = passwordEncoder.encode("123");
        // $2a$10$AlMpnTJ85PWWEzlCn0lUxeW5Rx3LuDorTqpgn.FuSY2zXWbJvRBuO
        String pasword2 = passwordEncoder.encode("123");
        System.out.println(pasword1);
        System.out.println(pasword2);

        boolean flag = passwordEncoder.matches("123", "$2a$10$KG3A4PWn/KYWhzfq9cmf8e7RM0wkpmScsJnFSe3WuUqwbe0cKt1na");
        System.out.println(flag);
    }


}
