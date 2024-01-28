package com.witeam.device.authenticate.spi.model;

import com.witeam.service.common.biz.GroupTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author gow 2024/01/24
 */
@Data
@Accessors(chain = true)
public class GroupDTO {
    private Date createTime;
    private Date modifiedTime;
    private Integer groupId;
    private Integer userId;
    private String groupName;
    private String groupKey;
    private String loginKey;
    private String groupToken;
    private GroupTypeEnum groupTypeEnum;
    private String description;
}
