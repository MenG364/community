package com.meng.community.util;

import com.alibaba.druid.sql.visitor.functions.Char;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: community
 * Created by MenG on 2022/5/23 16:49
 */

@Slf4j(topic = "SensitiveFilter.class")
@Component
public class SensitiveFilter {

    //替换符
    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    //当调用构造方法之后，该方法会自动调用
    @PostConstruct
    public void init() {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                //添加到前缀树
                this.addKeyword(keyword);
            }

        } catch (IOException e) {
            log.error("加载敏感词文件失败" + e.getMessage());

        }


    }

    //将一个敏感词添加到前缀树中
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null) {
                //初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            //指向子节点
            tempNode = subNode;

            //设置结束标识
            if (i == keyword.length() - 1) {
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    /***
     * 过滤敏感词
     * @param text 带过滤文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        //指针1
        TrieNode tempNode = rootNode;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;

        StringBuilder sb = new StringBuilder();

        while (begin < text.length()) {
            char c = text.charAt(position);
            //跳过符号
            if (isSymbol(c)){
                //若指针1处于根节点，将此字符计入结果，让指针2向下走一步
                if (tempNode==rootNode){
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头过中间，指针3向下走一步
                position++;
                continue;
            }

            //检测下级节点
            tempNode=tempNode.getSubNode(c);
            if (tempNode==null){
                //以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                ++begin;
                position=begin;
                //重新指向根节点
                tempNode=rootNode;
            }else if(tempNode.isKeyWordEnd()){
                //发现敏感词，将begin-position替换
                sb.append(REPLACEMENT);
                //进入下一个位置
                begin=++position;
                //重新指向根节点
                tempNode=rootNode;
            }else{
                //继续检测下一个字符
                if (position<text.length()-1){
                    position++;
                }else{
                    sb.append(text.charAt(begin));
                    position=++begin;
                    tempNode=rootNode;
                }

            }

        }
        return sb.toString();

    }

    //判断是否为符号
    private boolean isSymbol(Character c) {
        //c<0x2E80||c>0x9FFF 中亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }


    //定义前缀树
    private class TrieNode {
        //关键词结束标识
        private boolean isKeyWordEnd = false;

        //子节点(key是子节点字符，value是子节点)
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //添加子节点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }


}
