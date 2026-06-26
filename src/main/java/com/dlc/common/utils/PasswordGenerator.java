package com.dlc.common.utils;

import org.apache.shiro.crypto.hash.SimpleHash;

import java.util.UUID;

/**
 * 一次性工具：生成密码密文，用完可删
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        String password = "123456";
        String salt = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        String hash = new SimpleHash("SHA-256", password, salt, 16).toString();

        System.out.println("=== 密码生成结果 ===");
        System.out.println("明文密码: " + password);
        System.out.println("盐(salt): " + salt);
        System.out.println("密文(password): " + hash);
        System.out.println();
        System.out.println("执行以下 SQL 更新 admin 账号密码:");
        System.out.println("UPDATE sys_user SET password='" + hash + "', salt='" + salt + "' WHERE username='admin';");
    }
}
