package com.example.compiler.controller;


import com.example.compiler.entity.gui.GuiNode;
import com.example.compiler.entity.gui.Text;
import com.example.compiler.entity.token.Token;
import com.example.compiler.entity.wrong.LogLevel;
import com.example.compiler.entity.wrong.WrongMessage;
import com.example.compiler.lexer.Lexer;
import com.example.compiler.llParser.LLParser;
import com.example.compiler.llParser.LLUtil;
import com.example.compiler.semantic.SemanticAnalyzer;
import javafx.util.Pair;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin()
public class controller {
    /**
     * @Title : getLexer
     * @Description : Get result from lexer
     * @Param: Text text (The input code from user)
     * @Return : String res (Word segmentation results)
     * @Throws : None
     */
    @PostMapping("/lexer")
    public String getLexer(@RequestBody(required = false) Text text) {
        if (text == null || text.getSource().equals("") || text.getSource() == null)
            return "您此时的输入为空";
        String input = text.getSource();
        System.out.println(input);
        Lexer lexer = new Lexer();
        lexer.tokenize(input);
        List<Token> tokens = lexer.getFilteredTokens();
        StringBuilder res = new StringBuilder();
        for (Token token : tokens) {
            res.append(token);
            res.append("\n");
        }
        System.out.println(res);
        return res.toString();
    }

    @PostMapping("/LogLevels")
    public List<LogLevel> GetWrongMessage(@RequestBody Text text) {
        String input = text.getSource();
        LLParser llParser = new LLParser(input);
        return llParser.getLogLevels();
    }

    /**
     * @Title : getGrammerResult
     * @Description : start the grammar parser
     * @Param : Text text (The input code from user)
     * @Return : The success message
     * @Throws : None
     */
    @PostMapping("/grammar")
    public String getGrammerResult(@RequestBody Text text) {
        System.out.println(text.getSource());
        if (text.getSource().equals("") || text.getSource() == null)
            return "您此时的输入为空";
        String input = text.getSource();
        System.out.println(input);
        Lexer lexer = new Lexer();
        System.out.println("--------语法开始 ------");
        LLParser llParser = new LLParser(input);
        StringBuilder res = new StringBuilder();
        if (llParser.getWrongList().size() == 0) {
            List<String> a = llParser.getSyntaxTree().dfs();
            for (String s : a) {
                res.append(s);
                res.append("\n");
            }
            return res.toString();
        } else {
            res.append("日志格式[行=列=错误信息]").append("\n");
            for (Map.Entry<Pair<Integer, Integer>, WrongMessage> entry : llParser.getWrongList().entrySet()) {
                Pair<Integer, Integer> resultPair = entry.getKey();
                int row = resultPair.getKey();
                int col = resultPair.getValue();
                System.out.println("行：" + row + "， 列：" + col + "，此处的字符串\"" + entry.getValue().getTokenContent() + "\"  附近或许存在错误，提示：" +
                        entry.getValue().getErrorCode().getMessage());
                res.append("行：").append(row).append("， 列：").append(col).append("，此处的字符串\"").append(entry.getValue().getTokenContent()).append("\"  附近或许存在错误，提示：").append(entry.getValue().getErrorCode().getMessage());
                res.append("\n");
            }
            return res.toString();
        }
    }

    /**
     * @Title : getGrammerTree
     * @Description : Get the Tree-Style grammar parser tree
     * @Param : Text text (The input code from user)
     * @Return : GuiNode root (Return the root node)
     * @Throws : None
     */
    @PostMapping("/grammerTree")
    public GuiNode getGrammerTree(@RequestBody Text text) {
        System.out.println(text.getSource());
        if (text.getSource().equals("") || text.getSource() == null)
            return new GuiNode("-1", "输入为空");
        String input = text.getSource();
        System.out.println(input);
        System.out.println("--------语法开始 ------");
        LLParser llParser = new LLParser(input);
        System.out.println("-----------guinode---------");
        System.out.println("-----------guinode---------:   " + llParser.printGuiNode().getChildren().get(0).getId());
//        return llParser.getSyntaxTree().getRoot();
        return llParser.printGuiNode();
    }

    /**
     * @Title : getFirstSet
     * @Description : Get all the NonTerminals' first-set at grammar parser stage
     * @Param : None
     * @Return : List<String> res (Return the firstSet in List-Style)
     * @Throws : None
     */
    @GetMapping("/FirstSet")
    public List<String> getFirstSet() {
        LLUtil llUtil = new LLUtil();
        return llUtil.printFirstSet();
    }

    /**
     * @Title : getFollowSet
     * @Description : Get all the NonTerminals' follow-set at grammar parser stage
     * @Param : None
     * @Return : List<String> res (Return the followSet in List-Style)
     * @Throws : None
     */
    @GetMapping("/FollowSet")
    public List<String> getFollowSet() {
        LLUtil llUtil = new LLUtil();
        return llUtil.printFollowSet();
    }

    /**
     * @Title : getFinalTable
     * @Description : The user Click the button to get the grammar parser Table
     * @Param : None
     * @Return : String finalTable (Return the grammar parser table)
     * @Throws : None
     */
    @GetMapping("/finalTable")
    public String getFinalTable() {
        LLUtil llUtil = new LLUtil();
        String[][] finalTable = llUtil.printParsingTable();
        finalTable[0][0] = "VT/T";
        System.out.println(Arrays.deepToString(finalTable));
        return Arrays.deepToString(finalTable);
    }

    /**
     * @Title : getSemanticResult
     * @Description : Get final result
     * @Param : None
     * @Return : List<String> res (Return the followSet in List-Style)
     * @Throws : None
     */
    @PostMapping("/semanticResult")
    public String getSemanticResult(@RequestBody Text text) throws Exception {
        if (text.getSource().equals("") || text.getSource() == null)
            return "您此时的输入为空";
        String input = text.getSource();
        System.out.println(input);
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(input);
        return semanticAnalyzer.toString();
    }

    @PostMapping("/intermediateCode")
    public String getIntermediateCode(@RequestBody Text text) throws Exception {
        if (text.getSource().equals("") || text.getSource() == null)
            return "您此时的输入为空";
        String input = text.getSource();
        System.out.println(input);
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(input);
        System.out.println("****");
        List<String> resList = semanticAnalyzer.getIntermediateCodeList();
        StringBuilder res = new StringBuilder();
        for (String s : resList) {
            res.append(s);
            res.append("\n");
        }
        return res.toString();
    }

}
