package top.chriszwz.community;

import com.google.gson.Gson;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.tbp.v20190627.TbpClient;
import com.tencentcloudapi.tbp.v20190627.models.*;

import java.util.ArrayList;
import java.util.Arrays;

public class TextProcess
{
    public static void main(String [] args) {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential("AKID85nGVVGrFNGtHPIvIQJwKTN1QZS6UcU7", "PZT8YjfMo0we7LJ1ZsxnNindEagW6RWL");
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("tbp.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            TbpClient client = new TbpClient(cred, "", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            TextProcessRequest req = new TextProcessRequest();
            req.setBotId("e9e2b747-a306-45be-876c-7f789fe54395");
            req.setBotEnv("release");
            req.setTerminalId("123");
            req.setInputText("你是谁？");
            // 返回的resp是一个TextProcessResponse的实例，与请求对象对应
            TextProcessResponse resp = client.TextProcess(req);
            // 输出json格式的字符串回包
            String json = new Gson().toJson(resp.getResponseMessage().getGroupList()[0]);
            msg msg = new Gson().fromJson(json, msg.class);
            System.out.println(msg.getContent());
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }
}
