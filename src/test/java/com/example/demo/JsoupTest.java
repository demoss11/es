package com.example.demo;

import cn.hutool.json.JSONUtil;
import com.example.demo.domain.User;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JsoupTest {

    private RestHighLevelClient client;
    {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
        this.client = client;
    }

    @Test
    void test001() throws IOException {
        String url = "https://search.jd.com/Search?keyword=java";
        Document document = Jsoup.parse(new URL(url), 30000);
        Element goodsList = document.getElementById("J_goodsList");
        System.out.println(goodsList.html());
        Elements lis = goodsList.getElementsByTag("li");
        List<User> users = new ArrayList<>();
        BulkRequest request = new BulkRequest();
        String indexName = "java";
        for (Element li : lis) {
            String id = UUID.randomUUID().toString();
            Elements img = li.getElementsByTag("img");
            String src = img.get(0).attr("src");
            User user = new User(id,"hehe",src, LocalDate.now());
            users.add(user);
            System.out.println(src);
            IndexRequest indexRequest = new IndexRequest(indexName);
            indexRequest.source(JSONUtil.toJsonStr(user), XContentType.JSON);
            request.add(indexRequest);
        }

        BulkResponse responses = client.bulk(request, RequestOptions.DEFAULT);
        boolean b = responses.hasFailures();
        System.out.println(b);
        //System.out.println(document);
    }
}
