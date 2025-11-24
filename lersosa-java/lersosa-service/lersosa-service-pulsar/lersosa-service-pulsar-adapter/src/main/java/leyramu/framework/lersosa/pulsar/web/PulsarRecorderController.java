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

package leyramu.framework.lersosa.pulsar.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import leyramu.framework.lersosa.common.core.domain.R;
import leyramu.framework.lersosa.common.core.validate.AddGroup;
import leyramu.framework.lersosa.common.core.validate.EditGroup;
import leyramu.framework.lersosa.common.excel.utils.ExcelUtil;
import leyramu.framework.lersosa.common.idempotent.annotation.RepeatSubmit;
import leyramu.framework.lersosa.common.log.annotation.Log;
import leyramu.framework.lersosa.common.log.enums.BusinessType;
import leyramu.framework.lersosa.common.mybatis.core.page.PageQuery;
import leyramu.framework.lersosa.common.mybatis.core.page.TableDataInfo;
import leyramu.framework.lersosa.common.purge.annotation.NgxCacheCls;
import leyramu.framework.lersosa.common.web.core.BaseController;
import leyramu.framework.lersosa.pulsar.api.RecorderServiceI;
import leyramu.framework.lersosa.pulsar.domain.recorder.model.RecorderBo;
import leyramu.framework.lersosa.pulsar.domain.recorder.model.RecorderVo;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 脉冲星记录控制器.
 *
 * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
 * @version 1.0.0
 * @since 2025/1/16
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/recorder")
public class PulsarRecorderController extends BaseController {

    /**
     * 注入脉冲星记录服务.
     */
    private final RecorderServiceI recorderserviceI;

    /**
     * 添加新脉冲星记录.
     *
     * @param bo 新脉冲星记录
     * @return 响应结果
     * @apiNote 添加脉冲星记录
     */
    @CachePut(
        cacheNames = "pulsar:recorder:info",
        key = "#bo.id",
        condition = "#result != null"
    )
    @CacheEvict(
        cacheNames = "pulsar:recorder:list",
        condition = "#result != null",
        beforeInvocation = true
    )
    @NgxCacheCls
    @SaCheckPermission("pulsar:recorder:add")
    @Log(title = "保存新脉冲星", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/save")
    public R<Void> save(@Validated(AddGroup.class) @RequestBody RecorderBo bo) {
        return toAjax(recorderserviceI.insertByBo(bo));
    }

    /**
     * 修改新脉冲星记录.
     *
     * @param bo 脉冲星记录
     * @return 响应结果
     * @apiNote 修改脉冲星记录
     */
    @CachePut(
        cacheNames = "pulsar:recorder:info",
        key = "#bo.id",
        condition = "#result != null"
    )
    @CacheEvict(
        cacheNames = "pulsar:recorder:list",
        condition = "#result != null",
        beforeInvocation = true
    )
    @NgxCacheCls
    @SaCheckPermission("pulsar:recorder:edit")
    @Log(title = "修改新脉冲星", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody RecorderBo bo) {
        return toAjax(recorderserviceI.updateByBo(bo));
    }

    /**
     * 删除新脉冲星记录.
     *
     * @param ids 脉冲星记录 ID
     * @return 响应结果
     * @apiNote 删除脉冲星记录
     */
    @CacheEvict(
        cacheNames = {"pulsar:recorder:list", "pulsar:recorder:info"},
        condition = "#result != null",
        beforeInvocation = true
    )
    @NgxCacheCls
    @SaCheckPermission("pulsar:recorder:remove")
    @Log(title = "删除新脉冲星", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(recorderserviceI.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 查询新脉冲星列表.
     */
    @Cacheable(
        cacheNames = "pulsar:recorder:list",
        key = "#pageQuery.pageNum + '|' + #pageQuery.pageSize" +
            "+ T(java.util.Objects).hash(#bo.name, #bo.survey)",
        unless = "#result == null || #result.getRows().isEmpty()"
    )
    @SaCheckPermission("pulsar:recorder:list")
    @Log(title = "查询新脉冲星", businessType = BusinessType.OTHER)
    @GetMapping("/list")
    public TableDataInfo<RecorderVo> list(RecorderBo bo, PageQuery pageQuery) {
        return recorderserviceI.queryPageList(bo, pageQuery);
    }

    /**
     * 导出已知脉冲星列表.
     */
    @SaCheckPermission("pulsar:recorder:export")
    @Log(title = "导出已知脉冲星", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(RecorderBo bo, HttpServletResponse response) {
        List<RecorderVo> list = recorderserviceI.queryList(bo);
        ExcelUtil.exportExcel(list, "已知脉冲星", RecorderVo.class, response);
    }
}
