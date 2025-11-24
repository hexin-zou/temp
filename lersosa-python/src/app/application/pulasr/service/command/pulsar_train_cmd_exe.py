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
import glob
import io
import os
import threading
from contextlib import redirect_stdout, redirect_stderr

import torch
from torch.utils.data import DataLoader, ConcatDataset, random_split

from app.common.config import ModelConfig
from app.core.algo.pulsar.coatnet import CoAtNet
from app.core.algo.pulsar.coatnet import train_coatnet
from app.core.algo.pulsar.utils import SubimageDataset


class PulsarTrainCmdExe:
    """脉冲星训练命令执行器
        提供脉冲星训练命令处理器
    """

    # 构造函数
    def __init__(self):
        # 训练状态变量
        self._training_task = None
        self._stop_flag = False
        self._lock = threading.Lock()

        # 初始化模型配置
        self.model_config = ModelConfig
        self.model_path = self.model_config.MODEL_PATH
        self.model_save_path = self.model_config.MODEL_SAVE_PATH
        self.model_sources = self.model_config.MODEL_SOURCES
        self.model_image_save_path = self.model_config.MODEL_IMAGE_SAVE_PATH
        self.model_train_positive_sample_dir = self.model_config.MODEL_TRAIN_POSITIVE_SAMPLE_DIR
        self.model_train_negative_sample_dir = self.model_config.MODEL_TRAIN_NEGATIVE_SAMPLE_DIR
        self.model_train_network_type = self.model_config.MODEL_TRAIN_NETWORK_TYPE
        self.model_batch_size = self.model_config.MODEL_TRAIN_BATCH_SIZE
        self.model_epochs = self.model_config.MODEL_TRAIN_EPOCHS
        self.model_learning_rate = self.model_config.MODEL_TRAIN_LEARNING_RATE
        self.model_train_od = self.model_config.MODEL_TRAIN_OD

    # 脉冲星训练
    @classmethod
    async def execute(cls, code: str) -> str:
        """统一入口：处理 start/stop/普通代码执行"""
        instance = cls()
        code = code.strip().lower()

        if code == "start":
            return await instance._start_training()
        elif code == "stop":
            return await instance._stop_training()
        else:
            return await instance._run_code(code)

    async def _start_training(self) -> str:
        if self._training_task and not self._training_task.done():
            return "训练已在运行中..."

        stdout = io.StringIO()
        stderr = io.StringIO()

        with redirect_stdout(stdout), redirect_stderr(stderr):
            loop = asyncio.get_event_loop()
            future = loop.run_in_executor(None, self._train_model)  # type: ignore
            self._training_task = asyncio.wrap_future(future, loop=loop)

            try:
                await self._training_task
                return stdout.getvalue() or stderr.getvalue() or "训练完成"
            except Exception as e:
                return f"训练异常: {e}"

    async def _stop_training(self) -> str:
        """停止训练任务"""
        if self._training_task and not self._training_task.done():
            self._stop_flag = True
            await self._training_task
            self._training_task = None
            self._stop_flag = False
            return "训练已中断"
        else:
            return "当前没有运行中的训练任务"

    @staticmethod
    async def _run_code(code: str) -> str:
        """执行任意 Python 代码"""
        stdout = io.StringIO()
        stderr = io.StringIO()

        with redirect_stdout(stdout), redirect_stderr(stderr):
            try:
                exec(code)
            except Exception as e:
                return f"Error: {e}"

        return stdout.getvalue() or stderr.getvalue() or "代码执行完成"

    def _train_model(self):
        device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        print(f"Using device: {device}")

        # 加载数据集
        train_loader, val_loader = self._load_datasets()
        print("模型初始化完成")

        model = self._build_model()
        model.to(device)

        # 训练参数
        epochs = self.model_epochs
        lr = self.model_learning_rate

        # 执行训练
        print("开始训练...")
        for epoch in range(epochs):
            if self._should_stop:
                print("训练已手动中断")
                break
            print(f"Epoch {epoch + 1}/{epochs} 正在进行中...")

        # 调用实际训练函数
        train_coatnet(model, train_loader, val_loader, device,
                      epochs, lr, self.model_save_path, img=self.model_save_path)

    def _stop(self):
        """设置中断标志"""
        with self._lock:
            self._stop_flag = True

    @property
    def _should_stop(self):
        with self._lock:
            return self._stop_flag

    def _load_datasets(self):
        """加载正负样本数据集"""
        pos_paths = sorted(glob.glob(os.path.join(self.model_train_positive_sample_dir, "*.png")))
        neg_paths = sorted(glob.glob(os.path.join(self.model_train_negative_sample_dir, "*.png")))

        pos_labels = [1] * len(pos_paths)
        neg_labels = [0] * len(neg_paths)

        pos_dataset = SubimageDataset(pos_paths, labels=pos_labels)
        neg_dataset = SubimageDataset(neg_paths, labels=neg_labels)

        full_dataset = ConcatDataset([pos_dataset, neg_dataset])
        print(f"总样本数: {len(full_dataset)}")
        print(f"正样本数: {len(pos_paths)}")
        print(f"负样本数: {len(neg_paths)}")

        total_size = len(full_dataset)
        train_size = int(0.8 * total_size)
        val_size = total_size - train_size

        train_dataset, val_dataset = random_split(
            full_dataset, [train_size, val_size],
            generator=torch.Generator().manual_seed(42)
        )

        cpu_cores = os.cpu_count()
        num_workers = min(int(cpu_cores * 0.75), 30) if cpu_cores else 4

        train_loader = DataLoader(train_dataset, batch_size=self.model_batch_size,
                                  shuffle=True, num_workers=num_workers,
                                  pin_memory=True, persistent_workers=True)
        val_loader = DataLoader(val_dataset, batch_size=64, shuffle=False,
                                num_workers=num_workers, pin_memory=True,
                                persistent_workers=True)

        return train_loader, val_loader

    def _build_model(self):
        """构建模型"""
        channel = 5
        if self.model_train_network_type == 0:
            model = CoAtNet((170, 289), channel, [2, 2, 3, 5, 2], [64, 96, 192, 384, 768],
                            num_classes=2, block_types=['C', 'C', 'C', 'T'])
        elif self.model_train_network_type == 1:
            model = CoAtNet((289, 170), channel, [2, 2, 6, 14, 2], [64, 96, 192, 384, 768],
                            num_classes=2, block_types=['C', 'C', 'C', 'T'], od=self.model_train_od)
        elif self.model_train_network_type == 2:
            model = CoAtNet((289, 170), channel, [2, 2, 6, 14, 2], [128, 128, 256, 512, 1026],
                            num_classes=2, block_types=['C', 'C', 'C', 'T'], od=self.model_train_od)
        elif self.model_train_network_type == 3:
            model = CoAtNet((289, 170), channel, [2, 2, 6, 14, 2], [192, 192, 384, 768, 1536],
                            num_classes=2, block_types=['C', 'C', 'C', 'T'], od=self.model_train_od)
        elif self.model_train_network_type == 4:
            model = CoAtNet((289, 170), channel, [2, 2, 12, 28, 2], [192, 192, 384, 768, 1536],
                            num_classes=2, block_types=['C', 'C', 'C', 'T'], od=self.model_train_od)
        else:
            raise ValueError("未知网络类型")

        return model
