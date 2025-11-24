#  Copyright (c) 2025 Leyramu Group. All rights reserved.
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#
#  This project (Lersosa), including its source code, documentation, and any associated materials, is the intellectual property of Leyramu. No part of this software may be reproduced, distributed, or transmitted in any form or by any means, including photocopying, recording, or other electronic or mechanical methods, without the prior written permission of the copyright owner, Miraitowa_zcx, except in the case of brief quotations embodied in critical reviews and certain other noncommercial uses permitted by copyright law.
#
#  For inquiries related to licensing or usage outside the scope of this notice, please contact the copyright holder at 2038322151@qq.com.
#
#  The author disclaims all warranties, express or implied, including but not limited to the warranties of merchantability and fitness for a particular purpose. Under no circumstances shall the author be liable for any special, incidental, indirect, or consequential damages arising from the use of this software.
#
#  By using this project, users acknowledge and agree to abide by these terms and conditions.


from fastapi import UploadFile, File
from pydantic import BaseModel


# 脉冲星候选体匹配请求类
class PulsarMatcherRequest(BaseModel):
    """脉冲星候选体匹配请求数据模型
    Attributes:
        id: 候选体唯一标识符
        tenant_id: 租户/用户唯一标识符
        pfd_path: PFD文件存储路径（脉冲星候选体特征图文件）
        discoverer: 脉冲星发现者
    """
    id: int
    tenant_id: str
    pfd_path: str
    discoverer: str


# 脉冲星候选体打分请求类
class PulsarScoresRequest(BaseModel):
    """脉冲星候选体评分请求数据模型
        Attributes:
            tenant_id: 租户/用户唯一标识符
            file: 文件
            file_name: 候选体文件名
            file_url: 文件路径
        """
    tenant_id: str
    file: UploadFile = File(...)
    file_name: str
    file_url: str
