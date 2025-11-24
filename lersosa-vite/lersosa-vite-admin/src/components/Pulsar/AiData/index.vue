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
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="标记" prop="flag">
              <el-select v-model="queryParams.flag" placeholder="请选择标记" clearable @change="handleQuery">
                <el-option label="全部" value="" />
                <el-option label="未被标记" value="0" />
                <el-option label="正样本" value="1" />
                <el-option label="负样本" value="2" />
                <el-option label="已知脉冲星" value="3" />
                <el-option label="新脉冲星" value="4" />
              </el-select>
            </el-form-item>
            <el-form-item label="" prop="flagUser">
              <el-input v-model="queryParams.flagUser" placeholder="请输入第一标记人" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="" prop="flagUser">
              <el-input v-model="queryParams.reFlagUser" placeholder="请输入第二标记人" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </transition>

    <el-card shadow="hover">
      <template #header>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" @click="open = true"> 操作说明</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['pulsar:sign:importImg']" ref="fileData" type="success" plain icon="Upload" @click="handleFileSelect"
              >上传图片
            </el-button>
            <input ref="fileInput" type="file" style="display: none" @change="handleFile" />
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['pulsar:mark:export']" ref="exportData" type="primary" icon="Download" plain @click="handleAdd"
              >导出数据
            </el-button>
          </el-col>
          <div class="mt-3 flex gap-4 pl-4">
            <div v-for="item in colorLegends" :key="item.label" class="flex items-center">
              <div class="color-dot" :class="item.colorClass"></div>
              <span class="ml-2 text-sm">{{ item.label }}</span>
            </div>
          </div>
          <right-toolbar v-model:show-search="showSearch" @query-table="getCandidateList"></right-toolbar>
        </el-row>
      </template>

      <el-dialog v-model="exportDialog.visible" :title="exportDialog.title" width="500px" append-to-body>
        <el-form>
          <el-form-item label="第一标记人员">
            <el-select v-model="exportFrom.flagUser" placeholder="请选择第一标记人" clearable>
              <el-option label="全部" value="" />
              <el-option v-for="user in userList" :key="user" :label="user" :value="user" />
            </el-select>
          </el-form-item>
          <el-form-item label="第二标记人员">
            <el-select v-model="exportFrom.reFlagUser" placeholder="请选择第二标记人" clearable>
              <el-option label="全部" value="" />
              <el-option v-for="user in userList" :key="user" :label="user" :value="user" />
            </el-select>
          </el-form-item>
          <el-form-item label="第一标记时间">
            <el-date-picker
              v-model="exportFrom.queryFlagDate"
              type="date"
              placeholder="请选择第一标记时间"
              value-format="YYYY-MM-DD"
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="第二标记时间">
            <el-date-picker
              v-model="exportFrom.queryReFlagDate"
              type="date"
              placeholder="请选择第二标记时间"
              value-format="YYYY-MM-DD"
              style="width: 200px"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button :loading="buttonLoading" type="primary" @click="handleExport">确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
        </template>
      </el-dialog>

      <el-empty v-if="total === 0" description="暂无数据" style="margin-top: 10%">
        <el-button v-hasPermi="['pulsar:sign:list']" type="primary" class="submit" @click="getCandidateList"> 刷新页面 </el-button>
      </el-empty>
      <el-table v-else v-loading="loading" :data="candidateList">
        <el-table-column v-if="true" label="主键" align="center" prop="id" />
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
        <el-table-column label="AI打分" align="center" prop="score" />
        <el-table-column label="标记" align="center" prop="flag">
          <template #default="scope">
            <span v-if="scope.row.flag === POSITIVE">正样本</span>
            <span v-else-if="scope.row.flag === UNLABELED">未被标记</span>
            <span v-else-if="scope.row.flag === NEGATIVE">负样本</span>
            <span v-else-if="scope.row.flag === FOUND_PULSAR">已知脉冲星</span>
            <span v-else-if="scope.row.flag === NEW_PULSAR">新脉冲星</span>
            <span v-else>{{ scope.row.flag }}</span>
          </template>
        </el-table-column>
        <el-table-column label="标记人" align="center" prop="flagUser" />
        <el-table-column label="更正标记人" align="center" prop="reFlagUser" />
        <el-table-column label="标记时间" align="center" prop="flagDate" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.flagDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="更正标记时间" align="center" prop="reFlagDate" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.reFlagDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="检查" align="center" prop="check">
          <template #default="scope">
            <span v-if="scope.row.check === 0">未标记</span>
            <span v-else-if="scope.row.check === 1">未检查</span>
            <span v-else-if="scope.row.check === 2">已检查</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="删除" placement="top">
              <el-button v-hasPermi="['pulsar:sign:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        style="margin-top: 20px"
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        layout="prev, pager, next, jumper, ->, total"
        @current-change="getCandidateList"
      />
      <el-tour v-model="open">
        <el-tour-step :target="fileData?.$el" title="导入图片" description="导入候选体图片，文件类型为.png或者.zip">
          <el-card shadow="hover" class="mb-[10px]">
            <h2><strong>点击导入候选体图片</strong></h2>
            <h3>文件类型为.png或者.zip</h3>
          </el-card>
        </el-tour-step>
        <el-tour-step :target="exportData?.$el" title="导出标记情况" description="导出标记情况表格，等待时间可能较长，请耐心等待">
          <el-card shadow="hover" class="mb-[10px]">
            <h2><strong>点击导出标记情况表格</strong></h2>
            <h3>文件类型为.xlsx，导出表格命名中包含导出时间</h3>
            <h3>因数据过大，导出时间较长，请耐心等待</h3>
            <h3 style="color: red">如果表格无法打开，则说明无此人数据</h3>
          </el-card>
        </el-tour-step>
      </el-tour>
      <div>
        <PulsarImagePreview
          :isOpen="dialog"
          :candidateList="candidateList"
          :initImageIndex="initImageIndex"
          :is-allow-mark="false"
          :is-first-mark="false"
          @close="dialog = false"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import PulsarImagePreview from '@/components/PulsarImagePreview/index.vue';
import '@/assets/styles/pulsar.scss';
import api from '@/api/pulsar/mark';
import { getUserProfile, listUser } from '@/api/system/user';
import { CandidateForm, CandidateQuery, CandidateVo, FOUND_PULSAR, NEGATIVE, NEW_PULSAR, POSITIVE, UNLABELED } from '@/api/pulsar/mark/types';
import { parseTime } from '@/utils/lersosa';
import { getCurrentInstance } from 'vue';
import Cookies from 'js-cookie';
import { onActivated, onMounted, ref } from 'vue';
import { ButtonInstance } from 'element-plus';
import { UserQuery } from '@/api/system/user/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const fileInput = ref<HTMLInputElement | null>(null);
const candidateList = ref<CandidateVo[]>([]);
const ids = ref<Array<string | number>>([]);
const exportFrom = ref<CandidateForm>({});
const queryFormRef = ref<ElFormInstance>();
const fileData = ref<ButtonInstance>();
const exportData = ref<ButtonInstance>();
const userList = ref<string[]>([]);
const tenantId = ref<string>('');
const buttonLoading = ref(false);
const initImageIndex = ref(0);
const showSearch = ref(true);
const loading = ref(true);
const dialog = ref(false);
const open = ref(false);
const total = ref(0);
const reminderDays = 1;

const colorLegends = ref([
  { label: '未标记', colorClass: 'bg-unlabeled' },
  { label: '正样本', colorClass: 'bg-positive' },
  { label: '负样本', colorClass: 'bg-negative' },
  { label: '已知脉冲星', colorClass: 'bg-known' },
  { label: '新脉冲星', colorClass: 'bg-new' }
]);

const userQueryParams = ref<UserQuery>({
  pageNum: 1,
  pageSize: -1
});

const exportDialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const queryParams = ref<CandidateQuery>({
  pageNum: 1,
  pageSize: 20
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
 * 导出数据
 */
const handleExport = async () => {
  await proxy?.$modal.confirm('确定要导出数据吗？', '如果表格无法打开则无此人数据');
  buttonLoading.value = true;
  await api.exportData(exportFrom.value).then(() => {
    proxy?.$modal.msgSuccess('导出成功');
    buttonLoading.value = false;
  });
};

/**
 * 上传文件
 */
const handleFile = async (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (!target.files?.length) return;
  const file = target.files[0];
  const loadingInstance = ElLoading.service({
    lock: true,
    text: '文件上传中...',
    background: 'rgba(0, 0, 0, 0.7)'
  });
  if (!(file.type === 'application/png' || file.type !== 'application/zip')) {
    proxy?.$modal.msgError('请上传png或zip文件');
    loadingInstance.close();
    return;
  }
  api
    .importData(tenantId.value, file)
    .then((response) => {
      proxy?.$modal.msgSuccess(response.msg);
      console.log('Upload response:', response);
      setTimeout(getCandidateList, 3000);
    })
    .catch((err) => {
      console.error(err);
    })
    .finally(() => {
      loadingInstance.close();
      target.value = '';
    });
};

/**
 * 删除按钮操作
 */
const handleDelete = async (row?: CandidateVo) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除AI打分记录编号为"' + _ids + '"的数据项？').finally(() => (loading.value = false));
  api
    .delCandidate(_ids)
    .then(() => {
      proxy?.$modal.msgSuccess('删除成功');
      getCandidateList();
    })
    .catch((err) => {
      console.error(err);
    });
};

/**
 *  获取用户tenantId
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
  console.log(userList.value);
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

/** 选择文件 */
const handleFileSelect = () => {
  fileInput.value?.click();
};

/** 导出数据  */
const handleAdd = () => {
  exportDialog.visible = true;
  exportDialog.title = '导出数据';
};

/** 取消导出  */
const cancel = () => {
  exportDialog.visible = false;
};

/** 判断时间是否过期 */
const isTimeExpired = (lastTime: string) => {
  const lastDate = new Date(lastTime);
  const now = new Date();
  const diffHours = Math.abs(now.getTime() - lastDate.getTime()) / 36e5;
  return diffHours > reminderDays * 24;
};

/** 页面加载时 */
onMounted(() => {
  getTenantId();
  getUserList();
  getCandidateList();
  const lastReminder = Cookies.get('last_reminder');
  if (!lastReminder || isTimeExpired(lastReminder)) {
    nextTick(() => {
      open.value = true;
      // 设置新的Cookie（有效期30天）
      Cookies.set('last_reminder', new Date().toISOString(), {
        expires: reminderDays,
        sameSite: 'Lax'
      });
    });
  }
});

/** 页面激活时 */
onActivated(() => {
  getTenantId();
  getCandidateList();
});
</script>
<style scoped lang="scss">
@use '@/assets/styles/pulsar';
</style>
