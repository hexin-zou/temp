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

package leyramu.framework.lersosa.ai.qwen.command;

import leyramu.framework.lersosa.ai.qwen.dto.QwenPulsarDetectionCmd;
import leyramu.framework.lersosa.ai.qwen.dto.QwenUserDetectionCmd;
import leyramu.framework.lersosa.common.satoken.utils.LoginHelper;
import leyramu.framework.lersosa.pulsar.api.RemoteMarkService;
import leyramu.framework.lersosa.pulsar.api.domain.bo.RemoteMarkBo;
import leyramu.framework.lersosa.pulsar.api.domain.vo.RemoteMarkVo;
import leyramu.framework.lersosa.system.api.RemoteUserService;
import leyramu.framework.lersosa.system.api.model.LoginUser;
import leyramu.framework.lersosa.system.api.model.RoleDTO;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * QwenToolsChatCmd 类
 *
 * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
 * @version 1.0.0
 * @since 2025/4/29
 */
@Component
@RequiredArgsConstructor
public class QwenToolsChatCmdExe {

    ///  ==================== 测试 =============== ///

    @DubboReference(parameters = {"serialization", "fastjson2"})
    private RemoteMarkService remoteMarkService;

    @DubboReference
    private final RemoteUserService remoteUserService;

    /**
     * 执行命令
     *
     * @param cmd 命令
     * @return {@link List}<{@link RemoteMarkVo}>
     */
    public List<RemoteMarkVo> execute(QwenPulsarDetectionCmd cmd) {
        RemoteMarkBo bo = new RemoteMarkBo();
        bo.setTenantId("000000");
        bo.setFlag(cmd.getFlag());
        bo.setFileName(cmd.getName());
        bo.setFlagUser(cmd.getFlagUser());
        bo.setReFlagUser(cmd.getReFlagUser());
        bo.setFlagDate(cmd.getFlagDate());
        return remoteMarkService.selectListByBo(bo);
    }

    /**
     * 获取用户信息
     *
     * @return {@link String}
     */
    public String execute(QwenUserDetectionCmd cmd) {
        LoginUser loginUser = remoteUserService.getUserInfo(cmd.getUserId(), cmd.getTenantId());
        String nickname = loginUser.getNickname();
        List<String> roles = loginUser.getRoles().stream().map(RoleDTO::getRoleName).toList();
        return "用户的名称为：" + nickname + "，用户的身份为：" + roles;
    }
}

