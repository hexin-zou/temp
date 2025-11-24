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

package leyramu.framework.lersosa.pulsar.pulsar.service;

import leyramu.framework.lersosa.common.mybatis.core.page.PageQuery;
import leyramu.framework.lersosa.common.mybatis.core.page.TableDataInfo;
import leyramu.framework.lersosa.pulsar.api.PulsarServiceI;
import leyramu.framework.lersosa.pulsar.domain.pulsar.model.PulsarBo;
import leyramu.framework.lersosa.pulsar.domain.pulsar.model.PulsarVo;
import leyramu.framework.lersosa.pulsar.pulsar.command.PulsarModifyCmdExe;
import leyramu.framework.lersosa.pulsar.pulsar.command.PulsarRemoveCmdExe;
import leyramu.framework.lersosa.pulsar.pulsar.command.PulsarSaveCmdExe;
import leyramu.framework.lersosa.pulsar.pulsar.command.query.PulsarPageQryExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 脉冲星信息业务层.
 *
 * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
 * @version 1.0.0
 * @since 2025/1/18
 */
@Service
@RequiredArgsConstructor
public class PulsarServiceImpl implements PulsarServiceI {

    /**
     * 查询命令执行器.
     */
    private final PulsarPageQryExe pulsarPageQryExe;

    /**
     * 修改命令执行器.
     */
    private final PulsarModifyCmdExe pulsarModifyCmdExe;

    /**
     * 删除命令执行器.
     */
    private final PulsarRemoveCmdExe pulsarRemoveCmdExe;

    /**
     * 保存命令执行器.
     */
    private final PulsarSaveCmdExe pulsarSaveCmdExe;

    /**
     * 查询已知脉冲星.
     *
     * @param name      脉冲星名称
     * @return 已知脉冲星
     */
    @Override
    public PulsarVo queryByName(String name) {
        return pulsarPageQryExe.queryByName(name);
    }

    /**
     * 分页查询已知脉冲星列表.
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 已知脉冲星分页列表
     */
    @Override
    public TableDataInfo<PulsarVo> queryPageList(PulsarBo bo, PageQuery pageQuery) {
        return pulsarPageQryExe.queryPageList(bo, pageQuery);
    }

    /**
     * 查询符合条件的已知脉冲星列表.
     *
     * @param bo 查询条件
     * @return 已知脉冲星列表
     */
    @Override
    public List<PulsarVo> queryList(PulsarBo bo) {
        return pulsarPageQryExe.queryList(bo);
    }

    /**
     * 新增已知脉冲星.
     *
     * @param bo 已知脉冲星
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(PulsarBo bo) {
        return pulsarSaveCmdExe.insertByBo(bo);
    }

    /**
     * 修改已知脉冲星.
     *
     * @param bo 已知脉冲星
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(PulsarBo bo) {
        return pulsarModifyCmdExe.updateByBo(bo);
    }

    /**
     * 校验并批量删除已知脉冲星信息.
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        return pulsarRemoveCmdExe.deleteByIds(ids, isValid);
    }
}
