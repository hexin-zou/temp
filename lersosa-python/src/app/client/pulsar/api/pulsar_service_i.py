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


from abc import ABC, abstractmethod
from typing import List

from proto import Pulsar_pb2


class PulsarServiceI(ABC):
    """脉冲星服务接口
        定义与脉冲星业务相关的操作
    """

    @abstractmethod
    async def pulsar_match(self, candidate: List['PulsarMatcherRequest']) -> Pulsar_pb2.PulsarReply:
        """执行脉冲星匹配逻辑
        Args:
            candidate (List[PulsarMatcherRequest]): 候选体匹配请求对象列表，包含待匹配的脉冲星特征数据
        Returns:
            Pulsar_pb2.PulsarReply: 封装后的gRPC响应对象，包含匹配结果数据
        """
        pass

    @abstractmethod
    async def pulsar_scores(self, candidate: List['PulsarScoresRequest']) -> Pulsar_pb2.PulsarReply:
        """执行脉冲星打分逻辑
        Args:
            candidate (List[PulsarScoresRequest]): 评分请求对象列表，包含文件评分相关参数
        Returns:
            Pulsar_pb2.PulsarReply: 封装后的gRPC响应对象，包含评分结果数据
        """
        pass

    @abstractmethod
    async def pulsar_train(self, code: str) -> str:
        """处理训练终端
        Args:
            code (str): 训练终端编号
        Returns:
            str: 训练结果信息
        """
        pass
