package com.clq.utils;

/**
 * 
 * @author 陈礼强
 * @update date 2015-07-16
 */
public class EnvUtils {
    public static Env getEnv() {
        return threadLocal.get();
    }

    public static void removeEnv() {
        threadLocal.remove();
    }

    private static ThreadLocal<Env> threadLocal = new ThreadLocal<Env>() {
        @Override
        protected Env initialValue() {
            return new Env();
        }
    };

}
