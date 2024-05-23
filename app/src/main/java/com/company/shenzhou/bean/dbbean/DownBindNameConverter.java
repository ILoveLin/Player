package com.company.shenzhou.bean.dbbean;

import com.google.gson.Gson;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LoveLin
 * <p>
 * Describe 实现PropertyConverter,从而实现存储list
 */
public class DownBindNameConverter implements PropertyConverter<List<DownBindNameListBean>, String> {
    //将数据库中的值，转化为实体Bean类对象(比如List<String>)
    @Override
    public List<DownBindNameListBean> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        List<String> list_str = Arrays.asList(databaseValue.split(","));
        List<DownBindNameListBean> list_transport = new ArrayList<>();
        for (String s : list_str) {
            list_transport.add(new Gson().fromJson(s, DownBindNameListBean.class));
        }
        return list_transport;

//
//
//        /**ieno
//         * 此方法解决了这个报错:JsonSyntaxException: java.io.EOFException: End of input at line 1 column 19 path $.ImageContent
//         */
//        try {
//            CaseDBBean.ImagesBean[] array = new Gson().fromJson(databaseValue, CaseDBBean.ImagesBean[].class);
//            List<CaseDBBean.ImagesBean> list_str = Arrays.asList(array);
//            List<CaseDBBean.ImagesBean> srcBeanList = new ArrayList<>();
//            if (list_str.size() > 0) {
//                srcBeanList.addAll(list_str);
//            }
//            return srcBeanList;
//        } catch (Exception e) {
//            LogUtils.e("ReportImageConverter类的37行(最新版),JsonSyntaxException: java.io.EOFException: End of input at line 1 column 19 path $.ImageContent");
//            return null;
//        }
    }


    //将实体Bean类(比如List<String>)转化为数据库中的值(比如String)
    @Override
    public String convertToDatabaseValue(List<DownBindNameListBean> arrays) {
        if (arrays == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            for (DownBindNameListBean array : arrays) {
                String str = new Gson().toJson(array);
                sb.append(str);
                sb.append(",");
            }
            return sb.toString();

        }
    }
}
