package com.usth.mblog.shiro;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 整合登入用户的信息，给Shiro
 */
@Data
public class AccountProfile implements Serializable {

    private Long id;

    private String username;
    private String email;
    private String avatar;
    private String sign;

    private Date created;
}
