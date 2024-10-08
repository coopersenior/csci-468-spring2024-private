package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.Expression;
import edu.montana.csci.csci468.parser.expressions.IntegerLiteralExpression;
import org.objectweb.asm.Opcodes;

public class AssignmentStatement extends Statement {
    private Expression expression;
    private String variableName;

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = addChild(expression);
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        expression.validate(symbolTable);
        CatscriptType symbolType = symbolTable.getSymbolType(getVariableName());
        if (symbolType == null) {
            addError(ErrorType.UNKNOWN_NAME);
        } else if (symbolType.toString().equals("int") && !expression.equals("IntegerLiteralExpression")) {
            addError(ErrorType.INCOMPATIBLE_TYPES);
            // TOOD - verify compatilibity of types
        } else if (symbolType.toString().equals("bool") && !expression.equals("BooleanLiteralExpression")) {
            addError(ErrorType.INCOMPATIBLE_TYPES);
        }
    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
        super.execute(runtime);
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        expression.compile(code);
        // Store the expression result to the variable
        if (expression.getType().equals(CatscriptType.INT) || expression.getType().equals(CatscriptType.BOOLEAN)) {
            code.addVarInstruction(Opcodes.ISTORE, code.createLocalStorageSlotFor(variableName)); // ISTORE for int/boolean
        } else {
            code.addVarInstruction(Opcodes.ASTORE, code.createLocalStorageSlotFor(variableName)); // ASTORE for reference types
        }
    }
}
