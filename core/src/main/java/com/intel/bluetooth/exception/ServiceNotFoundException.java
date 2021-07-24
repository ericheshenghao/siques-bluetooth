package com.intel.bluetooth.exception;

/**
 * @author: 何胜豪
 * @Title: TODO
 * @Package: com.intel.bluetooth.exception
 * @Description:
 * @date : 2021/7/24 18:04
 */
public class ServiceNotFoundException  extends Exception{
    public ServiceNotFoundException(Throwable cause) {
        super(cause);
    }
    public ServiceNotFoundException(String message) {
        super(message);
    }

    public ServiceNotFoundException() {
        super();
    }

    public ServiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
