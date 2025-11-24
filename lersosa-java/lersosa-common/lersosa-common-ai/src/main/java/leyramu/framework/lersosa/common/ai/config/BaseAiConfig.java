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

package leyramu.framework.lersosa.common.ai.config;

import leyramu.framework.lersosa.common.ai.advisor.LoggingAdvisor;
import leyramu.framework.lersosa.common.ai.handler.ProcessResourceHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Ai模型基本配置.
 *
 * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
 * @version 1.0.0
 * @since 2025/5/1
 */
@Slf4j
@RequiredArgsConstructor
@ComponentScan(basePackages = "leyramu.framework.lersosa.common.ai")
public abstract class BaseAiConfig<T extends ChatModel, E extends EmbeddingModel, TOOL> {

    /**
     * 聊天模型.
     */
    protected final T chatModel;

    /**
     * 嵌入模型.
     */
    protected final E embeddingModel;

    /**
     * 工具.
     */
    protected List<Object> tools;

    /**
     * 处理资源处理器.
     */
    @Autowired
    private ProcessResourceHandler processResourceHandler;

    /**
     * 获取系统提示.
     *
     * @return 系统提示
     */
    protected abstract String getSystemPrompt();

    /**
     * 获取服务条款文件路径.
     *
     * @return 服务条款
     */
    protected String getTermsOfServicePath() {
        return "classpath:rag/*.*";
    }

    /**
     * 获取默认 Advisors.
     *
     * @param chatMemory 会话内存
     * @return {@link List<StreamAroundAdvisor>}
     */
    protected List<StreamAroundAdvisor> getDefaultAdvisors(ChatMemory chatMemory) {
        return List.of(
            new PromptChatMemoryAdvisor(chatMemory),
            new LoggingAdvisor()
        );
    }

    /**
     * 设置默认工具.
     *
     * @param tool     工具
     * @param mcpTools mcp工具
     */
    protected void setDefaultTools(TOOL tool, ToolCallbackProvider mcpTools) {
        this.tools = List.of(
            tool,
            mcpTools
        );
    }

    /**
     * 向量存储.
     *
     * @return {@link VectorStore}
     */
    public VectorStore vectorStore() {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    /**
     * 写入服务条款.
     *
     * @param vectorStore             向量存储
     * @param resourcePatternResolver 资源加载器
     * @return {@link CommandLineRunner}
     */
    public CommandLineRunner ingestTermsOfServiceToVectorStore(VectorStore vectorStore, ResourcePatternResolver resourcePatternResolver) {
        return _ -> Optional.of(resourcePatternResolver.getResources(getTermsOfServicePath()))
            .filter(arr -> arr.length > 0)
            .map(Arrays::stream)
            .ifPresentOrElse(
                stream -> stream.forEach(resource -> processResourceHandler.processResource(resource, vectorStore)),
                () -> log.warn("在路径{}中找不到服务条款文件", getTermsOfServicePath())
            );
    }

    /**
     * 会话内存.
     *
     * @return {@link ChatMemory}
     */
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    /**
     * 聊天客户端.
     *
     * @param chatMemory 会话内存
     * @return {@link ChatClient}
     */
    public ChatClient chatClient(ChatMemory chatMemory, TOOL tool, ToolCallbackProvider mcpTools) {
        ChatClient.Builder builder = ChatClient.builder(chatModel);

        // 设置系统提示词
        builder.defaultSystem(s -> s.text(getSystemPrompt()).param("current_date", LocalDate.now().toString()));

        // 添加默认 advisors
        getDefaultAdvisors(chatMemory).forEach(builder::defaultAdvisors);

        // 添加默认工具
        setDefaultTools(tool, mcpTools);
        tools.forEach(builder::defaultTools);

        // 构建 ChatClient
        return builder.build();
    }
}
