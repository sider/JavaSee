package com.github.kmizu.java_see;

public class Libs {
    public static interface SupplierWithException<T> {
        T get() throws Exception;
    }
    public static <T> T wrapException(SupplierWithException<T> block) {
        try {
            return block.get();
        } catch (RuntimeException e) {
            //rethrow
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
