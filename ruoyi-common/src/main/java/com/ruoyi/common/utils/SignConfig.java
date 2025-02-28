package com.ruoyi.common.utils;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class SignConfig {

    private static Logger log = LoggerFactory.getLogger(SignConfig.class);

    /**
     * 对象入参,对象转MAP
     * @param object
     * @return
     * @throws Exception
     */
//    public static String getSignNewObject(Object object,String key,String type) throws Exception {
//        Map<String, String> map = BeanUtils.describe(object);
//        return getSign(map,key,type);
//    }

    /**
     * 签名
     * @param map
     * @return
     */
    public static String getSign(Map<String,String> map,String key,String type,String isKey,String keyName) throws Exception{

        TreeMap<String,String> treeMap = new TreeMap<>(map);

        ArrayList<String> list = new ArrayList<String>();
        //type为0表示为空字段不参与签名，1表示参与签名,2表示为空字段和0不参与签名
        if(type.equals("0")){
            for(Map.Entry<String,String> entry:treeMap.entrySet()){
                if(entry.getValue() != null && StringUtils.isNotEmpty(entry.getValue().toString()) && !"null".equals(entry.getValue())
                        && !"".equals(entry.getValue()) ){
                    list.add(entry.getKey() + "=" + entry.getValue() + "&");
                }
            }
        }else if(type.equals("1")){
            for(Map.Entry<String,String> entry:treeMap.entrySet()){
                if(!entry.getValue().equals("sign123")){
                    list.add(entry.getKey() + "=" + entry.getValue() + "&");
                }
            }
        }else if(type.equals("2")){
            log.info("加密type类型 为0为空不参与验签");
            for(Map.Entry<String,String> entry:treeMap.entrySet()){
                if(entry.getValue() != null && StringUtils.isNotEmpty(entry.getValue().toString()) && !"null".equals(entry.getValue())
                        && !"".equals(entry.getValue()) && !"0".equals(entry.getValue()) && !entry.getValue().equals("0")
                        && !(entry.getValue().matches("-?\\d+(\\.\\d+)?") && Integer.parseInt(entry.getValue()) == 0)){
                    list.add(entry.getKey() + "=" + entry.getValue() + "&");
                }
            }
        }

        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
//        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);  //忽略大小写
//        Arrays.sort(arrayToSort);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();

        //上游不一样，拼接key的方式不一样
        if(isKey.equals("1")){
            result = result.substring(0, result.length() - 1);
            result +=  key;
        }else if(isKey.equals("2")){
            result +=  key;
        }else if(isKey.equals("3")){
            result = result.substring(0, result.length() - 1);
        }else{
            result += keyName + "=" + key;
        }

        try{
//            log.info("Sign 加密前 MD5:"+ result);
            result = MD5Utils.MD5Encode(result).toUpperCase();
//            log.info("Sign 加密后 Result:" + result);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 签名
     * @param map
     * @return
     */
    public static String getSignNew(Map<String,Object> map,String key,String type,String isKey,String keyName) throws Exception{

        TreeMap<String,Object> treeMap = new TreeMap<>(map);

        ArrayList<String> list = new ArrayList<String>();
        //type为0表示为空字段不参与签名，1表示参与签名
        if(type.equals("0")){
            for(Map.Entry<String,Object> entry:treeMap.entrySet()){
                if(entry.getValue() != null && StringUtils.isNotEmpty(entry.getValue().toString()) && !"null".equals(entry.getValue())
                        && !"".equals(entry.getValue()) ){
                    list.add(entry.getKey() + "=" + entry.getValue() + "&");
                }
            }
        }else if(type.equals("1")){
            for(Map.Entry<String,Object> entry:treeMap.entrySet()){
                if(!entry.getValue().equals("sign123")){
                    list.add(entry.getKey() + "=" + entry.getValue() + "&");
                }
            }
        }else if(type.equals("2")){
            log.info("加密type类型 为0为空不参与验签");
            for(Map.Entry<String,Object> entry:treeMap.entrySet()){
                if(entry.getValue() != null && StringUtils.isNotEmpty(entry.getValue().toString()) && !"null".equals(entry.getValue())
                        && !"".equals(entry.getValue()) && !"0".equals(entry.getValue())
                && !(entry.getValue() instanceof Integer && ((Integer) entry.getValue()).intValue() == 0)){
                    list.add(entry.getKey() + "=" + entry.getValue() + "&");
                }
            }
        }

        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
//        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);  //忽略大小写

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        //上游不一样，拼接key的方式不一样
        if(isKey.equals("1")){
            result = result.substring(0, result.length() - 1);
            result +=  key;
        }else if(isKey.equals("2")){
            result +=  key;
        }else if(isKey.equals("3")){
            result = result.substring(0, result.length() - 1);
        }else{
            result += keyName + "=" + key;
        }

        try{
            log.info("Sign 加密前 MD5:"+ result);
            result = MD5Utils.MD5Encode(result).toUpperCase();
            log.info("Sign 加密后 Result:" + result);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 签名（玖忆这家上游专用）下单
     * @param map
     * @return
     */
    public static String getSignNew_JY1(Map<String,Object> map,String key) throws Exception{

        String result = map.get("fxid").toString() + map.get("fxddh") +
                map.get("fxfee") + map.get("fxnotifyurl") + key;
        try{
            log.info("Sign 加密前 MD5:"+ result);
            result = MD5Utils.MD5Encode(result);
            log.info("Sign 加密后 Result:" + result);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 签名（玖忆这家上游专用）查单
     * @param map
     * @return
     */
    public static String getSignNew_JY2(Map<String,Object> map,String key) throws Exception{

        String result = map.get("fxid").toString() + map.get("fxddh") + "orderquery" + key;
        try{
            log.info("Sign 加密前 MD5:"+ result);
            result = MD5Utils.MD5Encode(result);
            log.info("Sign 加密后 Result:" + result);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 签名（锦鲤这家上游专用）下单
     * @param map
     * @return
     */
    public static String getSignNew_JL1(Map<String,Object> map,String key) throws Exception{

        String result = "merchantid=" + map.get("merchantid").toString() + "&type=" + map.get("type") + "&value=" +
                map.get("value") + "&orderid=" + map.get("orderid") + "&callbackurl=" + map.get("callbackurl") + key;

        try{
            log.info("Sign 加密前 MD5:"+ result);
            result = MD5Utils.MD5Encode(result);
            log.info("Sign 加密后 Result:" + result);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 签名（锦鲤这家上游专用）查单
     * @param map
     * @return
     */
    public static String getSignNew_JL2(Map<String,Object> map,String key) throws Exception{

        String result = "merchantid=" + map.get("merchantid").toString() + "&orderid=" + map.get("orderid").toString() + key;

        try{
            log.info("Sign 加密前 MD5:"+ result);
            result = MD5Utils.MD5Encode(result);
            log.info("Sign 加密后 Result:" + result);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 签名（盈通这家上游专用）下单
     * @param map
     * @return
     */
    public static String getSignNew_YT1(Map<String,Object> map,String key) throws Exception{

        String result = "";
        if(ObjectUtils.isNotEmpty(map.get("backUrl"))){

             result = "companyCodeNo=" + map.get("companyCodeNo").toString() + "&paySerialNo=" + map.get("paySerialNo") + "&payType=" +
                    map.get("payType") + "&amount=" + map.get("amount") + "&backUrl=" + map.get("backUrl") + "&key=" + key;
        }else{

            result = "companyCodeNo=" + map.get("companyCodeNo").toString() + "&paySerialNo=" + map.get("paySerialNo") + "&payType=" +
                    map.get("payType") + "&amount=" + map.get("amount")  + "&key=" + key;
        }


        try{
            log.info("Sign 加密前 MD5:"+ result);
            result = MD5Utils.MD5Encode(result).toUpperCase();;
            log.info("Sign 加密后 Result:" + result);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 签名（盈通这家上游专用）查单
     * @param map
     * @return
     */
    public static String getSignNew_YT2(Map<String,Object> map,String key) throws Exception{

        String result = "companyCodeNo=" + map.get("companyCodeNo").toString() + "&paySerialNo=" + map.get("paySerialNo") + "&queryTime=" +
                map.get("queryTime") + "&key=" + key;
        try{
            log.info("Sign 加密前 MD5:"+ result);
            result = MD5Utils.MD5Encode(result).toUpperCase();;
            log.info("Sign 加密后 Result:" + result);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 万嘉（万嘉这家上游专用）下单
     * @param map
     * @return
     */
    public static String getSignNew_WJ1(Map<String,Object> map,String key) throws Exception{

        String result = "";

        result = "key=" + map.get("merchant_no") + key + "&order_amount=" + map.get("order_amount") + "&order_no=" +
                map.get("order_no") + "&pay_code=" + map.get("pay_code") + "&ts=" + map.get("ts") ;

        try{
            log.info("Sign 加密前 MD5:"+ result);
            result = MD5Utils.MD5Encode(result);
            log.info("Sign 加密后 Result:" + result);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 万嘉（万嘉这家上游专用）查单
     * @param map
     * @return
     */
    public static String getSignNew_WJ2(Map<String,Object> map,String key) throws Exception{

        String result = "key=" + map.get("merchant_no") + key + "&merchant_no=" + map.get("merchant_no") + "&order_no=" + map.get("order_no") ;
        try{
            log.info("Sign 加密前 MD5:"+ result);
            result = MD5Utils.MD5Encode(result);
            log.info("Sign 加密后 Result:" + result);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * pga（pga这家上游专用）下单和查单
     * @param map
     * @return
     */
    public static String getSignNew_PGA(Map <String, Object> map, String key) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        StringJoiner sj = new StringJoiner("&");

        map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(x -> {
            if(x.getValue() != null && StringUtils.isNotEmpty(x.getValue().toString()) && !"null".equals(x.getValue())
                    && !"".equals(x.getValue()) && !"sign".equals(x.getKey())){
                sj.add(x.getKey() + "=" + x.getValue());
            }
        });
        log.info("pga 加密前字符串 HmacSHA256:"+ sj);
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] array = sha256_HMAC.doFinal(sj.toString().getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte item: array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        log.info("pga 加密后字符串 HmacSHA256:"+ sb.toString());
        return sb.toString();
    }




    /**
     * get请求生成参数
     * @param map
     * @return
     */
    public static String getHttpGetParam(Map<String,Object> map) {

        TreeMap<String,Object> treeMap = new TreeMap<>(map);

        ArrayList<String> list = new ArrayList<String>();
        //type为0表示为空字段不参与签名，1表示参与签名
        for(Map.Entry<String,Object> entry:treeMap.entrySet()) {
            if (entry.getValue() != null && StringUtils.isNotEmpty(entry.getValue().toString()) && !"null".equals(entry.getValue())
                    && !"".equals(entry.getValue())) {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }

        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        return result;
    }

    /**
     * 通用类型转换方法
     *
     * @param value      要转换的值
     * @param targetTypeName 目标类型的名称（如 "Integer", "String", "Double" 等）
     * @return 转换后的值，如果无法转换，则返回 null
     */
    public static Object dataTypeConvert(Object value, String targetTypeName) {
        // 如果值为 null，直接返回 null
        if (value == null) {
            return null;
        }

        try {

            // 将目标类型的字符串名称转换为 Class 对象
            Class<?> targetType = getTypeClass(targetTypeName);

            if (targetType == null) {
                throw new IllegalArgumentException("未知的目标类型: " + targetTypeName);
            }

            // 如果类型已经匹配，直接返回
            if (targetType.isInstance(value)) {
                return value;
            }

            // 转为字符串用于处理大多数类型
            String stringValue = value.toString();

            // 处理基础数据类型和常见类型
            if (targetType == String.class) {
                return stringValue;
            } else if (targetType == Integer.class || targetType == int.class) {
                return Integer.valueOf(stringValue);
            } else if (targetType == Long.class || targetType == long.class) {
                return Long.valueOf(stringValue);
            } else if (targetType == Double.class || targetType == double.class) {
                return Double.valueOf(stringValue);
            } else if (targetType == Float.class || targetType == float.class) {
                return Float.valueOf(stringValue);
            } else if (targetType == Boolean.class || targetType == boolean.class) {
                return Boolean.valueOf(stringValue);
            } else if (targetType == BigDecimal.class) {
                return new BigDecimal(stringValue);
            } else if (targetType == Short.class || targetType == short.class) {
                return Short.valueOf(stringValue);
            } else if (targetType == Byte.class || targetType == byte.class) {
                return Byte.valueOf(stringValue);
            } else if (targetType == Character.class || targetType == char.class) {
                if (stringValue.length() == 1) {
                    return stringValue.charAt(0);
                } else {
                    throw new IllegalArgumentException("无法将值转换为字符类型：" + stringValue);
                }
            }
        } catch (Exception e) {
            System.err.println("无法将值 [" + value + "] 转换为类型 [" + targetTypeName + "]：" + e.getMessage());
        }

        // 转换失败返回 null
        return null;
    }

    /**
     * 根据类型名称获取对应的 Class 对象
     *
     * @param typeName 类型名称（如 "Integer", "String", "Double" 等）
     * @return 对应的 Class 对象
     */
    private static Class<?> getTypeClass(String typeName) {
        switch (typeName) {
            case "String":
                return String.class;
            case "Integer":
            case "int":
                return Integer.class;
            case "Long":
            case "long":
                return Long.class;
            case "Double":
            case "double":
                return Double.class;
            case "Float":
            case "float":
                return Float.class;
            case "Boolean":
            case "boolean":
                return Boolean.class;
            case "BigDecimal":
                return BigDecimal.class;
            case "Short":
            case "short":
                return Short.class;
            case "Byte":
            case "byte":
                return Byte.class;
            case "Character":
            case "char":
                return Character.class;
            default:
                return null; // 如果未知类型，返回 null
        }
    }


    public static boolean checkIsSignValidFromResyponseStringObject(Object object,String key,String type,String lowerOrUpper,String isKey,String keyName) throws Exception {
        Map<String, String> map = org.apache.commons.beanutils.BeanUtils.describe(object);
        return checkIsSignValidFromResponseString(map,key,type,lowerOrUpper,isKey,keyName);
    }


    /**
     * object转换为map  验证签名
     * @param map
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws IllegalAccessException
     */
    private static boolean checkIsSignValidFromResponseString(Map<String,String> map,String key,String type,String lowerOrUpper,String isKey,String keyName) throws Exception {
        String signFromAPIResponse = null;
        if(map.get("sign") != null){
            signFromAPIResponse = map.get("sign").toString();
        }

        //大总统、evo的回调是大写Sign
        if(map.get("Sign") != null){
            signFromAPIResponse = map.get("Sign").toString();
        }

        if(signFromAPIResponse == "" || signFromAPIResponse == null){
            log.error("+++++++++++sign验签报空+++++++++++");
            return false;
        }

//        TreeMap<String,String> treeMap = new TreeMap<>(map);

        //清掉返回数据对象里面的Sign数据（不能把这个数据也加进去进行签名），然后用签名算法进行签名
        //type为0表示为空字段不参与签名，1表示参与签名
        String signForAPIResponse = "";
        if(type.equals("0") || type.equals("2")){
            map.put("sign","");
            //大总统、evo
            if(map.get("Sign") != null){
                map.put("Sign","");
            }
            //将API返回的数据根据用签名算法进行计算新的签名，用来跟API返回的签名进行比较
            //重新签名
            log.info("签名前的map="+map);
             signForAPIResponse = SignConfig.getSign(map,key,type,isKey,keyName);
            log.info("签名后的字符串="+signForAPIResponse);
        }else{
            map.put("sign","sign123");
            //大总统、evo
            if(map.get("Sign") != null){
                map.put("Sign","sign123");
            }
            //将API返回的数据根据用签名算法进行计算新的签名，用来跟API返回的签名进行比较
            //重新签名
            log.info("签名前的map="+map);
             signForAPIResponse = SignConfig.getSign(map,key,type,isKey,keyName);
            log.info("签名后的字符串="+signForAPIResponse);
        }

        if(lowerOrUpper.equals("1")){
            signForAPIResponse = signForAPIResponse.toLowerCase();
        }


        if(!signForAPIResponse.equals(signFromAPIResponse)){
            //签名验不过，表示这个API返回的数据有可能已经被篡改了
            log.error("++++++++++++验签匹配不一致+++++++++++++++++++++++++++++++++++");
            return false;
        }
        return true;
    }

    public static boolean checkIsSignValidFromResponse(Map<String,Object> map,String key,String type,String lowerOrUpper,String isKey,String keyName) throws Exception {
        String signFromAPIResponse = null;
        if(map.get("sign") != null){
            signFromAPIResponse = map.get("sign").toString();
        }
        //大总统、evo的回调是大写Sign
        if(map.get("Sign") != null){
            signFromAPIResponse = map.get("Sign").toString();
        }

        if(signFromAPIResponse == "" || signFromAPIResponse == null){
            log.error("+++++++++++sign验签报空+++++++++++");
            return false;
        }

        //清掉返回数据对象里面的Sign数据（不能把这个数据也加进去进行签名），然后用签名算法进行签名
        //type为0表示为空字段不参与签名，1表示参与签名
        String signForAPIResponse = "";
        if(type.equals("0") || type.equals("2")){
            map.put("sign","");
            //大总统、evo
            if(map.get("Sign") != null){
                map.put("Sign","");
            }

            //将API返回的数据根据用签名算法进行计算新的签名，用来跟API返回的签名进行比较
            //重新签名
            log.info("签名前的map="+map);
            signForAPIResponse = SignConfig.getSignNew(map,key,type,isKey,keyName);
            log.info("签名后的字符串="+signForAPIResponse);
        }else{

            map.put("sign","sign123");

            //大总统、evo
            if(map.get("Sign") != null){
                map.put("Sign","sign123");
            }
            //将API返回的数据根据用签名算法进行计算新的签名，用来跟API返回的签名进行比较
            //重新签名
            log.info("签名前的map="+map);
            signForAPIResponse = SignConfig.getSignNew(map,key,type,isKey,keyName);
            log.info("签名后的字符串="+signForAPIResponse);
        }

        if(lowerOrUpper.equals("1")){
            signForAPIResponse = signForAPIResponse.toLowerCase();
        }

        if(!signForAPIResponse.equals(signFromAPIResponse)){
            //签名验不过，表示这个API返回的数据有可能已经被篡改了
            log.error("++++++++++++验签匹配不一致+++++++++++++++++++++++++++++++++++");
            return false;
        }
        return true;
    }

    //玖忆专用的上游验签（回调过来的时候使用）
    public static boolean checkIsSignValidFromResponse_JY(Map<String,Object> map,String key) throws Exception {
        String signFromAPIResponse = null;
        if(map.get("fxsign") != null){
            signFromAPIResponse = map.get("fxsign").toString();
        }

        if(signFromAPIResponse == "" || signFromAPIResponse == null){
            log.error("+++++++++++sign验签报空+++++++++++");
            return false;
        }

        String result = map.get("fxstatus").toString() + map.get("fxid") +
                map.get("fxddh") + map.get("fxfee") + key;

        String signForAPIResponse = MD5Utils.MD5Encode(result);

        if(!signForAPIResponse.equals(signFromAPIResponse)){
            log.error("++++++++++++验签匹配不一致+++++++++++++++++++++++++++++++++++");
            return false;
        }
        return true;
    }

    //锦鲤专用的上游验签（回调过来的时候使用）
    public static boolean checkIsSignValidFromResponse_JL(Map<String,Object> map,String key) throws Exception {
        String signFromAPIResponse = null;
        if(map.get("sign") != null){
            signFromAPIResponse = map.get("sign").toString();
        }

        if(signFromAPIResponse == "" || signFromAPIResponse == null){
            log.error("+++++++++++sign验签报空+++++++++++");
            return false;
        }


        String result = "attach=" + map.get("attach").toString() + "&completetime=" + map.get("completetime") + "&merchantid=" +
                map.get("merchantid") + "&msg=" + map.get("msg") + "&opstate=" + map.get("opstate")
                + "&orderid=" + map.get("orderid") + "&sysorderid=" + map.get("sysorderid") + "&value=" + map.get("value") + key;


        String signForAPIResponse = MD5Utils.MD5Encode(result);

        if(!signForAPIResponse.equals(signFromAPIResponse)){
            log.error("++++++++++++验签匹配不一致+++++++++++++++++++++++++++++++++++");
            return false;
        }
        return true;
    }

    //盈通专用的上游验签（回调过来的时候使用）
    public static boolean checkIsSignValidFromResponse_YT(Map<String,Object> map,String key) throws Exception {
        String signFromAPIResponse = null;
        if(map.get("sign") != null){
            signFromAPIResponse = map.get("sign").toString();
        }

        if(signFromAPIResponse == "" || signFromAPIResponse == null){
            log.error("+++++++++++sign验签报空+++++++++++");
            return false;
        }

        //{msg=订单成功, sign=C8593EBF52F9E4174FBD938E63CF3C3A, paySerialNo=AH20250222203617074934, amount=500.0, code=200, endTime=2025-02-22 20:36:50}
        //amount=500.0, 这个参数少一个0
        String result = "paySerialNo=" + map.get("paySerialNo").toString() + "&amount=" + map.get("amount") + '0' +
                 "&endTime=" + map.get("endTime") + "&key=" + key;

        String signForAPIResponse = MD5Utils.MD5Encode(result).toUpperCase();

        if(!signForAPIResponse.equals(signFromAPIResponse)){
            log.error("++++++++++++验签匹配不一致+++++++++++++++++++++++++++++++++++");
            return false;
        }
        return true;
    }

    //万嘉专用的上游验签（回调过来的时候使用）
    public static boolean checkIsSignValidFromResponse_WJ(Map<String,Object> map,String key) throws Exception {
        String signFromAPIResponse = null;
        if(map.get("sign") != null){
            signFromAPIResponse = map.get("sign").toString();
        }

        if(signFromAPIResponse == "" || signFromAPIResponse == null){
            log.error("+++++++++++sign验签报空+++++++++++");
            return false;
        }

        String result = "key=" + map.get("merchant_no") + key + "&order_amount=" + map.get("order_amount") +
                "&order_no=" + map.get("order_no") + "&pay_code=" + map.get("pay_code");

        String signForAPIResponse = MD5Utils.MD5Encode(result);

        if(!signForAPIResponse.equals(signFromAPIResponse)){
            log.error("++++++++++++验签匹配不一致+++++++++++++++++++++++++++++++++++");
            return false;
        }
        return true;
    }

    //AGO专用的上游验签（回调过来的时候使用）
    public static boolean checkIsSignValidFromResponse_PGA(Map<String,Object> map,String key) throws Exception {
        String signFromAPIResponse = null;
        if(map.get("sign") != null){
            signFromAPIResponse = map.get("sign").toString();
        }

        if(signFromAPIResponse == "" || signFromAPIResponse == null){
            log.error("+++++++++++sign验签报空+++++++++++");
            return false;
        }

        String signForAPIResponse = getSignNew_PGA(map, key);

        if(!signForAPIResponse.equals(signFromAPIResponse)){
            log.error("++++++++++++验签匹配不一致+++++++++++++++++++++++++++++++++++");
            return false;
        }
        return true;
    }




    /**
     * 生成签名
     */
    public static String createSign(Map<String,Object> map, String key){
        SortedMap<Object,Object> parameters = new TreeMap<Object, Object>(map);
        StringBuffer sbkey = new StringBuffer();
        //1.将所有参与传参的参数按照accsii排序（升序)
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            //2.空值不传递，不参与签名组串
            if(null != v && !"".equals(v)) {
                sbkey.append(k + "=" + v + "&");
            }
        }
        sbkey=sbkey.append("key="+key);
        System.out.println("--------------"+sbkey.toString());
        //3.MD5加密,结果转换为大写字符
        String sign = getMD5(sbkey.toString()).toUpperCase();
        System.out.println("--------------"+sign);
        return sign;
    }

    /**
     * 对字符串进行MD5加密
     */
    public static String getMD5(String str) {
        MessageDigest digest;

        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(str.getBytes());
            return new BigInteger(1, digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
