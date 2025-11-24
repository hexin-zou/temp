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


from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.adapter.web import controllers
from app.core.auth import AuthMiddleware
from app.core.context import NacosLifecycle
from app.core.exception import ExceptionHandlers


# 创建FastAPI应用实例，继承自FastAPI
class App(FastAPI):
    def __init__(self) -> None:
        """初始化应用实例
            Attributes：
            title: 文档标题
            description: 文档描述
            version: 文档版本
            openapi_url: 接口 URL
            contact: 联系方式
            lifespan: 集成Nacos的生命周期管理器，用于服务注册与发现
        Notes：
            - 调用父类构造函数初始化核心功能
            - 注册所有路由配置
            - 添加CORS中间件
            - 配置全局异常处理器
            - 添加根路由健康检查
        """
        super().__init__(
            title="Lersosa 微服务权限管理系统 接口文档",
            description="微服务权限管理系统",
            version="3.2.0",
            openapi_url="/v3/api-docs",
            contact={
                "name": "Miraitowa_zcx",
                "email": "2038322151@qq.com",
                "url": "https://github.com/Leyramu/Lersosa"
            },
            lifespan=NacosLifecycle(self).lifespan
        )
        self._register_routes()
        self._add_cors_middleware()
        self._add_auth_middleware()
        self._add_exception_handlers()
        self._add_root_route()

    # 注册路由
    def _register_routes(self) -> None:
        """注册应用路由
            加载app/controller模块中定义的所有路由控制器
        """
        self.include_router(controllers)

    # 添加中间件
    def _add_cors_middleware(self) -> None:
        """配置CORS跨域中间件
        Attributes：
            allow_origins: 允许所有来源跨域访问
            allow_credentials: 允许携带身份凭证
            allow_methods: 允许所有HTTP方法
            allow_headers: 允许所有请求头
            expose_headers: 暴露所有响应头
        """
        self.add_middleware(
            CORSMiddleware,  # type: ignore
            allow_origins=["*"],
            allow_credentials=True,
            allow_methods=["*"],
            allow_headers=["*"],
            expose_headers=["*"],
        )

    # 添加鉴权中间件
    def _add_auth_middleware(self) -> None:
        """添加鉴权中间件
            使用自定义AuthMiddleware类统一处理鉴权
        """
        self.add_middleware(AuthMiddleware)  # type: ignore

    # 添加异常处理器
    def _add_exception_handlers(self) -> None:
        """注册全局异常处理器
            使用自定义ExceptionHandlers类统一处理系统异常
        """
        exception_handlers = ExceptionHandlers(self)
        exception_handlers.add_exception_handlers()

    # 添加根路由
    def _add_root_route(self) -> None:
        """添加基础健康检查端点
            返回应用基础状态信息，用于服务探活检测
        """

        # 根路由
        @self.get("/")
        async def root() -> dict:
            """根路由响应
            Returns:
                dict: 包含欢迎信息的字典
            """
            return {"message": "欢迎使用 Leyramu 内部网关！"}


# 创建应用实例
application = App()
