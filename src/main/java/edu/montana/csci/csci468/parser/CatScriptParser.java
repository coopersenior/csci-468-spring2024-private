package edu.montana.csci.csci468.parser;

import edu.montana.csci.csci468.parser.expressions.*;
import edu.montana.csci.csci468.parser.statements.*;
import edu.montana.csci.csci468.tokenizer.CatScriptTokenizer;
import edu.montana.csci.csci468.tokenizer.Token;
import edu.montana.csci.csci468.tokenizer.TokenList;
import edu.montana.csci.csci468.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

import static edu.montana.csci.csci468.tokenizer.TokenType.*;

public class CatScriptParser {

    private TokenList tokens;
    private FunctionDefinitionStatement currentFunctionDefinition;

    public CatScriptProgram parse(String source) {
        tokens = new CatScriptTokenizer(source).getTokens();

        // first parse an expression
        CatScriptProgram program = new CatScriptProgram();
        program.setStart(tokens.getCurrentToken());
        Expression expression = null;
        try {
            expression = parseExpression();
        } catch(RuntimeException re) {
            // ignore :)
        }
        if (expression == null || tokens.hasMoreTokens()) {
            tokens.reset();
            while (tokens.hasMoreTokens()) {
                program.addStatement(parseProgramStatement());
            }
        } else {
            program.setExpression(expression);
        }

        program.setEnd(tokens.getCurrentToken());
        return program;
    }

    public CatScriptProgram parseAsExpression(String source) {
        tokens = new CatScriptTokenizer(source).getTokens();
        CatScriptProgram program = new CatScriptProgram();
        program.setStart(tokens.getCurrentToken());
        Expression expression = parseExpression();
        program.setExpression(expression);
        program.setEnd(tokens.getCurrentToken());
        return program;
    }

    //============================================================
    //  Statements
    //============================================================

    private Statement parseProgramStatement() {
        Statement funcStatement = parseFunctionStatement();
        if (funcStatement != null){
            return funcStatement;
        }
        return parseStatement();
    }

    private Statement parseStatement() {
        Statement printStmt = parsePrintStatement();
        if (printStmt != null) {
            return printStmt;
        }
        Statement ifStmt = parseIfStatement();
        if (ifStmt != null) {
            return ifStmt;
        }
        return new SyntaxErrorStatement(tokens.consumeToken());
    }

    private Statement parseIfStatement() {
        // look at parse print statement
        if (tokens.match(IF)) {
            Token ifStart = tokens.consumeToken();
            IfStatement ifStatement = new IfStatement();
            require(LEFT_PAREN, ifStatement);
            Expression testExpression = parseExpression();
            ifStatement.setExpression(testExpression);
            require(RIGHT_PAREN, ifStatement);
            require(LEFT_BRACE, ifStatement);
            List<Statement> stms = new ArrayList<>();
            while(tokens.hasMoreTokens() && !tokens.match(RIGHT_BRACE)) {
                Statement stmt = parseStatement();
                stms.add(stmt);
            }
            require(RIGHT_BRACE, ifStatement);
            ifStatement.setTrueStatements(stms);
            // handle else
            return null;
        } else {
            return null;
        }
    }
    private Statement parseFunctionStatement() {
        return null;
    }

    private Statement parsePrintStatement() {
        if (tokens.match(PRINT)) {

            PrintStatement printStatement = new PrintStatement();
            printStatement.setStart(tokens.consumeToken());

            require(LEFT_PAREN, printStatement);
            printStatement.setExpression(parseExpression());
            printStatement.setEnd(require(RIGHT_PAREN, printStatement));

            return printStatement;
        } else {
            return null;
        }
    }

    //============================================================
    //  Expressions
    //============================================================

    private Expression parseExpression() {
        return parseEqualityExpression();
    }

    private Expression parseEqualityExpression() {
        Expression expression = parseComparisonExpression();
        while (tokens.match(EQUAL_EQUAL, BANG_EQUAL)) {
            Token operator = tokens.consumeToken();
            final Expression rightHandSide = parseFactorExpression();
            EqualityExpression equalityExpression = new EqualityExpression(operator, expression, rightHandSide);
            equalityExpression.setStart(expression.getStart());
            equalityExpression.setEnd(rightHandSide.getEnd());
            expression = equalityExpression;
        }
        return expression;
    }
    private Expression parseComparisonExpression() {
        Expression expression = parseAdditiveExpression();
        while (tokens.match(LESS, GREATER, LESS_EQUAL, GREATER_EQUAL)) {
            Token operator = tokens.consumeToken();
            final Expression rightHandSide = parseFactorExpression();
            ComparisonExpression comparisonExpression = new ComparisonExpression(operator, expression, rightHandSide);
            comparisonExpression.setStart(expression.getStart());
            comparisonExpression.setEnd(rightHandSide.getEnd());
            expression = comparisonExpression;
        }
        return expression;
    }

    private Expression parseAdditiveExpression() {
        Expression expression = parseFactorExpression();
        while (tokens.match(PLUS, MINUS)) {
            Token operator = tokens.consumeToken();
            final Expression rightHandSide = parseFactorExpression();
            AdditiveExpression additiveExpression = new AdditiveExpression(operator, expression, rightHandSide);
            additiveExpression.setStart(expression.getStart());
            additiveExpression.setEnd(rightHandSide.getEnd());
            expression = additiveExpression;
        }
        return expression;
    }

    private Expression parseFactorExpression() {
        Expression expression = parseUnaryExpression();
        while (tokens.match(STAR, SLASH)) {
            Token operator = tokens.consumeToken();
            final Expression rightHandSide = parseUnaryExpression();
            FactorExpression factorExpression = new FactorExpression(operator, expression, rightHandSide);
            factorExpression.setStart(expression.getStart());
            factorExpression.setEnd(rightHandSide.getEnd());
            expression = factorExpression;
        }
        return expression;
    }

    private Expression parseUnaryExpression() {
        if (tokens.match(MINUS, NOT)) {
            Token token = tokens.consumeToken();
            Expression rhs = parseUnaryExpression();
            UnaryExpression unaryExpression = new UnaryExpression(token, rhs);
            unaryExpression.setStart(token);
            unaryExpression.setEnd(rhs.getEnd());
            return unaryExpression;
        } else {
            return parsePrimaryExpression();
        }
    }
    private Expression parsePrimaryExpression() {
        if (tokens.match(INTEGER)) {
            Token integerToken = tokens.consumeToken();
            IntegerLiteralExpression integerExpression = new IntegerLiteralExpression(integerToken.getStringValue());
            integerExpression.setToken(integerToken);
            return integerExpression;
        } else if (tokens.match(TRUE, FALSE)) {
            Token booleanToken = tokens.consumeToken();
            BooleanLiteralExpression booleanLiteralExpression = new BooleanLiteralExpression(booleanToken.getType().equals(TRUE));
            booleanLiteralExpression.setToken(booleanToken);
            return booleanLiteralExpression;
        } else if (tokens.match(NULL)) {
            Token nullToken = tokens.consumeToken();
            NullLiteralExpression nullLiteralExpression = new NullLiteralExpression();
            nullLiteralExpression.setToken(nullToken);
            return nullLiteralExpression;
        } else if (tokens.match(STRING)) {
            Token stringToken = tokens.consumeToken();
            StringLiteralExpression stringExpression = new StringLiteralExpression(stringToken.getStringValue());
            stringExpression.setToken(stringToken);
            return stringExpression;
        } else if (tokens.match(IDENTIFIER)) {
            Token identifierToken = tokens.consumeToken();
            if (tokens.match(LEFT_PAREN)) {
                return parseFunctionCallExpression(identifierToken);
            } else {
                IdentifierExpression identifierExpression = new IdentifierExpression(identifierToken.getStringValue());
                identifierExpression.setToken(identifierToken);
                return identifierExpression;
            }
        } else if (tokens.match(LEFT_BRACKET)) {
            Token bracket = tokens.consumeToken();
            List<Expression> values = new ArrayList<>();
            if (!tokens.match(RIGHT_BRACKET)) {
                do {
                    Expression expression = parseExpression();
                    values.add(expression);
                } while (tokens.matchAndConsume(COMMA) && tokens.hasMoreTokens());
            }
            ListLiteralExpression ll = new ListLiteralExpression(values);
            ll.setStart(bracket);
            ll.setEnd(require(RIGHT_BRACKET, ll, ErrorType.UNTERMINATED_LIST));
            return ll;
        } else if (tokens.match(LEFT_PAREN)) {
            Token paren = tokens.consumeToken();
            Expression expression = parseExpression();
            ParenthesizedExpression parenExp = new ParenthesizedExpression(expression);
            parenExp.setStart(paren);
            parenExp.setEnd(require(RIGHT_PAREN, parenExp, ErrorType.UNTERMINATED_ARG_LIST));
            return parenExp;
        } else{
            SyntaxErrorExpression syntaxErrorExpression = new SyntaxErrorExpression(tokens.consumeToken());
            return syntaxErrorExpression;
        }
    }

    private Expression parseFunctionCallExpression(Token functionName) {
        tokens.consumeToken();
        List<Expression> args = new ArrayList<>();
        if (!tokens.match(RIGHT_PAREN)) {
            do {
                Expression expression = parseExpression();
                args.add(expression);
            } while (tokens.matchAndConsume(COMMA) && tokens.hasMoreTokens());
        }
        FunctionCallExpression fc = new FunctionCallExpression(functionName.getStringValue(), args);
        fc.setStart(functionName);
        fc.setEnd(require(RIGHT_PAREN, fc, ErrorType.UNTERMINATED_ARG_LIST));
        return fc;
    }

    //============================================================
    //  Parse Helpers
    //============================================================
    private Token require(TokenType type, ParseElement elt) {
        return require(type, elt, ErrorType.UNEXPECTED_TOKEN);
    }

    private Token require(TokenType type, ParseElement elt, ErrorType msg) {
        if(tokens.match(type)){
            return tokens.consumeToken();
        } else {
            elt.addError(msg, tokens.getCurrentToken());
            return tokens.getCurrentToken();
        }
    }

}
