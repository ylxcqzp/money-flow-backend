package com.ghost.moneyflowbackend.model.dto;

import lombok.Data;

@Data
public class CategoryUpdateRequest {
    private String name;
    private String type;
    private String icon;
    private Long parentId;
    private Integer sortOrder;
}
