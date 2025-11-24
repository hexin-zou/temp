/*
 * Copyright (c) 2023-2025 Leyramu Group. All rights reserved.
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

package leyramu.framework.lersosa.grpc.file.command;

import leyramu.framework.lersosa.common.core.exception.ServiceException;
import leyramu.framework.lersosa.resource.api.RemoteFileService;
import leyramu.framework.lersosa.resource.api.domain.RemoteFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 文件保存命令执行器.
 *
 * @author <a href="mailto:2038322151@qq.com">Miraitowa_zcx</a>
 * @version 2.1.0
 * @since 2025/3/7
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileSaveCmdExe {

    /**
     * 远程文件服务.
     */
    @DubboReference
    private final RemoteFileService remoteFileService;

    /**
     * 保存文件.
     *
     * @param pulsarFile 文件对象
     * @return 文件对象
     * @throws ServiceException 服务异常
     * @throws IOException      IO异常
     */
    public List<RemoteFile> save(MultipartFile pulsarFile) throws ServiceException, IOException {
        // 检查文件是否为ZIP格式
        if (isZipFile(pulsarFile)) {
            return handleZipFile(pulsarFile);
        } else {
            // 如果不是ZIP格式，直接上传
            return Collections.singletonList(remoteFileService.upload(
                pulsarFile.getName(),
                pulsarFile.getOriginalFilename(),
                pulsarFile.getContentType(),
                pulsarFile.getBytes()
            ));
        }
    }

    /**
     * 处理ZIP格式的文件.
     *
     * @param pulsarFile 文件对象
     * @return 文件对象列表
     * @throws IOException IO异常
     */
    private List<RemoteFile> handleZipFile(MultipartFile pulsarFile) throws IOException {
        List<RemoteFile> remoteFiles = new ArrayList<>();
        Path tempDir = null;

        try {
            // 创建临时目录用于解压
            tempDir = Files.createTempDirectory("unzipped");
            unzip(pulsarFile.getInputStream(), tempDir);

            // 遍历解压后的文件并上传
            File[] files = tempDir.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    remoteFiles.add(uploadFile(file));
                }
            }
        } finally {
            // 删除临时目录
            if (tempDir != null) {
                deleteDirectory(tempDir);
            }
        }

        return remoteFiles;
    }

    /**
     * 上传文件.
     *
     * @param file 文件对象
     * @return 文件对象
     * @throws IOException IO异常
     */
    private RemoteFile uploadFile(File file) throws IOException {
        return remoteFileService.upload(
            file.getName(),
            file.getName(),
            Files.probeContentType(file.toPath()),
            readAllBytesAsStream(file.toPath())
        );
    }

    /**
     * 读取文件字节流.
     *
     * @param path 文件路径
     * @return 文件字节流
     * @throws IOException IO异常
     */
    private byte[] readAllBytesAsStream(Path path) throws IOException {
        try (var inputStream = Files.newInputStream(path)) {
            return inputStream.readAllBytes();
        }
    }

    /**
     * 解压文件.
     *
     * @param inputStream   输入流
     * @param destDirectory 目标目录
     * @throws IOException IO异常
     */
    private void unzip(java.io.InputStream inputStream, Path destDirectory) throws IOException {
        try (var zipIn = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                Path filePath = sanitizeFilePath(destDirectory, entry.getName());
                if (!entry.isDirectory()) {
                    // 如果是文件，创建父目录并写入文件
                    Files.createDirectories(filePath.getParent());
                    Files.copy(zipIn, filePath);
                } else {
                    // 如果是目录，创建目录
                    Files.createDirectories(filePath);
                }
                zipIn.closeEntry();
            }
        }
    }

    /**
     * 验证文件路径是否有效.
     *
     * @param baseDir   基础目录
     * @param entryName 文件名
     * @return 文件路径
     */
    private Path sanitizeFilePath(Path baseDir, String entryName) {
        // 防止路径穿越攻击
        Path resolvedPath = baseDir.resolve(entryName).normalize();
        if (!resolvedPath.startsWith(baseDir)) {
            throw new IllegalArgumentException("无效的 zip 条目： " + entryName);
        }
        return resolvedPath;
    }

    /**
     * 删除临时目录.
     *
     * @param directory 目录
     */
    private void deleteDirectory(Path directory) {
        try (Stream<Path> stream = Files.walk(directory)) {
            stream.sorted((p1, p2) -> -p1.compareTo(p2))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        log.error("无法删除临时文件：{}", path, e);
                    }
                });
        } catch (IOException e) {
            log.error("无法删除临时目录：{}", directory, e);
        }
    }

    /**
     * 检查文件是否为ZIP格式.
     *
     * @param file 文件对象
     * @return 是否为ZIP格式
     */
    private boolean isZipFile(MultipartFile file) {
        return Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().endsWith(".zip");
    }
}
