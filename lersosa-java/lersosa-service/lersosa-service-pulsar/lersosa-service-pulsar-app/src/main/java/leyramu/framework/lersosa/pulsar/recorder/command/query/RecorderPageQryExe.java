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

package leyramu.framework.lersosa.pulsar.recorder.command.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import leyramu.framework.lersosa.common.core.utils.StringUtils;
import leyramu.framework.lersosa.common.mybatis.core.page.PageQuery;
import leyramu.framework.lersosa.common.mybatis.core.page.TableDataInfo;
import leyramu.framework.lersosa.pulsar.domain.recorder.model.RecorderBo;
import leyramu.framework.lersosa.pulsar.domain.recorder.model.RecorderE;
import leyramu.framework.lersosa.pulsar.domain.recorder.model.RecorderVo;
import leyramu.framework.lersosa.pulsar.mapper.recorder.gatewayimpl.database.RecorderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 脉冲星查询执行器.
 *
 * @author <a href="mailto:3267745251@qq.com">Flipped-yuye</a>
 * @version 1.0.0
 * @since 2025/3/5
 */
@Component
@RequiredArgsConstructor
public class RecorderPageQryExe {
    /**
     * 脉冲星信息映射器.
     */
    private final RecorderMapper baseMapper;

    /**
     * 查询记录脉冲星.
     *
     * @param name      脉冲星名称
     * @return 记录脉冲星
     */
    public RecorderVo queryByName(String name) {
        return baseMapper.selectVoOne(Wrappers.lambdaQuery(RecorderE.class).eq(RecorderE::getName, name));
    }

    /**
     * 分页查询记录脉冲星列表.
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 记录脉冲星分页列表
     */
    public TableDataInfo<RecorderVo> queryPageList(RecorderBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<RecorderE> lqw = buildQueryWrapper(bo);
        Page<RecorderVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的记录脉冲星列表.
     *
     * @param bo 查询条件
     * @return 记录脉冲星列表
     */
    public List<RecorderVo> queryList(RecorderBo bo) {
        LambdaQueryWrapper<RecorderE> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 构建查询条件.
     *
     * @param bo 查询参数
     * @return 查询条件
     */
    private LambdaQueryWrapper<RecorderE> buildQueryWrapper(RecorderBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<RecorderE> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), RecorderE::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getSurvey()), RecorderE::getSurvey, bo.getSurvey());
        lqw.eq(StringUtils.isNotBlank(bo.getDiscoverer()), RecorderE::getDiscoverer, bo.getDiscoverer());
        return lqw;
    }
}
