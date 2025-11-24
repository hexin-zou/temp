#  Copyright (c) 2025 Leyramu Group. All rights reserved.
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#
#  This project (Lersosa), including its source code, documentation, and any associated materials, is the intellectual property of Leyramu. No part of this software may be reproduced, distributed, or transmitted in any form or by any means, including photocopying, recording, or other electronic or mechanical methods, without the prior written permission of the copyright owner, Miraitowa_zcx, except in the case of brief quotations embodied in critical reviews and certain other noncommercial uses permitted by copyright law.
#
#  For inquiries related to licensing or usage outside the scope of this notice, please contact the copyright holder at 2038322151@qq.com.
#
#  The author disclaims all warranties, express or implied, including but not limited to the warranties of merchantability and fitness for a particular purpose. Under no circumstances shall the author be liable for any special, incidental, indirect, or consequential damages arising from the use of this software.
#
#  By using this project, users acknowledge and agree to abide by these terms and conditions.

#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#
#
#
#  The author disclaims all warranties, express or implied, including but not limited to the warranties of merchantability and fitness for a particular purpose. Under no circumstances shall the author be liable for any special, incidental, indirect, or consequential damages arising from the use of this software.
#
#  By using this project, users acknowledge and agree to abide by these terms and conditions.


from app.application.pulasr.service.command import PulsarScoresCmdExe, PulsarMatchCmdExe, PulsarTrainCmdExe
from app.client.pulsar.api.pulsar_service_i import PulsarServiceI
from app.client.pulsar.dto import PulsarMatcherRequest, PulsarScoresRequest
from proto import Pulsar_pb2


#  脉冲星业务层
class PulsarServiceImpl(PulsarServiceI):
    """脉冲星 业务服务层
        提供与脉冲星服务端交互的业务方法封装
    """

    # 初始化
    def __init__(self):
        # 脉冲星匹配命令执行器
        self._pulsarMatchCmdExe: PulsarMatchCmdExe = PulsarMatchCmdExe()

        # 脉冲星打分命令执行器
        self._pulsarScoresCmdExe: PulsarScoresCmdExe = PulsarScoresCmdExe()

        # 脉冲星训练命令执行器
        self._pulsarTrainCmdExe: PulsarTrainCmdExe = PulsarTrainCmdExe()

    # 脉冲星匹配
    async def pulsar_match(self, candidate: list[PulsarMatcherRequest]) -> Pulsar_pb2.PulsarReply:  # type: ignore
        """执行脉冲星匹配命令执行器
        Args:
            candidate: 候选体匹配请求对象列表，包含待匹配的脉冲星特征数据
        Returns:
            Pulsar_pb2.PulsarReply: 封装后的gRPC响应对象，包含匹配结果数据
        """
        return await self._pulsarMatchCmdExe.execute(candidate)

    # 脉冲星打分
    async def pulsar_scores(self, candidate: list[PulsarScoresRequest]) -> Pulsar_pb2.PulsarReply:  # type: ignore
        """执行脉冲星打分命令执行器
        Args:
            candidate: 评分请求对象列表，包含文件评分相关参数
        Returns:
            Pulsar_pb2.PulsarReply: 封装后的gRPC响应对象，包含评分结果数据
        """
        return await self._pulsarScoresCmdExe.execute(candidate)

    # 处理训练终端
    async def pulsar_train(self, code: str) -> str:
        """处理训练终端
        Args:
            code: 训练终端编号
        Returns:
            str: 训练结果信息
        """
        return await self._pulsarTrainCmdExe.execute(code)
