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

package leyramu.framework.lersosa.pulsar.api.domain.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 脉冲星匹配数据远程调用对象【BO】.
 *
 * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
 * @version 1.0.0
 * @since 2025/3/4
 */
@Data
@NoArgsConstructor
public class RemotePulsarMatchBo implements Serializable {

    /**
     * 序列化.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 租户 ID.
     */
    private String tenantId;

    /**
     * 脉冲星名字.
     */
    private String name;

    /**
     * 脉冲星周期.
     */
    private String period;

    /**
     * 脉冲星色散.
     */
    private String dispersionMeasure;

    /**
     * 天空赤道坐标 - 赤经.
     */
    private String raDeg;

    /**
     * 天空赤道坐标 - 赤纬.
     */
    private String decDeg;

    /**
     * 天空银道坐标 - 银经.
     */
    private Double galacticLongitude;

    /**
     * 天空银道坐标 - 银纬.
     */
    private Double galacticLatitude;

    /**
     * 发现者.
     */
    private String discoverer;
}
