package com.ghost.moneyflowbackend.model.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryVO {
    private Long id;
    private String name;
    private String type;
    private String icon;
    private Long parentId;
    private Integer sortOrder;
    private List<CategoryVO> children = new ArrayList<>();
}
