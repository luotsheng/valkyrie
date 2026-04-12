package com.changhong.opendb.app;

import java.io.File;

/**
 * 用户数据
 *
 * @author Luo Tiansheng
 * @since 2026/3/31
 */
public class Users
{
        public static final String userHome = System.getProperty("user.home");
        public static final File baseDir = new File(userHome, ".vdb");
        public static final File connectionDir = new File(baseDir, "C");
}
