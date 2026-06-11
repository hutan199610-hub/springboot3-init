package com.example.common.base;

import lombok.Data;

@Data
public class PageQuery {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
