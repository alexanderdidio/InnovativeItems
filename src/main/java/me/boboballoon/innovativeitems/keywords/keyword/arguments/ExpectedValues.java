package me.boboballoon.innovativeitems.keywords.keyword.arguments;

import me.boboballoon.innovativeitems.keywords.keyword.KeywordContext;
import me.boboballoon.innovativeitems.util.InitializationUtil;
import me.boboballoon.innovativeitems.util.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A class that represents a "primitive" type that can be parsed for a keywords arguments
 */
public class ExpectedValues implements ExpectedArguments {
    private final ExpectedPrimitives primitive;
    private final Consumer<KeywordContext> onError;

    public ExpectedValues(@NotNull ExpectedPrimitives primitive, @Nullable Consumer<KeywordContext> onError) {
        this.primitive = primitive;
        this.onError = onError;
    }

    public ExpectedValues(@NotNull ExpectedPrimitives primitive, @NotNull String fieldName) {
        this(primitive, context -> LogUtil.logKeywordError(context, fieldName));
    }

    public ExpectedValues(@NotNull ExpectedPrimitives primitive) {
        this(primitive, (Consumer<KeywordContext>) null);
    }

    /**
     * A method that returns the expected value to parse
     *
     * @return the expected value to parse
     */
    public ExpectedPrimitives getPrimitive() {
        return this.primitive;
    }

    /**
     * A method that returns the method to be called on if the parsing fails for any reason
     *
     * @return the method to be called on if the parsing fails for any reason
     */
    @Nullable
    public Consumer<KeywordContext> getOnError() {
        return this.onError;
    }

    /**
     * A method used to parse a given value based on the primitive type selected on this class
     *
     * @param rawValue the raw value
     * @param context the context in which the argument is being parsed
     * @return the parsed value
     */
    @Nullable
    public Object getValue(String rawValue, KeywordContext context) {
        if (this.primitive == ExpectedPrimitives.STRING) {
            return rawValue;
        }

        if (this.primitive == ExpectedPrimitives.CHAR) {
            return this.parseChar(rawValue, context);
        }

        if (this.primitive == ExpectedPrimitives.BOOLEAN) {
            return this.parseBoolean(rawValue, context);
        }

        if (this.primitive == ExpectedPrimitives.BYTE ||
                this.primitive == ExpectedPrimitives.SHORT ||
                this.primitive == ExpectedPrimitives.INTEGER ||
                this.primitive == ExpectedPrimitives.LONG ||
                this.primitive == ExpectedPrimitives.FLOAT ||
                this.primitive == ExpectedPrimitives.DOUBLE) {
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
    private Number parseNumber(String rawValue, KeywordContext context) {
        try {
            return (Number) InitializationUtil.initNumberUnsafe(rawValue, this.primitive.getRepresentingClass());
        } catch (Throwable e) {
            if (this.onError != null) {
                this.onError.accept(context);
            }
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
    private Character parseChar(String rawValue, KeywordContext context) {
        try {
            return InitializationUtil.initChar(rawValue);
        } catch (Throwable e) {
            if (this.onError != null) {
                this.onError.accept(context);
            }
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
    private Boolean parseBoolean(String rawValue, KeywordContext context) {
        try {
            return InitializationUtil.initBoolean(rawValue);
        } catch (Throwable e) {
            if (this.onError != null) {
                this.onError.accept(context);
            }
            return null;
        }
    }

    /**
     * A class used to list all possible primitive types to be parsed
     */
    public enum ExpectedPrimitives {
        BYTE(byte.class),
        SHORT(short.class),
        INTEGER(int.class),
        LONG(long.class),
        FLOAT(float.class),
        DOUBLE(double.class),
        BOOLEAN(boolean.class),
        CHAR(char.class),
        STRING(String.class);

        private final Class<?> clazz;

        ExpectedPrimitives(Class<?> clazz) {
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
