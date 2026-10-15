package com.evalforge.benchmark.enums;

public enum BenchmarkVersionStatus {
    //自定义枚举值
    DRAFT("DRAFT"),
    PUBLISHED("PUBLISHED");

    private final String value;

    //枚举类采用私有构造方法
    BenchmarkVersionStatus(String value){
        this.value = value;
    }

    //获得值
    public String getValue(){
        return value;
    }

}