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

package leyramu.framework.lersosa.pulsar.domain.recorder.model;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import leyramu.framework.lersosa.common.core.validate.AddGroup;
import leyramu.framework.lersosa.common.core.validate.EditGroup;
import leyramu.framework.lersosa.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 脉冲星记录业务对象.
 *
 * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
 * @version 1.0.0
 * @since 2025/1/18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = RecorderE.class, reverseConvertGenerate = false)
public class RecorderBo extends BaseEntity {

    /**
     * 主键.
     */
    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 租户 ID.
     */
    @NotBlank(message = "租户ID不能为空", groups = {AddGroup.class, EditGroup.class})
    private String tenantId;

    /**
     * 脉冲星名字.
     */
    @NotBlank(message = "脉冲星名字不能为空", groups = {AddGroup.class, EditGroup.class})
    private String name;

    /**
     * 脉冲星周期.
     */
    @NotBlank(message = "脉冲星周期不能为空", groups = {AddGroup.class, EditGroup.class})
    private String period;

    /**
     * 脉冲星色散.
     */
    @NotBlank(message = "脉冲星色散不能为空", groups = {AddGroup.class, EditGroup.class})
    private String dispersionMeasure;

    /**
     * 天空赤道坐标 - 赤经.
     */
    @NotBlank(message = "天空赤道坐标 - 赤经不能为空", groups = {AddGroup.class, EditGroup.class})
    private String raDeg;

    /**
     * 天空赤道坐标 - 赤纬.
     */
    @NotBlank(message = "天空赤道坐标 - 赤纬不能为空", groups = {AddGroup.class, EditGroup.class})
    private String decDeg;

    /**
     * 天空银道坐标 - 银经.
     */
    @NotBlank(message = "天空银道坐标 - 银经不能为空", groups = {AddGroup.class, EditGroup.class})
    private Double galacticLongitude;

    /**
     * 天空银道坐标 - 银纬.
     */
    @NotBlank(message = "天空银道坐标 - 银纬不能为空", groups = {AddGroup.class, EditGroup.class})
    private Double galacticLatitude;

    /**
     * 巡天项目名称.
     */
    @NotBlank(message = "巡天项目名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String survey;

    /**
     * 发现者.
     */
    @NotBlank(message = "发现者不能为空", groups = {AddGroup.class, EditGroup.class})
    private String discoverer;
}
