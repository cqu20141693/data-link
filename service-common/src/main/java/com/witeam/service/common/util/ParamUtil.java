package com.witeam.service.common.util;

import org.springframework.util.StringUtils;

import static com.witeam.service.common.page.PageOrderParam.ORDER_ASC;
import static com.witeam.service.common.page.PageOrderParam.ORDER_DESC;

public class ParamUtil {
    public static boolean isValidOrder(String order) {
        if (StringUtils.isEmpty(order)) {
            return false;
        }

        if (order.equals(ORDER_DESC) || order.equals(ORDER_ASC)) {
            return true;
        }

        return false;
    }
}
