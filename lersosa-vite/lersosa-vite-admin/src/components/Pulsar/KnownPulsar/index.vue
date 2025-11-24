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
      <div v-show="showSearch">
        <el-card shadow="hover" class="mb-[10px]">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="脉冲星名字" prop="name">
              <el-input v-model="queryParams.name" placeholder="请输入脉冲星名字" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="巡天项目名称" prop="survey">
              <el-input v-model="queryParams.survey" placeholder="请输入巡天项目名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item style="display: inline-block">
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
            <el-button v-hasPermi="['pulsar:find:add']" type="primary" plain icon="Plus" @click="handleAdd"> 新增 </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['pulsar:find:edit']" type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改 </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['pulsar:find:remove']" type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()"
              >删除
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['pulsar:find:export']" type="warning" plain icon="Download" @click="handleExport">导出 </el-button>
          </el-col>
          <right-toolbar v-model:show-search="showSearch" @query-table="getList"></right-toolbar>
        </el-row>
      </template>
      <el-empty v-if="total === 0" description="暂无数据" style="margin-top: 10%">
        <el-button v-hasPermi="['pulsar:find:list']" type="primary" class="submit" @click="getList">刷新页面 </el-button>
      </el-empty>
      <el-table v-else v-loading="loading" :data="pulsarList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column v-if="true" label="主键" align="center" prop="id" />
        <el-table-column label="脉冲星名字" align="center" prop="name" />
        <el-table-column label="脉冲星周期" align="center" prop="period" />
        <el-table-column label="脉冲星色散" align="center" prop="dispersionMeasure" />
        <el-table-column label="天空赤道坐标 - 赤经" align="center" prop="raDeg" />
        <el-table-column label="天空赤道坐标 - 赤纬" align="center" prop="decDeg" />
        <el-table-column label="天空银道坐标 - 银经" align="center" prop="galacticLongitude" />
        <el-table-column label="天空银道坐标 - 银纬" align="center" prop="galacticLatitude" />
        <el-table-column label="巡天项目名称" align="center" prop="survey" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button v-hasPermi="['pulsar:find:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button v-hasPermi="['pulsar:find:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>
    <!-- 添加或修改已知脉冲星对话框 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="500px" append-to-body>
      <el-form ref="pulsarFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="脉冲星名字" prop="name">
          <el-input v-model="form.name" placeholder="请输入脉冲星名字" />
        </el-form-item>
        <el-form-item label="脉冲星周期" prop="period">
          <el-input v-model="form.period" placeholder="请输入脉冲星周期" />
        </el-form-item>
        <el-form-item label="脉冲星色散" prop="dispersionMeasure">
          <el-input v-model="form.dispersionMeasure" placeholder="请输入脉冲星色散" />
        </el-form-item>
        <el-form-item label="天空赤道坐标 - 赤经" prop="raDeg">
          <el-input v-model="form.raDeg" placeholder="请输入天空赤道坐标 - 赤经" />
        </el-form-item>
        <el-form-item label="天空赤道坐标 - 赤纬" prop="decDeg">
          <el-input v-model="form.decDeg" placeholder="请输入天空赤道坐标 - 赤纬" />
        </el-form-item>
        <el-form-item label="天空银道坐标 - 银经" prop="galacticLongitude">
          <el-input v-model="form.galacticLongitude" placeholder="请输入天空银道坐标 - 银经" />
        </el-form-item>
        <el-form-item label="天空银道坐标 - 银纬" prop="galacticLatitude">
          <el-input v-model="form.galacticLatitude" placeholder="请输入天空银道坐标 - 银纬" />
        </el-form-item>
        <el-form-item label="巡天项目名称" prop="survey">
          <el-input v-model="form.survey" placeholder="请输入巡天项目名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Pulsar" lang="ts">
import { ref, onMounted, onActivated } from 'vue';
import { listPulsar, getPulsar, delPulsar, addPulsar, updatePulsar } from '@/api/pulsar/find';
import { PulsarVO, PulsarQuery, PulsarForm } from '@/api/pulsar/find/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const pulsarList = ref<PulsarVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const queryFormRef = ref<ElFormInstance>();
const pulsarFormRef = ref<ElFormInstance>();
const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: PulsarForm = {
  id: undefined,
  name: undefined,
  period: undefined,
  dispersionMeasure: undefined,
  raDeg: undefined,
  decDeg: undefined,
  galacticLongitude: undefined,
  galacticLatitude: undefined,
  survey: undefined
};
const data = reactive<PageData<PulsarForm, PulsarQuery>>({
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: undefined,
    period: undefined,
    dispersionMeasure: undefined,
    raDeg: undefined,
    decDeg: undefined,
    galacticLongitude: undefined,
    galacticLatitude: undefined,
    survey: undefined,
    params: {}
  },
  rules: {
    id: [{ required: true, message: '主键不能为空', trigger: 'blur' }],
    name: [{ required: true, message: '脉冲星名字不能为空', trigger: 'blur' }],
    period: [{ required: true, message: '脉冲星周期不能为空', trigger: 'blur' }],
    dispersionMeasure: [{ required: true, message: '脉冲星色散不能为空', trigger: 'blur' }],
    raDeg: [{ required: true, message: '天空赤道坐标 - 赤经不能为空', trigger: 'blur' }],
    decDeg: [{ required: true, message: '天空赤道坐标 - 赤纬不能为空', trigger: 'blur' }],
    galacticLongitude: [{ required: true, message: '天空银道坐标 - 银经不能为空', trigger: 'blur' }],
    galacticLatitude: [{ required: true, message: '天空银道坐标 - 银纬不能为空', trigger: 'blur' }],
    survey: [{ required: true, message: '巡天项目名称不能为空', trigger: 'blur' }]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询已知脉冲星列表 */
const getList = async () => {
  loading.value = true;
  const res = await listPulsar(queryParams.value);
  pulsarList.value = res.rows;
  total.value = res.total;
  loading.value = false;
};

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
};

/** 表单重置 */
const reset = () => {
  form.value = { ...initFormData };
  pulsarFormRef.value?.resetFields();
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
};

/** 多选框选中数据 */
const handleSelectionChange = (selection: PulsarVO[]) => {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = '添加已知脉冲星';
};

/** 修改按钮操作 */
const handleUpdate = async (row?: PulsarVO) => {
  reset();
  const _id = row?.id || ids.value[0];
  const res = await getPulsar(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = '修改已知脉冲星';
};

/** 提交按钮 */
const submitForm = () => {
  pulsarFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updatePulsar(form.value).finally(() => (buttonLoading.value = false));
      } else {
        await addPulsar(form.value).finally(() => (buttonLoading.value = false));
      }
      proxy?.$modal.msgSuccess('操作成功');
      dialog.visible = false;
      await getList();
    }
  });
};

/** 删除按钮操作 */
const handleDelete = async (row?: PulsarVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除已知脉冲星编号为"' + _ids + '"的数据项？').finally(() => (loading.value = false));
  await delPulsar(_ids);
  proxy?.$modal.msgSuccess('删除成功');
  await getList();
};

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download(
    'system/pulsar/export',
    {
      ...queryParams.value
    },
    `pulsar_${new Date().getTime()}.xlsx`
  );
};

/** 页面加载时 */
onMounted(() => {
  getList();
});

/** 页面激活时 */
onActivated(() => {
  getList();
});
</script>
