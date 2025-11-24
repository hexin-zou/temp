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

package leyramu.framework.lersosa.grpc.pulsar.chain;

import leyramu.framework.lersosa.common.tenant.helper.TenantHelper;
import leyramu.framework.lersosa.grpc.pulsar.handler.PulsarHandlerChain;
import leyramu.framework.lersosa.grpc.pulsar.processor.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Stream;

/**
 * 匹配链配置.
 *
 * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
 * @version 1.0.0
 * @since 2025/3/18
 */
@Configuration
public class PulsarMatchChainConfig {

    /**
     * 匹配链.
     *
     * @param knownCheckProcessor      存在已知脉冲星检查处理器
     * @param newCheckProcessor        存在新脉冲星检查处理器
     * @param saveKnownPulsarProcessor 保存已知处理器
     * @param saveNewPulsarProcessor   保存新处理器
     * @param modifyPulsarProcessor    修改处理器
     * @return {@link PulsarHandlerChain}
     */
    @Bean
    public PulsarHandlerChain pulsarMatchChain(
        KnownCheckProcessor knownCheckProcessor,
        NewCheckProcessor newCheckProcessor,
        SaveKnownPulsarProcessor saveKnownPulsarProcessor,
        SaveNewPulsarProcessor saveNewPulsarProcessor,
        ModifyPulsarProcessor modifyPulsarProcessor
    ) {
        return bo -> {
            TenantHelper.setDynamic(bo.getTenantId());
            if (!"notfound".equals(bo.getName())) {
                return Stream.of(
                    modifyPulsarProcessor,
                    knownCheckProcessor,
                    saveKnownPulsarProcessor
                ).allMatch(handler -> handler.handle(bo));
            } else {
                return Stream.of(
                    modifyPulsarProcessor,
                    newCheckProcessor,
                    saveNewPulsarProcessor
                ).allMatch(handler -> handler.handle(bo));
            }
        };
    }
}
