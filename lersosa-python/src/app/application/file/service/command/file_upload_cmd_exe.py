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


from typing import Iterable

import grpc
from fastapi import UploadFile, HTTPException

from app.common.config import GrpcConfig
from app.core.rpc import RpcClient
from app.domain.entity import GrpcEntity
from proto import File_pb2


class FileUploadCmdExe:
    """文件上传命令执行器
            提供文件上传命令处理器
        """

    # 初始化
    def __init__(self):
        # RPC客户端
        self._rpc_client: RpcClient = RpcClient(GrpcConfig.GRPC_PROTO_DIR)

    # 文件上传命令执行器
    async def execute(self, file_request: UploadFile) -> GrpcEntity:
        """通过gRPC流式上传文件到文件存储服务
        Args:
            file_request: UploadFile 包含待上传文件对象，包含文件名、内容类型和文件内容
        Returns:
            GrpcEntity: 包含gRPC服务返回的响应实体
        Raises:
            HTTPException: 当gRPC通信错误或常规异常时返回500错误
        """
        try:
            response = self._rpc_client.call_stream_method(
                method_name="UploadFile",
                request_generator=self._generate_file_requests(file_request)
            )
            return GrpcEntity(data=response)

        except grpc.RpcError as e:
            raise HTTPException(status_code=500, detail=f"{e.details()}, 文件已存在！")
        except Exception as e:
            raise HTTPException(status_code=500, detail=str(e))

    # 分块读取文件并生成 FileRequest 消息
    @staticmethod
    def _generate_file_requests(file: UploadFile) -> Iterable[File_pb2.FileRequest]:
        """文件分块生成器
            以1MB为块大小循环读取文件流，生成符合gRPC协议的文件块消息
            每个文件块包含二进制数据、文件名和MIME类型
        """
        chunk_size = 1024 * 1024
        while True:
            chunk = file.file.read(chunk_size)
            if not chunk:
                break
            file_chunk = File_pb2.FileChunk(
                data=chunk,
                file_name=file.filename,
                mime_type=file.content_type
            )
            yield File_pb2.FileRequest(file_chunk=file_chunk)
