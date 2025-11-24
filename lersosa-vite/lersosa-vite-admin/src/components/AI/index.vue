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
  <div :style="styles.layout">
    <!-- 左部对话栏 --->
<!--    <div :style="styles.menu">-->
<!--      &lt;!&ndash; 🌟 Logo &ndash;&gt;-->
<!--      <div :style="styles.logo">-->
<!--        <img src="../../assets/logo/logo.png" draggable="false" alt="logo" :style="styles['logo-img']" />-->
<!--        <span :style="styles['logo-span']">Pulsar Vision Ai</span>-->
<!--      </div>-->
<!--      &lt;!&ndash; 🔥 新建会话 -&ndash;&gt;-->
<!--      <Button type="link" :style="styles.addBtn" @click="onAddConversation">-->
<!--        <PlusOutlined />-->
<!--        新建会话-->
<!--      </Button>-->
<!--      &lt;!&ndash; 🔥 会话列表 -&ndash;&gt;-->
<!--      <Conversations-->
<!--        :items="conversationsItems"-->
<!--        :style="styles.conversations"-->
<!--        :active-key="activeKey"-->
<!--        @active-change="onConversationClick"-->
<!--      ></Conversations>-->
<!--    </div>-->
    <!-- 右部聊天栏 --->
    <div :style="styles.chat">
      <!-- 🔥 聊天列表 --->
      <Bubble.List :items="items" :roles="roles" :style="styles.messages" />

      <!-- 🌟 提示词 -->
      <Prompts :items="senderPromptsItems" @item-click="onPromptsItemClick" />

      <Sender :value="content" :style="styles.sender" :loading="agentRequestLoading" @submit="onSubmit" @change="(value) => (content = value)">
        <template #prefix>
          <Badge :dot="attachedFiles.length > 0 && !headerOpen">
            <Button type="text" @click="() => (headerOpen = !headerOpen)">
              <template #icon>
                <PaperClipOutlined />
              </template>
            </Button>
          </Badge>
        </template>

        <template #header>
          <Sender.Header title="上传文件" :open="headerOpen" :styles="{ content: { padding: 0 } }" @open-change="(open) => (headerOpen = open)">
            <Attachments :before-upload="() => false" :items="attachedFiles" @change="handleFileChange">
              <template #placeholder="type">
                <Flex v-if="type && type.type === 'inline'" align="center" justify="center" vertical gap="2">
                  <Typography.Text style="font-size: 30px; line-height: 1">
                    <CloudUploadOutlined />
                  </Typography.Text>
                  <Typography.Title :level="5" style="margin: 0; font-size: 14px; line-height: 1.5"> 上传文件 </Typography.Title>
                  <Typography.Text type="secondary"> 单击或拖动文件到此区域进行上传</Typography.Text>
                </Flex>
                <Typography.Text v-if="type && type.type === 'drop'"> 将文件放到此处</Typography.Text>
              </template>
            </Attachments>
          </Sender.Header>
        </template>
      </Sender>
    </div>
  </div>
</template>
<script setup lang="ts">
import type { AttachmentsProps, BubbleListProps, ConversationsProps, PromptsProps } from 'ant-design-x-vue';
import {
  CloudUploadOutlined,
  CommentOutlined,
  EllipsisOutlined,
  FireOutlined,
  HeartOutlined,
  PaperClipOutlined,
  PlusOutlined,
  ReadOutlined,
  ShareAltOutlined,
  SmileOutlined
} from '@ant-design/icons-vue';
import { Badge, Button, Flex, Space, Typography, theme, Tooltip } from 'ant-design-vue';
import { Attachments, Bubble, Conversations, Prompts, Sender, useXAgent, useXChat, Welcome } from 'ant-design-x-vue';
import { computed, h, ref, type VNode, watch } from 'vue';
import MarkdownIt from 'markdown-it';
import api from '@/api/ai/qwen';



interface Message {
  id: string;
  message: string;
  role: 'local' | 'ai';
  status: 'local' | 'loading' | 'error' | 'ai';
}

const headerOpen = ref(false);
const attachedFiles = ref<AttachmentsProps['items']>([]);
const content = ref('');
const agentRequestLoading = ref(false);
const messages = ref<Message[]>([]);
const { token } = theme.useToken();

// 默认对话
const defaultConversationsItems = [
  {
    key: '0',
    label: '什么是Pulsar Ai?'
  }
];
const roles: BubbleListProps['roles'] = {
  ai: {
    placement: 'start',
    typing: { step: 5, interval: 20 },
    styles: {
      content: {
        borderRadius: '16px'
      }
    },
    messageRender: (message) => {
      const md = new MarkdownIt();
      return h('div', {
          class: 'markdown-content',
          innerHTML: md.render(message)
      });
    },
  },
  local: {
    placement: 'end',
    variant: 'shadow'
  }
};

// 欢迎与和提示符
const placeholderNode = computed(() =>
  h(Space, { direction: 'vertical', size: 16, style: styles.value.placeholder }, [
    h(Welcome, {
      variant: 'borderless',
      // icon: ""
      // icon: 'https://mdn.alipayobjects.com/huamei_iwk9zp/afts/img/A*s5sNRo5LjfQAAAAAAAAAAAAADgCCAQ/fmt.webp',
      title: '你好，我是Pulsar Ai',
      description: '基于Ai Agent，帮助管理脉冲星候选体',
      extra: h(Space, {}, [h(Button, { icon: h(ShareAltOutlined) }), h(Button, { icon: h(EllipsisOutlined) })])
    }),
    h(Prompts, {
      title: '你想干什么？',
      items: placeholderPromptsItems,
      styles: {
        list: {
          width: '200%'
        },
        item: {
          flex: 1
        }
      },
      onItemClick: onPromptsItemClick
    })
  ])
);

// 🔥对话气泡框
const items = computed<BubbleListProps['items']>(() => {
  if (messages.value.length === 0) {
    // 添加欢迎语和提示符
    return [{ content: placeholderNode, variant: 'borderless'}];
  }
  return messages.value.map(({ id, message, role, status }) => ({
    key: id,
    loading: status === 'loading',
    role: role === 'local' ? 'local' : 'ai',
    content: message
}));
});

const handleFileChange: AttachmentsProps['onChange'] = (info) => (attachedFiles.value = info.fileList);
const conversationsItems = ref(defaultConversationsItems);
const activeKey = ref(defaultConversationsItems[0].key);

// 添加会话
const onAddConversation = () => {
  conversationsItems.value = [
    ...conversationsItems.value,
    {
      key: `${conversationsItems.value.length}`,
      label: `New Conversation ${conversationsItems.value.length}`
    }
  ];
  activeKey.value = `${conversationsItems.value.length}`;
};

// 更改对话
const onConversationClick: ConversationsProps['onActiveChange'] = (key: string) => {
  activeKey.value = key;
  // TODO: 获取历史会话创建新的会话记录
};

function renderTitle(icon: VNode, title: string) {
  return h(Space, { align: 'start' }, [icon, h('span', title)]);
}

// 🔥欢迎提示符
const placeholderPromptsItems: PromptsProps['items'] = [
  {
    key: '1',
    label: renderTitle(h(FireOutlined, { style: { color: '#FF4D4F' } }), '热点'),
    description: '你对什么感兴趣？',
    children: [
      {
        key: '1-1',
        description: `Pulsar Ai 是干什么的？`
      },
      {
        key: '1-2',
        description: `什么是脉冲星？`
      },
      {
        key: '1-3',
        description: `候选体样本是怎么来的？`
      }
    ]
  },
  {
    key: '2',
    label: renderTitle(h(ReadOutlined, { style: { color: '#1890FF' } }), '建议'),
    description: '希望对你有用',
    children: [
      {
        key: '2-1',
        icon: h(HeartOutlined),
        description: `现在总共有多少正样本?`
      },
      {
        key: '2-2',
        icon: h(SmileOutlined),
        description: `还有多少数据未标记?`
      },
      {
        key: '2-3',
        icon: h(CommentOutlined),
        description: `整体数据的标记情况?`
      }
    ]
  }
];

// 🔥提示符
const senderPromptsItems: PromptsProps['items'] = [
  {
    key: '1',
    description: 'Pulsar Ai 是干什么的？',
    icon: h(FireOutlined, { style: { color: '#FF4D4F' } })
  },
  {
    key: '2',
    description: '整体数据的标记情况',
    icon: h(ReadOutlined, { style: { color: '#1890FF' } })
  }
];

const test = () => {
  console.log(attachedFiles.value[0].originFileObj);
  const file = attachedFiles.value[0].originFileObj;
  const url = URL.createObjectURL(file);
  const a = document.createElement('a');
  a.href = url;
  a.download = file.name;
  a.click();
  URL.revokeObjectURL(url);
};

// 🔥点击提示符
const onPromptsItemClick: PromptsProps['onItemClick'] = (info) => {
  content.value = info.data.description as string;
  onSubmit();
};

// 🔥发送消息
const onSubmit = () => {
  if (!content.value.trim()) return;

  // 添加用户消息
  messages.value.push({
    id: `${messages.value.length}`,
    role: 'local',
    message: content.value,
    status: 'local'
  });

  // 添加ai消息
  messages.value.push({
    id: `${messages.value.length}`,
    role: 'ai',
    message: '',
    status: 'loading'
  });
  agentRequestLoading.value = true;
  const eventSource = api.aiChatStream(content.value);
  content.value = '';
  eventSource.onerror = (event) => {
    messages.value[messages.value.length - 1].status = 'error';
    messages.value[messages.value.length - 1].message = '请求异常！';
    agentRequestLoading.value = false;
    eventSource.close();
    return;
  };

  eventSource.onmessage = (event) => {
    if (event.data === '[complete]') {
      eventSource.close();
      agentRequestLoading.value = false;
      return;
    }
    messages.value[messages.value.length - 1].message += event.data;
  };

  eventSource.onopen = (event) => {
    messages.value[messages.value.length - 1].status = 'ai';
  };

  content.value = '';
};

const styles = computed(() => {
  return {
    'layout': {
      'width': '100%',
      'min-width': '1000px',
      'height': '100hv',
      'border-radius': `${token.value.borderRadius}px`,
      'display': 'flex',
      'background': `${token.value.colorBgContainer}`,
      'font-family': `AlibabaPuHuiTi, ${token.value.fontFamily}, sans-serif`
    },
    'menu': {
      'background': `${token.value.colorBgLayout}80`,
      'width': '20%',
      'height': '89vh',
      'display': 'flex',
      'flex-direction': 'column'
    },
    'conversations': {
      'padding': '0 12px',
      'flex': 1,
      'overflow-y': 'auto'
    },
    'chat': {
      'height': '89vh',
      'width': '75%',
      'max-width': '100%',
      'margin': '0 auto',
      'box-sizing': 'border-box',
      'display': 'flex',
      'flex-direction': 'column',
      'padding': `${token.value.paddingLG}px`,
      'gap': '16px'
    },
    'messages': {
      flex: 1
    },
    'placeholder': {
      'padding-top': '32px',
      'text-align': 'left',
      'flex': 1
    },
    'sender': {
      'box-shadow': token.value.boxShadow,
      'margin-top': 'auto'
    },
    'logo': {
      'display': 'flex',
      'height': '72px',
      'align-items': 'center',
      'justify-content': 'start',
      'padding': '0 24px',
      'box-sizing': 'border-box'
    },
    'logo-img': {
      width: '24px',
      height: '24px',
      display: 'inline-block'
    },
    'logo-span': {
      'display': 'inline-block',
      'margin': '0 8px',
      'font-weight': 'bold',
      'color': token.value.colorText,
      'font-size': '16px'
    },
    'addBtn': {
      background: '#1677ff0f',
      border: '1px solid #1677ff34',
      width: 'calc(100% - 24px)',
      margin: '0 12px 24px 12px'
    }
  } as const;
});
</script>
<style scoped>
.toggle-btn {
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  width: 24px;
  height: 48px;
  cursor: pointer;
  z-index: 1000;
}

.markdown_content img{
    max-width: 100%;
    height: 80px;
    width: 120px;
    border-radius: 8px;
    margin: 10px 0;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

:deep(img) {
    max-width: 48%;
    height: auto;
    border-radius: 8px;
    margin: 10px 0;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>
