package com.bagel.noink.utils;
import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AliyunOSSManager {
    private Context context;
    private static final String TAG = "AliyunOSSImageUploader";
    private static final String ENDPOINT = "https://oss-cn-hangzhou.aliyuncs.com"; // 例如："https://your_bucket_name.oss-cn-beijing.aliyuncs.com"
    private static final String BUCKET_NAME = "cardigan1008";

    private OSS ossClient;

    public AliyunOSSManager(Context context) throws IOException {
        this.context = context.getApplicationContext();
        initOSSClient();
    }

    private void initOSSClient() throws IOException {
        Properties configuration = new Properties();

        InputStream inputStream = context.getAssets().open("config.properties");
        configuration.load(inputStream);

        ossClient = new OSSClient(this.context,
                ENDPOINT,
                new OSSPlainTextAKSKCredentialProvider(configuration.getProperty("ACCESS_KEY_ID"), configuration.getProperty("ACCESS_KEY_SECRET")));

        inputStream.close();
    }


    public String uploadImage(String localFilePath, String uploadImageName) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, uploadImageName, localFilePath);

        try {
            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
            Log.d(TAG, "Upload result: " + putObjectResult.getETag());

            // 如果上传成功，返回上传图片的URL
            return ossClient.presignPublicObjectURL(BUCKET_NAME, uploadImageName);

        } catch (ClientException e) {
            Log.e(TAG, "ClientException: " + e.getMessage());
            // 本地异常，例如网络异常等
            e.printStackTrace();
        } catch (ServiceException e) {
            // 服务异常
            Log.e(TAG, "ErrorCode: " + e.getErrorCode());
            Log.e(TAG, "RequestId: " + e.getRequestId());
            Log.e(TAG, "HostId: " + e.getHostId());
            Log.e(TAG, "RawMessage: " + e.getRawMessage());

        }

        return null; // 上传失败时返回 null
    }


    public void closeConnection() {
        //gc
        ossClient = null;
    }
}
