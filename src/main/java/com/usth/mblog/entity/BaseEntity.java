package com.usth.mblog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Date created;
    private Date modified;

}
