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

package leyramu.framework.lersosa.pulsar.mark.command.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpSession;
import leyramu.framework.lersosa.common.core.utils.StringUtils;
import leyramu.framework.lersosa.common.mybatis.core.page.PageQuery;
import leyramu.framework.lersosa.common.mybatis.core.page.TableDataInfo;
import leyramu.framework.lersosa.pulsar.domain.mark.model.MarkBo;
import leyramu.framework.lersosa.pulsar.domain.mark.model.MarkE;
import leyramu.framework.lersosa.pulsar.domain.mark.model.MarkVo;
import leyramu.framework.lersosa.pulsar.mapper.mark.gatewayimpl.database.MarkMapper;
import leyramu.framework.lersosa.resource.api.RemoteFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;

/**
 * 脉冲星查询执行器.
 *
 * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
 * @version 2.7.0
 * @since 2025/1/16
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MarkPageQryExe {

    /**
     * 异步任务执行器.
     */
    private final Executor asyncTaskExecutor;

    /**
     * 脉冲星标记映射器.
     */
    private final MarkMapper baseMapper;

    /**
     * 文件服务远程调用.
     */
    @DubboReference
    private final RemoteFileService remoteFileService;

    /**
     * 查询符合条件的记录脉冲星列表.
     *
     * @param bo 查询条件
     * @return 记录脉冲星列表
     */
    public List<MarkVo> queryList(MarkBo bo) {
        LambdaQueryWrapper<MarkE> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 查询脉冲星图片列表.
     *
     * @param pageQuery      分页参数
     * @param ignoredSession 当前会话
     * @return 脉冲星分页图片列表
     */
    public TableDataInfo<MarkVo> queryPageImgList(PageQuery pageQuery, HttpSession ignoredSession) {
        LambdaQueryWrapper<MarkE> lqw = Wrappers.lambdaQuery();
        lqw.eq(MarkE::getFlag, 0);
        lqw.eq(MarkE::getCheck, 0);

        // 1. 先获取总记录数
        long total = baseMapper.selectCount(lqw);
        if (total == 0) {
            return TableDataInfo.build();
        }

        lqw.orderBy(true, false, MarkE::getScore);

        // 2. 计算随机起始位置
        int pageSize = pageQuery.getPageSize();
        long maxPage = (total + pageSize - 1) / pageSize;
        long randomPage = new Random().nextLong(maxPage) + 1;

        // 3. 构建带随机页码的分页参数并执行分页查询
        Page<MarkVo> result = baseMapper.selectVoPage(new Page<>(randomPage, pageSize), lqw);

        // 5. 修正分页元数据
        result.setCurrent(randomPage);
        result.setPages(maxPage);

        return TableDataInfo.build(result);

//        // 4.1 创建异步任务并使用 thenApply 继续后续处理
//        return CompletableFuture.supplyAsync(() -> {
//                try {
//                    return remoteFileService.selectUrlByFileNames(
//                            result.getRecords().stream()
//                                .map(MarkVo::getFileName)
//                                .collect(Collectors.toSet()))
//                        .stream()
//                        .collect(Collectors.toMap(OssUrlDTO::getFileName, Function.identity()));
//                } catch (Exception e) {
//                    log.error("文件服务调用异常", e);
//                    return Collections.<String, OssUrlDTO>emptyMap();
//                }
//            }, asyncTaskExecutor)
//            .thenApply(urlMap -> {
//                // 4.2 处理结果
//                result.getRecords().forEach(vo ->
//                    Optional.ofNullable(urlMap.get(vo.getFileName()))
//                        .ifPresent(dto -> vo.setPngUrl(dto.getPngUrl()))
//                );
//
//                // 5. 修正分页元数据
//                result.setCurrent(randomPage);
//                result.setPages(maxPage);
//
//                return TableDataInfo.build(result);
//            }).exceptionally(ex -> {
//                log.error("异步处理过程中发生异常", ex);
//                return TableDataInfo.build();
//            })
//            .join();
    }

    /**
     * 查询脉冲星列表.
     *
     * @param pageQuery 分页参数
     * @param bo        查询参数
     * @return 脉冲星分页列表
     */
    public TableDataInfo<MarkVo> queryPageList(MarkBo bo, PageQuery pageQuery) {
        return TableDataInfo.build((Page<MarkVo>) baseMapper.selectVoPage(pageQuery.build(), buildQueryWrapper(bo)));

//        // 异步文件URL处理
//        return CompletableFuture.supplyAsync(() -> {
//                try {
//                    return remoteFileService.selectUrlByFileNames(
//                            result.getRecords().stream()
//                                .map(MarkVo::getFileName)
//                                .collect(Collectors.toSet()))
//                        .stream()
//                        .collect(Collectors.toMap(OssUrlDTO::getFileName, Function.identity()));
//                } catch (Exception e) {
//                    log.error("文件服务调用异常", e);
//                    return Collections.<String, OssUrlDTO>emptyMap();
//                }
//            }, asyncTaskExecutor)
//            .thenApply(urlMap -> {
//                result.getRecords().forEach(vo ->
//                    Optional.ofNullable(urlMap.get(vo.getFileName()))
//                        .ifPresent(dto -> vo.setPngUrl(dto.getPngUrl()))
//                );
//                return TableDataInfo.build(result);
//            })
//            .exceptionally(ex -> {
//                log.error("异步处理过程中发生异常", ex);
//                return TableDataInfo.build();
//            })
//            .join();
    }

    /**
     * 构建查询条件.
     *
     * @param bo 查询参数
     * @return 查询条件
     */
    @SuppressWarnings("unused")
    private LambdaQueryWrapper<MarkE> buildQueryWrapper(MarkBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<MarkE> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getFlag() != null, MarkE::getFlag, bo.getFlag());
        lqw.eq(StringUtils.isNotBlank(bo.getFlagUser()), MarkE::getFlagUser, bo.getFlagUser());
        lqw.eq(StringUtils.isNotBlank(bo.getReFlagUser()), MarkE::getReFlagUser, bo.getReFlagUser());
        lqw.like(bo.getQueryFlagDate() != null, MarkE::getFlagDate, bo.getQueryFlagDate());
        lqw.like(bo.getQueryReFlagDate() != null, MarkE::getReFlagDate, bo.getQueryReFlagDate());
        lqw.eq(bo.getCheck() != null, MarkE::getCheck, bo.getCheck());
        lqw.eq(bo.getLocker() != null, MarkE::getLocker, bo.getLocker());
        lqw.orderBy(true, false, MarkE::getScore);
        return lqw;
    }
}
