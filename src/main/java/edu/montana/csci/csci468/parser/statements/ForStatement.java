package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.Expression;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.util.LinkedList;
import java.util.List;

import static edu.montana.csci.csci468.bytecode.ByteCodeGenerator.internalNameFor;

public class ForStatement extends Statement {
    private Expression expression;
    private String variableName;
    private List<Statement> body;

    public void setExpression(Expression expression) {
        this.expression = addChild(expression);
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public void setBody(List<Statement> statements) {
        this.body = new LinkedList<>();
        for (Statement statement : statements) {
            this.body.add(addChild(statement));
        }
    }

    public Expression getExpression() {
        return expression;
    }

    public String getVariableName() {
        return variableName;
    }

    public List<Statement> getBody() {
        return body;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        symbolTable.pushScope();
        if (symbolTable.hasSymbol(variableName)) {
            addError(ErrorType.DUPLICATE_NAME);
        } else {
            expression.validate(symbolTable);
            CatscriptType type = expression.getType();
            if (type instanceof CatscriptType.ListType) {
                symbolTable.registerSymbol(variableName, getComponentType());
            } else {
                addError(ErrorType.INCOMPATIBLE_TYPES, getStart());
                symbolTable.registerSymbol(variableName, CatscriptType.OBJECT);
            }
        }
        for (Statement statement : body) {
            statement.validate(symbolTable);
        }
        symbolTable.popScope();
    }

    private CatscriptType getComponentType() {
        return ((CatscriptType.ListType) expression.getType()).getComponentType();
    }

    // for (x in [1 2 3]) {print(x) }
    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
        List evalute = (List) expression.evaluate(runtime);
        for (Object obj : evalute) {
            runtime.pushScope();
            runtime.setValue(variableName, obj);
            for (Statement statement  : body) {
                statement.execute(runtime);
            }
            runtime.popScope();
        }
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        Integer i = code.nextLocalStorageSlot();
        Integer loopVariableSlot = code.createLocalStorageSlotFor(variableName);
        Label startOfLoop = new Label();
        Label endOfLoop = new Label();
        expression.compile(code);
        // compile the expression - leaves a list on top of the operand stack
        // invoke INVOKEINTERFACE java/util/List.operator ()Ljava/util/Iterator
        code.addMethodInstruction(Opcodes.INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;");
        // store that iterator into the iterator slot
        code.addVarInstruction(Opcodes.ASTORE, i);
        // add startOfLoop label (to jump back to at end of loop)
        code.addLabel(startOfLoop);
        // ALOAD the iterator slot
        code.addVarInstruction(Opcodes.ALOAD, i);
        // invoke hasNext
        code.addMethodInstruction(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z");
        // IFEQ (if thats false) jump to endOfLoop label (and later)
        code.addJumpInstruction(Opcodes.IFEQ, endOfLoop);
        // ALOAD iterator again
        code.addVarInstruction(Opcodes.ALOAD, i);
        // call next() on it
        code.addMethodInstruction(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;");
        // do a checkCast
        code.addTypeInstruction(Opcodes.CHECKCAST, internalNameFor(getComponentType().getJavaType()));
        // save that into the loop variable slot (might be boolean/int or reference type)
        if (getComponentType().equals(CatscriptType.INT) || getComponentType().equals(CatscriptType.BOOLEAN)) {
            code.addVarInstruction(Opcodes.ASTORE, loopVariableSlot);
            code.addVarInstruction(Opcodes.ALOAD, loopVariableSlot);
            unbox(code, getComponentType());
            code.addVarInstruction(Opcodes.ISTORE, loopVariableSlot);
        } else {
            // Store the reference type
            code.addVarInstruction(Opcodes.ASTORE, loopVariableSlot); // ASTORE for reference types
        }
        // compile loop body statement
        for (Statement statement : body) {
            statement.compile(code);
        }
        // unconditional jump to goto start of loop
        code.addJumpInstruction(Opcodes.GOTO, startOfLoop);
        code.addLabel(endOfLoop);
    }

}
