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

package leyramu.framework.lersosa.pulsar.api;

import jakarta.servlet.http.HttpSession;
import leyramu.framework.lersosa.common.mybatis.core.page.PageQuery;
import leyramu.framework.lersosa.common.mybatis.core.page.TableDataInfo;
import leyramu.framework.lersosa.pulsar.api.Mark.co.ChartECo;
import leyramu.framework.lersosa.pulsar.domain.mark.model.MarkBo;
import leyramu.framework.lersosa.pulsar.domain.mark.model.MarkE;
import leyramu.framework.lersosa.pulsar.domain.mark.model.MarkVo;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 脉冲星标记业务层 接口.
 *
 * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
 * @version 1.0.0
 * @since 2025/1/16
 */
public interface MarkServiceI {

    /**
     * 脉冲星标记.
     *
     * @param markBoList 标记参数
     * @param userId     用户id
     * @return 是否成功
     */
    boolean mark(List<MarkBo> markBoList, Long userId);

    /**
     * 查询符合条件的记录脉冲星列表.
     *
     * @param bo 查询条件
     * @return 记录脉冲星列表
     */
    List<MarkVo> queryList(@Param("bo") MarkBo bo);

    /**
     * 脉冲星分页图片列表.
     *
     * @param pageQuery 分页参数
     * @param session   会话
     * @return 脉冲星分页图片列表
     */
    TableDataInfo<MarkVo> queryPageImgList(PageQuery pageQuery, HttpSession session);

    /**
     * 脉冲星列表.
     *
     * @param pageQuery 分页参数
     * @param bo        查询参数
     * @return 脉冲星分页列表
     */
    TableDataInfo<MarkVo> queryPageList(MarkBo bo, PageQuery pageQuery);

    /**
     * 修改AI打分记录.
     *
     * @param bo AI打分记录
     * @return 是否修改成功
     */
    Boolean updateByBo(MarkBo bo);

    /**
     * 校验并批量删除AI打分记录信息.
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 更新匹配标记.
     *
     * @param id   脉冲星ID
     * @param flag 脉冲星匹配结果标识
     * @return 是否成功
     */
    Boolean updateMatchFlagById(Long id, Integer flag);

    /**
     * 保存ai打分数据.
     *
     * @param marks Ai 打分数据列表
     * @return 是否成功
     */
    Boolean savePulsarScore(List<MarkE> marks);

    /**
     * 获取候选体数量.
     *
     * @return 获取候选体数量
     */
    ChartECo getChartE();
}
