/*
 * Copyright (c) 2025 Leyramu. All rights reserved.
 * This project (Lersosa), including its source code, documentation, and any associated materials, is the intellectual property of Leyramu. No part of this software may be reproduced, distributed, or transmitted in any form or by any means, including photocopying, recording, or other electronic or mechanical methods, without the prior written permission of the copyright owner, Miraitowa_zcx, except in the case of brief quotations embodied in critical reviews and certain other noncommercial uses permitted by copyright law.
 * For inquiries related to licensing or usage outside the scope of this notice, please contact the copyright holder at 2038322151@qq.com.
 * The author disclaims all warranties, express or implied, including but not limited to the warranties of merchantability and fitness for a particular purpose. Under no circumstances shall the author be liable for any special, incidental, indirect, or consequential damages arising from the use of this software.
 * By using this project, users acknowledge and agree to abide by these terms and conditions.
 */

export interface CandidateVo {
  /**
   * 主键
   */
  id: number;

  /**
   * 租户 ID.
   */
  tenantId: string;

  /**
   * 图片绝度路径
   */
  fileName: string;

  /**
   * 文件路径
   */
  fileUrl: string;

  /**
   * 打分平方和
   */
  score: number;

  /**
   * pics打分
   */
  scorePics: number;

  /**
   * resnet打分
   */
  scoreResnet: number;

  /**
   * ensem打分
   */
  scoreEnsem: number;

  /**
   * 标记
   */
  flag: number;

  /**
   * 标记人
   */
  flagUser: string;

  /**
   * 更正标记人
   */
  reFlagUser: string;

  /**
   * 标记时间
   */
  flagDate: Date;

  /**
   * 更正标记时间
   */
  reFlagDate: Date;

  /**
   * 检查
   */
  check: number;

  /**
   * 读写锁
   */
  locker: number;
}

export interface CandidateForm extends BaseEntity {
  /**
   * 主键
   */
  id?: number;

  /**
   * 租户 ID.
   */
  tenantId?: string;

  /**
   * 图片绝度路径
   */
  file?: string;

  /**
   * 图片url
   */
  pngUrl?: string;

  /**
   * pfd url
   */
  pfdUrl?: string;

  /**
   * 打分平方和
   */
  score?: number;

  /**
   * pics打分
   */
  scorePics?: number;

  /**
   * resnet打分
   */
  scoreResnet?: number;

  /**
   * ensem打分
   */
  scoreEnsem?: number;

  /**
   * 标记
   */
  flag?: number;

  /**
   * 标记人
   */
  flagUser?: string;

  /**
   * 更正标记人
   */
  reFlagUser?: string;

  /**
   * 标记时间
   */
  queryFlagDate?: Date;

  /**
   * 更正标记时间
   */
  queryReFlagDate?: Date;

  /**
   * 检查
   */
  check?: number;

  /**
   * 读写锁
   */
  locker?: number;
}

export interface CandidateQuery extends PageQuery {
  /**
   * 图片绝度路径
   */
  fileName?: string;

  /**
   * 图片url
   */
  pngUrl?: string;

  /**
   * pfd url
   */
  pfdUrl?: string;

  /**
   * 打分平方和
   */
  score?: number;

  /**
   * pics打分
   */
  scorePics?: number;

  /**
   * resnet打分
   */
  scoreResnet?: number;

  /**
   * ensem打分
   */
  scoreEnsem?: number;

  /**
   * 标记
   */
  flag?: number;

  /**
   * 标记人
   */
  flagUser?: string;

  /**
   * 更正标记人
   */
  reFlagUser?: string;

  /**
   * 标记时间
   */
  flagDate?: Date;

  /**
   * 更正标记时间
   */
  reFlagDate?: Date;

  /**
   * 检查
   */
  check?: number;

  /**
   * 读写锁
   */
  locker?: number;

  /**
   * 日期范围参数
   */
  params?: any;
}

export interface CandidateMarkVo extends BaseEntity {
  /**
   * 标记
   */
  flag?: number;

  /**
   * 标记人
   */
  flagUser?: string;

  /**
   * 标记时间
   */
  flagDate?: Date;

  /**
   * 更正标记人
   */
  reFlagUser?: string;

  /**
   * 更正标记时间
   */
  reFlagDate?: Date;

  /**
   * 标记状态
   */
  check?: number;
}

export interface CandidateMatchVo extends BaseEntity {
  /**
   * id
   */
  id?: number;

  /**
   * 租户id
   */
  tenant_id?: string;

  /**
   * url(pfd)
   */
  url?: string;

  /**
   * 发现者
   */
  discoverer?: string;
}

/**
 * 用户评价
 */
export interface userGrade {
  /**
   * 用户名
   */
  name: string;
  /**
   * 评分
   */
  grade: number;
  /**
   * 标记次数
   */
  markNum: number;
  /**
   * 检查次数
   */
  checkNum: number;
}

/**
 * 统计图表
 */
export interface ChartECo extends BaseEntity {
  /**
   * 统计排名
   */
  userEvaluateList: userGrade[];
  /**
   * 总数
   */
  candidateTotal: number;
  /**
   * 正数总数
   */
  positiveTotal: number;
  /**
   * 负数总数
   */
  negativeTotal: number;
  /**
   * 未标记总数
   */
  unlabeledTotal: number;
  /**
   * 新增总数
   */
  newPulsarTotal: number;
  /**
   * 已知总数
   */
  foundPulsarTotal: number;
}

export const FLAG_STATUS = {
    UNLABELED: 0,
    POSITIVE:  1,
    NEGATIVE: 2,
    FOUND_PULSAR: 3,
    NEW_PULSAR: 4,
} as const;

// 解构并导出
export const {
    UNLABELED,
    POSITIVE,
    NEGATIVE,
    FOUND_PULSAR,
    NEW_PULSAR
} = FLAG_STATUS;

