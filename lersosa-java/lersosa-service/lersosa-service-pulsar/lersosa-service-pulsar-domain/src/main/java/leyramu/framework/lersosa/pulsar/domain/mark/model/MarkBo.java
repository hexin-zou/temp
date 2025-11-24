/*
 * Copyright (c) 2023-2025 Leyramu Group. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This project (Lersosa), including its source code, documentation, and any associated materials, is the intellectual property of Leyramu. No part of this software may be reproduced, distributed, or transmitted in any form or by any means, including photocopying, recording, or other electronic or mechanical methods, without the prior written permission of the copyright owner, Miraitowa_zcx, except in the case of brief quotations embodied in critical reviews and certain other noncommercial uses permitted by copyright law.
 *
 * For inquiries related to licensing or usage outside the scope of this notice, please contact the copyright holder at 2038322151@qq.com.
 *
 * The author disclaims all warranties, express or implied, including but not limited to the warranties of merchantability and fitness for a particular purpose. Under no circumstances shall the author be liable for any special, incidental, indirect, or consequential damages arising from the use of this software.
 *
 * By using this project, users acknowledge and agree to abide by these terms and conditions.
 */

package leyramu.framework.lersosa.pulsar.domain.mark.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import leyramu.framework.lersosa.common.core.validate.AddGroup;
import leyramu.framework.lersosa.common.core.validate.EditGroup;
import leyramu.framework.lersosa.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * AI 打分业务对象.
 *
 * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
 * @version 1.0.0
 * @since 2025/1/16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MarkE.class, reverseConvertGenerate = false)
public class MarkBo extends BaseEntity {

    /**
     * 主键.
     */
    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 租户 ID.
     */
    private String tenantId;

    /**
     * 文件名.
     */
    @NotBlank(message = "文件名不能为空", groups = {AddGroup.class, EditGroup.class})
    private String fileName;

    /**
     * 文件地址.
     */
    @NotBlank(message = "文件地址不能为空", groups = {AddGroup.class, EditGroup.class})
    private String fileUrl;

    /**
     * 打分平方和.
     */
    @NotNull(message = "打分平方和不能为空", groups = {AddGroup.class, EditGroup.class})
    private Double score;

    /**
     * 标记.
     */
    @NotNull(message = "标记不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer flag;

    /**
     * 标记人.
     */
    @NotBlank(message = "标记人不能为空", groups = {AddGroup.class, EditGroup.class})
    private String flagUser;

    /**
     * 更正标记人.
     */
    @NotBlank(message = "更正标记人不能为空", groups = {AddGroup.class, EditGroup.class})
    private String reFlagUser;

    /**
     * 查询用 - 标记时间（仅日期）.
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate queryFlagDate;

    /**
     * 查询用 - 更正标记时间（仅日期）.
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate queryReFlagDate;

    /**
     * 标记时间.
     */
    @NotNull(message = "标记时间不能为空", groups = {AddGroup.class, EditGroup.class})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime flagDate;

    /**
     * 更正标记时间.
     */
    @NotNull(message = "更正标记时间不能为空", groups = {AddGroup.class, EditGroup.class})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reFlagDate;

    /**
     * 审核状态 0-未审核 1-已审核，进入检查状态.
     */
    @NotNull(message = "审核状态不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer check;

    /**
     * 读写锁.
     */
    @NotNull(message = "读写锁不能为空", groups = {AddGroup.class, EditGroup.class})
    private Integer locker;
}
