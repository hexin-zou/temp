import request from '@/utils/request';
import { download } from '@/utils/request';
import { AxiosPromise } from 'axios';
import { CandidateVo, CandidateForm, CandidateQuery, CandidateMarkVo, ChartECo } from '@/api/pulsar/mark/types';

/**
 * 查询AI打分记录列表
 * @param query
 * @returns {*}
 */
export const candidateList = (query?: CandidateQuery): AxiosPromise<CandidateVo[]> => {
  return request({
    url: '/pulsar/sign/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询图片记录列表
 * @param query
 * @returns {*}
 */
export const candidateImgList = (query?: CandidateQuery): AxiosPromise<CandidateVo[]> => {
  return request({
    url: '/pulsar/sign/imgList',
    method: 'get',
    params: query
  });
};

/**
 * 标记AI打分记录
 * @param data
 */
export const candidateMark = (data: Array<CandidateMarkVo>): AxiosPromise => {
  return request({
    url: '/pulsar/sign/mark',
    method: 'post',
    data: data
  });
};

/**
 * 修改AI打分记录
 * @param data
 */
export const updateCandidate = (data: CandidateForm) => {
  return request({
    url: '/pulsar/sign',
    method: 'put',
    data: data
  });
};

/**
 * 删除AI打分记录
 * @param id
 */
export const delCandidate = (id: string | number | Array<string | number>) => {
  return request({
    url: '/pulsar/sign/' + id,
    method: 'delete'
  });
};

/** 匹配数据 */
export const matchData = (data: CandidateMarkVo[]) => {
  return request({
    url: '/python/pulsar/match',
    method: 'post',
    data: data
  });
};

/** 导入数据 */
export const importData = (tenantId: string | number, file: File): AxiosPromise<any> => {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('tenant_id', tenantId.toString()); // 改为表单字段
  return request({
    url: '/python/pulsar/scores',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

/** 导入数据 */
export const importFile = (file: File): AxiosPromise<any> => {
  const formData = new FormData();
  formData.append('file', file);
  return request({
    url: 'python/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

/** 导出数据 */
export const exportData = (data: CandidateForm): AxiosPromise<any> => {
    return download(
        '/pulsar/sign/export',
        data,
        `pulsar_${new Date().getTime()}.xlsx`
    )
};

/**
 * 获取图表数据
 * @returns
 */
export const chartE = (): AxiosPromise<ChartECo> => {
  return request({
    url: '/pulsar/sign/chartE',
    method: 'get'
  });
};

export default {
  candidateImgList,
  updateCandidate,
  candidateMark,
  candidateList,
  delCandidate,
  matchData,
  exportData,
  importData,
  importFile,
  chartE
};
