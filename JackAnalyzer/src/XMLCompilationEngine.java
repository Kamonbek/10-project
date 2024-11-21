import myenum.TokenType;
import utils.StringUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static myenum.StringEnum.*;

public class XMLCompilationEngine implements ICompilationEngine {
    private BufferedWriter bw;
    private JackTokenizer jackTokenizer;

    public XMLCompilationEngine(JackTokenizer jackTokenizer, String test) {
        String outputPath = jackTokenizer.getFilePath();
        try {
            outputPath = outputPath.replace(".jack", "T.xml");
            bw = new BufferedWriter(new FileWriter(outputPath));
            write("<tokens>");
            this.jackTokenizer = jackTokenizer;
            jackTokenizer.advance();
            while (jackTokenizer.hasMoreTokens()) {
                eat(jackTokenizer.tokenType());
            }
            write("</tokens>");
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public XMLCompilationEngine(JackTokenizer jackTokenizer) {
        String outputPath = jackTokenizer.getFilePath();
        try {
            outputPath = outputPath.replace(".jack", ".xml");
            bw = new BufferedWriter(new FileWriter(outputPath));
            this.jackTokenizer = jackTokenizer;
            while (jackTokenizer.hasMoreTokens()) {
                jackTokenizer.advance();
                compileClass();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void compileClass() {
        // TODO: Implement compileClass method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <class> tag.
        // 2. Use eat() to consume the 'class' keyword.
        // 3. Use eat() to consume the class name (identifier).
        // 4. Use eat() to consume the opening '{' symbol.
        // 5. Use a while loop to compile class variable declarations (compileClassVarDec()).
        // 6. Use a while loop to compile subroutine declarations (compileSubroutine()).
        // 7. Use eat() to consume the closing '}' symbol.
        // 8. Write the closing </class> tag.
        write("<class>");
        eat("class");
        eat(TokenType.IDENTIFIER);
        eat("{");
        while (jackTokenizer.isClassVarType()){
            compileClassVarDec();
        }
        while (jackTokenizer.isFunKeyword()){
            compileSubroutine();
        }
        eat("}");
        write("</class>");
    }

    @Override
    public void compileClassVarDec() {
        // TODO: Implement compileClassVarDec method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <classVarDec> tag.
        // 2. Use eat() to consume the keyword (static or field).
        // 3. Call compileType() to handle the type.
        // 4. Use eat() to consume the variable name (identifier).
        // 5. Use a while loop to handle multiple variable names separated by commas.
        // 6. Use eat() to consume the closing ';' symbol.
        // 7. Write the closing </classVarDec> tag.
        write("<classVarDec>");
        eat(TokenType.KEYWORD); // static or field
        compileType();
        eat(TokenType.IDENTIFIER); // variable name
        while (jackTokenizer.tokenType() == TokenType.SYMBOL && jackTokenizer.getThisToken().equals(",")) {
            eat(",");
            eat(TokenType.IDENTIFIER); // additional variable name
        }
        eat(";");
        write("</classVarDec>");
    }

    @Override
    public void compileVarDec() {
        // TODO: Implement compileVarDec method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <varDec> tag.
        // 2. Use eat() to consume the 'var' keyword.
        // 3. Call compileType() to handle the type.
        // 4. Use eat() to consume the variable name (identifier).
        // 5. Use a while loop to handle multiple variable names separated by commas.
        // 6. Use eat() to consume the closing ';' symbol.
        // 7. Write the closing </varDec> tag.
        write("<varDec>");
        eat("var");
        compileType();
        eat(TokenType.IDENTIFIER); // variable name
        while (jackTokenizer.tokenType() == TokenType.SYMBOL && jackTokenizer.getThisToken().equals(",")) {
            eat(",");
            eat(TokenType.IDENTIFIER); // additional variable name
        }
        eat(";");
        write("</varDec>");
    }


    @Override
    public String compileType() {
        // Check if the current token is a primitive type (int, char, boolean), an identifier (class name), or 'void'.
        if (jackTokenizer.isPrimitiveType() || jackTokenizer.tokenType() == TokenType.IDENTIFIER || jackTokenizer.getThisToken().equals("void")) {
            String type = jackTokenizer.getThisToken();
            eat(jackTokenizer.tokenType());
            return type;
        } else {
            throw new RuntimeException("Invalid type: " + jackTokenizer.getThisToken());
        }
    }

    @Override
    public void compileSubroutine() {
        // TODO: Implement compileSubroutine method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <subroutineDec> tag.
        write("<subroutineDec>");
        // 2. Use eat() to consume the subroutine keyword (constructor, function, method).
        eat(jackTokenizer.tokenType());
        // 3. Call compileType() to handle the return type or use eat() to consume 'void'.
        compileType();
        // 4. Use eat() to consume the subroutine name (identifier).
        eat(TokenType.IDENTIFIER);
        // 5. Use eat() to consume the opening '(' symbol.
        eat("(");
        // 6. Call compileParameterList() to handle the parameter list.
        compileParameterList();
        // 7. Use eat() to consume the closing ')' symbol.
        eat(")");
        // 8. Call compileSubroutineBody() to handle the subroutine body.
        compileSubroutineBody();
        // 9. Write the closing </subroutineDec> tag.
        write("</subroutineDec>");
    }

    @Override
    public void compileParameterList() {
        // TODO: Implement compileParameterList method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <parameterList> tag.
        write("<parameterList>");
        // 2. Use a while loop to handle multiple parameters separated by commas.
        while (jackTokenizer.tokenType() != TokenType.SYMBOL || !jackTokenizer.getThisToken().equals(")")) {
            // 3. Call compileType() to handle the parameter type.
            compileType();
            // 4. Use eat() to consume the parameter name (identifier).
            eat(TokenType.IDENTIFIER);
            // 5. If the next token is ',', use eat() to consume it.
            if (jackTokenizer.tokenType() == TokenType.SYMBOL && jackTokenizer.getThisToken().equals(",")) {
                eat(",");
            }
        }
        // 3. For each parameter, call compileType() and use eat() to consume the parameter name (identifier).
        // 4. Write the closing </>parameterList tag.
        write("</parameterList>");
    }


    @Override
    public void compileSubroutineBody() {
        // TODO: Implement compileSubroutineBody method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <subroutineBody> tag.
        write("<subroutineBody>");
        // 2. Use eat() to consume the opening '{' symbol.
        eat("{");
        // 3. Use a while loop to handle variable declarations (compileVarDec()).
        while (jackTokenizer.tokenType() == TokenType.KEYWORD && jackTokenizer.getThisToken().equals("var")) {
            compileVarDec();
        }
        // 4. Call compileStatements() to handle the statements.
        compileStatements();
        // 5. Use eat() to consume the closing '}' symbol.
        eat("}");
        // 6. Write the closing </subroutineBody> tag.
        write("</subroutineBody>");
    }


    @Override
    public void compileStatements() {
        // TODO: Implement compileStatements method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <statements> tag.
        write("<statements>");
        // 2. Use a while loop to handle different types of statements (let, if, while, do, return).
        while (jackTokenizer.tokenType() == TokenType.KEYWORD) {
            switch (jackTokenizer.getThisToken()) {
                case "let":
                    compileLet();
                    break;
                case "if":
                    compileIf();
                    break;
                case "while":
                    compileWhile();
                    break;
                case "do":
                    compileDo();
                    break;
                case "return":
                    compileReturn();
                    break;
                default:
                    throw new RuntimeException("Invalid statement: " + jackTokenizer.getThisToken());
            }
        }
        // 3. For each statement type, call the corresponding compile method (compileLet(), compileIf(), compileWhile(), compileDo(), compileReturn()).

        // 4. Write the closing </statements> tag.
        write("</statements>");
    }


    @Override
    public void compileLet() {
        // TODO: Implement compileLet method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <letStatement> tag.
        write("<letStatement>");
        // 2. Use eat() to consume the 'let' keyword.
        eat("let");
        // 3. Use eat() to consume the variable name (identifier).
        eat(TokenType.IDENTIFIER);
        // 4. If the next token is '[', handle array indexing by calling compileExpression().
        if (jackTokenizer.tokenType() == TokenType.SYMBOL && jackTokenizer.getThisToken().equals("[")) {
            eat("[");
            compileExpression();
            eat("]");
        }
        // 5. Use eat() to consume the '=' symbol.
        eat("=");
        // 6. Call compileExpression() to handle the expression.
        compileExpression();
        // 7. Use eat() to consume the closing ';' symbol.
        eat(";");
        // 8. Write the closing </letStatement> tag.
        write("</letStatement>");
    }


    @Override
    public void compileDo() {
        // TODO: Implement compileDo method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <doStatement> tag.
        write("<doStatement>");
        // 2. Use eat() to consume the 'do' keyword.
        eat("do");
        // 3. Use eat() to consume the subroutine call (identifier).
        eat(TokenType.IDENTIFIER);
        // 4. If the next token is '(', handle the subroutine call by calling compileExpressionList().
        if (jackTokenizer.tokenType() == TokenType.SYMBOL && jackTokenizer.getThisToken().equals("(")) {
            eat("(");
            compileExpressionList();
            eat(")");
        }
        // 5. If the next token is '.', handle the method call by consuming the class name, '.', and subroutine name, then call compileExpressionList().
        if (jackTokenizer.tokenType() == TokenType.SYMBOL && jackTokenizer.getThisToken().equals(".")) {
            eat(".");
            eat(TokenType.IDENTIFIER);
            eat("(");
            compileExpressionList();
            eat(")");
        }
        // 6. Use eat() to consume the closing ';' symbol.
        eat(";");
        // 7. Write the closing </doStatement> tag.
        write("</doStatement>");
    }

    @Override
    public int compileExpressionList() {
        // TODO: Implement compileExpressionList method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <expressionList> tag.
        write("<expressionList>");
        // 2. Use a while loop to handle multiple expressions separated by commas.
        int expressioncount = 0;
        while (jackTokenizer.tokenType() != TokenType.SYMBOL || !jackTokenizer.getThisToken().equals(")")) {
            // 3. Call compileExpression() to handle each expression.
            compileExpression();
            expressioncount++;
            // 4. If the next token is ',', use eat() to consume it.
            if (jackTokenizer.tokenType() == TokenType.SYMBOL && jackTokenizer.getThisToken().equals(",")) {
                eat(",");
            }
        }
        // 4. Write the closing </expressionList> tag.
        write("</expressionList>");
        // 5. Return the number of expressions in the list.
        return expressioncount;
    }

    @Override
    public void compileWhile() {
        // TODO: Implement compileWhile method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <whileStatement> tag.
        write("<whileStatement>");
        // 2. Use eat() to consume the 'while' keyword.
        eat("while");
        // 3. Use eat() to consume the opening '(' symbol.
        eat("(");
        // 4. Call compileExpression() to handle the condition expression.
        compileExpression();
        // 5. Use eat() to consume the closing ')' symbol.
        eat(")");
        // 6. Use eat() to consume the opening '{' symbol.
        eat("{");
        // 7. Call compileStatements() to handle the statements inside the while loop.
        compileStatements();
        // 8. Use eat() to consume the closing '}' symbol.
        eat("}");
        // 9. Write the closing </whileStatement> tag.
        write("</whileStatement>");
    }


    @Override
    public void compileReturn() {
        // TODO: Implement compileReturn method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <returnStatement> tag.
        write("<returnStatement>");
        // 2. Use eat() to consume the 'return' keyword.
        eat("return");
        // 3. If the next token is not ';', call compileExpression() to handle the return expression.
        if (jackTokenizer.tokenType() != TokenType.SYMBOL || !jackTokenizer.getThisToken().equals(";")) {
            compileExpression();
        }
        // 4. Use eat() to consume the closing ';' symbol.
        eat(";");
        // 5. Write the closing </returnStatement> tag.
        write("</returnStatement>");
    }


    @Override
    public void compileIf() {
        // TODO: Implement compileIf method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <ifStatement> tag.
        write("<ifStatement>");
        // 2. Use eat() to consume the 'if' keyword.
        eat("if");
        // 3. Use eat() to consume the opening '(' symbol.
        eat("(");
        // 4. Call compileExpression() to handle the condition expression.
        compileExpression();
        // 5. Use eat() to consume the closing ')' symbol.
        eat(")");
        // 6. Use eat() to consume the opening '{' symbol.
        eat("{");
        // 7. Call compileStatements() to handle the statements inside the if block.
        compileStatements();
        // 8. Use eat() to consume the closing '}' symbol.
        eat("}");
        // 9. If the next token is 'else', handle the else block by consuming 'else', '{', calling compileStatements(), and '}'.
        if (jackTokenizer.tokenType() == TokenType.KEYWORD && jackTokenizer.getThisToken().equals("else")) {
            eat("else");
            eat("{");
            compileStatements();
            eat("}");
        }
        // 10. Write the closing </ifStatement> tag.
        write("</ifStatement>");
    }

    @Override
    public void compileExpression() {
        // TODO: Implement compileExpression method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <expression> tag.
        write("<expression>");
        // 2. Call compileTerm() to handle the first term.
        compileTerm();
        // 3. Use a while loop to handle multiple terms separated by operators.
        while (jackTokenizer.isOp()) {
            // 4. For each operator, use eat() to consume the operator and call compileTerm() to handle the next term.
            eat(jackTokenizer.tokenType());
            compileTerm();
        }

        // 5. Write the closing </expression> tag.
        write("</expression>");
    }


    @Override
    public void compileTerm() {
        // TODO: Implement compileTerm method
        // Hint: The structure of the implementation should be as follows:
        // 1. Write the opening <term> tag.
        write("<term>");
        // 2. Check the type of the current token and handle accordingly:
        if (jackTokenizer.tokenType() == TokenType.INT_CONSTANT) {
            eat(TokenType.INT_CONSTANT);
        } else if (jackTokenizer.tokenType() == TokenType.STRING_CONSTANT) {
            eat(TokenType.STRING_CONSTANT);
        } else if (jackTokenizer.isKeywordConstant()) {
            eat(jackTokenizer.tokenType());
        } else if (jackTokenizer.tokenType() == TokenType.IDENTIFIER) {
            // 3. If the token is an identifier, handle variable, array, or subroutine calls.
            eat(TokenType.IDENTIFIER);
            if (jackTokenizer.tokenType() == TokenType.SYMBOL && jackTokenizer.getThisToken().equals("[")) {
                eat("[");
                compileExpression();
                eat("]");
            }
            if (jackTokenizer.tokenType() == TokenType.SYMBOL && jackTokenizer.getThisToken().equals("(")) {
                eat("(");
                compileExpressionList();
                eat(")");
            }
            if (jackTokenizer.tokenType() == TokenType.SYMBOL && jackTokenizer.getThisToken().equals(".")) {
                eat(".");
                eat(TokenType.IDENTIFIER);
                eat("(");
                compileExpressionList();
                eat(")");
            }
        } else if (jackTokenizer.tokenType() == TokenType.SYMBOL && jackTokenizer.getThisToken().equals("(")) {
            // 4. If the token is '(', handle the expression inside parentheses by calling compileExpression().
            eat("(");
            compileExpression();
            eat(")");
        } else if (jackTokenizer.isUnaryOp()) {
            // 5. If the token is a unary operator, use eat() to consume it and call compileTerm() to handle the term.
            eat(jackTokenizer.tokenType());
            compileTerm();
        }
        //    - If the token is an integer constant, use eat() to consume it.
        //    - If the token is a string constant, use eat() to consume it.
        //    - If the token is a keyword constant, use eat() to consume it.
        //    - If the token is an identifier, handle variable, array, or subroutine calls.
        //    - If the token is '(', handle the expression inside parentheses by calling compileExpression().
        //    - If the token is a unary operator, use eat() to consume it and call compileTerm() to handle the term.
        // 3. Write the closing </term> tag.
        write("</term>");
    }

    private void advance() {
        if (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
        }
    }

    private void eat(String str) {
        if (jackTokenizer.getThisToken().equals(str)) {
            // writes <tokenType> str </tokenType>
            write(jackTokenizer.getThisTokenAsTag());
        } else {
            throw new RuntimeException("expect " + str + " but get " + jackTokenizer.getThisToken());
        }
        advance();
    }

    private void eat(TokenType tokenType) {
        if (jackTokenizer.tokenType() == tokenType) {
            // writes <tokenType> val </tokenType>
            write(jackTokenizer.getThisTokenAsTag());
        } else {
            throw new RuntimeException("expect " + StringUtils.getTokenType(tokenType) + " but get " + StringUtils.getTokenType(jackTokenizer.tokenType()));
        }
        advance();
    }

    private void write(String str) {
        try {
            bw.write(str);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
