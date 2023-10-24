package com.hry.gloryapi.backend.common;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 自定义分页结果对象
 *
 * @author: huangry
 * @create: 2023/10/18
 **/
@Getter
@ToString
public class PageResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 查询数据列表
     */
    private List<T> records = Collections.emptyList();

    /**
     * 总数
     */
    private long total = 0;

    /**
     * 每页显示条数，默认 10
     */
    private long size = 10;

    /**
     * 当前页
     */
    private long current = 1;

    public PageResponse<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    public PageResponse<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    public PageResponse<T> setSize(long size) {
        this.size = size;
        return this;
    }

    public PageResponse<T> setCurrent(long current) {
        this.current = current;
        return this;
    }
}
