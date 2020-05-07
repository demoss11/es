package com.example.demo;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.example.demo.domain.TAgentInfo;
import com.example.demo.domain.User;
import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EsTest {
    private RestHighLevelClient client;
    {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
        this.client = client;
    }
    @Test
    void test001(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
        IndicesClient indices = client.indices();
        /*try {
            CreateIndexResponse hello_hehe = indices.create(new CreateIndexRequest("hello_hehe"), RequestOptions.DEFAULT);
            String index = hello_hehe.index();
            boolean acknowledged = hello_hehe.isAcknowledged();
            //hello_hehe.
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        String[] strings = new String[0];
        try {
            strings = indices.get(new GetIndexRequest("hello_hehe"), RequestOptions.DEFAULT).getIndices();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(strings);
    }

    @Test
    void testExistIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("hello_hehe");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    //测试删除suoyyin
    @Test
    void testdelete() throws IOException {
        AcknowledgedResponse hello_hehe = client.indices().delete(new DeleteIndexRequest("hello_hehe"), RequestOptions.DEFAULT);
        String s = hello_hehe.toString();
        System.out.println(s);
       /* DeleteResponse delete = client.delete(new DeleteRequest("hello_hello"), RequestOptions.DEFAULT);
        RestStatus status = delete.status();
        System.out.println(status);*/
    }

    //插入文档
    @Test
    void testInsert() throws IOException {
        /*User user = new User("大撒发射点","dfsadf");
        IndexRequest request = new IndexRequest("lalala");
        //规则 put /idnex/_doc/1
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));
        request.source(JSONUtil.toJsonStr(user), XContentType.JSON);
        //和护短发送请求
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        System.out.println(indexResponse.getIndex());
        System.out.println(indexResponse.status());*/
    }

    @Test
    void testGetDoc() throws IOException {
        GetRequest request = new GetRequest("lalala","2");
        GetResponse re = client.get(request, RequestOptions.DEFAULT);
        Map<String, Object> source = re.getSource();
        String sourceAsString = re.getSourceAsString();
        System.out.println(sourceAsString);
    }

    @Test
    void test002() throws IOException {
        ///IndicesClient indicesClient = client.indices();
        IndexRequest request = new IndexRequest("agentinfo");
        request.setIfPrimaryTerm(1);
        request.setIfSeqNo(12);

        //request.version(9);
        //request.versionType(VersionType.INTERNAL);
        //request.versionType()
        //request.opType(DocWriteRequest.OpType.);

        //CreateIndexRequest request = new CreateIndexRequest("agentinfo");
        TAgentInfo info = new TAgentInfo();
        info.setIp("192.168.1.3");
        info.setLocation("啊发士大夫大师傅撒打发");
        info.setAgentId("我要创建你式风格士大夫但是是否根深蒂固");
        request.id("Oncj3XEBLYsk96le7fnX");
        request.source(JSONUtil.toJsonStr(info),XContentType.JSON);
        //ndicesClient.
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        System.out.println(indexResponse.status().name());
        //CreateIndexResponse response = client.create(request, RequestOptions.DEFAULT);
    }

    @Test
    void test003() throws IOException {
        SearchRequest request = new SearchRequest("agentinfo");

        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("agentId", "士大夫");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.fetchSource("agentId","");
        searchSourceBuilder.query(matchQueryBuilder);
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("agentId");
        searchSourceBuilder.highlighter(highlightBuilder);
        request.source(searchSourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits()) {
            Map<String, Object> source = hit.getSourceAsMap();
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("agentId");
            String t = "";
            if(highlightField!=null){
                for (Text fragment : highlightField.getFragments()) {
                    t += fragment;
                }
                source.put("agentId",t);
            }
            System.out.println("=================");
            System.out.println(source);
            System.out.println("=================");
        }


    }

    @Test
    void test004() throws IOException {
        DeleteRequest request = new DeleteRequest("agentinfo","Oncj3XEBLYsk96le7fnX");
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        String s = response.status().toString();
        System.out.println(s);
    }

    @Test
    void test005() throws IOException {
        GetRequest request = new GetRequest("agentinfo","Oncj3XEBLYsk96le7fnX");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        String sourceAsString = response.getSourceAsString();
        long primaryTerm = response.getPrimaryTerm();
        long seqNo = response.getSeqNo();
        System.out.println(primaryTerm);
        System.out.println(seqNo);


    }

    //跟新
    @Test
    void testUpdate() throws IOException {
        UpdateRequest request = new UpdateRequest("agentinfo","Oncj3XEBLYsk96le7fnX")
                .doc("location","朝阳市公安局");

        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        String name = response.status().name();
        System.out.println(name);

        CreateIndexRequest cr = new CreateIndexRequest("");
        //shard

    }

    @Test
    void testBulk() throws IOException {
        BulkRequest request = new BulkRequest();
        IndexRequest request1 =  new IndexRequest("111");
        Map<String,String> map = new HashMap<>();
        map.put("name","zhangsan");
        request1.source(map,XContentType.JSON);
        request.add(request1);
        BulkResponse responses = client.bulk(request, RequestOptions.DEFAULT);
        boolean b = responses.hasFailures();
        System.out.println(b);
    }

    @Test
    void testAgg() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("ggg");
        Settings settings = Settings.builder()
                .put("number_of_shards", 5)
                .put("number_of_replicas", 0)
                .build();
        request.settings(settings);
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());

        //AggregationBuilders.
    }

    @Test
    void test006() throws IOException {
        GetIndexRequest request = new GetIndexRequest("_all");
        GetIndexResponse indexResponse = client.indices().get(request, RequestOptions.DEFAULT);
        System.out.println(indexResponse.getIndices());
    }

    @Test
    void test007() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("_all");
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response.isAcknowledged());
    }
}
