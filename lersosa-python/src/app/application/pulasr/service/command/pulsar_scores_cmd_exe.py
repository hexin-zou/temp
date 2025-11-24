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


import asyncio
import gc
import logging
import os
import time
from concurrent.futures import ThreadPoolExecutor

import torch
from torch.utils.data import DataLoader

from app.client.pulsar.dto import PulsarScoresRequest
from app.common.config import GrpcConfig
from app.core.algo.pulsar.utils import SubimageDataset
from app.core.model.model_loader import ModelLoader
from app.core.rpc import RpcClient
from app.domain.entity import GrpcEntity
from proto import Pulsar_pb2


class PulsarScoresCmdExe:
    """脉冲星分数命令执行器
        提供脉冲星分数命令处理器
    """

    # 初始化
    def __init__(self):
        # RPC客户端
        self.rpc_client: RpcClient = RpcClient(GrpcConfig.GRPC_PROTO_DIR)

        # 模型加载器
        self.model_loader: ModelLoader = ModelLoader()

    # 配置日志文件
    logging.basicConfig(filename='model_inference_errors.log', level=logging.ERROR)

    # 线程池大小建议根据 CPU 核心数调整
    _executor = ThreadPoolExecutor(max_workers=max(1, int(os.cpu_count() * 0.75)))

    # 脉冲星打分
    async def execute(self, candidate: list[PulsarScoresRequest]) -> Pulsar_pb2.PulsarReply:  # type: ignore
        """处理脉冲星评分文件的批量请求
        Args:
            candidate: 评分请求对象列表，包含文件评分相关参数
        Returns:
            Pulsar_pb2.PulsarReply: 封装后的gRPC响应对象，包含评分结果数据
        Notes:
            - 将请求参数转换为protobuf格式
            - 初始化模型并处理图片进行打分
            - 通过gRPC客户端调用远程评分服务
            - 将响应结果序列化为JSON格式返回
        """

        # 获取设备
        device = self.model_loader.get_instance().get_device()

        # 加载模型
        model = self.model_loader.get_instance().get_model()

        # 构建数据集与数据加载器
        try:
            dataset = SubimageDataset(
                image_paths=[req.file_url for req in candidate],
                is_testing=True
            )
        except Exception as e:
            return GrpcEntity(data={"error": f"加载数据集失败：{e}"})

        pb_requests = [
            Pulsar_pb2.PulsarScoresRequest(  # type: ignore
                tenant_id=req.tenant_id,
                file_name=req.file_name,
                file_url=req.file_url
            ) for req in candidate
        ]

        num_workers = min(int(os.cpu_count() * 0.75), 30) if os.cpu_count() else 4
        data_loader = DataLoader(
            dataset=dataset,
            batch_size=16,
            shuffle=False,
            num_workers=num_workers,
            pin_memory=True,
            persistent_workers=bool(num_workers > 0)
        )

        # 将同步的推理过程放到线程池中执行
        await asyncio.get_event_loop().run_in_executor(
            PulsarScoresCmdExe._executor,
            PulsarScoresCmdExe._process_batch,
            data_loader,
            model,
            device,
            pb_requests
        )

        return GrpcEntity(data=self.rpc_client.call_method("PulsarScores", pb_requests))

    @staticmethod
    def _process_batch(data_loader, model, device, pb_requests):
        """实际执行模型推理的同步方法，带重试机制及资源回收"""
        try:
            with torch.no_grad():
                for images, _ in data_loader:
                    retries = 0
                    success = False
                    while retries < 3 and not success:
                        try:
                            images = images.to(device)

                            outputs = model(images)
                            scores = torch.softmax(outputs, dim=1)[:, 1].cpu().numpy()

                            for i, score in enumerate(scores):
                                pb_requests[i].score = float(score)

                            success = True

                            del images, outputs, scores
                            torch.cuda.empty_cache() if device.type == 'cuda' else None

                        except Exception as e:
                            retries += 1
                            logging.error(f"处理批次时发生错误：{e}，正在进行第 {retries} 次重试...")
                            if retries == 3:
                                failed_urls = [req.file_url for req in pb_requests]
                                error_msg = f"已达到最大重试次数，放弃当前批次处理。失败的文件URL列表：{failed_urls}"
                                logging.critical(error_msg)
                            raise RuntimeError(f"处理批次错误：经过 3 次重试后仍然失败。") from e
                        time.sleep(1)

            torch.cuda.empty_cache() if device.type == 'cuda' else None
            gc.collect()
        finally:
            logging.info("模型推理完成，资源回收中...")
            pass
