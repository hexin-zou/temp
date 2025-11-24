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


from fastapi import UploadFile

from app.application.file.service.command import FileUploadCmdExe
from app.client.file.api.file_service_i import FileServiceI
from app.domain.entity import GrpcEntity


# 文件业务层
class FileServiceImpl(FileServiceI):
    """文件服务业务层
        提供与文件服务端交互的业务方法封装
    """

    # 初始化
    def __init__(self):
        self._fileUploadCmdExe: FileUploadCmdExe = FileUploadCmdExe()

    # 文件上传
    async def file_upload(self, file_request: UploadFile) -> GrpcEntity:
        """执行文件上传命令执行器
        Args:
            file_request: UploadFile 包含待上传文件对象，包含文件名、内容类型和文件内容
        Returns:
            GrpcEntity: 包含gRPC服务返回的响应实体
        """
        return await self._fileUploadCmdExe.execute(file_request)
