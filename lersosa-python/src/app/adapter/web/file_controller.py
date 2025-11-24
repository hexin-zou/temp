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


from fastapi import UploadFile, File
from starlette.responses import JSONResponse

from app.application import ServiceFactory
from app.client.file.api import FileServiceI
from app.common.domain import RepoResult
from app.core.base import BaseController, Post
from app.domain.enum import CodeStatus


# 文件控制器
class FileController(BaseController):
    """文件操作控制器，继承自基础控制器
        处理文件相关路由请求，包括文件上传等操作
    """

    # 初始化路由
    def __init__(self) -> None:
        """初始化文件控制器路由配置
            设置路由前缀为/file，归类到file标签组，定义404响应模板
        """
        super().__init__(
            prefix="/file",
            tags=["file"],
            responses={CodeStatus.NOT_FOUND: {"description": "Not found"}},
        )

        # 文件服务
        self._file_service: FileServiceI = ServiceFactory.get_file_service()

    # 上传
    @Post("/upload")
    async def upload_pulsar(self, file: UploadFile = File(...)) -> JSONResponse:
        """处理Pulsar文件上传请求
        Args:
            file: UploadFile 必需，通过表单提交的上传文件对象
        Returns:
            JSONResponse: 标准化响应格式，包含：
                - code: 服务返回状态码
                - msg: 服务返回消息说明
        Notes：
            - 调用文件服务层进行实际文件上传
            - 包装服务层返回结果为标准响应格式
        """

        # 检查文件后缀只能是png或zip
        if not file.filename.endswith((".png", ".zip", ".pfd")):
            return RepoResult.fail(
                code=CodeStatus.INTERNAL_SERVER_ERROR,
                msg="文件格式错误，只能上传png和pfd或zip格式的文件"
            )

        result = await self._file_service.file_upload(file)

        return RepoResult.success(
            code=result.data.code,
            msg=result.data.message
        )
