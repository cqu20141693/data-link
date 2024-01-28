package com.witeam.service.common.util.http;

import com.witeam.service.common.call.CommonCodeType;
import com.witeam.service.common.exception.HttpException;
import com.witeam.service.common.page.PageHttpReq;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HttpUtil {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void formatPageHttpReq(PageHttpReq pageHttpReq) {
        if (pageHttpReq.getPage() < 1) {
            pageHttpReq.setPage(1);
        }

        if (pageHttpReq.getPageSize() < 1) {
            pageHttpReq.setPageSize(1);
        }
    }

    public static Long dateStrToTimestamp(String dateStr) {
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }

        try {
            Date res = dateFormat.parse(dateStr);
            return res.getTime();
        } catch (ParseException e) {
            throw new HttpException(CommonCodeType.PARAM_CHECK_ERROR.getCode(), "时间参数格式错误");
        }
    }

    public static Long dateTimeStrToTimestamp(String dateTimeStr) {
        if (StringUtils.isEmpty(dateTimeStr)) {
            return null;
        }

        try {
            Date res = dateTimeFormat.parse(dateTimeStr);
            return res.getTime();
        } catch (ParseException e) {
            throw new HttpException(CommonCodeType.PARAM_CHECK_ERROR.getCode(), "时间参数格式错误");
        }
    }
}
