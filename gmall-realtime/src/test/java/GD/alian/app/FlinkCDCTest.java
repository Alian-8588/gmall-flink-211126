package GD.alian.app;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Properties;

public class FlinkCDCTest {

    public static void main(String[] args) throws Exception {

        Properties prop = new Properties();
        prop.setProperty("useSSL","false");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname("hadoop102")
                .port(3306)
                .username("root")
                .password("000000")
                .jdbcProperties(prop)
                .serverTimeZone("UTC")
                .databaseList("gmall_config")
                .tableList("gmall_config.table_process")
                .startupOptions(StartupOptions.initial())
                .deserializer(new JsonDebeziumDeserializationSchema())
                .build();

        DataStreamSource<String> mysqlSourceDS = env.fromSource(mySqlSource,
                WatermarkStrategy.noWatermarks(),
                "MysqlSource");


        mysqlSourceDS.print(">>>>>");

        env.execute();
    }

}
