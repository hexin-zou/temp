<!--
  - Copyright (c) 2025 Leyramu. All rights reserved.
  - This project (Lersosa), including its source code, documentation, and any associated materials, is the intellectual property of Leyramu. No part of this software may be reproduced, distributed, or transmitted in any form or by any means, including photocopying, recording, or other electronic or mechanical methods, without the prior written permission of the copyright owner, Miraitowa_zcx, except in the case of brief quotations embodied in critical reviews and certain other noncommercial uses permitted by copyright law.
  - For inquiries related to licensing or usage outside the scope of this notice, please contact the copyright holder at 2038322151@qq.com.
  - The author disclaims all warranties, express or implied, including but not limited to the warranties of merchantability and fitness for a particular purpose. Under no circumstances shall the author be liable for any special, incidental, indirect, or consequential damages arising from the use of this software.
  - By using this project, users acknowledge and agree to abide by these terms and conditions.
  -->
<template>
  <div class="p-2">
    <el-empty v-if="total === 0" description="暂无标记数据" style="margin-top: 10%">
      <el-button v-hasPermi="['pulsar:sign:imgList']" type="primary" class="submit" @click="getCandidateList"> 刷新页面 </el-button>
    </el-empty>
    <el-card v-else shadow="never">
      <el-row :gutter="16" class="masonry-row">
        <el-col v-for="(image, index) in candidateList" :key="index" :xs="24" :sm="12" :md="8" :lg="6" :xl="4" class="masonry-item">
          <div
            :class="['image-wrapper', { 'border-red': image.flag === UNLABELED || image.flag === NEGATIVE, 'border-green': image.flag === POSITIVE }]"
          >
            <img :src="image.fileUrl" @click="toggleFlag({ image: image })" alt="Pulsar.png" @contextmenu.prevent="showImageDialog(index)" />
          </div>
        </el-col>
      </el-row>
      <el-row style="margin-top: 20px">
        <el-col :span="24">
          <div style="display: flex; justify-content: center; gap: 16px">
            <el-button v-hasPermi="['pulsar:sign:mark']" type="primary" class="submit" @click="submitChanges">提交 </el-button>
            <el-button type="primary" class="next-page" @click="nextPage">下一页</el-button>
          </div>
        </el-col>
      </el-row>
      <div>
        <PulsarImagePreview
          :isOpen="dialog"
          :candidateList="candidateList"
          :initImageIndex="initImageIndex"
          :is-first-mark="true"
          :is-allow-mark="true"
          :imageToggleFlag="imageToggleFlag"
          @close="dialog = false"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import api from '@/api/pulsar/mark';
import { CandidateQuery, CandidateVo, NEGATIVE, POSITIVE, UNLABELED } from '@/api/pulsar/mark/types';
import { getCurrentInstance, onActivated, onMounted, ref } from 'vue';
import PulsarImagePreview from '@/components/PulsarImagePreview/index.vue';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const candidateList = ref<CandidateVo[]>([]);
const initImageIndex = ref(0);
const submitSuccess = ref(false);
const loading = ref(true);
const dialog = ref(false);
const total = ref(0);

const queryParams = ref<CandidateQuery>({
  pageNum: 1,
  pageSize: 20,
  flag: UNLABELED
});

/**
 * 获取候选体记录列表
 */
const getCandidateList = async () => {
  loading.value = true;
  api
    .candidateImgList(queryParams.value)
    .then((res) => {
      candidateList.value = res.rows;
      total.value = res.total;
    })
    .catch((err) => {
      console.error(err);
    })
    .finally(() => {
      loading.value = false;
    });
};

/**
 * 提交更改
 */
const submitChanges = async () => {
  let loadingInstance: ReturnType<typeof ElLoading.service> | null = null;
  try {
    await proxy.$modal.confirm('确定要提交当前页面的所有标记吗？', '提交确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    loadingInstance = ElLoading.service({
      lock: true,
      text: '提交中...',
      background: 'rgba(0, 0, 0, 0.7)'
    });
    const requestData = candidateList.value.map((image) => ({
      id: image.id,
      flag: image.flag !== UNLABELED ? POSITIVE : NEGATIVE,
      flagUser: image.flagUser,
      reFlagUser: image.reFlagUser,
      flagDate: image.flagDate,
      reFlagDate: image.reFlagDate,
      check: 1,
      locker: 0
    }));
    const res = await api.candidateMark(requestData);
    if (res.code === 200) {
      submitSuccess.value = true;
      proxy.$modal.msgSuccess({
        message: '提交更改成功',
        type: 'success',
        duration: 2000
      });
    }
  } catch (err) {
    console.error(err);
  } finally {
    loadingInstance?.close();
  }
};

/** 切换标记状态(图片查看情况下) */
const imageToggleFlag = (index: number) => {
  candidateList.value[index].flag = candidateList.value[index].flag === UNLABELED ? POSITIVE : UNLABELED;
};

/** 切换标记状态(列表情况下) */
const toggleFlag = ({ image }: { image: any }) => {
  image.flag = image.flag === UNLABELED ? POSITIVE : UNLABELED;
};

/** 查看图片 */
const showImageDialog = (key: number) => {
  initImageIndex.value = key;
  dialog.value = true;
};

/** 下一页 */
const nextPage = async () => {
  if (!submitSuccess.value) {
    const confirmed = await proxy.$modal
      .confirm('当前页面有未提交的更改，请先提交或确认要放弃更改', '确认翻页', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      .catch(() => false);
    if (!confirmed) return;
  }
  if (queryParams.value.pageNum * queryParams.value.pageSize >= total.value) {
    proxy.$modal.message({
      message: '已经是最后一页了',
      type: 'info'
    });
    return;
  }
  queryParams.value.pageNum++;
  submitSuccess.value = false;
  await getCandidateList();
};

/** 页面加载时 */
onMounted(() => {
  getCandidateList();
});

/** 页面激活时 */
onActivated(() => {
  getCandidateList();
});
</script>
<style scoped lang="scss">
@use '@/assets/styles/pulsar';
</style>
