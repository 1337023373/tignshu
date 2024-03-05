package com.atguigu;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;

public class MpGenerator {
    //具体设置 https://blog.csdn.net/cx19910829/article/details/126889724
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://192.168.157.166:3316/tingshu_payment?serverTimezone=GMT%2B8",
                        "root",
                        "123")
                .globalConfig(builder -> {
                    builder.author("亨亨sama") // 设置作者
                            //.enableSwagger() // 开启 swagger 模式
                            .disableOpenDir()
                           // .outputDir("C:\\230710\\code\\tingshu_parent\\tingshu_core\\tingshu_user\\src\\main\\java"); // 指定输出目录
                            .outputDir("D:\\workspace\\work\\audiobook\\code\\tingshu_parent\\tingshu_core\\tingshu_payment\\src\\main\\java"); // 指定输出目录
                })
                .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    return typeRegistry.getColumnType(metaInfo);
                }))
                .packageConfig(builder -> {
                    builder.parent("com.atguigu") // 设置父包名
                            .moduleName("") // 设置父包模块名
                            .xml("mapper.xml");
                })

                .strategyConfig(builder -> {
                    builder.addInclude("payment_info");// 设置需要生成的表名
                    builder.serviceBuilder().formatServiceFileName("%sService");
                    builder.controllerBuilder().enableRestStyle();
                    builder.entityBuilder()
                            .enableLombok() // 支持lombok开启注解
                            .enableChainModel(); //支持链式编程
                    //.addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
                //.templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
//         strategy.setInclude("user_info","user_paid_album","user_paid_track","user_vip_info",
////                "vip_service_config");
////        strategy.setInclude("album_info","album_stat","base_attribute","base_category_view",
////                "album_attribute_value","base_attribute_value","base_category1","base_category2",
////                "base_category3","track_info","track_stat");
////        strategy.setInclude("user_account","recharge_info","user_account_detail");
////        strategy.setInclude("user_info","user_address");
////        strategy.setInclude("order_info","order_detail","order_reduction");
////        strategy.setInclude("payment_info");
////        strategy.setInclude("base_brand","t_order_1","t_order_detail_1");
    }
}