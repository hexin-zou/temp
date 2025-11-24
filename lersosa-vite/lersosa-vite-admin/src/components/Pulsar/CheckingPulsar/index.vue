<!--
  - Copyright (c) 2025 Leyramu. All rights reserved.
  - This project (Lersosa), including its source code, documentation, and any associated materials, is the intellectual property of Leyramu. No part of this software may be reproduced, distributed, or transmitted in any form or by any means, including photocopying, recording, or other electronic or mechanical methods, without the prior written permission of the copyright owner, Miraitowa_zcx, except in the case of brief quotations embodied in critical reviews and certain other noncommercial uses permitted by copyright law.
  - For inquiries related to licensing or usage outside the scope of this notice, please contact the copyright holder at 2038322151@qq.com.
  - The author disclaims all warranties, express or implied, including but not limited to the warranties of merchantability and fitness for a particular purpose. Under no circumstances shall the author be liable for any special, incidental, indirect, or consequential damages arising from the use of this software.
  - By using this project, users acknowledge and agree to abide by these terms and conditions.
  -->

<template>
  <div class="p-2">
    <el-card shadow="hover" class="mb-[10px]">
      <el-row>
        <el-form-item label="标记人" prop="flagUser" style="width: 300px; margin-right: 10px">
          <el-select v-model="queryParams.flagUser" placeholder="请选择第一标记人" clearable @change="handleQuery">
            <el-option label="全部" value="" />
            <el-option v-for="user in userList" :key="user" :label="user" :value="user" />
          </el-select>
        </el-form-item>
        <el-button v-hasPermi="['pulsar:sign:list']" @click="reset" icon="Refresh">重置</el-button>
      </el-row>
    </el-card>
    <el-empty v-if="total === 0" description="暂无标记数据" style="margin-top: 10%">
      <el-button v-hasPermi="['pulsar:sign:list']" type="primary" class="submit" @click="getCandidateList"> 刷新页面 </el-button>
    </el-empty>
    <el-card v-else shadow="hover">
      <el-row :gutter="16" class="masonry-row">
        <el-col v-for="(image, index) in candidateList" :key="index" :xs="24" :sm="12" :md="8" :lg="6" :xl="4" class="masonry-item">
          <div :class="['image-wrapper', { 'border-red': image.flag === NEGATIVE, 'border-green': image.flag === POSITIVE }]">
            <img alt="image" :src="image.fileUrl" @click="toggleFlag({ image: image })" @contextmenu.prevent="showImageDialog(index)" />
          </div>
        </el-col>
      </el-row>
      <div style="text-align: center">
        <el-button type="primary" class="prev-page" @click="prevPage">上一页</el-button>
        <el-button v-hasPermi="['pulsar:sign:mark']" type="primary" class="submit" @click="submitChanges"> 提交 </el-button>
        <el-button type="primary" class="next-page" @click="nextPage">下一页</el-button>
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          layout="prev, pager, next, ->, total"
          @current-change="getCandidateList"
        />
      </div>
      <div>
        <PulsarImagePreview
          :isOpen="dialog"
          :candidateList="candidateList"
          :initImageIndex="initImageIndex"
          :is-first-mark="false"
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
import { CandidateQuery, CandidateVo, NEGATIVE, POSITIVE } from '@/api/pulsar/mark/types';
import { getUserProfile, listUser } from '@/api/system/user';
import { getCurrentInstance, onActivated, onMounted, ref } from 'vue';
import { UserQuery } from '@/api/system/user/types';
import PulsarImagePreview from '@/components/PulsarImagePreview/index.vue';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const candidateList = ref<CandidateVo[]>([]);
const userList = ref<string[]>([]);
const userName = ref<string>('');
const initImageIndex = ref(0);
const loading = ref(true);
const dialog = ref(false);
const total = ref(0);

const userQueryParams = ref<UserQuery>({
  pageNum: 1,
  pageSize: -1
});

const queryParams = ref<CandidateQuery>({
  pageNum: 1,
  pageSize: 20,
  flag: POSITIVE,
  check: 1,
  locker: 0,
  flagUser: userName.value
});

/**
 * 查询AI打分记录列表
 */
const getCandidateList = async () => {
  loading.value = true;
  api
    .candidateList(queryParams.value)
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
    await proxy.$modal.confirm('确定要提交当前页面的所有标记吗？', '提交确认');
    loadingInstance = ElLoading.service({
      lock: true,
      text: '提交中...',
      background: 'rgba(0, 0, 0, 0.7)'
    });
    const requestData = candidateList.value.map((image) => ({
      id: image.id,
      flag: image.flag,
      flagUser: image.flagUser,
      flagDate: image.flagDate,
      reFlagUser: image.flag == POSITIVE ? null : userName.value,
      reFlagDate: image.flag == POSITIVE ? null : new Date('YYYY-MM-DD'),
      check: 2
    }));
    const res = await api.candidateMark(requestData);
    if (res.code === 200) {
      proxy.$modal.msgSuccess({
        message: '提交更改成功',
        duration: 2000,
        onClose: () => {
          getCandidateList();
        }
      });
    }
  } catch (err) {
    console.error(err);
  } finally {
    loadingInstance?.close();
  }
};

/**
 * 获取当前用户姓名
 */
const getUserName = async () => {
  const res = await getUserProfile();
  userName.value = res.data.user.nickName;
  queryParams.value.flagUser = userName.value;
};

/**
 * 获取用户列表
 */
const getUserList = async () => {
  const res = await listUser(userQueryParams.value);
  userList.value = res.rows.map((user) => user.nickName);
};

/** 切换标记状态(图片查看情况下) */
const imageToggleFlag = (index: number) => {
  candidateList.value[index].flag = candidateList.value[index].flag === NEGATIVE ? POSITIVE : NEGATIVE;
};

/** 切换标记状态(列表情况下) */
const toggleFlag = ({ image }: { image: any }) => {
  image.flag = image.flag === NEGATIVE ? POSITIVE : NEGATIVE;
};

/** 上一页 */
const prevPage = () => {
  if (queryParams.value.pageNum > 1) {
    queryParams.value.pageNum--;
    getCandidateList();
  } else {
    proxy.$modal.msgWarning('已经是第一页了');
  }
};

/** 下一页 */
const nextPage = () => {
  if (queryParams.value.pageNum * queryParams.value.pageSize < total.value) {
    queryParams.value.pageNum++;
    getCandidateList();
  } else {
    proxy.$modal.msgWarning('已经是最后一页了');
  }
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getCandidateList();
};

/** 查看图片 */
const showImageDialog = (key: number) => {
  initImageIndex.value = key;
  dialog.value = true;
};

/** 重置按钮操作 */
const reset = () => {
  queryParams.value = {
    pageNum: 1,
    pageSize: 20,
    flag: POSITIVE,
    locker: 0,
    check: 1
  };
  getCandidateList();
};

/** 页面加载时 */
onMounted(async () => {
  getUserName()
    .then(() => {
      getCandidateList();
    })
    .finally(() => {
      getUserList();
    });
});

/** 页面激活时 */
onActivated(async () => {
  getUserName()
    .then(() => {
      getCandidateList();
    })
    .finally(() => {
      getUserList();
    });
});
</script>
<style scoped lang="scss">
@use '@/assets/styles/pulsar';
</style>
