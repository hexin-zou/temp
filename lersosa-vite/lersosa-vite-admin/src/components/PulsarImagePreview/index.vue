<!--
  - Copyright (c) 2025 Leyramu Group. All rights reserved.
  - Licensed under the Apache License, Version 2.0 (the "License");
  - you may not use this file except in compliance with the License.
  - You may obtain a copy of the License at
  -
  -      http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing, software
  - distributed under the License is distributed on an "AS IS" BASIS,
  - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  - See the License for the specific language governing permissions and
  - limitations under the License.
  -
  - This project (Lersosa), including its source code, documentation, and any associated materials, is the intellectual property of Leyramu. No part of this software may be reproduced, distributed, or transmitted in any form or by any means, including photocopying, recording, or other electronic or mechanical methods, without the prior written permission of the copyright owner, Miraitowa_zcx, except in the case of brief quotations embodied in critical reviews and certain other noncommercial uses permitted by copyright law.
  -
  - For inquiries related to licensing or usage outside the scope of this notice, please contact the copyright holder at 2038322151@qq.com.
  -
  - The author disclaims all warranties, express or implied, including but not limited to the warranties of merchantability and fitness for a particular purpose. Under no circumstances shall the author be liable for any special, incidental, indirect, or consequential damages arising from the use of this software.
  -
  - By using this project, users acknowledge and agree to abide by these terms and conditions.
  -->

<template>
  <el-image-viewer
    v-if="isOpen"
    show-progress
    :initial-index="initImageIndex"
    :url-list="candidateList.map((item) => item.fileUrl)"
    @close="$emit('update:is-open', false)"
  >
    <template #toolbar="{ actions, prev, next, reset, activeIndex, setActiveItem }">
      <el-icon @click="prev">
        <Back />
      </el-icon>
      <el-icon @click="next">
        <Right />
      </el-icon>
      <el-icon @click="setActiveItem(candidateList.length - 1)">
        <DArrowRight />
      </el-icon>
      <el-icon @click="actions('zoomOut')">
        <ZoomOut />
      </el-icon>
      <el-icon @click="actions('zoomIn')">
        <ZoomIn />
      </el-icon>
      <el-icon v-if="candidateList[activeIndex].flag === UNLABELED && isFirstMark">
        <CircleCloseFilled class="negative-color" />
      </el-icon>
      <el-icon v-if="candidateList[activeIndex].flag === UNLABELED && !isFirstMark">
        <QuestionFilled class="unlabeled-color" />
      </el-icon>
      <el-icon v-if="candidateList[activeIndex].flag === POSITIVE">
        <SuccessFilled class="positive-color" />
      </el-icon>
      <el-icon v-if="candidateList[activeIndex].flag === NEGATIVE">
        <CircleCloseFilled class="negative-color" />
      </el-icon>
      <el-icon v-if="candidateList[activeIndex].flag === FOUND_PULSAR">
        <WarningFilled class="known-color" />
      </el-icon>
      <el-icon v-if="candidateList[activeIndex].flag === NEW_PULSAR">
        <WarningFilled class="new-color" />
      </el-icon>
      <el-icon @click="actions('clockwise', { rotateDeg: 90, enableTransition: true })">
        <RefreshRight />
      </el-icon>
      <el-icon @click="actions('anticlockwise')">
        <RefreshLeft />
      </el-icon>
      <el-icon @click="reset">
        <Refresh />
      </el-icon>
      <el-icon @click="download(candidateList[activeIndex].fileUrl, candidateList[activeIndex].fileName)">
        <Download />
      </el-icon>
      <el-icon v-if="isAllowMark" @click="imageToggleFlag(activeIndex)">
        <Pointer />
      </el-icon>
    </template>
  </el-image-viewer>
</template>
<script setup lang="ts">
import { CandidateVo, FOUND_PULSAR, NEGATIVE, NEW_PULSAR, POSITIVE, UNLABELED } from '@/api/pulsar/mark/types';
import {
  Back,
  CircleCloseFilled,
  DArrowRight,
  Download,
  Pointer,
  QuestionFilled,
  Refresh,
  RefreshLeft,
  RefreshRight,
  Right,
  SuccessFilled,
  WarningFilled,
  ZoomIn,
  ZoomOut
} from '@element-plus/icons-vue';

/** 属性 */
const props = defineProps({
  //  是否打开
  isOpen: {
    Boolean,
    required: true
  },
  // 是否是第一次标记
  isFirstMark: {
    Boolean,
    required: true
  },
  // 是否允许标记
  isAllowMark: {
    Boolean,
    required: true
  },
  // 图片列表
  candidateList: {
    type: Array as PropType<CandidateVo[]>,
    required: true
  },
  // 初始图片索引
  initImageIndex: {
    type: Number,
    required: true
  },
  // 切换标记Function
  imageToggleFlag: {
    type: Function,
    required: false,
    // 添加校验器：当 isAllowMark 为 true 时，imageToggleFlag 必须存在
    validator: (value: Function | undefined, props: Record<string, any>) => {
      return !props.isAllowMark || typeof value === 'function';
    }
  }
});

/** 下载图片 */
const download = (imageUrl: string, fileName: string) => {
  const link = document.createElement('a');
  link.href = imageUrl;
  link.download = fileName;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};
</script>
<style scoped lang="scss">
@use '@/assets/styles/pulsar';
</style>
