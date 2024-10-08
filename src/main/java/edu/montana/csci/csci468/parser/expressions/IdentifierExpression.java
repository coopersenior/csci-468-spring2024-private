package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import org.objectweb.asm.Opcodes;

import static edu.montana.csci.csci468.bytecode.ByteCodeGenerator.internalNameFor;

public class IdentifierExpression extends Expression {
    private final String name;
    private CatscriptType type;

    public IdentifierExpression(String value) {
        this.name = value;
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
        CatscriptType type = symbolTable.getSymbolType(getName());
        if (type == null) {
            addError(ErrorType.UNKNOWN_NAME);
        } else {
            this.type = type;
        }
    }

    //==============================================================
    // Implementation
    //==============================================================

    @Override
    public Object evaluate(CatscriptRuntime runtime) {
        return runtime.getValue(name);
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        Integer i = code.resolveLocalStorageSlotFor(name);
        if (i != null) {
            // Local variable exists, load its value
            if (getType().equals(CatscriptType.INT) || getType().equals(CatscriptType.BOOLEAN)) {
                code.addVarInstruction(Opcodes.ILOAD, i);
            } else {
                code.addVarInstruction(Opcodes.ALOAD, i);
            }
        } else {
            code.addVarInstruction(Opcodes.ALOAD, 0);
            // No local variable, assume it's a field
            if (getType().equals(CatscriptType.INT) || getType().equals(CatscriptType.BOOLEAN)) {
                if (getType().equals(CatscriptType.NULL)) {
                    code.addInstruction(Opcodes.ACONST_NULL);
                } else {
                    code.addFieldInstruction(Opcodes.GETFIELD, name, "I", code.getProgramInternalName());
                }
            } else {
                if (getType().equals(CatscriptType.NULL)) {
                    code.addInstruction(Opcodes.ACONST_NULL);
                } else {
                    code.addFieldInstruction(Opcodes.GETFIELD, name, internalNameFor(getType().getClass()), code.getProgramInternalName());
                }
            }
        }
    }
}
