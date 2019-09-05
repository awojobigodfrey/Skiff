package io.dallen.compiler;

import java.util.*;

public class CompileScope {
    private Map<String, CompiledObject> variableTable = new HashMap<>();

    private CompileScope parent;

    public CompileScope(CompileScope parent) {
        this.parent = parent;
    }

    public void loadBuiltins() {
        declareObject(BuiltinTypes.VOID);
        declareObject(BuiltinTypes.STRING);
        declareObject(BuiltinTypes.INT);
        declareObject(BuiltinTypes.BOOL);
        declareObject(BuiltinTypes.LIST);
        declareObject(BuiltinTypes.EXCEPTION);

        declareObject(new CompiledFunction(
            "println",
            "skiff_println",
              Collections.singletonList(BuiltinTypes.STRING)));
    }

    public void declareObject(CompiledObject decVar) {
        try {
            getObject(decVar.getName());
            throw new UnsupportedOperationException("Cannot redefine variable " + decVar.getName());
        } catch(NoSuchElementException ex) {
            variableTable.put(decVar.getName(), decVar);
        }
    }

    public CompiledObject getObject(String name) throws NoSuchElementException {
        CompiledObject varFor = variableTable.get(name);
        if(varFor != null) {
            return varFor;
        }

        if(parent == null) {
            throw new NoSuchElementException(name);
        }

        return parent.getObject(name);
    }

    public CompiledFunction getFunction(String name) throws CompileException {
        CompiledObject varFor = getObject(name);
        if(!(varFor instanceof CompiledFunction)) {
            throw new CompileException("Variable '" + name + "' is not a function", null);
        }

        return (CompiledFunction) varFor;
    }

    public List<CompiledObject> getLocals() {
        return new ArrayList<>(variableTable.values());
    }
}
