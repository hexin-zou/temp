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

package leyramu.framework.lersosa.pulsar.recorder.service;

import leyramu.framework.lersosa.common.mybatis.core.page.PageQuery;
import leyramu.framework.lersosa.common.mybatis.core.page.TableDataInfo;
import leyramu.framework.lersosa.pulsar.api.RecorderServiceI;
import leyramu.framework.lersosa.pulsar.domain.recorder.model.RecorderBo;
import leyramu.framework.lersosa.pulsar.domain.recorder.model.RecorderVo;
import leyramu.framework.lersosa.pulsar.recorder.command.RecorderModifyCmdExe;
import leyramu.framework.lersosa.pulsar.recorder.command.RecorderRemoveCmdExe;
import leyramu.framework.lersosa.pulsar.recorder.command.RecorderSaveCmdExe;
import leyramu.framework.lersosa.pulsar.recorder.command.query.RecorderPageQryExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 脉冲星记录业务层.
 *
 * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
 * @author <a href="mailto:3267745251@qq.com">Flipped-yuye</a>
 * @version 1.0.0
 * @since 2025/1/18
 */
@Service
@RequiredArgsConstructor
public class RecorderServiceImpl implements RecorderServiceI {

    /**
     * 查询命令执行器.
     */
    private final RecorderPageQryExe recorderPageQryExe;

    /**
     * 修改命令执行器.
     */
    private final RecorderModifyCmdExe recorderModifyCmdExe;

    /**
     * 删除命令执行器.
     */
    private final RecorderRemoveCmdExe recorderRemoveCmdExe;

    /**
     * 保存命令执行器.
     */
    private final RecorderSaveCmdExe recorderSaveCmdExe;

    /**
     * 查询记录脉冲星.
     *
     * @param name      脉冲星名称
     * @return 记录脉冲星
     */
    @Override
    public RecorderVo queryByName(String name) {
        return recorderPageQryExe.queryByName(name);
    }

    /**
     * 分页查询记录脉冲星列表.
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 已知脉冲星分页列表
     */
    @Override
    public TableDataInfo<RecorderVo> queryPageList(RecorderBo bo, PageQuery pageQuery) {
        return recorderPageQryExe.queryPageList(bo, pageQuery);
    }

    /**
     * 查询符合条件的记录脉冲星列表.
     *
     * @param bo 查询条件
     * @return 记录脉冲星列表
     */
    @Override
    public List<RecorderVo> queryList(RecorderBo bo) {
        return recorderPageQryExe.queryList(bo);
    }

    /**
     * 新增记录脉冲星.
     *
     * @param bo 已知脉冲星
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(RecorderBo bo) {
        return recorderSaveCmdExe.insertByBo(bo);
    }

    /**
     * 修改记录脉冲星.
     *
     * @param bo 记录脉冲星
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(RecorderBo bo) {
        return recorderModifyCmdExe.updateByBo(bo);
    }

    /**
     * 校验并批量删除记录脉冲星信息.
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        return recorderRemoveCmdExe.deleteByIds(ids, isValid);
    }
}
