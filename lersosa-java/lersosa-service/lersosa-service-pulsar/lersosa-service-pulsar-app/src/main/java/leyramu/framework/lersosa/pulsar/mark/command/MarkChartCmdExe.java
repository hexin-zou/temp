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

package leyramu.framework.lersosa.pulsar.mark.command;

import leyramu.framework.lersosa.pulsar.api.Mark.co.ChartECo;
import leyramu.framework.lersosa.pulsar.api.Mark.co.UserEvaluateV;
import leyramu.framework.lersosa.pulsar.mapper.mark.gatewayimpl.database.MarkMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static leyramu.framework.lersosa.pulsar.mark.constant.markEvaluateConstant.*;

/**
 * 命令执行器控制器
 *
 * @author <a href="mailto:3267745251@qq.com">Flipped-yuye</a>
 * @version 1.0.0
 * @since 2025/04/22
 */
@Component
@RequiredArgsConstructor
public class MarkChartCmdExe {

    /**
     * 标记数据访问对象
     */
    private final MarkMapper markMapper;

    /**
     * 获取标记图表数据
     *
     * @return {@link ChartECo}
     */
    public ChartECo getChartE() {
        // 创建一个ConcurrentMap，用于存储标记和标记的数量
        ConcurrentMap<Integer, LongAccumulator> map = new ConcurrentHashMap<>(0);
        ConcurrentMap<String, ConcurrentMap<String, LongAccumulator>> userMap = new ConcurrentHashMap<>(0);
        IntStream.rangeClosed(0, 4).forEach(i -> map.put(i, new LongAccumulator(Long::sum, 0L)));

        // 创建一个线程池
        try (ThreadPoolExecutor commonPool = new ThreadPoolExecutor(
            // 核心线程数
            Runtime.getRuntime().availableProcessors(),
            // 最大线程数
            Runtime.getRuntime().availableProcessors() * 2,
            60L, TimeUnit.SECONDS,
            // 防止OOM
            new LinkedBlockingQueue<>(1000),
            // 拒绝策略
            new ThreadPoolExecutor.CallerRunsPolicy()
        )) {
            CompletableFuture<Void> processFlow = CompletableFuture.supplyAsync(markMapper::selectList)
                .thenApplyAsync((list) -> {
                    // 分片
                    int totalSize = list.size();
                    // 分割成4份
                    return IntStream.range(0, THREAD_COUNT)
                        .mapToObj(i -> list.subList(
                            i * totalSize / THREAD_COUNT,
                            Math.min((i + 1) * totalSize / THREAD_COUNT, totalSize)
                        ))
                        .collect(Collectors.toList());
                }, commonPool)
                // 创建子任务
                .thenComposeAsync((subLists) -> CompletableFuture.allOf(subLists.stream()
                    .map(subList -> CompletableFuture.runAsync(() ->
                        subList.forEach(candidateE -> {
                                map.get(candidateE.getFlag()).accumulate(1);
                                if (!(candidateE.getCheck() == 0)) {

                                    // 对第一标记者
                                    ConcurrentMap<String, LongAccumulator> innerMap = userMap.computeIfAbsent(candidateE.getFlagUser() == null ? UNUSED_DATA : candidateE.getFlagUser(), k -> new ConcurrentHashMap<>());
                                    innerMap.computeIfAbsent(candidateE.getFlag() == 1 ? POSITIVE_SAMPLE : NEGATIVE_SAMPLE, k -> new LongAccumulator(Long::sum, 0L)).accumulate(1);
                                    innerMap.computeIfAbsent(candidateE.getCheck() == 1 ? UNUSED_DATA : BE_CHECKED, k -> new LongAccumulator(Long::sum, 0L)).accumulate(1);
                                    innerMap.computeIfAbsent(Objects.equals(candidateE.getReFlagUser(), ADMIN) ? BE_ADMIN_CHECKED : UNUSED_DATA, k -> new LongAccumulator(Long::sum, 0L)).accumulate(1);

                                    // 对第二标记者
                                    userMap.computeIfAbsent(candidateE.getCheck() == 1 ? UNUSED_DATA : candidateE.getReFlagUser(), k -> new ConcurrentHashMap<>())
                                        .computeIfAbsent(CHECKED, k -> new LongAccumulator(Long::sum, 0L))
                                        .accumulate(1);
                                }
                            }
                        ), commonPool
                    ))
                    .toList().toArray(new CompletableFuture[0])), commonPool);
            // 等待所有任务完成
            processFlow.join();
            // 关闭线程池
            commonPool.shutdown();
        }

        long[] counts = new long[5];
        map.forEach((key, acc) -> {
            if (key >= 0 && key <= 4) {
                counts[key] = acc.longValue();
            }
        });

        // 创建用户评价列表
        List<UserEvaluateV> userEvaluateList = userMap.entrySet().stream()
            .filter(entry ->
                !Objects.equals(entry.getKey(), UNUSED_DATA) &&
                    !Objects.equals(entry.getKey(), USER) &&
                    !Objects.equals(entry.getKey(), ""))
            .map(entry -> {
                String key = entry.getKey();
                ConcurrentMap<String, LongAccumulator> innerMap = entry.getValue();
                return new UserEvaluateV(
                    key,
                    calculateTotalWork(
                        innerMap.get(POSITIVE_SAMPLE) == null ? 0 : innerMap.get(POSITIVE_SAMPLE).doubleValue(),
                        innerMap.get(NEGATIVE_SAMPLE) == null ? 0 : innerMap.get(NEGATIVE_SAMPLE).doubleValue(),
                        innerMap.get(BE_CHECKED) == null ? 0 : innerMap.get(BE_CHECKED).doubleValue(),
                        innerMap.get(CHECKED) == null ? 0 : innerMap.get(CHECKED).doubleValue(),
                        innerMap.get(BE_ADMIN_CHECKED) == null ? 0 : innerMap.get(BE_ADMIN_CHECKED).doubleValue(),
                        (double) counts[1] / userMap.size(),
                        (double) counts[2] / userMap.size()
                    ),
                    (innerMap.get(POSITIVE_SAMPLE) != null ? innerMap.get(POSITIVE_SAMPLE).longValue() : 0L)
                        + (innerMap.get(NEGATIVE_SAMPLE) != null ? innerMap.get(NEGATIVE_SAMPLE).longValue() : 0L),
                    (innerMap.get(CHECKED) != null ? innerMap.get(CHECKED).longValue() : 0L)
                );
            })
            // 排序
            .sorted(Comparator.comparing(UserEvaluateV::grade))
            .toList();

        return ChartECo.builder()
            .unlabeledTotal(counts[0])
            .positiveTotal(counts[1])
            .negativeTotal(counts[2])
            .foundCandidateTotal(counts[3])
            .newCandidateTotal(counts[4])
            .candidateTotal(Arrays.stream(counts).sum())
            .userEvaluateList(userEvaluateList)
            .build();
    }

    /**
     * 计算总工作量
     * &#064;公式：  Work总 = (50P)/μP + N/μN - 60*(C/(1+P+N))*(1 + C/(P+1)) + 30*(D^1.5)/(D+E+ε) - 200*(E²)/√(D+1)
     *
     * @param p   正例数量
     * @param n   负例数量
     * @param c   被修改数量
     * @param d   修改数量
     * @param e   被管理员修改数量
     * @param muP 正例平均时间
     * @param muN 负例平均时间
     * @return 总工作量
     */
    private static double calculateTotalWork(
        double p,
        double n,
        double c,
        double d,
        double e,
        double muP,
        double muN) {

        // 逐项计算
        double term1 = (50 * p) / (muP);
        double term2 = n / (muN);

        double term3Part1 = c / (1 + p + n);
        double term3Part2 = 1 + (c / (p + 1));
        double term3 = -60 * term3Part1 * term3Part2;

        double term4 = 30 * (Math.pow(d, 1.5)) / (d + 1);
        //double term5 = -200 * (e * e) / Math.sqrt(d + 1);

        return term1 + term2 + term3 + term4;
    }
}
