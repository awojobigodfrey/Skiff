package io.dallen.tokenizer;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LexerTest {
    @org.junit.Test
    public void lexSimpleSymbols() {
        String symbols = ": = , ( ) { } ; ! != == < > <= >= [ ] * + - / % ** && || += -= ++ -- => _";

        List<Token> tokens = new Lexer(symbols).lex();
        List<Token> expected = new ArrayList<>(List.of(
                new Token(Token.Symbol.COLON, 0),
                new Token(Token.Symbol.EQUAL, 0),
                new Token(Token.Symbol.COMMA, 0),
                new Token(Token.Symbol.LEFT_PAREN, 0),
                new Token(Token.Symbol.RIGHT_PAREN, 0),
                new Token(Token.Symbol.LEFT_BRACE, 0),
                new Token(Token.Symbol.RIGHT_BRACE, 0),
                new Token(Token.Symbol.SEMICOLON, 0),
                new Token(Token.Symbol.BANG, 0),
                new Token(Token.Symbol.BANG_EQUAL, 0),
                new Token(Token.Symbol.DOUBLE_EQUAL, 0),
                new Token(Token.Symbol.LEFT_ANGLE, 0),
                new Token(Token.Symbol.RIGHT_ANGLE, 0),
                new Token(Token.Symbol.LEFT_ANGLE_EQUAL, 0),
                new Token(Token.Symbol.RIGHT_ANGLE_EQUAL, 0),
                new Token(Token.Symbol.LEFT_BRACKET, 0),
                new Token(Token.Symbol.RIGHT_BRACKET, 0),
                new Token(Token.Symbol.STAR, 0),
                new Token(Token.Symbol.PLUS, 0),
                new Token(Token.Symbol.MINUS, 0),
                new Token(Token.Symbol.SLASH, 0),
                new Token(Token.Symbol.PERCENT, 0),
                new Token(Token.Symbol.DOUBLE_STAR, 0),
                new Token(Token.Symbol.DOUBLE_AND, 0),
                new Token(Token.Symbol.DOUBLE_OR, 0),
                new Token(Token.Symbol.PLUS_EQUAL, 0),
                new Token(Token.Symbol.MINUS_EQUAL, 0),
                new Token(Token.Symbol.DOUBLE_PLUS, 0),
                new Token(Token.Symbol.DOUBLE_MINUS, 0),
                new Token(Token.Symbol.FAT_ARROW, 0),
                new Token(Token.Symbol.UNDERSCORE, 0),
                new Token(Token.Textless.EOF, 0))
        );
        assertThat(tokens, is(expected));
    }

    @org.junit.Test
    public void lexKeywords() {
        String symbols = "if else while for return def class struct private static new switch match import loop case " +
                "true false next break try catch throw";

        List<Token> tokens = new Lexer(symbols).lex();
        List<Token> expected = new ArrayList<>(List.of(
                new Token(Token.Keyword.IF, 0),
                new Token(Token.Keyword.ELSE, 0),
                new Token(Token.Keyword.WHILE, 0),
                new Token(Token.Keyword.FOR, 0),
                new Token(Token.Keyword.RETURN, 0),
                new Token(Token.Keyword.DEF, 0),
                new Token(Token.Keyword.CLASS, 0),
                new Token(Token.Keyword.STRUCT, 0),
                new Token(Token.Keyword.PRIVATE, 0),
                new Token(Token.Keyword.STATIC, 0),
                new Token(Token.Keyword.NEW, 0),
                new Token(Token.Keyword.SWITCH, 0),
                new Token(Token.Keyword.MATCH, 0),
                new Token(Token.Keyword.IMPORT, 0),
                new Token(Token.Keyword.LOOP, 0),
                new Token(Token.Keyword.CASE, 0),
                new Token(Token.Keyword.TRUE, 0),
                new Token(Token.Keyword.FALSE, 0),
                new Token(Token.Keyword.NEXT, 0),
                new Token(Token.Keyword.BREAK, 0),
                new Token(Token.Keyword.TRY, 0),
                new Token(Token.Keyword.CATCH, 0),
                new Token(Token.Keyword.THROW, 0),
                new Token(Token.Textless.EOF, 0)
        ));
        assertThat(tokens, is(expected));
    }

    @org.junit.Test
    public void lexTextless() {
        String symbols = "varName var_name VARNAME varName25 varName_25 10 10.5 " +
                "\"Hello String\" 'Hello Sequence' r/$ReGeX|[Pattrn]^/giln";

        List<Token> tokens = new Lexer(symbols).lex();
        List<Token> expected = new ArrayList<>(List.of(
                new Token(Token.Textless.NAME, "varName", Token.IdentifierType.VARIABLE, 0),
                new Token(Token.Textless.NAME, "var_name", Token.IdentifierType.VARIABLE, 0),
                new Token(Token.Textless.NAME, "VARNAME", Token.IdentifierType.VARIABLE, 0),
                new Token(Token.Textless.NAME, "varName25", Token.IdentifierType.VARIABLE, 0),
                new Token(Token.Textless.NAME, "varName_25", Token.IdentifierType.VARIABLE, 0),
                new Token(Token.Textless.NUMBER_LITERAL, "10", 0),
                new Token(Token.Textless.NUMBER_LITERAL, "10.5", 0),
                new Token(Token.Textless.STRING_LITERAL, "Hello String", 0),
                new Token(Token.Textless.SEQUENCE_LITERAL, "Hello Sequence", 0),
                new Token(Token.Textless.REGEX_LITERAL, "$ReGeX|[Pattrn]^ giln", 0),
                new Token(Token.Textless.EOF, 0))
        );
        assertThat(tokens, is(expected));
    }

    @org.junit.Test
    public void lexEnrichment() {
        String symbols = "class MyClass { class MyInnerClass { MyClass MyInnerClass } MyClass MyInnerClass } " +
                "MyClass MyInnerClass";

        List<Token> tokens = new Lexer(symbols).lex();
        List<Token> expected = new ArrayList<>(List.of(
                new Token(Token.Keyword.CLASS, 0),
                new Token(Token.Textless.NAME, "MyClass", Token.IdentifierType.TYPE, 0),
                new Token(Token.Symbol.LEFT_BRACE, 0),
                new Token(Token.Keyword.CLASS, 0),
                new Token(Token.Textless.NAME, "MyInnerClass", Token.IdentifierType.TYPE, 0),
                new Token(Token.Symbol.LEFT_BRACE, 0),
                new Token(Token.Textless.NAME, "MyClass", Token.IdentifierType.TYPE, 0),
                new Token(Token.Textless.NAME, "MyInnerClass", Token.IdentifierType.TYPE, 0),
                new Token(Token.Symbol.RIGHT_BRACE, 0),
                new Token(Token.Textless.NAME, "MyClass", Token.IdentifierType.TYPE, 0),
                new Token(Token.Textless.NAME, "MyInnerClass", Token.IdentifierType.TYPE, 0),
                new Token(Token.Symbol.RIGHT_BRACE, 0),
                new Token(Token.Textless.NAME, "MyClass", Token.IdentifierType.TYPE, 0),
                new Token(Token.Textless.NAME, "MyInnerClass", Token.IdentifierType.VARIABLE, 0),
                new Token(Token.Textless.EOF, 0))
        );
        assertThat(tokens, is(expected));
    }
}
