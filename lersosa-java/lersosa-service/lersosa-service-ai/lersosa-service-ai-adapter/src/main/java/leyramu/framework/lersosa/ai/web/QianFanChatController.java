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

package leyramu.framework.lersosa.ai.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * 千帆模型控制层.
 *
 * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
 * @version 1.0.0
 * @since 2024/6/23
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/qianfan")
@ConditionalOnProperty(name = "spring.ai.qianfan.enable", havingValue = "true")
public class QianFanChatController {

    /**
     * 聊天客户端.
     */
    @Qualifier("qianFanChatClient")
    private final ChatClient chatClient;

    /**
     * 向量存储.
     */
    @Qualifier("qianFanVectorStore")
    private final VectorStore vectorStore;

    /**
     * 生成文本流.
     *
     * @param message 消息
     * @return 生成文本流
     * @apiNote 生成文本流
     */
    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> textChat(@RequestParam(value = "message", defaultValue = "讲个笑话") String message) {
        return Flux.defer(() -> chatClient.prompt()
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                //.advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .advisors(a -> a.param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .user(message)
                .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().query(message).build()))
                .stream()
                .content()
                .concatWith(Flux.just("[complete]")))
            .subscribeOn(Schedulers.boundedElastic())
            .onErrorResume(ex -> switch (ex) {
                case TimeoutException _ -> {
                    log.warn("请求超时", ex);
                    yield Flux.just("[error] 请求超时，请重试。");
                }
                case RuntimeException _ -> {
                    log.error("运行时异常", ex);
                    yield Flux.just("[error] 系统异常，请联系管理员。");
                }
                default -> {
                    log.error("未知异常", ex);
                    yield Flux.just("[error] 发生了未知错误。");
                }
            });
    }
}
