package ltd.hanzo.mall.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author 皓宇QAQ
 * @email 2469653218@qq.com
 * @link https://github.com/Tianhaoy/hanzomall
 * @http请求公共方法
 */
@Slf4j
public class HttpRequestUtil {
    //	接口调用方法举例：
    public  String getInfoFromServer4ResWait(String targetURL,String params,String way) {
        HttpURLConnection httpConnection = null;
        String output = "";
        try {
            log.debug("发送的参数为:"+params);
            if("GET".equals(way)){
                targetURL += params.toString();
            }
            log.debug("发送的路径为:" + targetURL);
            URL restServiceURL = new URL(targetURL);
            httpConnection = (HttpURLConnection) restServiceURL.openConnection();
            httpConnection.setReadTimeout(120 * 1000);//30秒超时
            httpConnection.setRequestMethod(way);
            httpConnection.setDoOutput(true); // 设置是否输出
            httpConnection.setDoInput(true);// 设置是否读入
            httpConnection.setUseCaches(false);// 设置是否使用缓存
            httpConnection.setInstanceFollowRedirects(true); // 设置此 HttpURLConnection 实例是否应该自动执行 HTTP 重定向
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setRequestProperty("Accept-Charset", "UTF-8");
            httpConnection.connect(); // 连接
            if("POST".equals(way)){
                /* 4. 处理输入输出 */
                OutputStream out = httpConnection.getOutputStream();
                out.write(params.getBytes("UTF-8"));// 写入参数到请求中
                out.flush();
                out.close();
            }
            //设置HTTP返回代码
            log.debug("======>返回 code:" + httpConnection.getResponseCode());
            if (httpConnection.getResponseCode() == 200) {
                BufferedReader responseBuffer =
                        new BufferedReader(new InputStreamReader((httpConnection.getInputStream()), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                while ((output = responseBuffer.readLine()) != null) {
                    sb.append(output);
                }
                output = sb.toString();
                log.debug("调用结束，返回"+output.toString());
            } else {
                JSONObject outJson = new JSONObject();
                outJson.put("ResultCode","1");
                outJson.put("ResultRemarks","接口调用失败");
                output = outJson.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpConnection.disconnect();
        }
        log.debug("返回结果:" +output);
        return output;
    }
}
