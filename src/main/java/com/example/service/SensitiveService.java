package com.example.service;

import com.example.aspect.LogAspect;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.chain.commands.ExceptionCatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import sun.text.normalizer.Trie;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        try{
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null){
                addWord(lineTxt.trim());

            }
            bufferedReader.close();
        }catch(Exception e){
            logger.error("读取敏感词文件失败" + e.getMessage());
        }

    }
    //添加敏感词
    private void addWord(String lineText){
        TrieNode tempNode = rootNode;
        for (int i = 0; i < lineText.length();i++){
            Character c = lineText.charAt(i);
            if (isSymbol(c)){
                continue;
            }
            TrieNode node = tempNode.getSubNode(c);
            if (node == null){
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }
            tempNode = node;

            if (i == lineText.length() - 1){
                tempNode.setKeywordEnd(true);
            }
        }
    }
    private class TrieNode {

        private boolean end = false;

        private Map<Character, TrieNode> subNodes = new HashMap<>();
        public void addSubNode(Character key, TrieNode node){
            subNodes.put(key, node);
        }

        TrieNode getSubNode(Character key){
            return subNodes.get(key);
        }

        boolean isKeywordEnd(){
            return end;
        }
        void setKeywordEnd(boolean aEnd){
            end = aEnd;
        }
    }

    public String filter(String text){

        if (StringUtils.isBlank(text)){
            return text;
        }
        StringBuilder sb = new StringBuilder();

        String replacement = "***";

        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;

        while (position < text.length()){
            char c = text.charAt(position);
            if (isSymbol(c)){
                if (tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            tempNode = tempNode.getSubNode(c);

            if (tempNode == null){
                sb.append(text.charAt(begin));
                position = begin + 1;
                begin = position;
                tempNode = rootNode;

            }else if (tempNode.isKeywordEnd()){
                sb.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            }else {
                position++;
            }

        }
        sb.append(text.substring(begin));
        return sb.toString();
    }

    private boolean isSymbol(char c){
        int ic = (int) c;
        //东亚文字0x2E80-0x9FFF

        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }
    private TrieNode rootNode = new TrieNode();


    public static void main(String[] args){
        SensitiveService sensitiveService = new SensitiveService();
        sensitiveService.addWord("赌博");
        sensitiveService.addWord("色情");
        System.out.println(sensitiveService.filter("你好色 情"));
    }

}
