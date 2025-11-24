<template>
  <div class="app-container home">
    <div class="block text-center">
      <el-carousel height="400px" class="mb-[10px]">
        <el-carousel-item v-for="item in 4" :key="item">
          <img v-if="item === 1" src="@/assets/images/login-background.jpg" alt="Pulsar" class="carousel-image" />
          <img v-if="item === 2" src="@/assets/images/FastTianYan.jpg" alt="Pulsar" class="carousel-image" />
          <img v-if="item === 3" src="@/assets/images/Pulsar.jpg" alt="Pulsar" class="carousel-image" />
          <img v-if="item === 4" src="@/assets/images/spacePulsar.jpg" alt="Pulsar" class="carousel-image" />
        </el-carousel-item>
      </el-carousel>
    </div>
    <div style="margin-bottom: 10px">
      <el-card shadow="hover">
        <el-row>
          <el-col :span="2">
            <img src="@/assets/logo/logo.png" alt="logo" class="element" />
          </el-col>
          <el-col :span="3">
            <h2>巡星者系统</h2>
            <p>PulsarVision</p>
          </el-col>
          <el-col :span="1">
            <div class="split-line"></div>
          </el-col>
          <el-col :span="12">
            <p class="p_front">
              巡星者系统（PulsarVision）是一款基于射电天文大数据分析与模式识别技术的自动化脉冲星搜寻平台，其核心功能是通过多维度信号特征匹配算法对海量观测数据中的候选体进行高效筛选，以提升新脉冲星发现的概率与可靠性。系统整合了脉冲星辐射信号的时域、频域及时频联合域特征，结合已知脉冲星数据库（如ATNF
              Pulsar Catalogue）与理论模型构建动态匹配模板库，利用自适应机器学习算法实现候选体信号的快速分类与验证。
            </p>
          </el-col>
          <el-col :span="1">
            <div class="split-line"></div>
          </el-col>
          <el-col :span="5">
            <p><b>当前版本:</b> <span>v1.0.0</span></p>
            <p>
              <el-button type="primary" icon="Cloudy" plain @click="goTarget('https://github.com/Leyramu/Pulsar')">访问GitHub </el-button>
              <el-button type="primary" icon="Cloudy" plain @click="goTarget('https://github.com/Leyramu/Pulsar')">更新日志 </el-button>
            </p>
          </el-col>
        </el-row>
      </el-card>
    </div>
    <div style="margin-bottom: 10px">
      <el-card shadow="hover">
        <el-row>
          <el-col :span="6">
            <el-statistic title="脉冲星样本总数" :value="tPulsarValue" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="脉冲星正样本" :value="pulsarValue" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="脉冲星负样本" :value="rfiValue" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="未标记样本" :value="unmarkedValue" />
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6" :offset="6">
            <el-statistic title="已知脉冲星" :value="knownPulsarValue" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="新脉冲星" :value="newPulsarValue" />
          </el-col>
        </el-row>
      </el-card>
    </div>
    <div>
      <el-row>
        <el-col :span="11">
          <el-card shadow="hover">
            <div ref="mainChart" style="height: 400px"></div>
          </el-card>
        </el-col>
        <el-col :span="1">
          <div class="split-line"></div>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <div ref="sideChart" style="height: 400px"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    <div style="margin-top: 20px">
      <el-card shadow="hover" style="height: 500px; overflow-y: auto">
        <div ref="gradeChart" :style="{ minHeight: chartHeight, width: '100%' }"></div>
      </el-card>
    </div>
    <!--    暂时舍弃    -->
    <!--    <div style="margin-top: 20px">-->
    <!--      <el-row>-->
    <!--        &lt;!&ndash; 消息通知列表 &ndash;&gt;-->
    <!--        <el-col :span="11">-->
    <!--          <el-card shadow="hover">-->
    <!--            <template #header>-->
    <!--              <div class="card-header">-->
    <!--                <span>系统消息</span>-->
    <!--                &lt;!&ndash;                <el-button type="primary" text>更多</el-button>&ndash;&gt;-->
    <!--              </div>-->
    <!--            </template>-->
    <!--            <el-timeline>-->
    <!--              <el-timeline-item v-for="(message, index) in noticeList.slice(0, 10)" :key="index" :timestamp="message.createTime">-->
    <!--                {{ message.noticeTitle }}-->
    <!--              </el-timeline-item>-->
    <!--            </el-timeline>-->
    <!--          </el-card>-->
    <!--        </el-col>-->
    <!--        <el-col :span="1">-->
    <!--          <div class="split-line"></div>-->
    <!--        </el-col>-->
    <!--        &lt;!&ndash; 日历模块 &ndash;&gt;-->
    <!--        <el-col :span="12">-->
    <!--          <el-card shadow="hover">-->
    <!--            <template #header>-->
    <!--              <div class="card-header">-->
    <!--                <span>观测日历</span>-->
    <!--              </div>-->
    <!--            </template>-->
    <!--            <el-calendar>-->
    <!--              <template #date-cell="{ data }">-->
    <!--                <div :class="data.isSelected ? 'is-selected' : ''">-->
    <!--                  {{ data.day.split('-').slice(2).join('-') }}-->
    <!--                </div>-->
    <!--              </template>-->
    <!--            </el-calendar>-->
    <!--          </el-card>-->
    <!--        </el-col>-->
    <!--      </el-row>-->
    <!--    </div>-->
  </div>
</template>

<script setup name="Index" lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
import * as echarts from 'echarts';
import apiMark from '@/api/pulsar/mark/index';
import apiRecorder from '@/api/pulsar/recorder/index';
import apiFind from '@/api/pulsar/find/index';
import { listNotice } from '@/api/system/notice';
import { NoticeVO } from '@/api/system/notice/types';
import { useTransition } from '@vueuse/core';
import { userGrade } from '@/api/pulsar/mark/types';

const chartHeight = ref('400px');

const noticeList = ref<NoticeVO[]>([]);
const mainChart = ref(null);
const sideChart = ref(null);
const gradeChart = ref(null);
let mainInstance, sideInstance, gradeInstance;

const userEvaluateList = ref<userGrade[]>();

const goTarget = (url: string) => {
  window.open(url);
};
const tPulsarSource = ref(0);
const pulsarSource = ref(0);
const rfiSource = ref(0);
const unmarkedSource = ref(0);
const knownPulsarSource = ref(0);
const newPulsarSource = ref(0);
const rNewPulsarSource = ref(0);
const rFoundPulsarSource = ref(0);
const tPulsarValue = useTransition(tPulsarSource, {
  duration: 500
});
const pulsarValue = useTransition(pulsarSource, {
  duration: 500
});
const rfiValue = useTransition(rfiSource, {
  duration: 500
});
const unmarkedValue = useTransition(unmarkedSource, {
  duration: 500
});
const knownPulsarValue = useTransition(knownPulsarSource, {
  duration: 500
});
const newPulsarValue = useTransition(newPulsarSource, {
  duration: 500
});

const getPulsarSource = async () => {
  const TRes = await apiMark.chartE();
  tPulsarSource.value = TRes.data?.candidateTotal || 0;
  pulsarSource.value = TRes.data?.positiveTotal || 0;
  rfiSource.value = TRes.data?.negativeTotal || 0;
  unmarkedSource.value = TRes.data?.unlabeledTotal || 0;
  rNewPulsarSource.value = TRes.data?.newPulsarTotal || 0;
  rFoundPulsarSource.value = TRes.data?.foundPulsarTotal || 0;
  userEvaluateList.value = TRes.data?.userEvaluateList || [];

  const knownPulsarRes = await apiFind.listPulsar({
    pageNum: 1,
    pageSize: -1
  });
  knownPulsarSource.value = knownPulsarRes.rows?.length || 0;
  const newPulsarRes = await apiRecorder.listPulsar({
    pageNum: 1,
    pageSize: -1
  });
  newPulsarSource.value = newPulsarRes.rows.length;
};

const formatUserGradeData = (data: userGrade[]) => {
  if (!data || data.length === 0) {
    return [['人员', '分数', '标记数量', '检查数量']];
  }

  const basisData = (100 * data[0].markNum) / data[data.length - 1].markNum;
  const header = ['人员', '分数', '标记数量', '检查数量'];
  const rows = data.map((item) => [item.name, parseFloat((item.grade - data[0].grade + basisData).toFixed(2)), item.markNum, item.checkNum]);
  return [header, ...rows];
};

/** 响应式更新图表 */
const updateCharts = () => {
  const mainOption = {
    title: { text: '样本分布统计', left: 'center' },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['总样本', '正样本', '负样本', '未标记', '已编脉冲星样本', '未编脉冲星样本']
    },
    yAxis: { type: 'value' },
    series: [
      {
        data: [tPulsarSource.value, pulsarSource.value, rfiSource.value, unmarkedSource.value, rFoundPulsarSource.value, rNewPulsarSource.value],
        type: 'bar',
        itemStyle: {
          color: (params) => ['#188df0', '#52c41a', '#f5222d', '#8c8c8c', '#188df0', '#ff7f0e'][params.dataIndex]
        }
      }
    ]
  };

  const sideOption = {
    title: {
      text: '脉冲星分类统计',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    dataset: {
      source: [
        ['类型', '数量'],
        ['已知脉冲星', knownPulsarSource.value],
        ['新发现脉冲星', newPulsarSource.value]
      ]
    },
    xAxis: {
      type: 'category',
      axisLabel: {
        rotate: 0
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: true
      }
    },
    series: [
      {
        type: 'bar',
        encode: { x: '类型', y: '数量' },
        itemStyle: {
          color: (params) =>
            new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: params.name === '已知脉冲星' ? '#83bff6' : '#ffb248' },
              { offset: 1, color: params.name === '已知脉冲星' ? '#188df0' : '#ff7f0e' }
            ])
        },
        barWidth: '60%'
      }
    ],
    grid: {
      top: '20%',
      left: '10%',
      right: '10%',
      bottom: '15%'
    }
  };

  const gradeOption = {
    title: { text: '用户标注评分排行榜', left: 'center' },
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: {
      data: ['分数', '标记数量', '检查数量'],
      top: 40
    },
    dataset: {
      source: formatUserGradeData(userEvaluateList.value || [])
    },
    yAxis: {
      type: 'category',
      axisLine: { show: true },
      axisLabel: { rotate: 0, align: 'center', margin: 50 }
    },
    xAxis: [
      { type: 'value', name: '分数', position: 'bottom', offset: 0, axisLabel: { color: '#f0182a' } },
      { type: 'value', name: '标记数量', position: 'bottom', show: false },
      { type: 'value', name: '检查数量', position: 'bottom', show: false }
    ],
    series: [
      {
        name: '分数',
        type: 'bar',
        xAxisIndex: 0,
        encode: { x: 1, y: 0 },
        itemStyle: { color: '#FF4444' },
        barWidth: 20,
        label: { show: true, position: 'right' }
      },
      {
        name: '标记数量',
        type: 'bar',
        xAxisIndex: 1,
        encode: { x: 2, y: 0 },
        itemStyle: { color: '#4A90E2' },
        barWidth: 8,
        label: {
          show: true,
          position: 'right',
          formatter: (params) => params.value[2]
        }
      },
      {
        name: '检查数量',
        type: 'bar',
        xAxisIndex: 2,
        encode: { x: 3, y: 0 },
        itemStyle: { color: '#95A5A6' },
        barWidth: 8,
        label: {
          show: true,
          position: 'right',
          formatter: (params) => params.value[3]
        }
      }
    ]
  };

  const rowsCount = userEvaluateList.value?.length || 0;
  chartHeight.value = `${Math.max(400, rowsCount * 80)}px`;
  mainInstance.setOption(mainOption);
  sideInstance.setOption(sideOption);
  gradeInstance.setOption(gradeOption);
  gradeInstance.resize();
};

/** 获取系统公告列表 */
const getNoticeList = async () => {
  const res = await listNotice({
    pageNum: 1,
    pageSize: -1,
    noticeTitle: '',
    createByName: '',
    status: '',
    noticeType: ''
  });
  noticeList.value = res.rows;
};

function showLoading() {
    mainInstance.showLoading();
    sideInstance.showLoading();
    gradeInstance.showLoading();
}

// 隐藏加载动画
function hideLoading() {
    mainInstance.hideLoading();
    sideInstance.hideLoading();
    gradeInstance.hideLoading();
}

// 设置 ResizeObserver
function setupResizeListener() {
    const resizeObserver = new ResizeObserver(() => {
        setTimeout(() => {
            mainInstance?.resize();
            sideInstance?.resize();
            gradeInstance?.resize();
        }, 100);
    });

    if (mainChart.value) resizeObserver.observe(mainChart.value as HTMLElement);
    if (sideChart.value) resizeObserver.observe(sideChart.value as HTMLElement);
    if (gradeChart.value) resizeObserver.observe(gradeChart.value as HTMLElement);

    // 保存 observer 实例以便卸载时清理
    ;(window as any).__resizeObserver__ = resizeObserver;
}

onMounted(async () => {
    await nextTick(); // 确保 DOM 已经挂载

    // 初始化图表实例
    mainInstance = echarts.init(mainChart.value);
    sideInstance = echarts.init(sideChart.value);
    gradeInstance = echarts.init(gradeChart.value);

    // 显示加载状态
    showLoading();

    try {
        // 并行加载所有数据
        await Promise.all([getPulsarSource(), getNoticeList()]);

        // 数据加载完成，更新图表
        updateCharts();
    } catch (error) {
        console.error('数据加载失败', error);
    } finally {
        // 隐藏加载状态
        hideLoading();

        // 启动 ResizeObserver
        setupResizeListener();
    }
});

/** 销毁图表 */
onBeforeUnmount(() => {
  mainInstance.dispose();
  sideInstance.dispose();
  gradeInstance.dispose();
});
</script>

<style scoped lang="scss">
.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }

  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }

  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: 'open sans', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
}

.demonstration {
  color: var(--el-text-color-secondary);
}

.el-carousel__item h3 {
  color: #475669;
  opacity: 0.75;
  line-height: 150px;
  margin: 0;
  text-align: center;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
  background-color: #d3dce6;
}

.img-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center; /* 新增居中定位 */
  display: block;
}

.el-carousel__item {
  padding: 0 !important;
  overflow: hidden;
}

.split-line {
  width: 1px; /* 设置分割线的宽度 */
  height: 100%; /* 或者具体的高度值 */
  background-color: #ccc; /* 分割线的颜色 */
  margin-left: auto; /* 根据需要调整 */
  margin-right: auto; /* 根据需要调整 */
}

.p_front {
  font-size: 14px; /* 固定字体大小 */
}

.element {
  width: 80%; /* 宽度为父容器的80% */
  aspect-ratio: 8/8.3; /* 宽高比固定为8:7 */
  /* 或者直接使用小数 */
  /* aspect-ratio: 1.142; */
}

.parent-container {
  display: flex; /* 启用Flexbox布局 */
  flex-direction: row; /* 默认值，子元素沿水平方向从左到右排列 */
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.calendar-events {
  display: flex;
  justify-content: center;
  gap: 2px;
  margin-top: 2px;
}

.event-marker {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.is-selected {
  background-color: var(--el-color-primary-light-9);
}

/* 滚动条整体部分 */
.el-card ::-webkit-scrollbar {
  width: 10px; /* 滚动条宽度 */
}

/* 滚动条的拖动条部分 */
.el-card ::-webkit-scrollbar-thumb {
  border-radius: 5px;
  background-color: rgba(0, 0, 0, 0.3);
}

/* 滚动条的轨道部分 */
.el-card ::-webkit-scrollbar-track {
  border-radius: 5px;
  background-color: rgba(0, 0, 0, 0.1);
}
</style>
