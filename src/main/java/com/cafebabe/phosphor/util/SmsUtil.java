package com.cafebabe.phosphor.util;


import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author supersuntangao@gmail.com
 *
 * class_name: SMSUtil
 *
 * create_date: 2018/10/25
 *
 * create_time: 09:34
 *
 * description: 封装短信验证码
 **/

public final class SmsUtil {

    private static final String TPL_ID = "106234";

    private static final String TPL_VALUE= "#code#=";

    private static final String KEY = "37181fb763ecbddfa20e11c898bf2ff8";

    private static final String DTYPE = "json";

    private static final String URL = "http://v.juhe.cn/sms/send";

    /**
     *
     * @author supersuntangao@gmail.com
     *
     * class_name: SMSUtil
     *
     * create_date: 2018/10/11
     *
     * create_time: 00:09
     *
     * description:
     **/

    public static String sendVerCode(String mobile,Integer randomCode){
        Map<String,String> map = new HashMap<>(1);
        String randomCodeString =  randomCode.toString();
        map.put("key",KEY);
        map.put("dtype",DTYPE);
        map.put("mobile",mobile);
        map.put("tpl_id",TPL_ID);
        map.put("tpl_value",TPL_VALUE+randomCodeString);
        return OkHttpUtil.postSync(URL,map);
    }


}
