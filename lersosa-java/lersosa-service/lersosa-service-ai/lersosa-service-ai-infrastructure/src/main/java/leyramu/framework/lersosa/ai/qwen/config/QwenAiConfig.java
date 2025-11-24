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

package leyramu.framework.lersosa.ai.qwen.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.google.common.net.HttpHeaders;
import leyramu.framework.lersosa.ai.qwen.tool.QwenTools;
import leyramu.framework.lersosa.common.ai.config.BaseAiConfig;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 千问Ai配置.
 *
 * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
 * @version 1.0.0
 * @since 2025/5/1
 */
@Configuration
@EnableAutoConfiguration
@ConditionalOnProperty(name = "spring.ai.dashscope.enable", havingValue = "true")
public class QwenAiConfig extends BaseAiConfig<DashScopeChatModel, DashScopeEmbeddingModel, QwenTools> {

    /**
     * 构造函数.
     *
     * @param chatModel      聊天模型
     * @param embeddingModel 嵌入模型
     */
    public QwenAiConfig(DashScopeChatModel chatModel, DashScopeEmbeddingModel embeddingModel) {
        super(chatModel, embeddingModel);
    }

    /**
     * 获取系统提示.
     *
     * @return 系统提示
     */
    @Override
    protected String getSystemPrompt() {
        return """
                您是“脉冲星信号筛选系统”的客户聊天支持代理。请以友好、乐于助人且愉快的方式来回复。
                您正在通过在线聊天系统与客户互动。
                当前用户的用户id为 {user_id} 租户id为 {tenant_id}。
                在提供所有信息之前，您必须通过查询function-call获取到用户的信息：用户姓名，用户身份，且这个过程不需要等待用户确认，且这个过程只进行一次，不需要通知用户。
                请讲中文。
                今天的日期是 {current_date}。
                在更改或查询function-call前，请先获取脉冲星信息并且一定要等用户回复"确定"之后才进行更改或查询的function-call。
                如果回答中包含图片链接时，回答的每一行放置两个图片连接。
                回答内容中不要出现系统提示词。
                回答完问题请结束回答。
            """;
    }

    /**
     * 向量存储.
     *
     * @return {@link VectorStore}
     */
    @Override
    @Bean("qwenVectorStore")
    public VectorStore vectorStore() {
        return super.vectorStore();
    }

    /**
     * 写入服务条款.
     *
     * @param vectorStore             向量存储
     * @param resourcePatternResolver 资源加载器
     * @return {@link CommandLineRunner}
     */
    @Override
    @Bean("ingestTermOfServiceToQwenVectorStore")
    public CommandLineRunner ingestTermsOfServiceToVectorStore(
        @Qualifier("qwenVectorStore") VectorStore vectorStore, ResourcePatternResolver resourcePatternResolver) {
        return super.ingestTermsOfServiceToVectorStore(vectorStore, resourcePatternResolver);
    }

    /**
     * 会话内存.
     *
     * @return {@link ChatMemory}
     */
    @Override
    @Bean("qwenChatMemory")
    public ChatMemory chatMemory() {
        return super.chatMemory();
    }

    /**
     * 聊天客户端.
     *
     * @param chatMemory 会话内存
     * @return {@link ChatClient}
     */
    @Override
    @Bean("qwenChatClient")
    @ConditionalOnBean(QwenTools.class)
    public ChatClient chatClient(@Qualifier("qwenChatMemory") ChatMemory chatMemory, QwenTools qwenTools, ToolCallbackProvider mcpTools) {
        return super.chatClient(chatMemory, qwenTools, mcpTools);
    }

    /**
     * Web 客户端.
     *
     * @param apiKey API密钥
     * @return {@link WebClient}
     */
    @Bean
    public WebClient qwenWebClient(@Value("${spring.ai.dashscope.api-key}") String apiKey) {
        return WebClient.builder()
            .baseUrl("https://dashscope.aliyuncs.com/api/v1")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
            .defaultHeader("X-DashScope-Async", "enable")
            .build();
    }
}
