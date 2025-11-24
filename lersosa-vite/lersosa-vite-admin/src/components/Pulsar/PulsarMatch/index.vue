<!--
  - Copyright (c) 2025 Leyramu. All rights reserved.
  - This project (Lersosa), including its source code, documentation, and any associated materials, is the intellectual property of Leyramu. No part of this software may be reproduced, distributed, or transmitted in any form or by any means, including photocopying, recording, or other electronic or mechanical methods, without the prior written permission of the copyright owner, Miraitowa_zcx, except in the case of brief quotations embodied in critical reviews and certain other noncommercial uses permitted by copyright law.
  - For inquiries related to licensing or usage outside the scope of this notice, please contact the copyright holder at 2038322151@qq.com.
  - The author disclaims all warranties, express or implied, including but not limited to the warranties of merchantability and fitness for a particular purpose. Under no circumstances shall the author be liable for any special, incidental, indirect, or consequential damages arising from the use of this software.
  - By using this project, users acknowledge and agree to abide by these terms and conditions.
  -->

<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-row ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="标记人" prop="flagUser" style="width: 300px; margin-right: 10px">
              <el-select v-model="queryParams.flagUser" placeholder="请选择第一标记人" clearable @change="handleQuery">
                <el-option label="全部" value="" />
                <el-option v-for="user in userList" :key="user" :label="user" :value="user" />
              </el-select>
            </el-form-item>
            <el-form-item prop="reFlagUser" style="width: 250px; margin-right: 10px">
              <el-select v-model="queryParams.reFlagUser" placeholder="请选择第二标记人" clearable @change="handleQuery">
                <el-option label="全部" value="" />
                <el-option v-for="user in userList" :key="user" :label="user" :value="user" />
              </el-select>
            </el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-row>
        </el-card>
      </div>
    </transition>

    <el-card shadow="hover">
      <template #header>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="connection" @click="matchPagePulsar">匹配本页数据</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="primary" plain icon="connection" @click="matchAllPulsar">匹配全部数据</el-button>
          </el-col>
          <right-toolbar v-model:show-search="showSearch" @query-table="getCandidateList"></right-toolbar>
        </el-row>
      </template>
      <el-empty v-if="total === 0" description="暂无数据" style="margin-top: 10%">
        <el-button v-hasPermi="['pulsar:sign:list']" type="primary" class="submit" @click="getCandidateList"> 刷新页面 </el-button>
      </el-empty>
      <el-table v-else v-loading="loading" :data="candidateList">
        <el-table-column v-if="true" label="id" align="center" prop="id" />
        <el-table-column label="图片" align="center" prop="fileUrl">
          <template #default="scope">
            <img
              :src="scope.row.fileUrl"
              alt="图片"
              style="max-width: 100px; max-height: 100px"
              @contextmenu.prevent="showImageDialog(scope.$index)"
            />
          </template>
        </el-table-column>
        <el-table-column label="打分平方和" align="center" prop="score" />
        <el-table-column label="标记" align="center" prop="flag">
          <template #default="scope">
            <span v-if="scope.row.flag === 1">正样本</span>
            <span v-else-if="scope.row.flag === 0">未被标记</span>
            <span v-else-if="scope.row.flag === 2">负样本</span>
            <span v-else-if="scope.row.flag === 3">已知脉冲星</span>
            <span v-else-if="scope.row.flag === 4">新脉冲星</span>
            <span v-else>{{ scope.row.flag }}</span>
          </template>
        </el-table-column>
        <el-table-column label="标记人" align="center" prop="flagUser" />
        <el-table-column label="更正标记人" align="center" prop="reFlagUser" />
        <el-table-column label="标记时间" align="center" prop="flagDate" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.flagDate, '{y}-{m}-{d}-{h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="更正标记时间" align="center" prop="reFlagDate" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.reFlagDate, '{y}-{m}-{d}-{h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="检查" align="center" prop="check">
          <template #default="scope">
            <span v-if="scope.row.check === 1">未检查</span>
            <span v-else-if="scope.row.check === 2">已检查</span>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        :total="total"
        @pagination="getCandidateList"
      />
      <div>
        <el-image-viewer
          v-if="dialog"
          show-progress
          :initial-index="initImageIndex"
          :url-list="candidateList.map((item) => item.fileUrl)"
          @close="dialog = false"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import api from '@/api/pulsar/mark';
import { CandidateMatchVo, CandidateQuery, CandidateVo, POSITIVE } from '@/api/pulsar/mark/types';
import { parseTime } from '@/utils/lersosa';
import { getUserProfile, listUser } from '@/api/system/user';
import { onActivated, onMounted, ref } from 'vue';
import { UserQuery } from '@/api/system/user/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const matchPayload = ref<CandidateMatchVo[]>([]);
const matchAllPayload = ref<CandidateMatchVo[]>([]);
const candidateList = ref<CandidateVo[]>([]);
const queryFormRef = ref<ElFormInstance>();
const initImageIndex = ref<number>(0);
const userList = ref<string[]>([]);
const tenantId = ref<string>('');
const matchLoading = ref(false);
const showSearch = ref(true);
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
  flag: POSITIVE
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
 * 匹配本页pulsar
 */
const matchPagePulsar = async () => {
  await proxy.$modal.confirm('匹配操作只会匹配检查过的数据，若数据并未检查，请先进行检查');
  matchLoading.value = true;
  const loadingInstance = ElLoading.service({
    lock: true,
    text: '数据匹配中...',
    background: 'rgba(0, 0, 0, 0.7)'
  });
  matchPayload.value = candidateList.value
    .filter((item) => item.check === 2)
    .map((item) => ({
      id: item.id,
      tenant_id: tenantId.value,
      pfd_path: item.fileUrl,
      discoverer: item.reFlagUser === null ? item.flagUser : item.reFlagUser
    }));
  api
    .matchData(matchPayload.value)
    .then((res) => {
      ElMessage({
        type: 'success',
        message: `${res.msg}`,
        duration: 3000
      });
    })
    .catch((error) => {
      console.error(error);
    })
    .finally(() => {
      matchLoading.value = false;
      loadingInstance.close();
      getCandidateList();
      matchPayload.value = [];
    });
};

/**
 * 匹配全部pulsar
 */
const matchAllPulsar = async () => {
  await proxy.$modal.confirm('匹配操作只会匹配检查过的数据，若数据并未检查，请先进行检查');
  matchLoading.value = true;
  const loadingInstance = ElLoading.service({
    lock: true,
    text: '数据匹配中...',
    background: 'rgba(0, 0, 0, 0.7)'
  });
  api
    .candidateList({
      pageNum: 1,
      pageSize: -1,
      flag: 1,
      check: 2
    })
    .then((res) => {
      matchAllPayload.value = res.rows.map((item) => ({
        id: item.id,
        tenant_id: tenantId.value,
        pfd_path: item.fileUrl,
        discoverer: item.reFlagUser === null ? item.flagUser : item.reFlagUser
      }));
    })
    .catch((err) => {
      console.error(err);
    })
    .finally(() => {
      api
        .matchData(matchAllPayload.value)
        .then((res) => {
          proxy.$modal.msgSuccess({ msg: res.msg });
        })
        .catch((error) => {
          console.error(error);
        })
        .finally(() => {
          matchLoading.value = false;
          loadingInstance.close();
          getCandidateList();
          matchAllPayload.value = [];
        });
    });
};

/**
 * 获取用户tenantId
 */
const getTenantId = async () => {
  const res = await getUserProfile();
  tenantId.value = res.data.user.tenantId;
};

/**
 * 获取用户列表
 */
const getUserList = async () => {
  const res = await listUser(userQueryParams.value);
  userList.value = res.rows.map((user) => user.nickName);
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getCandidateList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
};

/** 查看图片 */
const showImageDialog = (key: number) => {
  initImageIndex.value = key;
  dialog.value = true;
};

/** 初始化加载 */
onMounted(async () => {
  getTenantId()
    .then(() => {
      getCandidateList();
    })
    .finally(() => {
      getUserList();
    });
});

/** 页面加载时 */
onActivated(async () => {
  getTenantId()
    .then(() => {
      getCandidateList();
    })
    .finally(() => {
      getUserList();
    });
});
</script>
<style scoped>
@use '@/assets/styles/pulsar.scss';
</style>
