package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {     //元数据处理器类 ==> 被@TableField(fill = FieldFill.*)注解的元数据

    /**
     * 处理 创建时(@TableField(fill = FieldFill.INSERT))字段的值
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        //添加时为(@TableField(fill = FieldFill.INSERT))字段赋值当前时间
        if(metaObject.hasSetter("createTime")) {
            metaObject.setValue("createTime", LocalDateTime.now());
        }
        if(metaObject.hasSetter("updateTime")) {
            metaObject.setValue("updateTime", LocalDateTime.now());
        }

        /*添加时为(@TableField(fill = FieldFill.INSERT))字段赋值当前执行操作用户的 id
        其中 id 的值由线程局部变量ThreadLocal实例提供*/
        if(metaObject.hasSetter("createUser")) {
            metaObject.setValue("createUser", BaseContext.getCurrentId());
        }
        if(metaObject.hasSetter("updateUser")) {
            metaObject.setValue("updateUser", BaseContext.getCurrentId());
        }
    }

    /**
     * 处理 修改时(@TableField(fill = FieldFill.UPDATE))字段的值
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {

        //修改时为(@TableField(fill = FieldFill.UPDATE))字段赋值当前时间
        if(metaObject.hasSetter("updateTime")) {
            metaObject.setValue("updateTime", LocalDateTime.now());
        }

        //修改时为(@TableField(fill = FieldFill.UPDATE))字段赋值当前执行操作用户的 id(同上)
        if(metaObject.hasSetter("updateUser")) {
            metaObject.setValue("updateUser", BaseContext.getCurrentId());
        }
    }
}
