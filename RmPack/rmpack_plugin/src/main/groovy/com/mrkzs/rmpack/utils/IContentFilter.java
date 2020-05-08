package com.mrkzs.rmpack.utils;

/**
 * Created by KINCAI
 * <p>
 * Desc TODO
 * <p>
 * Date 2018-4-6 11:38
 */

public interface IContentFilter<T> {
    boolean filter(T filtered);
}
