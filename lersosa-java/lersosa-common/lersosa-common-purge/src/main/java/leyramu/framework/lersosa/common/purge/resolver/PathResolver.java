/*
 * Copyright (c) 2025 Leyramu Group. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This project (Lersosa), including its source code, documentation, and any associated materials, is the intellectual property of Leyramu. No part of this software may be reproduced, distributed, or transmitted in any form or by any means, including photocopying, recording, or other electronic or mechanical methods, without the prior written permission of the copyright owner, Miraitowa_zcx, except in the case of brief quotations embodied in critical reviews and certain other noncommercial uses permitted by copyright law.
 *
 * For inquiries related to licensing or usage outside the scope of this notice, please contact the copyright holder at 2038322151@qq.com.
 *
 * The author disclaims all warranties, express or implied, including but not limited to the warranties of merchantability and fitness for a particular purpose. Under no circumstances shall the author be liable for any special, incidental, indirect, or consequential damages arising from the use of this software.
 *
 * By using this project, users acknowledge and agree to abide by these terms and conditions.
 */

package leyramu.framework.lersosa.common.purge.resolver;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 路径解析器.
 *
 * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
 * @version 1.0.0
 * @since 2025/4/21
 */
@Slf4j
public final class PathResolver {

    /**
     * 解析类路径（支持@RestController等组合注解）.
     *
     * @param controllerClass 控制器类
     * @return 类路径
     */
    public static String resolveClassPath(Class<?> controllerClass) {
        // 查找所有层级注解（包括元注解）
        RequestMapping mapping = AnnotationUtils.findAnnotation(controllerClass, RequestMapping.class);
        if (mapping != null) {
            return extractFirstPath(mapping.value(), mapping.path());
        }

        // 检查其他可能包含路径的注解
        RestController restController = AnnotationUtils.findAnnotation(controllerClass, RestController.class);
        if (restController != null && StringUtils.isNotBlank(restController.value())) {
            return normalizePath(restController.value());
        }

        return "";
    }

    /**
     * 解析方法路径（支持所有HTTP方法注解）.
     *
     * @param method 方法
     * @return 方法路径
     */
    public static String resolveMethodPath(Method method) {
        // 查找所有可能的映射注解
        for (Class<? extends Annotation> annotationType : Arrays.asList(
            GetMapping.class, PostMapping.class, PutMapping.class,
            DeleteMapping.class, PatchMapping.class, RequestMapping.class)) {

            Annotation mapping = AnnotationUtils.findAnnotation(method, annotationType);
            if (mapping != null) {
                try {
                    Method valueMethod = mapping.getClass().getMethod("value");
                    Method pathMethod = mapping.getClass().getMethod("path");

                    String[] values = (String[]) valueMethod.invoke(mapping);
                    String[] paths = (String[]) pathMethod.invoke(mapping);

                    return extractFirstPath(values, paths);
                } catch (Exception e) {
                    log.error("反射获取路径失败", e);
                }
            }
        }
        return "";
    }

    /**
     * 提取第一个路径.
     *
     * @param values 值数组
     * @param paths  路径数组
     * @return 路径
     */
    public static String extractFirstPath(String[] values, String[] paths) {
        String[] candidates = paths.length > 0 ? paths : values;
        if (candidates.length == 0) {
            return "";
        }

        String path = candidates[0];
        return path.startsWith("/") ? path : "/" + path;
    }

    /**
     * 构建完整的路径.
     *
     * @param classPath  类路径
     * @param methodPath 方法路径
     * @return 完整路径
     */
    public static String buildFullPath(String classPath, String methodPath) {
        classPath = normalizePath(classPath);
        methodPath = normalizePath(methodPath);

        if (classPath.isEmpty()) {
            return methodPath;
        }
        if (methodPath.isEmpty()) {
            return classPath;
        }

        return classPath + "/" + methodPath + "/";
    }

    /**
     * 去除路径中的重复斜杠和尾部斜杠.
     *
     * @param path 路径
     * @return 去除重复斜杠和尾部斜杠后的路径
     */
    public static String normalizePath(String path) {
        if (StringUtils.isBlank(path)) {
            return "";
        }
        return path.replaceAll("^/+", "").replaceAll("/+$", "");
    }
}
