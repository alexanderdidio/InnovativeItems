package me.boboballoon.innovativeitems.functions.arguments;

import me.boboballoon.innovativeitems.functions.FunctionContext;
import me.boboballoon.innovativeitems.util.InitializationUtil;
import me.boboballoon.innovativeitems.util.LogUtil;
import me.boboballoon.innovativeitems.util.TextUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A class that represents a "primitive" type that can be parsed for a keywords arguments
 */
public class ExpectedPrimitive implements ExpectedArguments {
    private final PrimitiveType primitive;
    private final Consumer<FunctionContext> onError;
    private final Predicate<Object> condition;

    public ExpectedPrimitive(@NotNull PrimitiveType primitive, @Nullable Consumer<FunctionContext> onError, @Nullable Predicate<Object> condition) {
        this.primitive = primitive;
        this.onError = onError;
        this.condition = condition;
    }

    public ExpectedPrimitive(@NotNull PrimitiveType primitive, @Nullable Consumer<FunctionContext> onError) {
        this(primitive, onError, null);
    }

    public ExpectedPrimitive(@NotNull PrimitiveType primitive, @Nullable Predicate<Object> condition) {
        this(primitive, (Consumer<FunctionContext>) null, condition);
    }

    public ExpectedPrimitive(@NotNull PrimitiveType primitive, @NotNull String fieldName, @Nullable Predicate<Object> condition) {
        this(primitive, context -> LogUtil.logFunctionError(context, fieldName), condition);
    }

    public ExpectedPrimitive(@NotNull PrimitiveType primitive, @NotNull String fieldName) {
        this(primitive, fieldName, null);
    }

    public ExpectedPrimitive(@NotNull PrimitiveType primitive) {
        this(primitive, (Consumer<FunctionContext>) null, null);
    }

    /**
     * A method that returns the expected value to parse
     *
     * @return the expected value to parse
     */
    public PrimitiveType getPrimitive() {
        return this.primitive;
    }

    /**
     * A method that returns the method to be called on if the parsing fails for any reason
     *
     * @return the method to be called on if the parsing fails for any reason
     */
    @NotNull
    @Override
    public Consumer<FunctionContext> getOnError() {
        return this.onError != null ? this.onError : ExpectedArguments.DEFAULT_ERROR;
    }

    /**
     * A method that returns a predicate that will be used to test the final parsed object
     *
     * @return a predicate that will be used to test the final parsed object
     */
    @Nullable
    public Predicate<Object> getCondition() {
        return this.condition;
    }

    /**
     * A method used to parse a given value based on the primitive type selected on this class
     *
     * @param rawValue the raw value
     * @param context the context in which the argument is being parsed
     * @return the parsed value
     */
    @Nullable
    @Override
    public Object getValue(@NotNull String rawValue, @NotNull FunctionContext context) {
        if (this.primitive == PrimitiveType.STRING) {
            return this.parseString(rawValue, context);
        }

        if (this.primitive == PrimitiveType.CHAR) {
            return this.parseChar(rawValue, context);
        }

        if (this.primitive == PrimitiveType.BOOLEAN) {
            return this.parseBoolean(rawValue, context);
        }

        if (this.primitive == PrimitiveType.BYTE ||
                this.primitive == PrimitiveType.SHORT ||
                this.primitive == PrimitiveType.INTEGER ||
                this.primitive == PrimitiveType.LONG ||
                this.primitive == PrimitiveType.FLOAT ||
                this.primitive == PrimitiveType.DOUBLE) {
            return this.parseNumber(rawValue, context);
        }

        throw new UnsupportedOperationException("The following parsing was unable to be completed for ability " + context.getAbilityName() + "!");
    }

    /**
     * A method used to parse the provided number unsafely
     *
     * @param rawValue the raw value of the number
     * @param context the context in which the argument is being parsed
     * @return the number that is represented by the provided rawValue
     */
    private Number parseNumber(String rawValue, FunctionContext context) {
        try {
            Number value = InitializationUtil.initNumber(rawValue, (Class<? extends Number>) this.primitive.getRepresentingClass());

            if (this.condition != null && !this.condition.test(value)) {
                this.throwError(context);
                return null;
            }

            return value;
        } catch (Throwable ee) {
            this.throwError(context);
            return null;
        }
    }

    /**
     * A method used to parse the provided char
     *
     * @param rawValue the raw value of the char
     * @param context the context in which the argument is being parsed
     * @return the number that is represented by the provided rawValue
     */
    private Character parseChar(String rawValue, FunctionContext context) {
        try {
            char value = InitializationUtil.initChar(rawValue);

            if (this.condition != null && !this.condition.test(value)) {
                this.throwError(context);
                return null;
            }

            return value;
        } catch (Throwable e) {
            this.throwError(context);
            return null;
        }
    }

    /**
     * A method used to parse the provided boolean
     *
     * @param rawValue the raw value of the boolean
     * @param context the context in which the argument is being parsed
     * @return the number that is represented by the provided rawValue
     */
    private Boolean parseBoolean(String rawValue, FunctionContext context) {
        try {
            boolean value = InitializationUtil.initBoolean(rawValue);

            if (this.condition != null && !this.condition.test(value)) {
                this.throwError(context);
                return null;
            }

            return value;
        } catch (Throwable e) {
            this.throwError(context);
            return null;
        }
    }

    /**
     * A method used to parse the provided string
     *
     * @param rawValue the raw value of the string
     * @param context the context in which the argument is being parsed
     * @return the string that is represented by the provided rawValue
     */
    private String parseString(String rawValue, FunctionContext context) {
        if (this.condition != null && !this.condition.test(rawValue)) {
            this.throwError(context);
            return null;
        }

        return TextUtil.format(rawValue);
    }

    /**
     * A method used to check if the onError field is null and if not, throw the custom error
     *
     * @param context the context in which the argument is being parsed
     */
    private void throwError(FunctionContext context) {
        if (this.onError != null) {
            this.onError.accept(context);
        }
    }

    /**
     * A class used to list all possible primitive types to be parsed
     */
    public enum PrimitiveType {
        BYTE(Byte.class), //all numerical types must be the wrappers due to reflection usage on the InitializationUtil.initNumber() method
        SHORT(Short.class),
        INTEGER(Integer.class),
        LONG(Long.class),
        FLOAT(Float.class),
        DOUBLE(Double.class),
        BOOLEAN(boolean.class),
        CHAR(char.class),
        STRING(String.class);

        private final Class<?> clazz;

        PrimitiveType(Class<?> clazz) {
            this.clazz = clazz;
        }

        /**
         * A method used to return what class each type represents
         *
         * @return what class each type represents
         */
        public Class<?> getRepresentingClass() {
            return this.clazz;
        }
    }
}
