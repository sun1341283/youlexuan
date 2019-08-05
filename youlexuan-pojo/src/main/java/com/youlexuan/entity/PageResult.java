package com.youlexuan.entity;

import java.io.Serializable;
import java.util.List;

/**
 * easyUI的datagrid
 * 前台往后台：page-pageNum rows-PageSize
 * 后台往前台：total\list
* */
public class PageResult implements Serializable {

    //总条数
    private long total;

    //返回的分页结果list
    private List rows;

    public PageResult() {
    }

    public PageResult(long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
