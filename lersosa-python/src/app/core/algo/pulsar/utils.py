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


from io import BytesIO

import matplotlib  # 导入Matplotlib并设置后端
import numpy as np
import requests
from PIL import Image

matplotlib.use('Agg')  # 设置为非交互式后端
import matplotlib.pyplot as plt
import torch
from torch.utils.data import Dataset
import os


class SubimageDataset(Dataset):
    def __init__(self, image_paths, labels=None, transform=None, is_testing=False):
        """
        Args:
            image_paths (list): 所有图像路径的列表
            labels (list): 对应的标签列表（可选）
            transform (callable): 应用于数据的变换操作
        """
        self.image_paths = image_paths
        self.labels = labels
        self.transform = transform
        self.is_testing = is_testing

        # 子图参数配置（可配置化）
        self.subimage_specs = [
            {'coords': (474, 542, 763, 712), 'is_color': True},  # 第1张（彩色）
            {'coords': (851, 542, 1140, 712), 'is_color': False},  # 彩色子图
            {'coords': (851, 319, 1140, 490), 'is_color': False}  # 灰度子图
        ]

        # 预计算目标尺寸
        self.ref_size = (289, 170)  # (width, height)

        # 缓存最后打开的图像路径（针对连续访问优化）
        self._last_path = None
        self._last_img = None

    def __len__(self):
        return len(self.image_paths)

    def _process_image(self, img):
        """核心处理逻辑（与原始代码保持兼容）"""
        channels = []

        for spec in self.subimage_specs:
            # 解包坐标并裁剪
            left, upper, right, lower = spec['coords']
            sub_img = img.crop((left, upper, right, lower))

            # 调整尺寸（保持宽高比时可选用其他方式）
            if sub_img.size != self.ref_size:
                sub_img = sub_img.resize(self.ref_size, Image.BILINEAR)

            # 通道处理
            if spec['is_color']:
                # 拆分RGB通道并归一化
                r, g, b = sub_img.split()
                for channel in [r, g, b]:
                    channel_array = np.array(channel, dtype=np.float32) / 255.0
                    channels.append(channel_array)
            else:
                # 灰度图处理
                gray_array = np.array(sub_img.convert('L'), dtype=np.float32) / 255.0
                channels.append(gray_array)

        return np.stack(channels, axis=-1)

    def __getitem__(self, idx):
        # 使用缓存优化连续访问
        current_path = self.image_paths[idx]
        if current_path != self._last_path:
            # 兼容网络访问
            if current_path.startswith(("http://", "https://")):
                img_data = BytesIO(requests.get(current_path).content)
                self._last_img = Image.open(img_data).convert('RGB')
            else:
                self._last_img = Image.open(current_path).convert('RGB')
            self._last_path = current_path
        img = self._last_img

        # 处理图像
        data = self._process_image(img)
        # 转换为Tensor并调整维度顺序
        data_tensor = torch.from_numpy(data).permute(2, 0, 1)  # HWC -> CHW
        assert data_tensor.shape[0] == 5, f"通道数错误: 应为7，实际{data_tensor.shape[0]}"
        # 转换为Tensor并调整维度顺序

        # 应用数据增强
        if self.transform:
            data_tensor = self.transform(data_tensor)

        # 提取文件名
        filename = os.path.basename(current_path)
        if self.is_testing == False:
            # 训练验证阶段返回数据和标签（如果有）
            if self.labels is not None:
                return data_tensor, torch.tensor(self.labels[idx], dtype=torch.long)
            return data_tensor
        else:
            return data_tensor, filename  # 测试时返回图像和文件名


def outputanalysis(outputs, labels, threshold=0.1):
    count1 = 0
    count2 = 0
    # print(len(outputs))
    # print(len(labels))
    if len(outputs.shape) != 1:
        for i in range(len(labels)):
            if labels[i] == 1 and outputs[i, 1] > threshold:
                count1 += 1
            elif labels[i] == 0 and outputs[i, 1] >= threshold:
                count2 += 1
        # outputs = np.argmax(outputs,axis=1)
    elif len(outputs.shape) == 1:
        for i in range(len(labels)):
            if labels[i] == 1 and outputs[i] > threshold:
                count1 += 1
            elif labels[i] == 0 and outputs[i] >= threshold:
                count2 += 1
        # outputs = np.argmax(outputs)
    # print("real pulsar/all:%d/%d"%(labels.sum(),len(labels)))
    # outputs = np.argmax(outputs,axis=1)
    recall = count1 / labels.sum()
    false_positive_rate = count2 / (len(labels) - labels.sum())
    # print("recall:%d/%d=%f"%(count1,labels.sum(),recall))
    # print("false positive rate:%d/%d=%f"%(count2,(len(labels)-labels.sum()),count2/(len(labels)-labels.sum())))
    # labels = np.argmax(labels,axis=1)
    count = labels.sum() - count1 + count2
    accuracy = 1 - (count / len(labels))
    # print("acc=%f"%(accuracy))
    f1 = 2 * accuracy * recall / (accuracy + recall)
    # np.save(np.concatenate('output'(pred,outputs,log_prob,labels)), )
    return recall, false_positive_rate, accuracy, f1


def save_verification_image(array, output_path):
    """
    将多通道数组保存为验证图像，每个通道显示为子图
    Args:
        array: 形状为(高度, 宽度, 通道数)的数组
        output_path: 输出图片路径
    """
    # 创建子图布局
    fig, axes = plt.subplots(2, 2, figsize=(12, 10))
    fig.suptitle("Channel Visualization", fontsize=14)

    # 通道配置：标题、颜色映射和显示范围
    channel_configs = [
        ("Red Channel", "Reds", (0, 0)),  # 左上
        ("Green Channel", "Greens", (0, 1)),  # 右上
        ("Blue Channel", "Blues", (1, 0)),  # 左下
        ("Gray Channel", "gray", (1, 1))  # 右下
    ]

    for idx, (title, cmap, pos) in enumerate(channel_configs):
        ax = axes[pos[0], pos[1]]  # 使用正确的二维索引

        # 提取通道数据并归一化（注意数据类型转换）
        channel_data = array[..., idx]
        if array.dtype == np.float32:
            channel_data = (channel_data - channel_data.min()) / (channel_data.max() - channel_data.min())

        # 显示通道图像
        img = ax.imshow(channel_data, cmap=cmap)
        ax.set_title(title)
        ax.axis('off')

    plt.tight_layout()
    plt.savefig(output_path, bbox_inches='tight', dpi=150)
    plt.close()
