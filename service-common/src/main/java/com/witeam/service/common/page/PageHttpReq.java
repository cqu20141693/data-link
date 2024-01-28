package com.witeam.service.common.page;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PageHttpReq {
    protected int page = 1;
    protected int pageSize = 10;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @JsonIgnore
    public int getOffset() {
        return (this.getPage() - 1) * this.getPageSize();
    }

}
