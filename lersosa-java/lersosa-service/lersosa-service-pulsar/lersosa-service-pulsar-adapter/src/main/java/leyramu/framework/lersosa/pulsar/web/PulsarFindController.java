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
import leyramu.framework.lersosa.pulsar.api.PulsarServiceI;
import leyramu.framework.lersosa.pulsar.domain.pulsar.model.PulsarBo;
import leyramu.framework.lersosa.pulsar.domain.pulsar.model.PulsarVo;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 已知脉冲星控制器.
 *
 * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
 * @version 1.0.0
 * @since 2025/1/16
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/find")
public class PulsarFindController extends BaseController {

    /**
     * 脉冲星服务.
     */
    private final PulsarServiceI pulsarServiceI;

    /**
     * 查询已知脉冲星列表.
     */
    @Cacheable(
        cacheNames = "pulsar:find:list",
        key = "#pageQuery.pageNum + '|' + #pageQuery.pageSize" +
            "+ T(java.util.Objects).hash(#bo.name, #bo.survey)",
        unless = "#result == null || #result.getRows().isEmpty()"
    )
    @SaCheckPermission("pulsar:find:list")
    @GetMapping("/list")
    @Log(title = "查询已知脉冲星", businessType = BusinessType.OTHER)
    public TableDataInfo<PulsarVo> list(PulsarBo bo, PageQuery pageQuery) {
        return pulsarServiceI.queryPageList(bo, pageQuery);
    }

    /**
     * 导出已知脉冲星列表.
     */
    @SaCheckPermission("pulsar:find:export")
    @Log(title = "导出已知脉冲星", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PulsarBo bo, HttpServletResponse response) {
        List<PulsarVo> list = pulsarServiceI.queryList(bo);
        ExcelUtil.exportExcel(list, "已知脉冲星", PulsarVo.class, response);
    }

    /**
     * 新增已知脉冲星.
     */
    @CachePut(
        cacheNames = "pulsar:find:info",
        key = "#bo.id",
        condition = "#result != null"
    )
    @CacheEvict(
        cacheNames = "pulsar:find:list",
        condition = "#result != null",
        beforeInvocation = true
    )
    @NgxCacheCls
    @SaCheckPermission("pulsar:find:add")
    @Log(title = "添加已知脉冲星", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PulsarBo bo) {
        return toAjax(pulsarServiceI.insertByBo(bo));
    }

    /**
     * 修改已知脉冲星.
     */
    @CachePut(
        cacheNames = "pulsar:find:info",
        key = "#bo.id",
        condition = "#result != null"
    )
    @CacheEvict(
        cacheNames = "pulsar:find:list",
        condition = "#result != null",
        beforeInvocation = true
    )
    @NgxCacheCls
    @SaCheckPermission("pulsar:find:edit")
    @Log(title = "修改已知脉冲星", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PulsarBo bo) {
        return toAjax(pulsarServiceI.updateByBo(bo));
    }

    /**
     * 删除已知脉冲星.
     *
     * @param ids 主键串
     */
    @CacheEvict(
        cacheNames = {"pulsar:find:list", "pulsar:find:info"},
        condition = "#result != null",
        beforeInvocation = true
    )
    @NgxCacheCls
    @SaCheckPermission("pulsar:find:remove")
    @Log(title = "删除已知脉冲星", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(pulsarServiceI.deleteWithValidByIds(List.of(ids), true));
    }
}
