package io.dallen.parser;

import io.dallen.compiler.CompileError;
import io.dallen.parser.splitter.BraceSplitter;
import io.dallen.parser.splitter.LayeredSplitter;
import io.dallen.parser.splitter.SplitLayer;
import io.dallen.parser.splitter.SplitSettings;
import io.dallen.tokenizer.Token;
import io.dallen.AST.*;

import io.dallen.tokenizer.Token.Keyword;
import io.dallen.tokenizer.Token.Symbol;
import io.dallen.tokenizer.Token.Textless;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {

    private List<Token> tokens;

    private int pos;

    // defines a multipass split. When a successful split is made, the resulting action will be executed on the result
    private static final SplitSettings splitSettings = new SplitSettings()
            .addLayer(new SplitLayer()
                .addSplitRule(Token.Symbol.EQUAL, ExpressionParser::parseAssignment))
            .addLayer(new SplitLayer()
                .addSplitRule(Token.Symbol.BOOL_AND, ExpressionParser.boolCombineAction(BoolOp.AND)))
            .addLayer(new SplitLayer()
                .addSplitRule(Token.Symbol.BOOL_OR, ExpressionParser.boolCombineAction(BoolOp.OR)))
            .addLayer(new SplitLayer()
                .addSplitRule(Token.Symbol.DOUBLE_EQUAL, ExpressionParser.compareAction(CompareOp.EQ))
                .addSplitRule(Token.Symbol.LEFT_ANGLE, ExpressionParser.compareAction(CompareOp.LT))
                .addSplitRule(Token.Symbol.RIGHT_ANGLE, ExpressionParser.compareAction(CompareOp.GT)))
            .addLayer(new SplitLayer()
                .addSplitRule(Token.Symbol.SLASH, ExpressionParser.mathAction(MathOp.DIV))
                .addSplitRule(Token.Symbol.STAR, ExpressionParser.mathAction(MathOp.MUL)))
            .addLayer(new SplitLayer()
                .addSplitRule(Token.Symbol.PLUS, ExpressionParser.mathAction(MathOp.PLUS))
                .addSplitRule(Token.Symbol.MINUS, ExpressionParser.mathAction(MathOp.MINUS)))
            .addLayer(new SplitLayer()
                .addSplitRule(Token.Symbol.DOT, ExpressionParser.statementAction(Dotted::new))).leftToRight(false);


    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token current() {
        if (pos >= tokens.size()) {
            return Token.EOF;
        }

        return tokens.get(pos);
    }

    private Token consume() {
        Token tok = current();
        next();
        return tok;
    }

    private Token consumeExpected(Token.TokenType type) {
        Token t;
        if ((t = consume()).type != type) {
            throw new ParserError("Parse error Expected: " + type.toString(), t);
        }
        return t;
    }

    private Token tryConsumeExpected(Token.TokenType type) {
        Token t = current();
        if (current().type == type) {
            consume();
        } else {
            return null;
        }
        return t;
    }

    private void next() {
        pos++;
    }

    private Token peek() {
        if (pos + 1 >= tokens.size()) {
            return Token.EOF;
        }

        return tokens.get(pos + 1);
    }

    private List<Token> consumeTo(Token.TokenType type) {
        List<Token> tokens = new ArrayList<>();
        BraceManager braceManager = new BraceManager(BraceManager.leftToRight);
        while (true) {
            if (current().type == Token.Textless.EOF) {
                if (braceManager.isEmpty()) {
                    break;
                }
                throw new ParserError("Parse error", current());
            }
            if (current().type == type && braceManager.isEmpty()) {
                break;
            }
            braceManager.check(current());
            tokens.add(current());
            next();
        }
        next();
        return tokens;
    }

    // Just selects the tokens, does not advance the current location
    private List<Token> selectTo(Token.TokenType type) {
        List<Token> selected = new ArrayList<>();
        int loc = pos;
        while (loc < tokens.size() && tokens.get(loc).type != type) {
            selected.add(tokens.get(loc));
            loc++;
        }
        return selected;
    }

    // Check if one token comes before another
    private boolean containsBefore(Token.TokenType what, Token.TokenType before) {
        for (int lpos = pos; lpos < tokens.size(); lpos++) {
            if (tokens.get(lpos).type == what) {
                return true;
            }
            if (tokens.get(lpos).type == before) {
                return false;
            }
        }
        return before == Token.Textless.EOF;
    }

    public List<Statement> parseBlock() {
        ArrayList<Statement> statements = new ArrayList<>();

        while (!current().type.equals(Token.Textless.EOF)) {
            Token.TokenType i = current().type;
            if (Token.Keyword.WHILE.equals(i)) {
                statements.add(parseWhileBlock());
                continue;
            }
            if (Token.Keyword.IF.equals(i)) {
                statements.add(parseIfBlock());
                continue;
            }
            if (Token.Keyword.ELSE.equals(i)) {
                attachElseBlock(statements);
                continue;
            }
            if (Token.Keyword.DEF.equals(i)) {
                statements.add(parseFunctionDef());
                continue;
            }
            if (Token.Keyword.CLASS.equals(i)) {
                statements.add(parseClassDef());
                continue;
            }
            if (Token.Keyword.RETURN.equals(i)) {
                statements.add(parseReturn());
                continue;
            }
            statements.add(parseExpression());
        }
        return statements;
    }

    private ClassDef parseClassDef() {
        consumeExpected(Keyword.CLASS);
        Token name = consumeExpected(Textless.NAME);
        // extends

        consumeExpected(Symbol.LEFT_BRACE);
        List<Token> bodyTokens = consumeTo(Symbol.RIGHT_BRACE);
        List<Statement> body = new Parser(bodyTokens).parseBlock();

        return new ClassDef(name.literal, new ArrayList<>(), body);
    }

    private void attachElseBlock(ArrayList<Statement> statements) {
        if(statements.size() < 1) {
            throw new CompileError("Else statement requires If, none found");
        }
        Statement parentStmt = statements.get(statements.size() - 1);

        consumeExpected(Token.Keyword.ELSE);
        ElseBlock toAttach;
        if(current().type == Token.Keyword.IF) {
            IfBlock on = parseIfBlock();
            toAttach = new ElseIfBlock(on);
        } else {
            consumeExpected(Token.Symbol.LEFT_BRACE);
            List<Token> bodyTokens = consumeTo(Token.Symbol.RIGHT_BRACE);
            List<Statement> body = new Parser(bodyTokens).parseBlock();
            toAttach = new ElseAlwaysBlock(body);
        }

        if(parentStmt instanceof IfBlock) {
            ((IfBlock) parentStmt).elseBlock = toAttach;
        } else if(parentStmt instanceof ElseIfBlock) {
            ((ElseIfBlock) parentStmt).elseBlock = toAttach;
        } else {
            throw new CompileError("Else statement requires If, " + parentStmt.getClass().getName() + " found");
        }
    }

    private IfBlock parseIfBlock() {
        consumeExpected(Token.Keyword.IF);

        consumeExpected(Token.Symbol.LEFT_PAREN);
        List<Token> condTokens = consumeTo(Token.Symbol.RIGHT_PAREN);
        consumeExpected(Token.Symbol.LEFT_BRACE);
        Statement cond = new Parser(condTokens).parseExpression();

        List<Token> bodyTokens = consumeTo(Token.Symbol.RIGHT_BRACE);
        List<Statement> body = new Parser(bodyTokens).parseBlock();

        return new IfBlock(cond, body);
    }

    private WhileBlock parseWhileBlock() {
        next();

        consumeExpected(Token.Symbol.LEFT_PAREN);
        List<Token> condTokens = consumeTo(Token.Symbol.RIGHT_PAREN);
        consumeExpected(Token.Symbol.LEFT_BRACE);
        Statement cond = new Parser(condTokens).parseExpression();

        List<Token> bodyTokens = consumeTo(Token.Symbol.RIGHT_BRACE);
        List<Statement> body = new Parser(bodyTokens).parseBlock();

        return new WhileBlock(cond, body);
    }

    private FunctionDef parseFunctionDef() {
        consumeExpected(Token.Keyword.DEF);

        String funcName = consume().literal;
        consumeExpected(Token.Symbol.LEFT_PAREN);
        List<Token> paramTokens = consumeTo(Token.Symbol.RIGHT_PAREN);

        List<FunctionParam> params;
        try {
             params = BraceSplitter.splitAll(paramTokens, Token.Symbol.COMMA)
                    .stream()
                    .map(e -> BraceSplitter.splitAll(e, Token.Symbol.COLON))
                    .map(e -> new FunctionParam(new Parser(e.get(1)).parseType(), e.get(0).get(0).literal))
                    .collect(Collectors.toList());
        } catch (IndexOutOfBoundsException ex) {
            throw new CompileError("Failed to parse function args for " + funcName);
        }

        Type returnType = Type.VOID;

        if(current().type == Symbol.COLON) {
            consumeExpected(Token.Symbol.COLON);
            List<Token> returnTypeTokens = consumeTo(Token.Symbol.LEFT_BRACE);
            returnType = new Parser(returnTypeTokens).parseType();
        } else {
            consumeExpected(Symbol.LEFT_BRACE);
        }

        List<Token> bodyTokens = consumeTo(Token.Symbol.RIGHT_BRACE);
        List<Statement> body = new Parser(bodyTokens).parseBlock();

        return new FunctionDef(returnType, funcName, params, body);
    }

    private Return parseReturn() {
        consumeExpected(Token.Keyword.RETURN);

        Statement value = new Parser(consumeTo(Token.Symbol.SEMICOLON)).parseExpression();
        return new Return(value);
    }

    private Type parseType() {
        Statement typeName = new Parser(consumeTo(Token.Symbol.LEFT_ANGLE)).parseExpression();
        if(current().isEOF()) {
            return new Type(typeName, 0, new ArrayList<>());
        }
        List<Type> genericParams = BraceSplitter
                .customSplitAll(BraceManager.leftToRightAngle, consumeTo(Token.Symbol.RIGHT_ANGLE), Token.Symbol.COMMA)
                .stream()
                .map(e -> new Parser(e).parseType())
                .collect(Collectors.toList());

        return new Type(typeName, 0, genericParams);
    }

    public Statement parseExpression() {
        List<Token> workingTokens = selectTo(Token.Symbol.SEMICOLON);

        Statement parsed = new LayeredSplitter(splitSettings).execute(workingTokens);
        if (parsed != null) {
            pos += workingTokens.size() + 1;
            return parsed;
        }
        if (current().type == Keyword.NEW) {
            consumeExpected(Keyword.NEW);
            List<Token> name = consumeTo(Symbol.LEFT_PAREN);
            List<List<Token>> paramz = BraceSplitter.splitAll(consumeTo(Symbol.RIGHT_PAREN), Symbol.COMMA);
            Statement typeStmt = new Parser(name).parseExpression();
            List<Statement> params = paramz
                .stream()
                .map(e -> new Parser(e).parseExpression())
                .collect(Collectors.toList());
            return new New(typeStmt, params);
        } else if (current().type == Token.Symbol.LEFT_PAREN) {
            consumeExpected(Token.Symbol.LEFT_PAREN);
            Statement sub = new Parser(consumeTo(Token.Symbol.RIGHT_PAREN)).parseExpression();
            return new Parened(sub);
        } else if (current().type == Token.Textless.NAME) {
            return handleNameToken(workingTokens);
        } else if (current().type == Token.Textless.NUMBER_LITERAL) {
            tryConsumeExpected(Token.Symbol.SEMICOLON);
            return new NumberLiteral(Double.parseDouble(current().literal));
        } else if (current().type == Token.Textless.STRING_LITERAL) {
            tryConsumeExpected(Token.Symbol.SEMICOLON);
            return new StringLiteral(current().literal);
        } else {
            throw new ParserError("Unknown token sequence", current());
        }
    }

    private Statement handleNameToken(List<Token> workingTokens) {

        if(containsBefore(Token.Symbol.COLON, Token.Symbol.SEMICOLON)) {
            Token name = consume();
            consumeExpected(Token.Symbol.COLON);
            Type type = new Parser(consumeTo(Token.Symbol.SEMICOLON)).parseType();
            return new Declare(type, name.literal);

        } else if(containsBefore(Token.Symbol.LEFT_PAREN, Token.Symbol.SEMICOLON)) {
            List<Token> funcName = consumeTo(Token.Symbol.LEFT_PAREN);
            if(funcName.size() > 1) {
                throw new ParserError("Function call name was multi token", funcName.get(0));
            }
//            Statement parsedName = new Parser(funcName).parseExpression();
            List<Statement> funcParams = consumeFunctionParams();
            tryConsumeExpected(Token.Symbol.SEMICOLON);
            return new FunctionCall(funcName.get(0).literal, funcParams);

        } else if(containsBefore(Token.Symbol.LEFT_BRACKET, Token.Symbol.SEMICOLON)) {
            List<Token> name = consumeTo(Symbol.LEFT_BRACKET);
            Statement left = new Parser(name).parseExpression();
            List<Token> sub = consumeTo(Token.Symbol.RIGHT_BRACKET);
            Statement inner = new Parser(sub).parseExpression();
            return new Subscript(left, inner);
        } else {
            Token name = consume();
            tryConsumeExpected(Token.Symbol.SEMICOLON);
            return new Variable(name.literal);
        }
    }

    private List<Statement> consumeFunctionParams() {
        List<Token> params = consumeTo(Token.Symbol.RIGHT_PAREN);
        List<List<Token>> paramTokens = BraceSplitter.splitAll(params, Token.Symbol.COMMA);
        return paramTokens.stream()
                .filter(arr -> !arr.isEmpty())
                .map(Parser::new)
                .map(Parser::parseExpression)
                .collect(Collectors.toList());
    }
}


