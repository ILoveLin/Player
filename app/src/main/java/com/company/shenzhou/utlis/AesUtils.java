package com.company.shenzhou.utlis;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.EncryptUtils;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2024/5/29 15:33
 * desc：AES 加密解密
 */
public class AesUtils {

    private static final String SHA1PRNG = "SHA1PRNG";   // SHA1PRNG 强随机种子算法
    private static final String AES = "AES";   //AES 加密
    private static final String AES_KEY = "664c0fce6df2da5f";
    private static final String CIPHERMODE = "AES/ECB/ZeroBytePadding"; //AES算法/CBC模式/ZeroBytePadding填充模式


    /**
     * AES-解密
     */
    public static String encrypt(String base64String) {

        //解密：结果字符串->base64解码->AES->明文
        //base64解码
        byte[] bytes = EncodeUtils.base64Decode(base64String);
        //AES解密->明文
        byte[] ecbs = EncryptUtils.decryptAES(bytes, AES_KEY.getBytes(), CIPHERMODE, new byte[0]);
        if (null== ConvertUtils.bytes2String(ecbs)){
            return "";
        }
        return ConvertUtils.bytes2String(ecbs);

    }

}
