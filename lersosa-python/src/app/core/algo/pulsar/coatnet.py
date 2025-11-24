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

#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#
#
#
#  The author disclaims all warranties, express or implied, including but not limited to the warranties of merchantability and fitness for a particular purpose. Under no circumstances shall the author be liable for any special, incidental, indirect, or consequential damages arising from the use of this software.
#
#  By using this project, users acknowledge and agree to abide by these terms and conditions.

import os
import time

import matplotlib
import numpy as np
import torch.optim as optim
from torch.utils.data import DataLoader
from torchvision import transforms, datasets
from tqdm import tqdm

from app.core.algo.pulsar.utils import outputanalysis

matplotlib.use('Agg')  # 设置为非交互式后端
import matplotlib.pyplot as plt


# 定义训练和验证函数
def train(model, device, train_loader, optimizer, criterion):
    model.train()
    running_loss = 0.0
    correct = 0
    total = 0

    # 使用 tqdm 包装 train_loader，添加进度条
    for batch_idx, (data, target) in enumerate(tqdm(train_loader, desc="Training", unit="batch")):
        # # 记录数据加载开始时间
        # data_load_start = time.time()

        data, target = data.to(device), target.to(device)

        # # 记录数据加载结束时间
        # data_load_end = time.time()
        # # 记录计算开始时间
        # compute_start = time.time()

        optimizer.zero_grad()
        output = model(data)
        loss = criterion(output, target)
        loss.backward()
        optimizer.step()

        running_loss += loss.item()
        _, predicted = output.max(1)
        total += target.size(0)
        correct += predicted.eq(target).sum().item()

        # # 记录计算结束时间
        # compute_end = time.time()
        # # 打印每个 batch 的数据加载和计算时间
        # print(
        #     f"Batch {batch_idx + 1}: Data Load Time: {data_load_end - data_load_start:.4f}s, Compute Time: {compute_end - compute_start:.4f}s")

    train_loss = running_loss / len(train_loader)
    train_acc = 100.0 * correct / total
    return train_loss, train_acc


def validate(model, device, val_loader, criterion, threshold=0.1):
    model.eval()
    running_loss = 0.0
    correct = 0
    total = 0
    all_outputs = []
    all_targets = []

    with torch.no_grad():
        # 使用 tqdm 包装 val_loader，添加进度条
        for data, target in tqdm(val_loader, desc="Validating", unit="batch"):
            data, target = data.to(device), target.to(device)
            output = model(data)
            loss = criterion(output, target)

            running_loss += loss.item()
            _, predicted = output.max(1)
            total += target.size(0)
            correct += predicted.eq(target).sum().item()

            # 收集所有输出和目标，用于计算指标
            all_outputs.append(output.cpu().numpy())
            all_targets.append(target.cpu().numpy())

    val_loss = running_loss / len(val_loader)
    val_acc = 100.0 * correct / total

    # 整合所有批次的输出和目标
    all_outputs = np.concatenate(all_outputs)
    all_targets = np.concatenate(all_targets)

    # 计算指标
    recall, fpr, accuracy, f1 = outputanalysis(all_outputs, all_targets, threshold)

    return val_loss, val_acc, recall, fpr, f1


# 定义训练模块
def train_coatnet(model, train_loader, val_loader, device, epochs=10, lr=0.001, save_path='trained_model/coatnet.pth',
                  threshold=0.1, img=""):
    # 定义损失函数和优化器
    criterion = nn.CrossEntropyLoss()
    optimizer = optim.Adam(model.parameters(), lr=lr, amsgrad=True)

    best_val_f1 = 0.0  # 用于保存最佳模型
    train_losses = []
    val_losses = []
    val_f1s = []

    # 训练和验证
    for epoch in range(epochs):
        start_time = time.time()
        train_loss, train_acc = train(model, device, train_loader, optimizer, criterion)
        val_loss, val_acc, val_recall, val_fpr, val_f1 = validate(model, device, val_loader, criterion, threshold)
        end_time = time.time()

        # 记录损失和F1分数
        train_losses.append(train_loss)
        val_losses.append(val_loss)
        val_f1s.append(val_f1)

        print(f"Epoch [{epoch + 1}/{epochs}], "
              f"Train Loss: {train_loss:.4f}, Train Acc: {train_acc:.2f}%, "
              f"Val Loss: {val_loss:.4f}, Val Acc: {val_acc:.2f}%, "
              f"Val Recall: {val_recall:.4f}, Val FPR: {val_fpr:.4f}, Val F1: {val_f1:.4f}, "
              f"Time: {end_time - start_time:.2f}s")

        # 保存最佳模型（基于F1分数）
        if val_f1 > best_val_f1:
            best_val_f1 = val_f1
            if isinstance(model, nn.DataParallel):
                torch.save(model.module, save_path)  # 保存去除了 DataParallel 包装的模型
            else:
                torch.save(model, save_path)  # 直接保存整个模型
            # torch.save(model.state_dict(), save_path)
            print(f"Saved model with Val F1: {val_f1:.4f}")

    # 绘制损失下降曲线
    plt.figure(figsize=(10, 5))
    plt.plot(train_losses, label="Train Loss")
    plt.plot(val_losses, label="Validation Loss")
    plt.xlabel("Epoch")
    plt.ylabel("Loss")
    plt.title("Loss Curve")
    plt.legend()
    plt.savefig("/home/liucong/haokai/coatnet/coatnet/image/loss/" + img + "_loss.png")  # 保存为 PNG 文件
    plt.close()  # 关闭图形，释放资源

    # 绘制F1分数变化曲线
    plt.figure(figsize=(10, 5))
    plt.plot(val_f1s, label="Validation F1 Score")
    plt.xlabel("Epoch")
    plt.ylabel("F1 Score")
    plt.title("F1 Score Curve")
    plt.legend()
    plt.savefig("/home/liucong/haokai/coatnet/coatnet/image/loss/" + img + "_F1.png")  # 保存为 PNG 文件
    plt.close()  # 关闭图形，释放资源

    print("Training finished!")


# 数据加载部分
def load_data(data_dir, batch_size=32, image_size=(224, 224)):
    transform = transforms.Compose([
        transforms.Resize(image_size),
        transforms.ToTensor(),
        transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
    ])

    train_dataset = datasets.ImageFolder(os.path.join(data_dir, 'train'), transform)
    val_dataset = datasets.ImageFolder(os.path.join(data_dir, 'val'), transform)

    train_loader = DataLoader(train_dataset, batch_size=batch_size, shuffle=True)
    val_loader = DataLoader(val_dataset, batch_size=batch_size, shuffle=False)

    return train_loader, val_loader


import torch
import math
from collections import OrderedDict
from einops import rearrange
import torch.nn as nn
import torch.nn.functional as F


def conv_3x3_bn(in_c, out_c, image_size, downsample=False):
    stride = 2 if downsample else 1
    layer = nn.Sequential(
        nn.Conv2d(in_c, out_c, 3, stride, 1, bias=False),
        nn.BatchNorm2d(out_c),
        nn.GELU()
    )
    return layer


# 修改1: 修正尺寸计算顺序（高度在前，宽度在后）
def calc_output_size(input_size, kernel=3, stride=2, padding=1):
    h = (input_size[0] + 2 * padding - kernel) // stride + 1
    w = (input_size[1] + 2 * padding - kernel) // stride + 1
    # 处理奇数尺寸
    if (input_size[0] - kernel + 2 * padding) % stride != 0:
        h += 1
    if (input_size[1] - kernel + 2 * padding) % stride != 0:
        w += 1
    return h, w


class SE(nn.Module):
    def __init__(self, in_c, out_c, expansion=0.25):
        super(SE, self).__init__()
        self.avg_pool = nn.AdaptiveAvgPool2d(1)
        self.fc = nn.Sequential(
            nn.Linear(out_c, int(in_c * expansion), bias=False),
            nn.GELU(),
            nn.Linear(int(in_c * expansion), out_c, bias=False),
            nn.Sigmoid()
        )

    def forward(self, x):
        b, c, _, _ = x.size()
        y = self.avg_pool(x).view(b, c)
        y = self.fc(y).view(b, c, 1, 1)
        return x * y


class MBConv(nn.Module):
    def __init__(self, in_c, out_c, image_size, downsample=False, expansion=4):
        super(MBConv, self).__init__()
        self.downsample = downsample
        self.input_size = image_size  # 记录输入尺寸
        hidden_dim = int(in_c * expansion)

        # 确保非下采样时输入输出通道一致
        if not downsample and in_c != out_c:
            raise ValueError(f"非下采样时通道数必须相同, 但输入{in_c} 输出{out_c}")
        # 添加残差连接卷积
        if in_c != out_c and not downsample:
            self.res_conv = nn.Conv2d(in_c, out_c, 1, bias=False)
        else:
            self.res_conv = nn.Identity()
        if self.downsample:
            self.output_size = calc_output_size(image_size)  # 计算输出尺寸
            self.pool = nn.MaxPool2d(
                kernel_size=3,
                stride=2,
                padding=1,
                ceil_mode=True  # 添加ceil模式适配奇数尺寸
            )
            self.proj = nn.Conv2d(in_c, out_c, 1, 1, 0, bias=False)
        else:
            self.output_size = image_size

        layers = OrderedDict()
        expand_conv = nn.Sequential(
            nn.Conv2d(in_c, hidden_dim, 1, 1, 0, bias=False),
            nn.BatchNorm2d(hidden_dim),
            nn.GELU(),
        )
        layers["expand_conv"] = expand_conv

        dw_conv = nn.Sequential(
            nn.Conv2d(hidden_dim, hidden_dim, 3, 1, 1, groups=hidden_dim, bias=False),
            nn.BatchNorm2d(hidden_dim),
            nn.GELU(),
        )
        layers["dw_conv"] = dw_conv
        layers["se"] = SE(in_c, hidden_dim)

        pro_conv = nn.Sequential(
            nn.Conv2d(hidden_dim, out_c, 1, 1, 0, bias=False),
            nn.BatchNorm2d(out_c)
        )
        layers["pro_conv"] = pro_conv
        self.block = nn.Sequential(layers)

    def forward(self, x):
        if self.downsample:
            # 打印输入形状细节

            x_pool = self.pool(x)
            # 添加详细的维度调试信息

            assert x_pool.shape[2] == self.output_size[0], f"高度不匹配: {x_pool.shape[2]} vs {self.output_size[0]}"
            assert x_pool.shape[3] == self.output_size[1], f"宽度不匹配: {x_pool.shape[3]} vs {self.output_size[1]}"
            return self.proj(x_pool) + self.block(x_pool)
        else:
            # 添加残差连接维度适配
            residual = self.res_conv(x)
            x = residual + self.block(x)
            return x


class Attention(nn.Module):
    def __init__(self, in_c, out_c, base_size, heads=8, dim_head=32, dropout=0.):
        super().__init__()
        self.in_c = in_c  # 新增通道记录
        self.heads = heads
        self.scale = dim_head ** -0.5
        self.base_h, self.base_w = base_size
        # 修改4: 使用实际输入尺寸代替base_size
        self.register_buffer('current_h', torch.tensor(base_size[0]))
        self.register_buffer('current_w', torch.tensor(base_size[1]))

        # 自动计算dim_head确保整除
        if dim_head is None:
            assert in_c % (3 * heads) == 0, f"输入通道{in_c}必须能被3*heads整除"
            dim_head = in_c // (3 * heads)

        inner_dim = heads * dim_head

        self.qkv = nn.Linear(in_c, 3 * inner_dim, bias=False)  # 关键修改
        self.proj = nn.Sequential(
            nn.Linear(inner_dim, out_c),
            nn.Dropout(dropout)
        ) if (heads != 1 or dim_head != in_c) else nn.Identity()

        # 修改5: 动态生成相对位置编码表
        self.relative_bias_table = nn.Parameter(
            torch.zeros((2 * self.base_h - 1) * (2 * self.base_w - 1), heads)
        )
        self._generate_relative_index()

    def _generate_relative_index(self):
        device = self.relative_bias_table.device
        h, w = self.current_h.item(), self.current_w.item()

        # 修改6: 生成非对称网格
        y_coords = torch.arange(h, device=device)
        x_coords = torch.arange(w, device=device)
        grid = torch.stack(torch.meshgrid(y_coords, x_coords, indexing='ij')).flatten(1)

        relative_coords = grid[:, :, None] - grid[:, None, :]
        relative_coords[0] += h - 1  # y轴偏移
        relative_coords[1] += w - 1  # x轴偏移
        relative_coords[0] *= 2 * w - 1  # 缩放y坐标
        self.register_buffer("relative_index", relative_coords.sum(0).flatten())

    def forward(self, x):
        B, N, C = x.shape

        # 改进的尺寸计算算法
        h, w = self._get_factor_pair(N)

        assert h * w == N, f"尺寸分解失败! {h}x{w}={h * w} ≠ {N}"

        # 更新状态并重新生成索引
        self._update_size((h, w))
        self._generate_relative_index()

        qkv = self.qkv(x).chunk(3, dim=-1)  # 现在可以安全拆分
        q, k, v = map(lambda t: rearrange(t, 'b n (h d) -> b h n d', h=self.heads), qkv)

        dots = torch.matmul(q, k.transpose(-1, -2)) * self.scale

        # 安全索引处理
        safe_index = torch.clamp(self.relative_index, 0, self.relative_bias_table.size(0) - 1)
        relative_bias = self.relative_bias_table[safe_index]

        # 修改8: 调整形状适应非对称
        relative_bias = relative_bias.view(h * w, h * w, self.heads).permute(2, 0, 1)
        dots = dots + relative_bias.unsqueeze(0)

        attn = F.softmax(dots, dim=-1)
        out = torch.matmul(attn, v)
        out = rearrange(out, 'b h n d -> b n (h d)')
        return self.proj(out)

    def _get_factor_pair(self, N):
        """智能分解N为最接近的两个因数"""
        sqrt_n = int(math.sqrt(N))
        for h in range(sqrt_n, 0, -1):
            if N % h == 0:
                return h, N // h
        # 处理质数情况
        return 1, N

    def _update_size(self, new_size):
        """统一更新尺寸状态"""
        self.current_h.fill_(new_size[0])
        self.current_w.fill_(new_size[1])


class FFN(nn.Module):
    def __init__(self, dim, hidden_dim, dropout=0.):
        super(FFN, self).__init__()
        self.ffn = nn.Sequential(
            nn.Linear(dim, hidden_dim),
            nn.GELU(),
            nn.Dropout(dropout),
            nn.Linear(hidden_dim, dim),
            nn.Dropout(dropout)
        )

    def forward(self, x):
        return self.ffn(x)


class Transformer(nn.Module):
    def __init__(self, in_c, out_c, image_size,
                 downsample=False,  # 调整参数顺序
                 heads=8, dim_head=32,  # 确保heads参数正确接收整数值
                 dropout=0., expansion=4):
        super().__init__()

        # 添加参数验证
        assert isinstance(heads, int) and heads > 0, f"heads必须为正整数，实际得到{heads}(类型:{type(heads)})"

        # 核心修改点：强制通道投影逻辑
        self.must_project = (in_c != out_c)  # 只要通道不同就必须投影
        self.proj = nn.Conv2d(in_c, out_c, 1, bias=False) if self.must_project else nn.Identity()

        # 统一输出通道为out_c
        self.actual_out_c = out_c

        # 第一阶段：基础参数设置
        self.downsample = downsample
        self.input_size = image_size

        # 投影层设置
        self.proj = nn.Conv2d(in_c, out_c, 1, bias=False) if (in_c != out_c) else nn.Identity()

        # 第三阶段：下采样设置
        if downsample:
            self.output_size = ((image_size[0] + 1) // 2, (image_size[1] + 1) // 2)
            self.pool = nn.Sequential(
                nn.ConstantPad2d((0, 1, 0, 1), 0),
                nn.MaxPool2d(3, 2, 1)
            )
        else:
            self.output_size = image_size

        # 注意力层使用统一输出通道
        self.attn = Attention(
            in_c=self.actual_out_c,  # 固定使用目标输出通道
            out_c=self.actual_out_c,
            base_size=image_size,
            heads=heads,
            dim_head=dim_head,
            dropout=dropout
        )

        # 动态调整LayerNorm参数
        self.norm1 = nn.LayerNorm(self.actual_out_c)

        self.norm2 = nn.LayerNorm(out_c)
        self.ffn = FFN(out_c, int(out_c * expansion), dropout)

        # 调试输出最终配置

    def forward(self, x):

        # 强制应用投影并保持4D形状
        x = self.proj(x)

        # 增强维度断言
        assert x.dim() == 4, f"投影后维度错误！应为4D，实际得到{x.dim()}D"
        B, C, H, W = x.shape

        # 增强下采样处理
        if self.downsample:
            # 精确计算输出尺寸
            H_out = (H + 1) // 2
            W_out = (W + 1) // 2

            # 更新后续模块尺寸
            self.attn.base_size = (H_out, W_out)
            self.attn._update_size((H_out, W_out))
        # 保留原始空间维度信息
        self.orig_shape = (H, W)

        # 三维重组处理
        x_flat = rearrange(x, 'b c h w -> b (h w) c')
        x_norm = self.norm1(x_flat)

        # 注意力处理
        attn_out = self.attn(x_norm)

        # 恢复四维形状
        try:
            out = rearrange(attn_out, 'b (h w) c -> b c h w', h=H, w=W)

        except Exception as e:
            print(f"!! 重组失败！原始尺寸H={H}, W={W}, 元素数={H * W}")
            print(f"!! 实际输入形状: {attn_out.shape}")
            raise

        return out


class CoAtNet(nn.Module):
    def __init__(self, image_size=(170, 289), in_channels=7,  # 高度在前，宽度在后
                 num_blocks=[2, 2, 3, 5, 2], channels=[64, 96, 192, 384, 768],
                 num_classes=2, block_types=['C', 'C', 'T', 'T']):
        super().__init__()
        current_size = image_size

        self.s0 = self._make_layer(conv_3x3_bn, in_channels, channels[0], num_blocks[0], current_size)
        current_size = self._get_output_size(self.s0, current_size)

        self.s1 = self._make_layer(MBConv, channels[0], channels[1], num_blocks[1], current_size)
        current_size = self._get_output_size(self.s1, current_size)

        self.s2 = self._make_layer(MBConv, channels[1], channels[2], num_blocks[2], current_size)
        current_size = self._get_output_size(self.s2, current_size)

        self.s3 = self._make_layer(Transformer, channels[2], channels[3], num_blocks[3], current_size)
        current_size = self._get_output_size(self.s3, current_size)

        self.s4 = self._make_layer(Transformer, channels[3], channels[4], num_blocks[4], current_size)
        current_size = self._get_output_size(self.s4, current_size)

        self.pool = nn.AdaptiveAvgPool2d(1)
        self.fc = nn.Linear(channels[-1], num_classes)

    def _get_output_size(self, layer, input_size):
        """自动计算模块输出尺寸"""
        if any(isinstance(m, (nn.Conv2d, nn.MaxPool2d)) for m in layer.modules()):
            h = (input_size[0] + 2 * 1 - 3) // 2 + 1  # 适用于stride=2的层
            w = (input_size[1] + 2 * 1 - 3) // 2 + 1
            return (h, w)
        return input_size

    def _make_layer(self, block, in_c, out_c, depth, image_size):
        layers = []
        current_size = image_size

        for i in range(depth):
            downsample = (i == 0) and (in_c != out_c)

            # 智能参数传递
            if block == Transformer:  # 仅Transformer需要特殊参数
                layer = block(
                    in_c=in_c,
                    out_c=out_c,
                    image_size=current_size,
                    downsample=downsample,
                    heads=8,  # 明确传递Transformer专用参数
                    dim_head=32
                )
            else:  # 卷积类模块使用基础参数
                layer = block(
                    in_c=in_c,
                    out_c=out_c,
                    image_size=current_size,
                    downsample=downsample
                )

            # 动态获取输出尺寸
            if hasattr(layer, 'output_size'):
                new_size = layer.output_size

                current_size = new_size
            else:
                pass

            layers.append(layer)
            in_c = out_c

        return nn.Sequential(*layers)

    def forward(self, x):

        x = self._forward_stage(x, self.s0, "s0")
        x = self._forward_stage(x, self.s1, "s1")
        x = self._forward_stage(x, self.s2, "s2")
        x = self._forward_stage(x, self.s3, "s3")
        x = self._forward_stage(x, self.s4, "s4")

        return self.fc(self.pool(x).flatten(1))

    def _forward_stage(self, x, stage, name):

        try:
            x = stage(x)

        except Exception as e:
            print(f"!! 阶段 {name} 执行失败！")
            raise

        # 形状完整性检查
        assert x.min() >= -1e5 and x.max() <= 1e5, "数值溢出！"
        return x
