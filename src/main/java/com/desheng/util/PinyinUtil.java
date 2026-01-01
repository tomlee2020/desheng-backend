package com.desheng.util;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * 拼音转换工具类
 * 用于将中文转换为拼音，支持全拼和简拼
 */
@Slf4j
public class PinyinUtil {

    /**
     * 获取全拼（带声调）
     * 例如：水稻 -> shui dao
     */
    public static String getPinyinWithTone(String chinese) {
        try {
            return PinyinHelper.convertToPinyinString(chinese, " ", PinyinFormat.WITH_TONE_MARK);
        } catch (Exception e) {
            log.warn("Failed to convert to pinyin with tone: {}", chinese, e);
            return chinese;
        }
    }

    /**
     * 获取全拼（不带声调）
     * 例如：水稻 -> shui dao
     */
    public static String getPinyinWithoutTone(String chinese) {
        try {
            return PinyinHelper.convertToPinyinString(chinese, " ", PinyinFormat.WITHOUT_TONE);
        } catch (Exception e) {
            log.warn("Failed to convert to pinyin without tone: {}", chinese, e);
            return chinese;
        }
    }

    /**
     * 获取简拼（首字母）
     * 例如：水稻 -> sd
     */
    public static String getPinyinShort(String chinese) {
        try {
            return PinyinHelper.getShortPinyin(chinese);
        } catch (Exception e) {
            log.warn("Failed to convert to short pinyin: {}", chinese, e);
            return chinese;
        }
    }

    /**
     * 获取全拼（不带声调，用于搜索）
     * 例如：水稻 -> shuidao
     */
    public static String getPinyinForSearch(String chinese) {
        try {
            return PinyinHelper.convertToPinyinString(chinese, "", PinyinFormat.WITHOUT_TONE);
        } catch (Exception e) {
            log.warn("Failed to convert to pinyin for search: {}", chinese, e);
            return chinese;
        }
    }
}
