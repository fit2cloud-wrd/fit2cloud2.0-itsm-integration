package com.fit2cloud.itsm.common.constants;

/**
 * @description: api字段映射源类型与目标类型
 * @author zhaoqian
 * @date 2022/6/28 10:27 上午
*/
public class ParameterType {

    public enum ORIGIN_FIELD_TYPE {
        String("String", "字符串(String)"),
        Integer("Integer", "整型(Integer)"),
        Long("Long", "长整型(Long)"),
        Double("Double", "双浮点型(Double)"),
        Boolean("Boolean", "布尔类型(Boolean)"),
        Dictionary("Dictionary", "字典"),
        ;

        ORIGIN_FIELD_TYPE(String type, String desc) {
            this.type = type;
            this.desc = desc;
        }

        private final String type;

        private final String desc;

        public String getType() {
            return type;
        }

        public String getDesc() {
            return desc;
        }
    }

    public enum TARGET_FIELD_TYPE {
        String("String", "字符串(String)"),
        Integer("Integer", "整型(Integer)"),
        Long("Long", "长整型(Long)"),
        Double("Double", "双浮点型(Double)"),
        Boolean("Boolean", "布尔类型(Boolean)"),
        Date("Date", "日期型"),
        StringList("List<String>", "字符串集合(List<String>)"),
        ;

        TARGET_FIELD_TYPE(String type, String desc) {
            this.type = type;
            this.desc = desc;
        }

        private final String type;

        private final String desc;

        public String getType() {
            return type;
        }

        public String getDesc() {
            return desc;
        }
    }

}
