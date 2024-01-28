package com.witeam.service.common.schema.data.exception;


public enum DataSchemaErrorCode
{
    //SchemaException中返回的错误码: 000000-099999
    /**
     * 系统内部错误
     */
    SERVER_ERROR("0010000000", "server internal error."),

    /**
     * 参数错误
     */
    BAD_PARAMETERS("0010000001", "mapping parameter error."),

    /**
     * 数据转换错误
     */
    DATA_PARSE_ERROR("0010000002", "data parse error."),

    /**
     * 数据为空
     */
    EMPTY_DATA("0010000003", "data can not be empty."),

    /**
     * 参数校验错误
     */
    PARAMS_VALIDATE_ERROR("0010000004", "parameter validate error."),

    /**
     * 路径深度超长
     */
    OUTSIDE_PATH_DEPTH("0010000005", "outside path depth."),

    /**
     * 非法字段合并（只能合并相同类型字段）
     */
    INVALID_MERGE("0010000006", "can not merge diffrent type field."),

    /**
     * BOOL类型只能有两种取值
     */
    BOOL_MAP_SIZE_ERROR("0010000007", "bool map size must be 2."),

    /**
     * BOOL类型的值只能是0或1
     */
    BOOL_VALUE_ERROR("0010000008", "bool value must be 0 or 1."),

    /**
     * 数组元素为null
     */
    ARRAY_ITEMS_IS_NULL("001000009", "array items is null."),

    /**
     * Schema的type和definitions都为空
     */
    TYPE_IS_NULL_AND_DEFINITIONS_IS_NULL("0010000010", "type is null and definitions is null."),

    /**
     * 不支持这种数据类型
     */
    NOT_SUPPORT_THE_DATA_TYPE("0010000011", "not allowed object type to parse"),

    /**
     * JSON字符串为空
     */
    JSON_STRING_IS_EMPTY("0010000012", "json string is empty"),

    /**
     * JSON字符串解析错误
     */
    JSON_STRING_PARSE_ERROR("0010000013", "json string parse error"),

    /**
     * 字段的最大长度为null
     */
    FIELD_MAX_LENGTH_IS_NULL("0010000014", "field.getMaxLength() is null."),

    /**
     * Integer型数据解析出错
     */
    INTEGER_PARSE_ERROR("0010000015", "please ensure int value, ranging from -2147483648 to 2147483647"),

    /**
     * Long型数据解析出错
     */
    LONG_PARSE_ERROR("0010000016", "please ensure long value, ranging from -9223372036854774808 to 9223372036854774807"),

    /**
     * Float型数据解析出错
     */
    FLOAT_PARSE_ERROR("0010000017", "please ensure float value, ranging from -3.40282346638528860E38 to 3.40282346638528860E38"),

    /**
     * Double型数据解析出错
     */
    DOUBLE_PARSE_ERROR("0010000018", "please ensure double value, ranging from -1.7976931348623157E308 to 1.7976931348623157E308"),

    /**
     * 值必须大于0
     */
    VALUE_MUST_MORE_THAN_ZERO("0010000019", "the value must more than zero"),

    /**
     * 时间戳超过最大值
     */
    EXCEED_THE_MAX_TIMESTAMP_LIMIT("0010000020", "Exceeding the maximum timestamp limit 7258118400000L"),


    /**
     * 对象数据为空
     */
    OBJECT_DATA_IS_EMPTY("0010000021", "Object field %s cannot be empty"),

    /**
     * 数组数据为空
     */
    ARRAY_DATA_IS_EMPTY("0010000022", "Array field %s cannot be empty"),

    /**
     * 数组元素不能为布尔型
     */
    ARRAY_ITEMS_IS_BOOLEAN("0010000023", "Array element cannot be boolean"),

    //Result.Error中返回的错误码: 100000-999999

    /**
     * Object字段为空
     */
    OBJECT_FIELD_IS_NULL("0010100001", "object field is null."),

    /**
     * 非Object类型的值
     */
    VALUE_IS_NOT_OBJECT("0010100002", "value is not object."),

    /**
     * 非Array类型的数据
     */
    VALUE_IS_NOT_ARRAY("0010100003", "value is not array."),

    /**
     * 该字段的某些属性值为空
     */
    SOME_PROPERTY_IS_EMPTY("0010100004", "need some property, but value is empty."),

    /**
     * 需要的属性不存在
     */
    REQUIRED_PROPERTY_NOT_EXISTS("0010100005", "required property %s not exists."),

    /**
     * 字段不支持该属性
     */
    PROPERTY_NOT_SUPPORTED("0010100006", "property %s not supported."),

    /**
     * 数组元素个数超出最大值限制
     */
    BEYOND_ARRAY_ITEM_MAX_NUM_LIMIT("0010100007", "array item num must smaller than %d."),
    /**
     * 数组元素个数超出最小值限制
     */
    BEYOND_ARRAY_ITEM_MIN_NUM_LIMIT("0010100008", "array item num must greater than %d."),

    /**
     * 字段值超过最大值限制
     */
    VALUE_MUST_LESS_THAN("0010100009", "value must less than %s."),

    /**
     * 字段值超过最小值限制
     */
    VALUE_MUST_GREATER_THAN("0010100010", "value must greater than %s."),

    /**
     * 字段值必须为枚举中的一种
     */
    VALUE_MUST_IN_ENUM("0010100011", "value must in enum %s."),

    /**
     * 非String类型的数据
     */
    VALUE_IS_NOT_STRING("0010100012", "value should be string."),

    /**
     * 字段的最大长度
     */
    MAX_LENGTH("0010100013", "max length is %d."),

    /**
     * 字段的最小长度
     */
    MIN_LENGTH("0010100014", "min length is %d."),

    /**
     * 字段值为空
     */
    VALUE_IS_EMPTY("0010100015", "value is empty."),

    /**
     * 非bool类型的数据
     */
    VALUE_IS_NOT_BOOL("0010100016", "value shoud be boolean."),

    /**
     * 非Integer类型的数据
     */
    VALUE_IS_NOT_INTEGER("0010100017", "value should be integer."),

    /**
     * 字段值必须为数字
     */
    VALUE_SHOULD_BE_NUMBER("0010100018", "value should be number."),

    /**
     * 字段数值太大
     */
    VALUE_IS_TOO_BIG("0010100019", "value is too big."),

    /**
     * 数组元素个数不能为0
     */
    ARRAY_ITEM_NUM_ZERO("0010100020", "array item num can not be zero."),

    ;

    /** 错误码 */
    private String errorCode;

    /** 默认的错误消息提示 */
    private String msg;

    DataSchemaErrorCode(String errorCode, String msg) {
        this.errorCode = errorCode;
        this.msg = msg;
    }

    public static DataSchemaErrorCode getByCode(String errorCode){
        DataSchemaErrorCode[] dataSchemaErrorCodes = DataSchemaErrorCode.values();
        for(DataSchemaErrorCode dataSchemaErrorCode : dataSchemaErrorCodes){
            if(dataSchemaErrorCode.getErrorCode().equals(errorCode)){
                return dataSchemaErrorCode;
            }
        }

        return null;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMsg() {
        return msg;
    }
}
