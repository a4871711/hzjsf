package com.dlc.common.utils;

import java.util.UUID;

public class UuidTools {

    /**
     * 自动生成32位的UUid，对应数据库的主键id进行插入用。
     * @return
     */
    public static String get32Uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
