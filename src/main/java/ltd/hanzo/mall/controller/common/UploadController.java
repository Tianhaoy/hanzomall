package ltd.hanzo.mall.controller.common;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import ltd.hanzo.mall.common.Constants;
import ltd.hanzo.mall.util.Result;
import ltd.hanzo.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author 皓宇QAQ
 * @qq交流群 951485783
 * @email 2469653218@qq.com
 * @link https://github.com/Tianhaoy/hanzomall
 */
@Slf4j
@Controller
@RequestMapping("/admin")
public class UploadController {

    @PostMapping({"/upload/file"})
    @ResponseBody
    public Result upload(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile file) throws URISyntaxException {
        try {
            String fileName = file.getOriginalFilename();
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            //生成文件名称通用方法
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Random r = new Random();
            StringBuilder tempName = new StringBuilder();
            tempName.append(sdf.format(new Date())).append(r.nextInt(100)).append(suffixName);
            String newFileName = tempName.toString();
            String addressData = this.ossClient(newFileName,file);
            Result resultSuccess = ResultGenerator.genSuccessResult();
            resultSuccess.setData(addressData);
            log.debug("图片访问Url："+resultSuccess.getData().toString());
            //双保险 将图片保存在oss的同时也在服务器中保存照片 但是数据库中的链接是oss的
            File fileDirectory = new File(Constants.FILE_UPLOAD_DIC);
            //创建文件
            File destFile = new File(Constants.FILE_UPLOAD_DIC + newFileName);
            if (!fileDirectory.exists()) {
                if (!fileDirectory.mkdir()) {
                    throw new IOException("文件夹创建失败,路径为：" + fileDirectory);
                }
            }
            file.transferTo(destFile);
            log.debug("在服务器路径 "+Constants.FILE_UPLOAD_DIC+"同步保存图片--");
            //服务器中保存照片结束
            return resultSuccess;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult("文件上传失败");
        }
    }

    private String ossClient(String newFileName, MultipartFile file) {
        String addressData = "";
        // 创建OSSClient实例
        OSS client = new OSSClientBuilder().build(Constants.END_POINT,Constants.ACCESS_KEY_ID ,Constants.ACCESS_KEY_SECRET );
        try {
            PutObjectResult result =client.putObject(Constants.BUCKET_NAME,Constants.FILEDIR+newFileName, new ByteArrayInputStream(file.getBytes()));
//          OSSObject object = client.getObject(Constants.BUCKET_NAME, newFileName);
//          object.getObjectContent().close();
            client.shutdown();
            if (null != result) {
                // 获取上传后的图片链接
                // 后端将地址拼接一下，oss那里设为了公共读，阿里云oss屁事太多了
                addressData= Constants.IMAGES_ADDRESS + "/" + Constants.FILEDIR+ newFileName;
            }
        } catch (IOException e) {
            e.printStackTrace();
            addressData = "";
        }
        return addressData;
    }
}

//    File fileDirectory = new File(Constants.FILE_UPLOAD_DIC);
//    //创建文件
//    File destFile = new File(Constants.FILE_UPLOAD_DIC + newFileName);
//        try {
//        if (!fileDirectory.exists()) {
//            if (!fileDirectory.mkdir()) {
//                throw new IOException("文件夹创建失败,路径为：" + fileDirectory);
//            }
//        }
//        file.transferTo(destFile);
//        Result resultSuccess = ResultGenerator.genSuccessResult();
//        resultSuccess.setData(HanZoMallUtils.getHost(new URI(httpServletRequest.getRequestURL() + "")) + "/upload/" + newFileName);
//        log.debug(resultSuccess.getData().toString());
//        return resultSuccess;
//    } catch (IOException e) {
//        e.printStackTrace();
//        return ResultGenerator.genFailResult("文件上传失败");
//    }

