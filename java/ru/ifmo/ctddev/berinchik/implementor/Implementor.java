package ru.ifmo.ctddev.berinchik.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.*;
import java.nio.file.Path;
import java.util.*;

public class Implementor implements Impler {
    final private static Map<Class, String> defaultValues;

    private void createFile(final File file) throws ImplerException {
        if (file.exists()) {
            return;
        }
        try {
            file.getParentFile().mkdirs();
            if (!file.createNewFile()) {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new ImplerException("Couldn't create file " + file.getAbsolutePath());
        }
    }

    private String typeToString(Class<?> token) {
        Class<?> type = token.isArray() ? token.getComponentType() : token;
        return type.isPrimitive() ? "" : type.getCanonicalName();
    }

    private String getImports(Class<?> token) throws ImplerException {
        SortedSet<String> set = new TreeSet<>();
        for (Method method : getMethodList(token)) {
            int mod = method.getModifiers();
            if (!Modifier.isAbstract(mod)
                    || Modifier.isPrivate(mod)
                    || Modifier.isFinal(mod)) continue;
            set.add(typeToString(method.getReturnType()));
            for (Class<?> type : method.getParameterTypes()) {
                set.add(typeToString(type));
            }
        }
        set.remove("");
        StringBuilder sb = new StringBuilder();
        for (String x : set) {
            sb.append("import ").append(x).append(";\n");
        }
        return sb.toString();
    }

    private String getWrapper(final String inner, final Class<?> token) {
        return "@SuppressWarnings(\"unchecked\")\n"
                .concat("@Deprecated\n")
                .concat(getModifiers(token.getModifiers()))
                .concat("class " + token.getSimpleName() + "Impl")
                .concat((Modifier.isInterface(token.getModifiers()) ? " implements " : " extends "))
                .concat(token.getSimpleName())
                .concat("{ " + inner + "}");
    }

    private String getPackage(final Class<?> token) {
        return (token.getPackage() != null ? "package " + token.getPackage().getName() + ";\n" : "");
    }

    private File getTargetFile(final Class<?> token, final Path root) throws ImplerException {
        final Package pack = token.getPackage();
        String child = token.getSimpleName() + "Impl.java";
        if (pack != null) {
            child = pack.getName().replace('.', '/') + "/" + child;
        }
        final File file = new File(root.toAbsolutePath().toString(), child);
        createFile(file);
        return file;
    }

    private String getParameterTypes(Class<?>[] params, boolean parameterized) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < params.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            if (parameterized) {
                sb.append(params[i].getCanonicalName()).append(" ");
            }
            sb.append(varName(i));
        }
        return sb.append(")").toString();
    }

    private String getConstructors(Class<?> token) throws ImplerException {
        StringBuilder cons = new StringBuilder();
        try {
            for (Constructor constructor : token.getDeclaredConstructors()) {
                int mod = constructor.getModifiers();
                if (Modifier.isPrivate(mod)) {
                    continue;
                }
                cons.append(getModifiers(mod))
                        .append(token.getSimpleName())
                        .append("Impl")
                        .append(getParameterTypes(constructor.getParameterTypes(), true))
                        .append("{ super")
                        .append(getParameterTypes(constructor.getParameterTypes(), false))
                        .append("; }\n");
            }
        } catch (SecurityException e) {
            throw new ImplerException("SecurityException while accesing to constructors");
        }
        if (cons.length() == 0 && !token.isInterface()) {
            throw new ImplerException("Can't inherit any constructor");
        }
        return cons.toString();
    }

    private String getMethods(Class<?> token) throws ImplerException {
        StringBuilder methods = new StringBuilder();

        for (Method m : getMethodList(token)) {
            int mod = m.getModifiers();
            if (!Modifier.isAbstract(mod)
                    || Modifier.isPrivate(mod)
                    || Modifier.isFinal(mod)) continue;

            methods.append(getModifiers(mod))
                    .append(m.getReturnType().getCanonicalName()).append(" ")
                    .append(m.getName())
                    .append(getParameterTypes(m.getParameterTypes(), true))
                    .append("{\n");
            methods.append("return ");
            Class<?> result = m.getReturnType();
            methods.append(result.isPrimitive() ? defaultValues.get(result) : "null").append(";\n}\n\n");
        }
        return methods.toString();
    }

    private String getImplementedClass(Class<?> token) throws ImplerException {
        return getPackage(token)
                .concat(getImports(token))
                .concat(getWrapper(getConstructors(token) + getMethods(token), token));
    }


    private List<Method> getMethodList(Class<?> token) throws ImplerException {
        Queue <Class<?>> queue = new ArrayDeque<>();
        queue.add(token);
        List<MethodShell> metShellList = new LinkedList<>();

        try {
            while (!queue.isEmpty()) {
                Class<?> inter = queue.poll();
                for (Method m : inter.getDeclaredMethods()) {
                    MethodShell ms = new MethodShell(m);
                    if (!metShellList.contains(ms)) {
                        metShellList.add(ms);
                    }
                }
                Class<?> ancestor = inter.getSuperclass();
                if (ancestor != null) {
                    queue.add(ancestor);
                }
                Collections.addAll(queue, inter.getInterfaces());
            }
        } catch (SecurityException e) {
            throw new ImplerException("Security exception while accessing to methods");
        }

        List<Method> metList = new LinkedList<>();
        for (MethodShell ms : metShellList) {
            metList.add(ms.m);
        }
        return metList;
    }

    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        if (Modifier.isFinal(token.getModifiers())) {
            throw new ImplerException("Inherent class is final!");
        }
        final File targetFile = getTargetFile(token, root);
        try {
            Writer writer = new FileWriter(targetFile);
            writer.write(getImplementedClass(token));
            try {
                writer.close();
            } catch (IOException e) {
                throw new ImplerException("Can't close file " + targetFile);
            }
        } catch (IOException e) {
            throw new ImplerException("Couldn't write in file " + targetFile);
        }
    }

    public static void main(String[] args) {

    }


    private String getModifiers(int mod) {
        return Modifier.toString(mod & ~Modifier.ABSTRACT & ~Modifier.INTERFACE & ~Modifier.TRANSIENT) + " ";
    }

    static {
        defaultValues = new HashMap<>();
        defaultValues.put(long.class, "42L");
        defaultValues.put(int.class, "42");
        defaultValues.put(float.class, "13.0f");
        defaultValues.put(double.class, "2.0d");
        defaultValues.put(short.class, "10");
        defaultValues.put(byte.class, "11");
        defaultValues.put(char.class, "'\u0042'");
        defaultValues.put(boolean.class, "true");
        defaultValues.put(void.class, "");
    }

    private String varName(int index) {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append((char) (index % 10 + 'a'));
            index /= 10;
        } while (index > 0);
        return sb.toString();
    }

    private class MethodShell {
        private final Method m;

        @Override
        public boolean equals(Object obj) {
            return m.getName().equals(((MethodShell) obj).m.getName()) &&
                    Arrays.equals(m.getParameterTypes(), ((MethodShell) obj).m.getParameterTypes());
        }

        MethodShell(Method m) {
            this.m = m;
        }
    }
}
