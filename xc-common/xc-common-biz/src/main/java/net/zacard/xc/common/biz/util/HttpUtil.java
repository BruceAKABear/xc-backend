package net.zacard.xc.common.biz.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import net.zacard.xc.common.biz.infra.exception.BusinessException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;

/**
 * 内部使用oKhttp
 *
 * @author guoqw
 * @since 2018-09-25 10:42
 */
public class HttpUtil {

    /**
     * 默认client
     */
    private static final OkHttpClient CLIENT = new OkHttpClient();

    /**
     * json
     */
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private static final int SUCCESS_CODE = 200;

    private static final String INNER_SUCCESS_CODE = "1";

    /**
     * 请求响应数据的key，为了兼容，目前有2种情况，优先使用严选规范的data来获取
     */
    private static final String[] RESPONSE_BODY_KEY = {"data", "result"};

    private static final String[] RESPONSE_MSG_KEY = {"message", "msg"};

    /**
     * 增加timeout的client，共享默认client的连接池和线程池
     */
    private static OkHttpClient customOkHttpClient(long timeout) {
        return CLIENT.newBuilder()
                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * ResponseBody
     */
    public static String getString(String url) {
        return getBody(url, 0);
    }

    /**
     * Http get方法，返回clazz类型对象
     */
    public static <T> T get(String url, Class<T> clazz) {
        return get(url, clazz, null);
    }

    /**
     * Http get方法，不返回对象
     */
    public static void get(String url) {
        getBody(url, 0);
    }

    /**
     * get方法获取class对象数据
     *
     * @param url     请求url
     * @param clazz   返回数据类型
     * @param headers 请求头
     * @return 请求结果
     */
    public static <T> T get(String url, Class<T> clazz, Map<String, String> headers) {
        String bodyStr = getBody(url, 0, headers);
        if (bodyStr == null) {
            return null;
        }
        return JSON.parseObject(bodyStr, clazz);
    }

    /**
     * Http get方法，针对返回的结果包装为Response场景
     */
    public static <T> T getWithResponse(String url, Class<T> clazz) {
        String bodyStr = getBody(url, 0);
        if (bodyStr == null) {
            return null;
        }
        bodyStr = getResultString(true, bodyStr);
        return JSON.parseObject(bodyStr, clazz);
    }

    /**
     * Http get方法，返回clazz类型的list
     */
    public static <T> List<T> getList(String url, Class<T> clazz) {
        String bodyStr = getBody(url, 0);
        if (bodyStr == null) {
            return Collections.emptyList();
        }
        return JSON.parseArray(bodyStr, clazz);
    }

    /**
     * Http get方法，返回clazz类型的list
     * 针对返回结果包装为Response的场景
     */
    public static <T> List<T> getListWithResponse(String url, Class<T> clazz) {
        String bodyStr = getBody(url, 0);
        if (bodyStr == null) {
            return Collections.emptyList();
        }
        bodyStr = getResultString(true, bodyStr);
        return JSON.parseArray(bodyStr, clazz);
    }

    /**
     * Http post方法，不需要返回值
     */
    public static void post(String url, String json) {
        doPost(url, json, 0, null, false);
    }

    /**
     * post方法直接获取返回的body string
     */
    public static String postString(String url, String json) {
        return postBody(url, json, 0);
    }

    /**
     * Http post方法，返回为clazz类型的对象
     *
     * @param json String类型的请求参数
     */
    public static <T> T post(String url, String json, Class<T> clazz) {
        return doPost(url, json, 0, clazz, false);
    }

    /**
     * Http post方法，返回类型为clazz类型的对象
     * 针对返回结果包装成Response的场景
     *
     * @param json String类型的请求参数
     */
    public static <T> T postWithResponse(String url, String json, Class<T> clazz) {
        return doPost(url, json, 0, clazz, true);
    }

    /**
     * Http post方法，返回结果为clazz类型list
     *
     * @param json String类型的请求参数
     */
    public static <T> List<T> postList(String url, String json, Class<T> clazz) {
        return doPostList(url, json, 0, clazz, false);
    }

    /**
     * Http post方法，返回结果为clazz类型list
     * 针对结果包装为Response的场景
     *
     * @param json String类型的请求参数
     */
    public static <T> List<T> postListWithResponse(String url, String json, Class<T> clazz) {
        return doPostList(url, json, 0, clazz, true);
    }

    /**
     * @param jsonObj Object类型的请求参数
     */
    public static void post(String url, Object jsonObj) {
        String json = JSON.toJSONString(jsonObj);
        doPost(url, json, 0, null, false);
    }

    /**
     * Http post方法，返回结果为clazz类型对象
     *
     * @param jsonObj Object类型的请求参数
     */
    public static <T> T post(String url, Object jsonObj, Class<T> clazz) {
        String json = JSON.toJSONString(jsonObj);
        return doPost(url, json, 0, clazz, false);
    }

    /**
     * Http post方法，返回结果为clazz类型对象
     * 针对返回结果包装为Response的场景
     *
     * @param jsonObj Object类型的请求参数
     */
    public static <T> T postWithResponse(String url, Object jsonObj, Class<T> clazz) {
        String json = JSON.toJSONString(jsonObj);
        return doPost(url, json, 0, clazz, true);
    }

    public static <T> T postWithResponse(String url, Object jsonObj, TypeReference<T> typeReference) {
        String json = JSON.toJSONString(jsonObj);
        return doPostType(url, json, 0, typeReference, true);
    }

    public static <T> T postType(String url, Object jsonObj, TypeReference<T> typeReference) {
        String json = JSON.toJSONString(jsonObj);
        return doPostType(url, json, 0, typeReference, false);
    }

    /**
     * Http post方法，返回结果为clazz类型list
     *
     * @param jsonObj Object类型的请求参数
     */
    public static <T> List<T> postList(String url, Object jsonObj, Class<T> clazz) {
        String json = JSON.toJSONString(jsonObj);
        return doPostList(url, json, 0, clazz, false);
    }

    /**
     * Http post方法，返回结果为clazz类型list
     * 针对返回结果包装为Response的场景
     *
     * @param jsonObj Object类型的请求参数
     */
    public static <T> List<T> postListWithResponse(String url, Object jsonObj, Class<T> clazz) {
        String json = JSON.toJSONString(jsonObj);
        return doPostList(url, json, 0, clazz, true);
    }

    public static String postString(String url, Object jsonObj) {
        String json = JSON.toJSONString(jsonObj);
        return postBody(url, json, 0);
    }

    public static String postStringWithResponse(String url, Object jsonObj) {
        String json = JSON.toJSONString(jsonObj);
        String responseJson = postBody(url, json, 0);
        return getResultString(true, responseJson);
    }

    public static String postStringWithResponse(String url, String json) {
        String responseJson = postBody(url, json, 0);
        return getResultString(true, responseJson);
    }

    /**
     * 带timeout参数的get方法
     * 返回clazz类型的对象
     */
    public static <T> T get(String url, long timeout, Class<T> clazz) {
        String bodyStr = getBody(url, timeout);
        if (bodyStr == null) {
            return null;
        }
        return JSON.parseObject(bodyStr, clazz);
    }

    /**
     * 带timeout参数的get方法
     * 返回clazz类型的对象
     * 针对返回结果包装为Response的场景
     */
    public static <T> T getWithResponse(String url, long timeout, Class<T> clazz) {
        String bodyStr = getBody(url, timeout);
        if (bodyStr == null) {
            return null;
        }
        bodyStr = getResultString(true, bodyStr);
        return JSON.parseObject(bodyStr, clazz);
    }

    /**
     * 带timeout参数的get方法
     * 返回类型为clazz类型的list
     */
    public static <T> List<T> getList(String url, long timeout, Class<T> clazz) {
        String bodyStr = getBody(url, timeout);
        if (bodyStr == null) {
            return Collections.emptyList();
        }
        return JSON.parseArray(bodyStr, clazz);
    }

    /**
     * 带timeout参数的get方法
     * 返回类型为clazz类型的list
     * 针对返回结果包装为Response的类型
     */
    public static <T> List<T> getListWithResponse(String url, long timeout, Class<T> clazz) {
        String bodyStr = getBody(url, timeout);
        if (bodyStr == null) {
            return Collections.emptyList();
        }
        bodyStr = getResultString(true, bodyStr);
        return JSON.parseArray(bodyStr, clazz);
    }

    /**
     * 文件下载
     */
    public static InputStream downFile(String url) {
        return downFile(url, 0);
    }

    /**
     * 文件下载
     */
    public static InputStream downFile(String url, long timeout) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .get();
        OkHttpClient client;
        if (timeout <= 0 || timeout > Integer.MAX_VALUE) {
            client = CLIENT;
        } else {
            client = customOkHttpClient(timeout);
        }
        try {
            Response response = client.newCall(builder.build()).execute();
            ResponseBody body = response.body();
            if (body == null) {
                return null;
            }
            return body.byteStream();
        } catch (IOException e) {
            throw new BusinessException("文件下载出错", e);
        }
    }

    public static void post(String url, String json, long timeout) {
        doPost(url, json, timeout, null, false);
    }

    public static <T> T post(String url, String json, long timeout, Class<T> clazz) {
        return doPost(url, json, timeout, clazz, false);
    }

    public static <T> T postWithResponse(String url, String json, long timeout, Class<T> clazz) {
        return doPost(url, json, timeout, clazz, true);
    }

    public static <T> List<T> postList(String url, String json, long timeout, Class<T> clazz) {
        return doPostList(url, json, timeout, clazz, false);
    }

    public static <T> List<T> postListWithResponse(String url, String json, long timeout, Class<T> clazz) {
        return doPostList(url, json, timeout, clazz, true);
    }

    public static void post(String url, Object jsonObj, long timeout) {
        String json = JSON.toJSONString(jsonObj);
        doPost(url, json, timeout, null, false);
    }

    public static <T> T post(String url, Object jsonObj, long timeout, Class<T> clazz) {
        String json = JSON.toJSONString(jsonObj);
        return doPost(url, json, timeout, clazz, false);
    }

    public static <T> T postWithResponse(String url, Object jsonObj, long timeout, Class<T> clazz) {
        String json = JSON.toJSONString(jsonObj);
        return doPost(url, json, timeout, clazz, true);
    }

    public static <T> List<T> postList(String url, Object jsonObj, long timeout, Class<T> clazz) {
        String json = JSON.toJSONString(jsonObj);
        return doPostList(url, json, timeout, clazz, false);
    }

    public static <T> List<T> postListWithResponse(String url, Object jsonObj, long timeout, Class<T> clazz) {
        String json = JSON.toJSONString(jsonObj);
        return doPostList(url, json, timeout, clazz, true);
    }

    public static String uploadFile(String url, String filename, byte[] content) {
        return uploadFile(url, filename, "file", 20000, content, false);
    }

    public static String uploadFile(String url, String filename, String formDataName, byte[] content) {
        return uploadFile(url, filename, formDataName, 20000, content, false);
    }

    public static String uploadFileWithResponse(String url, String filename, byte[] content) {
        return uploadFile(url, filename, "file", 20000, content, true);
    }

    public static String uploadFile(String url, String filename, String formDataName, long timeout, byte[] content, boolean withResponse) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(formDataName, filename,
                        RequestBody.create(MediaType.parse("multipart/form-data"), content))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        OkHttpClient client;
        if (timeout <= 0 || timeout > Integer.MAX_VALUE) {
            client = CLIENT;
        } else {
            client = customOkHttpClient(timeout);
        }
        try {
            Response response = client.newCall(request).execute();
            int code = response.code();
            if (code != SUCCESS_CODE) {
                throw new RuntimeException("http调用失败.code=" + code);
            }
            ResponseBody responseBody = response.body();
            String json = responseBody == null ? null : responseBody.string();
            return getResultString(withResponse, json);
        } catch (IOException e) {
            throw new BusinessException("文件上传出错", e);
        }
    }

    /**
     * doPost方法
     */
    private static <T> T doPost(String url, String json, long timeout, Class<T> clazz, boolean withResponse) {
        String responseJson = responseJson(url, json, timeout, withResponse);
        if (responseJson == null || clazz == null) {
            return null;
        }
        return JSON.parseObject(responseJson, clazz);
    }

    private static <T> T doPostType(String url, String json, long timeout, TypeReference<T> typeReference, boolean withResponse) {
        String responseJson = responseJson(url, json, timeout, withResponse);
        if (responseJson == null || typeReference == null) {
            return null;
        }
        return JSON.parseObject(responseJson, typeReference);
    }

    private static String responseJson(String url, String json, long timeout, boolean withResponse) {
        String responseJson = postBody(url, json, timeout);
        if (responseJson == null) {
            return null;
        }
        return getResultString(withResponse, responseJson);
    }

    /**
     * doPostList方法
     */
    private static <T> List<T> doPostList(String url, String json, long timeout, Class<T> clazz, boolean withResponse) {
        String responseJson = postBody(url, json, timeout);
        if (responseJson == null) {
            return Collections.emptyList();
        }
        responseJson = getResultString(withResponse, responseJson);
        return JSON.parseArray(responseJson, clazz);
    }

    /**
     * 根据返回结果是否包装为Response解析出实际Result
     */
    public static String getResultString(boolean withResponse, String responseJson) {
        if (withResponse) {
            // 先判断是否请求成功
            JSONObject bodyObj = JSON.parseObject(responseJson);
            String innerCode = bodyObj.getString("code");
            if (StringUtils.equals(innerCode, SUCCESS_CODE + "")
                    || StringUtils.equals(innerCode, INNER_SUCCESS_CODE)) {
                for (String key : RESPONSE_BODY_KEY) {
                    responseJson = bodyObj.getString(key);
                    if (StringUtils.isNotBlank(responseJson)) {
                        break;
                    }
                }
            } else {
                String message = null;
                for (String key : RESPONSE_MSG_KEY) {
                    message = bodyObj.getString(key);
                    if (StringUtils.isNotBlank(message)) {
                        break;
                    }
                }
                if (StringUtils.isBlank(message)) {
                    message = "未知错误";
                }
                throw new BusinessException("http请求调用失败,失败原因:" + message);
            }
        }
        return responseJson;
    }

    /**
     * okHttpClient get方法调用
     */
    private static String getBody(String url, long timeout) {
        return getBody(url, timeout, null);
    }

    /**
     * okHttpClient get方法调用
     */
    private static String getBody(String url, long timeout, Map<String, String> headers) {
        return exec(url, "GET", null, timeout, headers);
    }

    /**
     * okHttpClient post方法调用
     */
    private static String postBody(String url, String json, long timeout) {
        return postBody(url, json, timeout, null);
    }

    private static String postBody(String url, String json, long timeout, Map<String, String> headers) {
        return exec(url, "POST", json, timeout, headers);
    }

    private static String exec(String url, String method, String requestBody, long timeout, Map<String, String> headers) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .method(method, requestBody == null ? null : RequestBody.create(MEDIA_TYPE_JSON, requestBody));
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(builder::header);
        }
        Request request = builder.build();
        for (HttpUtilRequestProcessor httpUtilRequestProcessor : ServiceLoader.load(
                HttpUtilRequestProcessor.class)) {
            request = httpUtilRequestProcessor.process(request);
        }
        OkHttpClient client;
        if (timeout <= 0 || timeout > Integer.MAX_VALUE) {
            client = CLIENT;
        } else {
            client = customOkHttpClient(timeout);
        }
        try {
            Response response = client.newCall(request).execute();
            int code = response.code();
            ResponseBody responseBody = response.body();
            String responseBodyStr = responseBody == null ? null : responseBody.string();
            if (code != SUCCESS_CODE) {
                throw new RuntimeException("http调用失败.code=" + code + ",body=" + responseBodyStr);
            }
            return responseBodyStr;
        } catch (IOException e) {
            throw ExceptionUtil.unchecked(e);
        }
    }

    /**
     * Request增加处理
     */
    public interface HttpUtilRequestProcessor {
        Request process(Request request);
    }

}
