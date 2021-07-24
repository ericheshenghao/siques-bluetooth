package com.intel.bluetooth.exception;

/**
 * @author: 何胜豪
 * @Title: TODO
 * @Package: com.intel.bluetooth.exception
 * @Description:
 * @date : 2021/7/24 18:04
 */
public class ServiceOffLineException extends Exception{
    public ServiceOffLineException(Throwable cause) {
        super(cause);
    }
    public ServiceOffLineException(String message) {
        super(message);
    }

    public ServiceOffLineException() {
        super();
    }

    public ServiceOffLineException(String message, Throwable cause) {
        super(message, cause);
    }
}
