package org.verwandlung.voj.web.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializeUtil {
    private  static Logger logger = LoggerFactory.getLogger("SerializeUtil");
    public static String serializeObject(Object obj){
        logger.info("serialize object ："+obj);
        String jsonObj = JSON.toJSONString(obj);
        return jsonObj;
    }
    public static JSONObject unserializeObject(String serobj){
        logger.info("unserialize object ："+serobj);
        JSONObject jsonObj = JSON.parseObject(serobj);
        return jsonObj;
    }
}
