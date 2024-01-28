package com.witeam.service.common.http;

import org.apache.commons.codec.binary.Hex;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserCookie {

    private int userID;
    private String userName;
    private long creationTime;
    private String sign;


    public UserCookie(int userID, String userName) {
        this(userID, userName, 0, "");
    }

    public UserCookie(int userID, String userName, long creationTime, String sign) {
        this.userID = userID;
        this.userName = userName;
        if (creationTime <= 0) {
            this.creationTime = System.currentTimeMillis();
        } else {
            this.creationTime = creationTime;
        }

        this.sign = sign;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    /**
     * 利用盐进行签名，覆盖新值
     *
     * @param salt
     * @param reset 是否用新值重置sign值，如果salt等参数不对，将不会重置
     * @return
     */
    public String sign(String salt, boolean reset) {
        if (StringUtils.isEmpty(salt)) {
            return "";
        }

        String newSign = this.signParams(salt);
        if (StringUtils.isEmpty(newSign)) {
            return "";
        }

        if (reset) {
            this.sign = newSign;
        }

        return newSign;
    }

    /**
     * 使用给定的salt对已有参数进行签名运算
     * 签名结果与当前sign值比对，返回结果
     *
     * @param salt
     * @return
     */
    public boolean validate(String salt) {
        String newSign = this.signParams(salt);
        if (StringUtils.isEmpty(newSign)) {
            return false;
        }

        if (newSign.equals(this.sign)) {
            return true;
        }

        return false;
    }

    private String signParams(String salt) {
        if (StringUtils.isEmpty(salt)) {
            return null;
        }

        if (this.userID == 0 || StringUtils.isEmpty(this.userName)) {
            return null;
        }

        String forSignStr = String.format("%s%s%s%s", Long.toString(this.userID), this.userName, Long.toString(this.creationTime), salt);


        try {
            MessageDigest msgDigest = MessageDigest.getInstance("SHA-256");
            msgDigest.update(forSignStr.getBytes());
            byte[] byteBuffer = msgDigest.digest();
            return Hex.encodeHexString(byteBuffer);

        } catch (NoSuchAlgorithmException e ) {
            //除非代码写错了，都是hardcode的s
            return null;
        }
    }

    /**
     * 格式如下:
     * userid||username||timestamp||sign
     *
     * @return
     */
    public String toCookieString() {
        return String.format("%s||%s||%s||%s", Long.toString(this.userID), this.userName, Long.toString(this.creationTime), this.sign);
    }

    /**
     *
     * @param cookieString
     * @return
     */
    public static UserCookie parseCookieString(String cookieString) {
        if (StringUtils.isEmpty(cookieString)) {
            return null;
        }

        String[] params = cookieString.split("\\|\\|");
        if (params.length != 4) {
            //当前必须是4个
            return null;
        }

        try {
            int userID = Integer.parseInt(params[0]);
            long creationTime = Long.parseLong(params[2]);

            UserCookie userCookie = new UserCookie(userID, params[1], creationTime, params[3]);
            return userCookie;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}