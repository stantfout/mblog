package com.usth.mblog.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author wwb
 * @since 2020-06-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserCollection implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private Long postId;

    private Long postUserId;

    private Date created;

    private Date modified;


}
