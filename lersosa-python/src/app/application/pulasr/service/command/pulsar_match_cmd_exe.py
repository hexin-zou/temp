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


from app.client.pulsar.dto import PulsarMatcherRequest
from app.common.config import GrpcConfig
from app.core.rpc import RpcClient
from app.domain.entity import GrpcEntity
from proto import Pulsar_pb2


class PulsarMatchCmdExe:
    """脉冲星匹配命令执行器
        提供脉冲星匹配命令处理器
    """

    # 脉冲星匹配
    @staticmethod
    async def execute(candidate: list[PulsarMatcherRequest]) -> Pulsar_pb2.PulsarReply:  # type: ignore
        """执行脉冲星候选体的批量匹配核心业务逻辑
        Args:
            candidate: 候选体匹配请求对象列表，包含待匹配的脉冲星特征数据
        Returns:
            Pulsar_pb2.PulsarReply: 封装后的gRPC响应对象，包含匹配结果数据
        Notes:
            - 遍历候选体列表构造gRPC请求对象
            - 通过gRPC客户端调用远程匹配服务
            - 将响应结果序列化为JSON格式返回
        """

        all_matches = []

        # 遍历列表
        for a_candidate in candidate:
            # a_pfd = pfd(a_candidate.pfd_path)
            # 坐标转换
            # galactic = CoordinateConvert.equatorial2galactic(a_pfd.rastr, a_pfd.decstr)

            # 目录查询
            # sys.modules['__main__'].psrcat = psrcat
            # with open(r"D:\ATNFcatlog.pkl", "rb") as f:
            #     model = dill.load(f, encoding='latin1', errors='strict')
            #     model.init()
            #     match = model.match(galactic[0], galactic[1], a_pfd.bestdm, a_pfd.topo_p1 * 2)

            all_matches.append(Pulsar_pb2.PulsarMatcherRequest(  # type: ignore
                id=a_candidate.id,
                tenant_id=a_candidate.tenant_id,
                name="已知脉冲星",
                # name=match
                # period=a_pfd.topo_p1,
                # dispersion_measure=a_pfd.bestdm,
                # ra_deg=a_pfd.rastr,
                # dec_deg=a_pfd.decstr,
                # galactic_longitude=galactic[0],
                # galactic_latitude=galactic[1],
                # profiles_shape=a_pfd.profs.shape,
                # folding_period=a_pfd.fold_p1,
                period="0.09",
                dispersion_measure="0.09",
                ra_deg="0.09",
                dec_deg="0.09",
                galactic_longitude=0.09,
                galactic_latitude=0.09,
                profiles_shape="0.09",
                folding_period=0.09,
                discoverer=a_candidate.discoverer
            ))

        return (GrpcEntity(
            data=RpcClient(GrpcConfig.GRPC_PROTO_DIR)
            .call_method("PulsarMatcher", all_matches)))
