package com.example.demo.controller;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping()
public class ContentController {

    @Autowired private RestHighLevelClient client;

    @GetMapping(value = "/dataList")
    public ResponseEntity<List<Map<String, Object>>> dataList() throws IOException {
        FuzzyQueryBuilder termQueryBuilder = QueryBuilders.fuzzyQuery("name", "h");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        String highligtField = "name";
        highlightBuilder.field(highligtField);
        highlightBuilder.preTags("<h3 style='color:red;font-size:20'>");
        highlightBuilder.postTags("<h3/>");
        //highlightBuilder.
        builder.query(termQueryBuilder);
        builder.highlighter(highlightBuilder);
        SearchRequest request = new SearchRequest("java");
        request.source(builder);
        SearchResponse searchResponse = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        List<Map<String,Object>> map = new ArrayList<>();
        hits.forEach(hit->{
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField highlightField = highlightFields.get(highligtField);
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if(highlightField!=null){
                String title = "";
                Text[] fragments = highlightField.getFragments();
                for (Text fragment : fragments) {
                    title += fragment;
                }

                sourceAsMap.put(highligtField,title);

            }
            map.add(sourceAsMap);

        });
        /*String url = "https://search.jd.com/Search?keyword=java";
        Document document = Jsoup.parse(new URL(url), 30000);
        Element goodsList = document.getElementById("J_goodsList");
        System.out.println(goodsList.html());
        Elements lis = goodsList.getElementsByTag("li");
        List<User> users = new ArrayList<>();

        for (Element li : lis) {
            Elements img = li.getElementsByTag("img");
            String src = img.get(0).attr("src");
            User user = new User("hha","hehe",src);
            users.add(user);
        }*/

        return ResponseEntity.ok(map);
    }
}
