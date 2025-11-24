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


from typing import Any

import numpy as np


# 坐标转换工具类
class CoordinateConvertUtil:

    def __init__(self):
        pass

    @staticmethod
    def equatorial2galactic(RA: Any, Dec: Any) -> list[float]:
        """将赤道坐标系(RA, Dec)转换为银河系坐标系(l, b)
        Args:
            RA (bytes): 赤经坐标，需为字节字符串格式（例：b 'HH:MM:SS'）
            Dec (bytes): 赤纬坐标，需为字节字符串格式（例：b 'DD:MM:SS'）
        Returns:
            list[float]: 包含银河系坐标的二维列表 [银经l, 银纬b]，单位为度，保留3位小数
        Notes:
            - 使用标准天文坐标转换公式，将时分秒格式的赤道坐标转换为以弧度为单位的数值，
            - 再通过球面坐标系转换公式计算对应的银道坐标
        """
        strRA = RA.decode().split(':')
        hour = float(strRA[0])
        minute = float(strRA[1])
        second = float(strRA[2])
        RA = (hour + minute / 60.0 + second / 3600.0) * 15 * np.pi / 180

        strDec = Dec.decode().split(':')
        arcsec = float(strDec[2])
        arcmin = float(strDec[1])
        degree = float(strDec[0])
        Dec = (abs(degree) + arcmin / 60.0 + arcsec / 3600.0) * np.pi / 180

        if (degree < 0.0): Dec = -1.0 * Dec
        RAgp = 192.85948 * np.pi / 180.0
        Decgp = 27.12825 * np.pi / 180.0
        lcp = 122.932 * np.pi / 180.0
        sinb = np.sin(Dec) * np.sin(Decgp) + np.cos(Dec) * np.cos(Decgp) * np.cos(RA - RAgp)
        cosbsinlcp_l = np.cos(Dec) * np.sin(RA - RAgp)
        cosbcoslcp_l = np.sin(Dec) * np.cos(Decgp) - np.cos(Dec) * np.sin(Decgp) * np.cos(RA - RAgp)
        b = np.arcsin(sinb)
        sinlcp_l = cosbsinlcp_l / np.cos(b)
        coslcp_l = cosbcoslcp_l / np.cos(b)
        if (sinlcp_l > 0):
            lcp_l = np.arccos(coslcp_l)
        else:
            if (coslcp_l < 0):
                lcp_l = 2 * np.pi - np.arccos(coslcp_l)
            else:
                lcp_l = np.arcsin(sinlcp_l)
        l = lcp - lcp_l
        if (l < 0): l = 2 * np.pi + l

        l = l * 180 / np.pi
        b = b * 180 / np.pi
        return [round(l, 3), round(b, 3)]
