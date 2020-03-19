package ltd.hanzo.mall.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.service.SmsService;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author 皓宇QAQ
 * @qq交流群 951485783
 * @email 2469653218@qq.com
 * @link https://github.com/Tianhaoy
 * 2020年3月17日17:18:22
 * @发送短信接口实现
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Value("${sms.accessKeyId}")
    private String accessKeyId;

    @Value("${sms.accessKeySecret}")
    private String accessKeySecret;

    @Value("${sms.product}")
    private String product;

    @Value("${sms.domain}")
    private String domain;

    @Value("${sms.signName}")
    private String signName;

    @Value("${sms.templateCode}")
    private String templateCode;

    @Override
    public boolean sendSms(String phoneNumber, String randomCode) {
        //设置超时时间-可自行调整
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4FkKQn3SP7nnqmuL24ds", "Bd8MWzhAu2RgSsPlHxJsIRJE7GmmPN");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(domain);
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        request.putQueryParameter("SignName", "半藏商城");//直接去signName就会提示签名不合法 这样写就可以发送短信
        request.putQueryParameter("TemplateCode", templateCode);
        JSONObject object = new JSONObject();
        try {
            object.put("code",randomCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String templateParam =object.toString();
        request.putQueryParameter("TemplateParam", templateParam);
        try {
            //请求失败这里会抛ClientException异常
            CommonResponse response = client.getCommonResponse(request);
            log.info("阿里云短信服务返回消息:"+response.getData());
            // 使用alibaba的fastjson
            Map<String, Object> map=  JSON.parseObject(response.getData());
            if (("OK").equals(map.get("Code"))){
                return true;
            }else{
                return  false;
            }
        } catch (ClientException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return false;
    }
}
