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


from inspect import iscoroutinefunction

from fastapi import APIRouter


# 路由控制器
class BaseController(APIRouter):
    """基础控制器类，用于定义路由和路由处理方法"""

    # 初始化
    def __init__(self, *args: any, **kwargs: any) -> None:
        """控制器初始化方法
        Args:
            *args: 可变位置参数，传递给父类APIRouter的初始化方法
            **kwargs: 可变关键字参数，传递给父类APIRouter的初始化方法
        Returns:
            None
        """
        super().__init__(*args, **kwargs)
        self.register_routes()

    # 装饰器工厂方法
    @classmethod
    def _create_decorator(cls: any, method_type: str) -> callable:
        """装饰器工厂方法
        Args:
            cls: 类对象
            method_type: HTTP方法类型字符串（如 'get','post'）
        Returns:
            decorator: 实际的路由装饰器函数
        """

        def decorator(path: str, *args: tuple, **kwargs: any) -> callable:
            """实际装饰器实现，为被装饰方法添加路由元数据"""

            def wrapper(func: callable) -> callable:
                """存储路由元信息"""
                func.__router_method__ = (method_type, path, args, kwargs)
                return func

            return wrapper

        return decorator

    # Get 装饰器
    @classmethod
    def get(cls: any, path: str, *args: any, **kwargs: any) -> callable:
        """装饰器用于定义 GET 请求的路由
        Args:
            path: API路径字符串
            *args: 路由位置参数
            **kwargs: 路由关键字参数
        Returns:
            配置好的路由装饰器
        """
        return cls._create_decorator('get')(path, *args, **kwargs)

    # Post 装饰器
    @classmethod
    def post(cls: any, path: str, *args: any, **kwargs: any) -> callable:
        """装饰器用于定义 POST 请求的路由
        Args:
            path: API路径字符串
            *args: 路由位置参数
            **kwargs: 路由关键字参数
        Returns:
            配置好的路由装饰器
        """
        return cls._create_decorator('post')(path, *args, **kwargs)

    # Put 装饰器
    @classmethod
    def put(cls: any, path: str, *args: any, **kwargs: any) -> callable:
        """装饰器用于定义 PUT 请求的路由
        Args:
            path: API路径字符串
            *args: 路由位置参数
            **kwargs: 路由关键字参数
        Returns:
            配置好的路由装饰器
        """
        return cls._create_decorator('put')(path, *args, **kwargs)

    # Delete 装饰器
    @classmethod
    def delete(cls: any, path: str, *args: any, **kwargs: any) -> callable:
        """装饰器用于定义 DELETE 请求的路由
        Args:
            path: API路径字符串
            *args: 路由位置参数
            **kwargs: 路由关键字参数
        Returns:
            配置好的路由装饰器
        """
        return cls._create_decorator('delete')(path, *args, **kwargs)

    # WebSocket 装饰器
    @classmethod
    def websocket(cls: any, path: str, *args: any, **kwargs: any) -> callable:
        """装饰器用于定义 WebSocket 路由
        Args:
            path: API路径字符串
            *args: 路由位置参数
            **kwargs: 路由关键字参数
        Returns:
            配置好的路由装饰器
        """
        return cls._create_decorator('websocket')(path, *args, **kwargs)

    # 路由注册
    def register_routes(self) -> None:
        """注册所有带有装饰器的方法为路由"""
        for name in dir(self):
            method = getattr(self, name)
            if iscoroutinefunction(method) and hasattr(method, '__router_method__'):
                route_method, path, args, kwargs = method.__router_method__
                if route_method == 'get':
                    self.add_api_route(path, method, methods=["GET"], **kwargs)
                elif route_method == 'post':
                    self.add_api_route(path, method, methods=["POST"], **kwargs)
                elif route_method == 'put':
                    self.add_api_route(path, method, methods=["PUT"], **kwargs)
                elif route_method == 'delete':
                    self.add_api_route(path, method, methods=["DELETE"], **kwargs)
                elif route_method == 'websocket':
                    self.add_api_websocket_route(path, method, **kwargs)


# 导出装饰器
Get = BaseController.get
Post = BaseController.post
Put = BaseController.put
Delete = BaseController.delete
Ws = BaseController.websocket
