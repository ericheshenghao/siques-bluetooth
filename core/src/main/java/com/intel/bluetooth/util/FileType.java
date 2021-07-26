package com.intel.bluetooth.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: 何胜豪
 * @Title: TODO
 * @Package: com.intel.bluetooth.util
 * @Description:
 * @date : 2021/7/26 23:06
 */
public  class FileType {
    private  static List<String>  list = new ArrayList<>();

    static {
        list.add("jpeg");
        list.add("jpg");
        list.add("gif");
        list.add("bmp");
        list.add("png");
        list.add("tif");
    }

    public static boolean isImage(String sufix){
        return list.contains(sufix);
    }
}
