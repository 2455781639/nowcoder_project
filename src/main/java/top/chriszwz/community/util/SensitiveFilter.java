package top.chriszwz.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/*
 * @Description: 过滤敏感词类
 * @Author: Chris(张文卓)
 * @Date: 2022/6/27 20:04
 */
@Component
public class SensitiveFilter {

    //实例化logger
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct//初始化方法，当容器实例化构造bean后，这个方法即被调用
    public void init(){
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");//加载敏感词库
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))
        ){
            String keyword;
            while ((keyword = reader.readLine()) != null){
                this.addKeyword(keyword);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //将一个敏感词添加到前缀树中
    private void addKeyword(String keyword){
        TrieNode node = rootNode;
        for (int i = 0; i < keyword.length(); i++){
            char c = keyword.charAt(i);
            TrieNode subNode = node.getSubNode(c);
            if (subNode == null){
                //如果没有子节点，则创建一个新的子节点
                subNode = new TrieNode();
                node.addSubNode(c, subNode);
            }
            //指向子节点
            node = subNode;

            //设置结束标识
            if (i == keyword.length() - 1){
                node.setKeywordEnd(true);
            }
        }
    }

    //过滤敏感词后的文本
    public String filter(String text){
        if(StringUtils.isBlank(text)){//判断文本是否为空
            return null;
        }
        //指针1
        TrieNode node = rootNode;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();
        while (position < text.length()){
            char c = text.charAt(position);
            //跳过符号
            if(isSymbol(c)){
                if(node == rootNode){
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            //检查下级节点
            node = node.getSubNode(c);
            if(node == null){
                // 以begin开头的不是敏感词
                sb.append(text.charAt(begin));
                position = ++begin;
                //重新指向根节点
                node = rootNode;
            }else if (node.isKeywordEnd()){
                //发现敏感词
                sb.append(REPLACEMENT);
                //进入下一个位置
                begin = ++position;
            }else{
                //检查下一个
                position++;
            }
        }
        //将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }

    //判断是否为符号
    private boolean isSymbol(char c){
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //前缀树
    private class TrieNode {

        //关键词结束标识
        private boolean isKeywordEnd = false;

        //子节点(key是下级字符， value是下级节点)
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子节点
        public void addSubNode(Character key, TrieNode node){
            if(subNodes == null){
                subNodes = new HashMap<>();
            }
            subNodes.put(key, node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
