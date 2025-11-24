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


from fastapi import UploadFile, File, Form
from fastapi.websockets import WebSocket
from starlette.responses import JSONResponse

from app.application import ServiceFactory
from app.client.file.api import FileServiceI
from app.client.pulsar.api import PulsarServiceI
from app.client.pulsar.dto import PulsarMatcherRequest, PulsarScoresRequest
from app.common.domain import RepoResult
from app.core.base import BaseController, Post, Ws
from app.domain.enum import CodeStatus


#  脉冲星控制器
class PulsarController(BaseController):
    """脉冲星服务控制器，提供匹配和打分功能
        继承自BaseController，配置统一路由前缀和标签
    """

    # 初始化路由
    def __init__(self) -> None:
        """初始化脉冲星控制器配置
            设置路由前缀为/pulsar，归类到pulsar标签组，定义404响应模板
        """
        super().__init__(
            prefix="/pulsar",
            tags=["pulsar"],
            responses={CodeStatus.NOT_FOUND: {"description": "Not found"}},
        )

        # 脉冲星服务
        self._pulsarService: PulsarServiceI = ServiceFactory.get_pulsar_service()

        # 文件服务
        self._fileService: FileServiceI = ServiceFactory.get_file_service()

    # 匹配
    @Post("/match")
    async def pulsar_match(self, request: list[PulsarMatcherRequest]) -> JSONResponse:
        """执行脉冲星匹配操作
        Args:
            request: 匹配请求参数列表，包含多个PulsarMatchRequest对象
        Returns:
            JSONResponse: 返回匹配服务调用的结果，包含成功状态和数据
        """
        result = await self._pulsarService.pulsar_match(request)

        return RepoResult.success(
            code=result.data.code,
            msg=result.data.message
        )

    # 打分
    @Post("/scores")
    async def pulsar_scores(self, tenant_id: str = Form(...), file: UploadFile = File(...)) -> JSONResponse:
        """处理脉冲星打分文件上传
        Args:
            tenant_id: 租户ID，来自表单数据
            file: 上传的文件对象，要求为png格式
        Returns:
            JSONResponse: 返回文件处理结果，包含解析后的打分数据
        Notes：
            - 验证文件格式是否为png
            - 读取并解码文件内容
            - 解析每行数据生成请求对象
        """

        # 检查文件后缀
        if not file.filename.endswith((".png", ".zip")):
            return RepoResult.fail(
                code=CodeStatus.INTERNAL_SERVER_ERROR,
                msg="文件格式错误，只能上传png图片"
            )

        file_result = await self._fileService.file_upload(file)
        print(file_result)
        if file_result.data == CodeStatus.SUCCESS:
            pulsar_scores_list = []
            for file_data in file_result.data.data:
                pulsar_scores_list.append(PulsarScoresRequest(
                    tenant_id=tenant_id,
                    file=file,
                    file_name=file_data.original_name,
                    file_url=file_data.url
                ))

            result = await self._pulsarService.pulsar_scores(pulsar_scores_list)

            return RepoResult.success(
                code=result.data.code,
                msg=result.data.message
            )

        return RepoResult.fail(
            code=file_result.data.code,
            msg=file_result.data.message
        )

    @Ws("/train")
    async def pulsar_train(self, websocket: WebSocket):
        await websocket.accept()
        try:
            while True:
                data = await websocket.receive_text()
                result = await self._pulsarService.pulsar_train(data)
                await websocket.send_text(result)
        except Exception as e:
            await websocket.send_text(f"Error: {str(e)}")
