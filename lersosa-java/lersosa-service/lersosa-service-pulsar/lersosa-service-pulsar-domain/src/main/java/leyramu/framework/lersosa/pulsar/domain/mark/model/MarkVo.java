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

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 打分视图对象.
 *
 * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
 * @version 1.0.0
 * @since 2025/1/16
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MarkE.class)
public class MarkVo implements Serializable {

    /**
     * 序列化.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键.
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 租户 ID.
     */
    private String tenantId;

    /**
     * 图片名称.
     */
    @ExcelProperty(value = "图片名称")
    private String fileName;

    /**
     * 文件地址.
     */
    @ExcelProperty(value = "文件地址")
    private String fileUrl;

    /**
     * 打分平方和.
     */
    @ExcelProperty(value = "打分平方和")
    private Double score;

    /**
     * 标记.
     */
    @ExcelProperty(value = "标记")
    private Integer flag;

    /**
     * 标记人.
     */
    @ExcelProperty(value = "标记人")
    private String flagUser;

    /**
     * 更正标记人.
     */
    @ExcelProperty(value = "更正标记人")
    private String reFlagUser;

    /**
     * 标记时间.
     */
    @ExcelProperty(value = "标记时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime flagDate;

    /**
     * 更正标记时间.
     */
    @ExcelProperty(value = "更正标记时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reFlagDate;

    /**
     * 审核状态.
     */
    @ExcelProperty(value = "审核状态")
    private Integer check;

    /**
     * 读写锁.
     */
    @ExcelProperty(value = "读写锁")
    private Integer locker;
}
