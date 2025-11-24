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

package leyramu.framework.lersosa.grpc.pulsar.processor;

import leyramu.framework.lersosa.common.core.utils.SpringUtils;
import leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass;
import leyramu.framework.lersosa.common.purge.annotation.NgxCacheCls;
import leyramu.framework.lersosa.grpc.pulsar.command.PulsarSaveCmdExe;
import leyramu.framework.lersosa.grpc.pulsar.handler.PulsarMatchHandler;
import leyramu.framework.lersosa.pulsar.api.domain.bo.RemotePulsarMatchBo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

/**
 * 保存已知匹配数据处理器.
 *
 * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
 * @version 1.0.0
 * @since 2025/3/18
 */
@Component
@RequiredArgsConstructor
public class SaveNewPulsarProcessor implements PulsarMatchHandler {

    /**
     * 脉冲星保存命令执行器.
     */
    private final PulsarSaveCmdExe pulsarSaveCmdExe;

    /**
     * 处理脉冲星匹配数据.
     *
     * @param pulsarMatcherRequest 脉冲星匹配数据
     * @return 是否处理成功
     */
    @Override
    public Boolean handle(PulsarOuterClass.PulsarMatcherRequest pulsarMatcherRequest) {
        return SpringUtils.getAopProxy(this).doHandle(pulsarMatcherRequest);
    }

    /**
     * 处理器.
     *
     * @param pulsarMatcherRequest 脉冲星匹配数据
     * @return 是否处理成功
     */
    @CacheEvict(
        cacheNames = "pulsar:recorder:list",
        allEntries = true
    )
    @NgxCacheCls
    public Boolean doHandle(PulsarOuterClass.PulsarMatcherRequest pulsarMatcherRequest) {
        RemotePulsarMatchBo remotePulsarMatchBo = new RemotePulsarMatchBo();
        BeanUtils.copyProperties(pulsarMatcherRequest, remotePulsarMatchBo);
        return pulsarSaveCmdExe.saveNewPulsarMatch(remotePulsarMatchBo);
    }
}
