package com.cross.sync.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class Slf4fLogger {
    public static void trace(Object object, String message, Object... objects) {
        Logger logger = LoggerFactory.getLogger(object.getClass());
        logger.trace(message, objects);
    }

    public static void info(Object object, String message, Object... objects) {
        Logger logger = LoggerFactory.getLogger(object.getClass());
        logger.info(message, objects);
    }

    public static void debug(Object object, String message, Object... objects) {
        Logger logger = LoggerFactory.getLogger(object.getClass());
        logger.debug(message, objects);
    }

    public static void error(Object object, String message, Object... objects) {
        Logger logger = LoggerFactory.getLogger(object.getClass());
        logger.error(message, objects);
    }
}
