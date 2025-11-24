#  Copyright (c) 2024 Leyramu Group. All rights reserved.
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


import asyncio
from contextlib import asynccontextmanager
from typing import Any

from fastapi import FastAPI

from app.common.config import NacosConfig, ServerConfig
from app.common.nacos import NacosClientWrapper
from app.core.model.model_loader import ModelLoader
from app.core.redis import RedisService, RedisClient


#  Nacos 生命周期管理
class NacosLifecycle:
    """Nacos 生命周期管理类
        用于管理FastAPI应用与Nacos服务发现组件的集成生命周期
    """

    def __init__(self, app: FastAPI) -> None:
        """初始化Nacos生命周期管理器
        Args:
            app: FastAPI应用实例，用于集成生命周期管理
        """
        self.nacos_client = NacosClientWrapper(
            server_addresses=NacosConfig.NACOS_SERVER_ADDR,
            namespace=NacosConfig.NACOS_NAMESPACE,
            username=NacosConfig.NACOS_USERNAME,
            password=NacosConfig.NACOS_PASSWORD
        )
        self.app = app
        self.service_name = NacosConfig.NACOS_SERVICE_NAME
        self.group_name = NacosConfig.NACOS_GROUP
        self.ip = ServerConfig.SERVER_IP
        self.port = ServerConfig.SERVER_PORT
        self.heartbeat_task = NacosConfig.HEARTBEAT_INTERVAL

    # 服务启动
    async def start(self) -> None:
        """启动服务注册与心跳维护协程
        Notes：
            - 注册当前服务到Nacos服务注册中心
            - 创建定时心跳任务维持服务健康状态
        """
        # 注册服务到Nacos
        await self.nacos_client.register_service(self.service_name, self.group_name, self.ip, self.port)

        # 启动心跳任务
        self.heartbeat_task = asyncio.create_task(
            self.nacos_client.service_heartbeat(self.service_name, self.group_name, self.ip, self.port,
                                                self.heartbeat_task))

    # 停止服务
    async def stop(self) -> None:
        """停止服务维护协程
        Notes：
            - 取消正在运行的心跳任务
            - 安全等待任务取消完成
            - 向Nacos发起服务注销请求
        """
        if self.heartbeat_task:
            # 取消心跳任务
            self.heartbeat_task.cancel()
            try:
                # 等待心跳任务完成或被取消
                await asyncio.wait_for(self.heartbeat_task, timeout=1)
            except (asyncio.exceptions.CancelledError, asyncio.exceptions.TimeoutError):
                # 如果心跳任务被取消或等待超时，忽略错误
                pass

        # 注销服务
        if hasattr(self, 'nacos_client'):
            await self.nacos_client.unregister_service(self.service_name, self.group_name, self.ip, self.port)

    # 生命周期管理
    @asynccontextmanager
    async def lifespan(self, _app: FastAPI) -> Any:
        """FastAPI生命周期管理上下文管理器
        Args:
            _app: FastAPI应用实例（由框架自动注入）
        Yields:
            维持应用运行期的上下文控制权
        Notes：
            - 在上下文进入时执行服务注册
            - 在上下文退出时执行服务注销
        """
        try:
            _ = ModelLoader.get_instance().load_model()
            _ = RedisService(RedisClient())
            await self.start()
            yield
        finally:
            await self.stop()
