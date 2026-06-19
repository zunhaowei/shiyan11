package com.example.auth.util;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;

/**
 * 敏感词过滤器 —— 基于 DFA（确定有限自动机）实现高效敏感词扫描与替换
 */
@Component
public class SensitiveWordFilter {

    /** 敏感词库（可配置扩展） */
    private static final Set<String> SENSITIVE_WORDS = new HashSet<>(Arrays.asList(
            "敏感词1", "敏感词2", "敏感词3", "敏感词4", "敏感词5",
            "暴力", "色情", "赌博", "毒品", "吸毒", "违法",
            "脏话1", "脏话2", "脏话3", "反动", "邪教"
    ));

    /** DFA 节点 */
    private static class TrieNode {
        /** 是否为敏感词结尾 */
        private boolean isEnd = false;
        /** 子节点映射 */
        private Map<Character, TrieNode> children = new HashMap<>();

        public boolean isEnd() {
            return isEnd;
        }

        public void setEnd(boolean end) {
            isEnd = end;
        }

        public Map<Character, TrieNode> getChildren() {
            return children;
        }
    }

    /** DFA 根节点 */
    private TrieNode rootNode;

    /**
     * 初始化 DFA 敏感词树
     */
    @PostConstruct
    public void init() {
        rootNode = new TrieNode();
        buildTrieTree();
    }

    /**
     * 构建 DFA 字典树
     */
    private void buildTrieTree() {
        for (String word : SENSITIVE_WORDS) {
            TrieNode currentNode = rootNode;
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                TrieNode child = currentNode.getChildren().get(c);
                if (child == null) {
                    child = new TrieNode();
                    currentNode.getChildren().put(c, child);
                }
                currentNode = child;
                // 如果是最后一个字符，标记为敏感词结尾
                if (i == word.length() - 1) {
                    currentNode.setEnd(true);
                }
            }
        }
    }

    /**
     * 检测文本中是否包含敏感词
     * @param text 待检测文本
     * @return 是否包含敏感词
     */
    public boolean containsSensitiveWord(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        for (int i = 0; i < text.length(); i++) {
            if (checkSensitiveWord(text, i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从指定位置开始检测敏感词
     * @param text 待检测文本
     * @param startIndex 起始位置
     * @return 是否检测到敏感词
     */
    private boolean checkSensitiveWord(String text, int startIndex) {
        TrieNode currentNode = rootNode;
        for (int i = startIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            currentNode = currentNode.getChildren().get(c);
            if (currentNode == null) {
                break;
            }
            if (currentNode.isEnd()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 过滤敏感词（用*替换）
     * @param text 待过滤文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder(text);
        int length = text.length();

        for (int i = 0; i < length; i++) {
            int endIndex = findSensitiveWordEnd(text, i);
            if (endIndex > i) {
                // 将敏感词替换为*
                for (int j = i; j < endIndex; j++) {
                    result.setCharAt(j, '*');
                }
                i = endIndex - 1; // 跳过已处理的部分
            }
        }

        return result.toString();
    }

    /**
     * 查找敏感词结束位置
     * @param text 待检测文本
     * @param startIndex 起始位置
     * @return 敏感词结束位置（不包含），未找到返回 startIndex
     */
    private int findSensitiveWordEnd(String text, int startIndex) {
        TrieNode currentNode = rootNode;
        int maxEndIndex = startIndex;

        for (int i = startIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            currentNode = currentNode.getChildren().get(c);
            if (currentNode == null) {
                break;
            }
            if (currentNode.isEnd()) {
                maxEndIndex = i + 1;
            }
        }

        return maxEndIndex;
    }

    /**
     * 获取敏感词数量
     * @return 敏感词库大小
     */
    public int getSensitiveWordCount() {
        return SENSITIVE_WORDS.size();
    }
}