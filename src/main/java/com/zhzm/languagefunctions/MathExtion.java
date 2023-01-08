package com.zhzm.languagefunctions;

public class MathExtion {
    public static Number plus(Number var1, Number var2) {
        if (var1.getClass().getSimpleName().equals(Double.class.getSimpleName()) || var2.getClass().getSimpleName().equals(Double.class.getSimpleName()))
            return var1.doubleValue() + var2.doubleValue();
        else
            return var1.intValue() + var2.intValue();
    }
    public static Number minus(Number var1, Number var2) {
        if (var1.getClass().getSimpleName().equals(Double.class.getSimpleName()) || var2.getClass().getSimpleName().equals(Double.class.getSimpleName()))
            return var1.doubleValue() - var2.doubleValue();
        else
            return var1.intValue() - var2.intValue();
    }
    public static Number multi(Number var1, Number var2) {
        if (var1.getClass().getSimpleName().equals(Double.class.getSimpleName()) || var2.getClass().getSimpleName().equals(Double.class.getSimpleName()))
            return var1.doubleValue() * var2.doubleValue();
        else
            return var1.intValue() * var2.intValue();
    }
    public static Number div(Number var1, Number var2) {
        if (var1.getClass().getSimpleName().equals(Double.class.getSimpleName()) || var2.getClass().getSimpleName().equals(Double.class.getSimpleName()))
            return var1.doubleValue() / var2.doubleValue();
        else
            return var1.intValue() / var2.intValue();
    }
}
