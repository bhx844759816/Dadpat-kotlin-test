package com.bhx.common.utils;


import java.lang.reflect.ParameterizedType;

public class ReflexUtils {
    /**
     * 实例化第i个泛型参数对象
     *
     * @param object 类
     * @param i      第几个泛型
     * @param <T>    实例对象
     * @return
     */
    public static <T> T getNewInstance(Object object, int i) {
        if (object != null) {
            try {
                return ((Class<T>) ((ParameterizedType) (object.getClass()
                        .getGenericSuperclass())).getActualTypeArguments()[i])
                        .newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }

        }
        return null;

    }

    public static <T> T getInstance(Object object, int i) {
        if (object != null) {
            return (T) ((ParameterizedType) object.getClass()
                    .getGenericSuperclass())
                    .getActualTypeArguments()[i];
        }
        return null;

    }


}
