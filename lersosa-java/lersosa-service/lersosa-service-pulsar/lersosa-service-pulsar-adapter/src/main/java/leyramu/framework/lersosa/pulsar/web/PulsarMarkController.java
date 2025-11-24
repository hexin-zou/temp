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
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotEmpty;
import leyramu.framework.lersosa.common.core.domain.R;
import leyramu.framework.lersosa.common.core.validate.EditGroup;
import leyramu.framework.lersosa.common.excel.utils.ExcelUtil;
import leyramu.framework.lersosa.common.idempotent.annotation.RepeatSubmit;
import leyramu.framework.lersosa.common.log.annotation.Log;
import leyramu.framework.lersosa.common.log.enums.BusinessType;
import leyramu.framework.lersosa.common.mybatis.core.page.PageQuery;
import leyramu.framework.lersosa.common.mybatis.core.page.TableDataInfo;
import leyramu.framework.lersosa.common.purge.annotation.NgxCacheCls;
import leyramu.framework.lersosa.common.satoken.utils.LoginHelper;
import leyramu.framework.lersosa.common.web.core.BaseController;
import leyramu.framework.lersosa.pulsar.api.Mark.co.ChartECo;
import leyramu.framework.lersosa.pulsar.api.MarkServiceI;
import leyramu.framework.lersosa.pulsar.domain.mark.model.MarkBo;
import leyramu.framework.lersosa.pulsar.domain.mark.model.MarkVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 脉冲星标记控制器.
 *
 * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
 * @version 1.0.0
 * @since 2025/1/16
 */
@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/sign")
public class PulsarMarkController extends BaseController {

    /**
     * 脉冲星标记服务.
     */
    private final MarkServiceI markServiceI;

    /**
     * 标记脉冲星.
     *
     * @param dataObj 标记参数
     */
    @CacheEvict(
        cacheNames = {"pulsar:mark:list", "pulsar:mark:chartE"},
        allEntries = true,
        beforeInvocation = true
    )
    @NgxCacheCls
    @SaCheckPermission("pulsar:sign:mark")
    @PostMapping("/mark")
    @RepeatSubmit()
    @Log(title = "标记脉冲星", businessType = BusinessType.UPDATE)
    public R<String> mark(@RequestBody List<MarkBo> markBoList) {
        return markServiceI.mark(markBoList, LoginHelper.getUserId()) ? R.ok("标记成功") : R.fail("标记失败");
    }

    /**
     * 脉冲星列表.
     *
     * @param pageQuery 分页参数
     * @return 脉冲星分页列表
     */
    @Cacheable(
        cacheNames = "pulsar:mark:list",
        key = "#pageQuery.pageNum + '|' + #pageQuery.pageSize" +
            "+ T(java.util.Objects).hash(#bo.flag, #bo.check, #bo.locker, #bo.flagUser, #bo.reFlagUser)",
        unless = "#result == null || #result.getRows().isEmpty()"
    )
    @SaCheckPermission("pulsar:sign:list")
    @GetMapping("/list")
    @Log(title = "查看脉冲星列表", businessType = BusinessType.OTHER)
    public TableDataInfo<MarkVo> list(MarkBo bo, PageQuery pageQuery) {
        return markServiceI.queryPageList(bo, pageQuery);
    }

    /**
     * 脉冲星图片列表.
     *
     * @param pageQuery 分页参数
     * @param session   会话
     * @return 脉冲星分页列表
     */
    @SaCheckPermission("pulsar:sign:imgList")
    @GetMapping("/imgList")
    @Log(title = "查看脉冲星图片列表", businessType = BusinessType.OTHER)
    public TableDataInfo<MarkVo> imgList(PageQuery pageQuery, HttpSession session) {
        return markServiceI.queryPageImgList(pageQuery, session);
    }

    /**
     * 修改AI打分记录.
     */
    @CacheEvict(
        value = {"pulsar:mark:list", "pulsar:mark:chartE"},
        allEntries = true,
        beforeInvocation = true
    )
    @NgxCacheCls
    @SaCheckPermission("pulsar:sign:edit")
    @Log(title = "AI打分记录", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MarkBo bo) {
        return toAjax(markServiceI.updateByBo(bo));
    }

    /**
     * 删除AI打分记录.
     *
     * @param ids 主键串
     */
    @CacheEvict(
        value = {"pulsar:mark:list", "pulsar:mark:chartE"},
        allEntries = true,
        beforeInvocation = true
    )
    @NgxCacheCls
    @SaCheckPermission("pulsar:sign:remove")
    @Log(title = "删除打分记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(markServiceI.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 获取首页分类数据.
     */
    @Cacheable(
        cacheNames = "pulsar:mark:chartE",
        unless = "#result == null"
    )
    @Log(title = "AI打分记录", businessType = BusinessType.OTHER)
    @GetMapping("/chartE")
    public R<ChartECo> chartE() {
        return R.ok(markServiceI.getChartE());
    }

    /**
     * 导出脉冲星标记结果.
     *
     * @param bo       查询参数
     * @param response 响应
     */
    @SaCheckPermission("pulsar:mark:export")
    @Log(title = "导出脉冲星标记结果", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MarkBo bo, HttpServletResponse response) {
        Optional.ofNullable(markServiceI.queryList(bo))
            .map(list -> list.stream()
                .filter(item -> item.getFlag() != 0)
                .collect(Collectors.groupingBy(MarkVo::getFlag)))
            .filter(map -> !map.isEmpty())
            .map(map -> map.entrySet().stream()
                .map(entry -> Map.of("data" + entry.getKey(), entry.getValue()))
                .toList())
            .ifPresent(sheetData -> ExcelUtil.exportTemplateMultiSheet(
                sheetData,
                "标记脉冲星数据",
                "excel/template/mark_template.xlsx",
                response
            ));
    }
}
