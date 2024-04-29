package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.eval.ReturnException;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.Expression;
import org.objectweb.asm.Opcodes;

public class ReturnStatement extends Statement {
    private Expression expression;
    private FunctionDefinitionStatement function;

    public void setExpression(Expression parseExpression) {
        this.expression = addChild(parseExpression);
    }

    public void setFunctionDefinition(FunctionDefinitionStatement func) {
        this.function = func;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        if (expression != null) {
            expression.validate(symbolTable);
            if (!function.getType().isAssignableFrom(expression.getType())) {
                expression.addError(ErrorType.INCOMPATIBLE_TYPES);
            }
        } else {
            if (!function.getType().equals(CatscriptType.VOID)) {
                addError(ErrorType.INCOMPATIBLE_TYPES);
            }
        }
    }

    //==============================================================
    // Implementation
    //==============================================================


    @Override
    public void execute(CatscriptRuntime runtime) {
        if (expression != null) {
            Object returnValue = expression.evaluate(runtime);
            throw new ReturnException(returnValue);
        }
        super.execute(runtime);
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        System.out.println("returnStatement");
        if (expression != null) {
            // if return type is object
            //      and type is primitive (int or boolean) need to box
            // if its int or boolean use IRETURN
            // else use ARETURN
            // If return type is object and type is primitive (int or boolean), box the value
            //code.addVarInstruction(Opcodes.ALOAD, 0);

            if (expression.getType().equals(CatscriptType.BOOLEAN) || expression.getType().equals(CatscriptType.INT)) {
                // Box the value
                box(code, expression.getType());
            }
            expression.compile(code);
            // Use IRETURN if the expression type is INT or BOOLEAN, otherwise use ARETURN
            if (expression.getType().equals(CatscriptType.BOOLEAN) || expression.getType().equals(CatscriptType.INT)) {
                code.addInstruction(Opcodes.IRETURN);
            } else {
                code.addInstruction(Opcodes.ARETURN);
            }
        } else {
            code.addInstruction(Opcodes.RETURN);
        }
    }

}