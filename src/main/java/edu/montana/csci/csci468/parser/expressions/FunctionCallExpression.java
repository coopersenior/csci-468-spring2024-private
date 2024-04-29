package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.statements.CatScriptProgram;
import edu.montana.csci.csci468.parser.statements.FunctionDefinitionStatement;
import edu.montana.csci.csci468.parser.statements.Statement;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static edu.montana.csci.csci468.bytecode.ByteCodeGenerator.internalNameFor;

public class FunctionCallExpression extends Expression {
    private final String name;
    List<Expression> arguments;
    private CatscriptType type;

    public FunctionCallExpression(String functionName, List<Expression> arguments) {
        this.arguments = new LinkedList<>();
        for (Expression value : arguments) {
            this.arguments.add(addChild(value));
        }
        this.name = functionName;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public String getName() {
        return name;
    }

    @Override
    public CatscriptType getType() {
        return type;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        FunctionDefinitionStatement function = symbolTable.getFunction(getName());
        if (function == null) {
            addError(ErrorType.UNKNOWN_NAME);
            type = CatscriptType.OBJECT;
        } else {
            type = function.getType();
            if (arguments.size() != function.getParameterCount()) {
                addError(ErrorType.ARG_MISMATCH);
            } else {
                for (int i = 0; i < arguments.size(); i++) {
                    Expression argument = arguments.get(i);
                    argument.validate(symbolTable);
                    CatscriptType parameterType = function.getParameterType(i);
                    if (!parameterType.isAssignableFrom(argument.getType())) {
                        argument.addError(ErrorType.INCOMPATIBLE_TYPES);
                    }
                }
            }
        }
    }

    //==============================================================
    // Implementation
    //==============================================================

    @Override
    public Object evaluate(CatscriptRuntime runtime) {
        List<Object> parameters = new ArrayList<>();
        for (Expression argument : arguments) {
            Object argValue = argument.evaluate(runtime);
            parameters.add(argValue);
        }
        CatScriptProgram program = getProgram();
        FunctionDefinitionStatement function = program.getFunction(name);
        if (function != null) {
            return function.invoke(runtime, parameters);
        } else {
            return null;
        }
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        System.out.println("funcCallEx");
        code.addVarInstruction(Opcodes.ALOAD, 0);

        // Compile parameter expressions and box if needed
        for (Expression expression : arguments) {
            expression.compile(code);
            if (expression.getType().equals(CatscriptType.OBJECT)) {
                if (expression.getType().equals(CatscriptType.INT) || expression.getType().equals(CatscriptType.BOOLEAN)) {
                    box(code, expression.getType());
                }
            }

        }
        // Invoke the function
        code.addMethodInstruction(Opcodes.INVOKEVIRTUAL,
                code.getProgramInternalName(), name, getProgram().getFunction(name).getDescriptor());
    }
//    code.addVarInstruction(Opcodes.ALOAD, 0);
//
//    // Compile parameter expressions and box if needed
//        for (Expression expression : arguments) {
//        expression.compile(code);
//        if (expression.getType().equals(CatscriptType.OBJECT)) {
//            box(code, expression.getType());
//        }
//    }
//
//    // Invoke the function (assuming it returns an integer)
//        code.addMethodInstruction(Opcodes.INVOKEVIRTUAL,
//            code.getProgramInternalName(), "print", "(Ljava/lang/Integer;)Z");
//
//    // Check if the function returns an integer before conversion and printing
//        if (getProgram().getFunction(name).getType().equals(CatscriptType.INT)) {
//        // Print the return value directly (no conversion needed)
//        code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, code.getProgramInternalName(), "print", "(Ljava/lang/Object;)V");
//    }


}
