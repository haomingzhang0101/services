package com.haoming.house.api.utils;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public final class Rests {

    private static final Logger LOGGER = LoggerFactory.getLogger(Rests.class);

    private Rests(){};

    private static DefaultHandler defaultHandler = new DefaultHandler();

    /**
     * Execute service call, and return status
     * @author zhanghm
     * @date 2018-08-18 12:40
     */
    public static <T> T exc(Callable<T> callable) {
        return exc(callable, defaultHandler);
    }

    public static <T> T exc(Callable<T> callable, ResultHandler handler) {
        T result = sendReq(callable);
        return handler.handle(result);
    }

    public static String toUrl(String serviceName, String path) {
        return "http://" + serviceName + path;
    }

    public static class DefaultHandler implements ResultHandler {

        @Override
        public <T> T handle(T result) {
            int code = 1;
            String msg = "";
            try {
                code = (Integer) FieldUtils.readDeclaredField(result, "code", true);
                msg = (String) FieldUtils.readDeclaredField(result, "msg", true);
            } catch (Exception e) {
                //ignore
            }
            if (code != 0) {
                throw new RestException("Get erroNo " + code + " when execute rest call with errorMsg " + msg);
            }
            return result;
        }
    }

    public interface ResultHandler{
        <T> T handle(T result);
    }

    public static <T> T sendReq(Callable<T> callable) {
        T result = null;
        try {
            result = callable.call();
        } catch (Exception e) {
            throw new RestException("sendReq error");
        } finally {
            LOGGER.info("result");
        }
        return result;
    }
}
