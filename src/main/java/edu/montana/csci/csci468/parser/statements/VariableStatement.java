package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.Expression;
import org.objectweb.asm.Opcodes;

import static edu.montana.csci.csci468.bytecode.ByteCodeGenerator.internalNameFor;

public class VariableStatement extends Statement {
    private Expression expression;
    private String variableName;
    private CatscriptType explicitType;
    private CatscriptType type;

    public Expression getExpression() {
        return expression;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public void setExpression(Expression parseExpression) {
        this.expression = addChild(parseExpression);
    }

    public void setExplicitType(CatscriptType type) {
        this.explicitType = type;
    }

    public CatscriptType getExplicitType() {
        return explicitType;
    }

    public boolean isGlobal() {
        return getParent() instanceof CatScriptProgram;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        expression.validate(symbolTable);
        if (symbolTable.hasSymbol(variableName)) {
            addError(ErrorType.DUPLICATE_NAME);
        } else {
            // TODO if there is an explicit type, ensure it is correct
            //      if not, infer the type from the right hand side expression

            // var x : int = 10
            // var x = 10
            try {
                Integer.parseInt(expression.toString());
                if (!explicitType.equals(CatscriptType.INT)) {
                    addError(ErrorType.INCOMPATIBLE_TYPES);
                    explicitType = CatscriptType.INT;
                }
            }
            catch(Exception e) {
                // do nothing
            }

            if (explicitType != null) {
                type = explicitType;
            } else {
                type = expression.getType();
            }
            symbolTable.registerSymbol(variableName, type);
        }
    }

    public CatscriptType getType() {
        return type;
    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
        Object value = expression.evaluate(runtime);
        runtime.setValue(variableName, value);
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        if (isGlobal()) {
            System.out.println("global");
            if (getType().equals(CatscriptType.INT) || getType().equals(CatscriptType.BOOLEAN)) {
                code.addField(variableName, "I"); // descriptor based on type of fieldf
            } else {
                System.out.println(internalNameFor(getType().getClass()));
                code.addField(variableName, "Ljava/lang/Object;"); // descriptor based on type of field
            }
            // push the 'this' pointer
            code.addVarInstruction(Opcodes.ALOAD, 0);
            // Compile the expression
            expression.compile(code);
            // save the expression result to the field
            //code.addFieldInstruction(Opcodes.PUTFIELD, variableName, "Ljava/lang/Object;", code.getProgramInternalName());

//            System.out.println(internalNameFor(getType().getClass()));
//            System.out.println(getType());
//            System.out.println(expression.getType());
//            System.out.println(code.getProgramInternalName());
            if (getType().equals(CatscriptType.INT) || getType().equals(CatscriptType.BOOLEAN)) {
                //box(code, getType());
                code.addFieldInstruction(Opcodes.PUTFIELD, variableName, "I", code.getProgramInternalName());
            } else {
                code.addFieldInstruction(Opcodes.PUTFIELD, variableName, "Ljava/lang/Object;", code.getProgramInternalName()); // maybe internalNameFor(getType().getClass()) for descriptor
            }
        } else {
            System.out.println("local");
            // Local variable
            Integer slot = code.createLocalStorageSlotFor(variableName);
            // Compile the expression
            expression.compile(code);
            // Store the expression result to the local variable slot
            if (expression.getType().equals(CatscriptType.INT) || expression.getType().equals(CatscriptType.BOOLEAN)) {
                code.addVarInstruction(Opcodes.ISTORE, slot); // ISTORE for int/boolean
            } else {
                code.addVarInstruction(Opcodes.ASTORE, slot); // ASTORE for reference types
            }
        }
    }
}
