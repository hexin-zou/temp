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

package leyramu.framework.lersosa.pulsar.recorder.command;

import leyramu.framework.lersosa.common.core.utils.MapstructUtils;
import leyramu.framework.lersosa.pulsar.domain.recorder.model.RecorderBo;
import leyramu.framework.lersosa.pulsar.domain.recorder.model.RecorderE;
import leyramu.framework.lersosa.pulsar.mapper.recorder.gatewayimpl.database.RecorderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 新脉冲星保存命令执行器.
 *
 * @author <a href="mailto:3267745251@qq.com">Flipped-yuye</a>
 * @version 1.0.0
 * @since 2025/3/5
 */
@Component
@RequiredArgsConstructor
public class RecorderSaveCmdExe {

    /**
     * 脉冲星记录映射器.
     */
    private final RecorderMapper baseMapper;

    /**
     * 新增记录脉冲星.
     *
     * @param bo 记录脉冲星
     * @return 是否新增成功
     */
    public Boolean insertByBo(RecorderBo bo) {
        RecorderE add = MapstructUtils.convert(bo, RecorderE.class);
        validEntityBeforeSave(add);
        return baseMapper.insert(add) > 0;
    }

    /**
     * 保存前的数据校验.
     */
    private void validEntityBeforeSave(RecorderE entity) {
        //TODO 做一些数据校验,如唯一约束
    }
}
