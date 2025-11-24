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

package leyramu.framework.lersosa.common.ai.factory;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * PDF资源处理工厂类.
 *
 * @author <a href="mailto:3267745251@qq.com">Flipped-yuye</a>
 * @version 1.0.0
 * @since 2025/05/22
 */
@Slf4j
@Component
public class PdfFontFactory extends PDFTextStripper {

    /**
     * 创建一个PDF字体工厂
     */
    public PdfFontFactory() {
        super();
    }

    /**
     * 重写writeString方法，去除特殊字符
     */
    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {

        StringBuilder cleanString = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            // 只保留基本多语言平面字符
            if (c >= 0x0020 || c == '\n' || c == '\r' || c == '\t' || c == '\f') {
                cleanString.append(c);
            }
        }
        super.writeString(cleanString.toString(), textPositions);
    }

    /**
     * 处理PDF文件
     *
     * @param resource PDF文件
     * @return List<Document>
     */
    public List<Document> handlePdf(Resource resource) {
        try (PDDocument pdfDocument = new PDFParser(new RandomAccessReadBuffer(resource.getInputStream())).parse()) {
            log.info("正在处理文件：{}", resource.getFilename());
            return IntStream.rangeClosed(0, pdfDocument.getNumberOfPages()).mapToObj(page -> {
                    this.setStartPage(page);
                    this.setEndPage(page);
                    try {
                        String text = this.getText(pdfDocument);
                        if (text == null || text.trim().isEmpty()) {
                            return null;
                        }
                        return (new Document(text));
                    } catch (IOException e) {
                        log.warn("解析第 {} 页失败", page, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("加载或解析 PDF 文件失败: {}", resource.getFilename(), e);
            throw new RuntimeException(e);
        }
    }
}
