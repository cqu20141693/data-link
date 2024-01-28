package com.witeam.service.common.page;


import com.witeam.service.common.util.ParamUtil;

public class PageOrderParam extends PageInfoParam {
    public final static String ORDER_ASC = "asc";
    public final static String ORDER_DESC = "desc";
    protected String order =ORDER_DESC;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        if (ParamUtil.isValidOrder(order)) {
            this.order = order;
        } else {
            this.order = ORDER_DESC;
        }
    }
}
