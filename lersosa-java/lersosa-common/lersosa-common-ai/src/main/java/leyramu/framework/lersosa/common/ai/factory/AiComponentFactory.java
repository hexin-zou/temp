///*
// * Copyright (c) 2025 Leyramu Group. All rights reserved.
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *
// * This project (Lersosa), including its source code, documentation, and any associated materials, is the intellectual property of Leyramu. No part of this software may be reproduced, distributed, or transmitted in any form or by any means, including photocopying, recording, or other electronic or mechanical methods, without the prior written permission of the copyright owner, Miraitowa_zcx, except in the case of brief quotations embodied in critical reviews and certain other noncommercial uses permitted by copyright law.
// *
// * For inquiries related to licensing or usage outside the scope of this notice, please contact the copyright holder at 2038322151@qq.com.
// *
// * The author disclaims all warranties, express or implied, including but not limited to the warranties of merchantability and fitness for a particular purpose. Under no circumstances shall the author be liable for any special, incidental, indirect, or consequential damages arising from the use of this software.
// *
// * By using this project, users acknowledge and agree to abide by these terms and conditions.
// */
//
//package leyramu.framework.lersosa.common.ai.factory;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.memory.ChatMemory;
//import org.springframework.ai.chat.memory.InMemoryChatMemory;
//import org.springframework.ai.chat.model.ChatModel;
//import org.springframework.ai.embedding.EmbeddingModel;
//import org.springframework.ai.reader.TextReader;
//import org.springframework.ai.transformer.splitter.TokenTextSplitter;
//import org.springframework.ai.vectorstore.SimpleVectorStore;
//import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.ResourceLoader;
//
//import java.time.LocalDate;
//
///**
// * Ai 组件工厂.
// *
// * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
// * @version 1.0.0
// * @since 2025/5/8
// */
//@Slf4j
//@RequiredArgsConstructor
//public final class AiComponentFactory<T extends ChatModel, E extends EmbeddingModel> {
//
//    /**
//     * 聊天模型.
//     */
//    private final T chatModel;
//
//    /**
//     * 嵌入模型.
//     */
//    private final E embeddingModel;
//
//    /**
//     * 获取服务条款文件路径.
//     *
//     * @return 服务条款
//     */
//    public String getTermsOfServicePath() {
//        return "classpath:rag/terms-of-service.txt";
//    }
//
//    /**
//     * 向量存储.
//     *
//     * @return {@link VectorStore}
//     */
//    public VectorStore vectorStore() {
//        return SimpleVectorStore.builder(embeddingModel).build();
//    }
//
//    /**
//     * 写入服务条款.
//     *
//     * @param vectorStore    向量存储
//     * @param resourceLoader 资源加载器
//     * @return {@link CommandLineRunner}
//     */
//    public CommandLineRunner ingestTermsOfServiceToVectorStore(
//        VectorStore vectorStore,
//        ResourceLoader resourceLoader) {
//        return _ -> {
//            String termsOfServicePath = getTermsOfServicePath();
//            Resource resource = resourceLoader.getResource(termsOfServicePath);
//            if (!resource.exists()) {
//                log.warn("在路径{}中找不到服务条款文件", termsOfServicePath);
//                return;
//            }
//            vectorStore.write(
//                new TokenTextSplitter().transform(new TextReader(resource).read())
//            );
//        };
//    }
//
//    /**
//     * 会话内存.
//     *
//     * @return {@link ChatMemory}
//     */
//    public ChatMemory chatMemory() {
//        return new InMemoryChatMemory();
//    }
//
//    /**
//     * 聊天客户端.
//     *
//     * @param chatMemory 会话内存
//     * @return {@link ChatClient}
//     */
//    public ChatClient chatClient(ChatMemory chatMemory) {
//        ChatClient.Builder builder = ChatClient.builder(chatModel);
//
//        // 设置系统提示词
//        builder.defaultSystem(s -> s.text(getSystemPrompt()).param("current_date", LocalDate.now().toString()));
//
//        // 添加默认 advisors
//        getDefaultAdvisors(chatMemory).forEach(builder::defaultAdvisors);
//
//        if (!tools.isEmpty()) {
//            tools.forEach(builder::defaultTools);
//        }
//
//        // 构建 ChatClient
//        return builder.build();
//    }
//}
