/*
 * Copyright (c) 2025 Leyramu Group. All rights reserved.
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

package leyramu.framework.lersosa.common.ai.handler;

import leyramu.framework.lersosa.common.ai.strategy.ResourceProcessingStrategy;
import leyramu.framework.lersosa.common.ai.strategy.ResourceProcessingStrategyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * 处理资源.
 *
 * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
 * @version 1.0.0
 * @since 2025/5/27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessResourceHandler {

    /**
     * 资源处理策略管理器.
     */
    private final ResourceProcessingStrategyManager strategyManager;

    /**
     * 处理资源.
     *
     * @param resource    资源
     * @param vectorStore 向量存储
     */
    public void processResource(Resource resource, VectorStore vectorStore) {
        String filename = resource.getFilename();
        if (filename == null) {
            log.warn("无法识别文件名");
            return;
        }

        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

        try {
            ResourceProcessingStrategy strategy = strategyManager.getStrategy(extension);
            strategy.process(resource, vectorStore);
        } catch (Exception e) {
            log.warn("处理文件 {} 时出错", filename, e);
        }
    }
}
