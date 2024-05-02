package edu.montana.csci.csci468.demo;

import edu.montana.csci.csci468.CatscriptTestBase;
import edu.montana.csci.csci468.parser.expressions.BooleanLiteralExpression;
import edu.montana.csci.csci468.parser.expressions.ComparisonExpression;
import edu.montana.csci.csci468.parser.expressions.FunctionCallExpression;
import edu.montana.csci.csci468.parser.expressions.ListLiteralExpression;
import edu.montana.csci.csci468.parser.statements.ForStatement;
import edu.montana.csci.csci468.parser.statements.IfStatement;
import edu.montana.csci.csci468.parser.statements.PrintStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PartnerTest extends CatscriptTestBase {

    @Test
    public void parseFunctionCallExpressionWithList() {
        FunctionCallExpression expr = parseExpression("foo([[1, 2], 3], true)", false);
        assertEquals("foo", expr.getName());
        // verify args size
        assertEquals(2, expr.getArguments().size());
        // verify args instance types
        assertTrue(expr.getArguments().get(0) instanceof ListLiteralExpression);
        assertTrue(expr.getArguments().get(1) instanceof BooleanLiteralExpression);
        ListLiteralExpression innerList = (ListLiteralExpression) expr.getArguments().get(0);
        // verify list size
        assertEquals(2, innerList.getValues().size());
    }

    @Test
    public void forStatementWithIfStatementParses() {
        ForStatement expr = parseStatement("for(i in [1, 2, 3]){ if(i > 1) { print(true) } else { print( i ) } }");
        assertNotNull(expr);
        assertEquals("i", expr.getVariableName());
        // verify instance types
        assertTrue(expr.getExpression() instanceof ListLiteralExpression);
        assertTrue(expr.getBody().get(0) instanceof IfStatement);
        assertEquals(1, expr.getBody().size());
        // verify ifStatement instance types
        assertTrue(((IfStatement) expr.getBody().get(0)).getTrueStatements().get(0) instanceof PrintStatement);
        assertTrue(((IfStatement) expr.getBody().get(0)).getElseStatements().get(0) instanceof PrintStatement);
    }

    @Test
    void varAndIfStmtInsideFunctionWorksProperly() {
        // verify multiple types work in functions
        assertEquals("23\n", executeProgram("function foo() : int {\n" +
                "  var x = 19\n" +
                "  if(x > 10){ return x + 4 }" +
                "  else { return x }" +
                "}\n" +
                "print( foo() )\n"));

    }

}
