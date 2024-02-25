package com.atguigu.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.NestedQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import co.elastic.clients.elasticsearch.core.search.Suggestion;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.AlbumFeignClient;
import com.atguigu.CategoryFeignClient;
import com.atguigu.UserInfoFeignClient;
import com.atguigu.constant.RedisConstant;
import com.atguigu.entity.*;
import com.atguigu.query.AlbumIndexQuery;
import com.atguigu.repository.AlbumRepository;
import com.atguigu.repository.SuggestRepository;
import com.atguigu.result.RetVal;
import com.atguigu.service.SearchService;
import com.atguigu.util.PinYinUtils;
import com.atguigu.vo.AlbumInfoIndexVo;
import com.atguigu.vo.AlbumSearchResponseVo;
import com.atguigu.vo.AlbumStatVo;
import com.atguigu.vo.UserInfoVo;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private SuggestRepository suggestRepository;
    @Autowired
    private AlbumFeignClient albumFeignClient;

    @Autowired
    private CategoryFeignClient categoryFeignClient;
    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void onSaleAlbum(Long albumId) {
//        数据存储在AlbumInfoIndex中,所以创建它的对象
        AlbumInfoIndex albumInfoIndex = new AlbumInfoIndex();
//        通过专辑id获取专辑基本信息
        RetVal<AlbumInfo> data = albumFeignClient.getAlbumInfoById(albumId);
//        这样强转最后汇报类型异常错误,说明需要在方法的类型上添加泛型
        AlbumInfo albumInfo = data.getData();
//        使用工具类,把专辑基本信息拿到index中
        BeanUtils.copyProperties(albumInfo, albumInfoIndex);

//        根据专辑id查询专辑属性信息
        List<AlbumAttributeValue> albumPropertyValueList = albumInfo.getAlbumPropertyValueList();
//        albumInfoIndex.setAttributeValueIndexList(albumPropertyValueList);
//        很明显这里没法直接把得到的专辑属性直接set进去,因为类型不同,所以需要迭代
        if (!CollectionUtils.isEmpty(albumPropertyValueList)) {
//            使用stream流,把专辑属性值的id和属性id放进去,使用工具类,并且返回list
            List<AttributeValueIndex> attributeValueIndexList =
                    albumPropertyValueList.stream().map(albumAttributeValue -> {
//                创建AttributeValueIndex容器
                        AttributeValueIndex atrrbuteValueIndex = new AttributeValueIndex();
//                把专辑属性值的id和属性id放进去,使用工具类
                        BeanUtils.copyProperties(albumAttributeValue, atrrbuteValueIndex);
                        return atrrbuteValueIndex;
                    }).collect(Collectors.toList());
            albumInfoIndex.setAttributeValueIndexList(attributeValueIndexList);
        }
//        根据三级分类id查询专辑分类信息,通过categoryview表
//        实现方法是通过远程调用
        BaseCategoryView categoryView = categoryFeignClient.getCategoryView(albumInfo.getCategory3Id());
//        把categoryView的信息放进去
        albumInfoIndex.setCategory2Id(categoryView.getCategory2Id());
        albumInfoIndex.setCategory1Id(categoryView.getCategory1Id());
//        根据用户id查询用户信息,通过远程调用,因为远程调用返回的是userinfovo的类型,所以这里也需要接收这样的类型
        UserInfoVo userInfoVo = userInfoFeignClient.getUserInfo(albumInfo.getUserId()).getData();
        // 判断
        if (userInfoVo != null) {
            albumInfoIndex.setAnnouncerName(userInfoVo.getNickname());
        }


//        查询统计信息,使用模拟数据(直接过拷)
        int num1 = new Random().nextInt(1000);
        int num2 = new Random().nextInt(100);
        int num3 = new Random().nextInt(50);
        int num4 = new Random().nextInt(300);
        albumInfoIndex.setPlayStatNum(num1);
        albumInfoIndex.setSubscribeStatNum(num2);
        albumInfoIndex.setBuyStatNum(num3);
        albumInfoIndex.setCommentStatNum(num4);
        //热点分数 你还需要去做一个计算公式
        double hotScore = num1 * 0.2 + num2 * 0.3 + num3 * 0.4 + num4 * 0.1;
        albumInfoIndex.setHotScore(hotScore);
        albumRepository.save(albumInfoIndex);

        //专辑自动补全内容
        SuggestIndex suggestIndex = new SuggestIndex();
        suggestIndex.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        suggestIndex.setTitle(albumInfo.getAlbumTitle());
        suggestIndex.setKeyword(new Completion(new String[]{albumInfo.getAlbumTitle()}));
        suggestIndex.setKeywordPinyin(new Completion(new String[]{PinYinUtils.toHanyuPinyin(albumInfo.getAlbumTitle())}));
        suggestIndex.setKeywordSequence(new Completion(new String[]{PinYinUtils.getFirstLetter(albumInfo.getAlbumTitle())}));
        suggestRepository.save(suggestIndex);
        //专辑主播名称自动补全
        if (!StringUtils.isEmpty(albumInfoIndex.getAnnouncerName())) {
            SuggestIndex announcerSuggestIndex = new SuggestIndex();
            announcerSuggestIndex.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            announcerSuggestIndex.setTitle(albumInfoIndex.getAnnouncerName());
            announcerSuggestIndex.setKeyword(new Completion(new String[]{albumInfoIndex.getAnnouncerName()}));
            announcerSuggestIndex.setKeywordPinyin(new Completion(new String[]{PinYinUtils.toHanyuPinyin(albumInfoIndex.getAnnouncerName())}));
            announcerSuggestIndex.setKeywordSequence(new Completion(new String[]{PinYinUtils.getFirstLetter(albumInfoIndex.getAnnouncerName())}));
            suggestRepository.save(announcerSuggestIndex);
        }
        ;
    }

    /**
     * 下架专辑
     */
    @Override
    public void offSaleAlbum(Long albumId) {
//        下架专辑,直接删除
        albumRepository.deleteById(albumId);

    }

    /**
     * 获取主页频道数据
     *
     * @param category1Id
     * @return
     */

    @Autowired
    private ElasticsearchClient client;

    @SneakyThrows
    @Override

    public List<Map<Object, Object>> getChannelData(Long category1Id) {
//        通过一级id获取到三级分类id，通过远程调用,获取主页频道数据
        List<BaseCategory3> category3List = categoryFeignClient.getCategory3ListByCategory1Id(category1Id).getData();
//        把list 转化为 list<FieldValue>类型
        List<FieldValue> fieldValueList = category3List.stream().map(BaseCategory3::getId)
                .map(m -> FieldValue.of(m)).collect(Collectors.toList());
//        通过es查询
        SearchResponse<AlbumInfoIndex> response = client.search(s -> s
                        .index("albuminfo")
                        .query(q -> q
                                .terms(t -> t
                                        .field("category3Id")
                                        .terms(new TermsQueryField.Builder().value(fieldValueList).build())
                                ))
                        .aggregations("category3IdAggs", a -> a
                                .terms(t -> t
                                        .field("category3Id"))
                                .aggregations("topSixHotScoreAgg", xa -> xa
                                        .topHits(xt -> xt.size(6)
                                                .sort(xs -> xs
                                                        .field(f -> f.field("hotScore").order(SortOrder.Desc))))))

                , AlbumInfoIndex.class
        );
        //4.建立三级分类id和三级分类对象的映射关系
        Map<Long, BaseCategory3> category3Map = category3List.stream().collect(Collectors.toMap(BaseCategory3::getId, baseCategory3 -> baseCategory3));
//        解析结果
        Aggregate category3IdAgg = response.aggregations().get("category3IdAggs");
        List<Map<Object, Object>> retMapList = category3IdAgg.lterms().buckets().array().stream().map(bucket -> {
            Long category3Id = bucket.key();
            Aggregate topSixHotScoreAgg = bucket.aggregations().get("topSixHotScoreAgg");
            List<AlbumInfoIndex> albumInfoIndexList = topSixHotScoreAgg.topHits().hits().hits().stream().map(hit ->
                    JSONObject.parseObject(hit.source().toString(), AlbumInfoIndex.class)).collect(Collectors.toList());
            Map<Object, Object> retMap = new HashMap<>();
            retMap.put("baseCategory3", category3Map.get(category3Id));
            retMap.put("list", albumInfoIndexList);
            return retMap;
        }).collect(Collectors.toList());
        return retMapList;
    }

    /**
     * 搜索自动补全,通过输入的关键字自动匹配对应的数据展示
     *
     * @param keyword
     * @return
     */
    @SneakyThrows
    @Override
    public Set<String> autoCompleteSuggest(String keyword) {
//        组织suggest搜索语句
        Suggester suggester = new Suggester.Builder()
                .suggesters("suggestionKeyword", s -> s
                        .prefix(keyword)
                        .completion(c -> c
                                .field("keyword")))
                .suggesters("suggestionKeywordSequence", s -> s
                        .prefix(keyword)
                        .completion(c -> c
                                .field("keywordSequence")))
                .suggesters("suggestionKeywordPinyin", s -> s
                        .prefix(keyword)
                        .completion(c -> c
                                .field("keywordPinyin"))).build();
//         对自动补全语句进行搜索
        SearchResponse<SuggestIndex> suggestResponse = client.search(s -> s
                .index("suggestinfo")
                .suggest(suggester), SuggestIndex.class);
//        对自动补全结果进行解析
        Set<String> suggestSet = analysisResponse(suggestResponse);
        return suggestSet;
    }

    /**
     * 搜索，可以通过关键字，也可以通过分类
     *
     * @param albumIndexQuery
     * @return
     */
    @SneakyThrows
    @Override
    public AlbumSearchResponseVo search(AlbumIndexQuery albumIndexQuery) {
//        编写Dsl语句
        SearchRequest request = buildQueryDsl(albumIndexQuery);
//       执行查询
        SearchResponse<AlbumInfoIndex> response = client.search(request, AlbumInfoIndex.class);
//        解析结果
        AlbumSearchResponseVo responseVo = parseSearchResult(response);
//      设置其他参数
        responseVo.setPageNo(albumIndexQuery.getPageNo());
        responseVo.setPageSize(albumIndexQuery.getPageSize());
//        设置总页数
        responseVo.setTotalPages((responseVo.getTotal() % albumIndexQuery.getPageSize() == 0 ? responseVo.getTotal() / albumIndexQuery.getPageSize() : responseVo.getTotal() / albumIndexQuery.getPageSize() + 1));
        return responseVo;
    }

    /**
     * 更新排行榜列表
     */
    @SneakyThrows
    @Override
    public void updateRanking() {
//      获取到所有category1Id
        List<BaseCategory1> category1List = categoryFeignClient.getCategory1();
//        对category1List进行遍历
        if (!CollectionUtils.isEmpty(category1List)) {
            //                对每一个category1Id进行遍历
            for (BaseCategory1 category1 : category1List) {
                String[] rankingTypeList = new String[]{"hotScore", "playStatNum", "subscribeStatNum", "buyStatNum", "commentStatNum"};
//                对每一个rankingType的参数进行遍历，这样就能得到对应的数据
                for (String rankingType : rankingTypeList) {
                    SearchResponse<AlbumInfoIndex> response = client.search(s -> s
                            .index("albuminfo")
                            .query(q -> q
                                    .term(t -> t.field("category1Id").value(category1.getId())))
                            .size(10)
                            .sort(sort -> sort.field(f -> f.field(rankingType).order(SortOrder.Desc))), AlbumInfoIndex.class);
                    List<AlbumInfoIndex> albumInfoIndexList = response.hits().hits().stream().map(Hit::source).collect(Collectors.toList());
                    //把数据存储到redis中
                    redisTemplate.boundHashOps(RedisConstant.RANKING_KEY_PREFIX + category1.getId()).put(rankingType, albumInfoIndexList);
                }
            }
            ;
        }
    }

    /**
     * 通过专辑id获取信息
     *
     * @param albumId
     * @return
     */

//    注入线程,保证线程的创建由自己掌控,避免消耗
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Override
    public HashMap<String, Object> getAlbumDetail(Long albumId) {

        //        创建map存放信息
        HashMap<String, Object> map = new HashMap<>();
//      使用异步编排
        CompletableFuture<AlbumInfo> albumInfoFuture = CompletableFuture.supplyAsync(() -> {
//        获取专辑基本信息
            AlbumInfo albumInfo = albumFeignClient.getAlbumInfoById(albumId).getData();
//            放入map中
            map.put("albumInfo", albumInfo);
            return albumInfo;
        },threadPoolExecutor);
        CompletableFuture<Void> albumStatFuture = CompletableFuture.runAsync(() -> {
            //        获取专辑统计信息
            AlbumStatVo albumStatVo = albumFeignClient.getAlbumStatInfo(albumId).getData();
            map.put("albumStatVo", albumStatVo);
        },threadPoolExecutor);
        CompletableFuture<Void> categoryViewFuture = albumInfoFuture.thenAcceptAsync((albumInfo) -> {
            //        获取专辑分类信息
            BaseCategoryView categoryView = categoryFeignClient.getCategoryView(albumInfo.getCategory3Id());
            map.put("baseCategoryView", categoryView);
        },threadPoolExecutor);
        CompletableFuture<Void> announcerFuture = albumInfoFuture.thenAcceptAsync((albumInfo -> {
            //        获取用户基本信息
            UserInfoVo userInfoVo = userInfoFeignClient.getUserInfo(albumInfo.getUserId()).getData();
            map.put("announcer", userInfoVo);
        }),threadPoolExecutor);
//        allOf() 方法被用来创建一个新的 CompletableFuture，等待其他完成后,统一返回一个新的CompletableFuture。
        CompletableFuture.allOf(albumInfoFuture, albumStatFuture, categoryViewFuture, announcerFuture).join();
//        返回
        return map;
    }


    /**
     * 解析搜索结果
     *
     * @param response
     * @return
     */
    private AlbumSearchResponseVo parseSearchResult(SearchResponse<AlbumInfoIndex> response) {
        AlbumSearchResponseVo responseVo = new AlbumSearchResponseVo();
//        解析hits，获取专辑列表信息
        List<Hit<AlbumInfoIndex>> searchAlbumInfoHits = response.hits().hits();
//        获取专辑总数
        responseVo.setTotal(response.hits().total().value());
        List<AlbumInfoIndexVo> albumInfoIndexList = searchAlbumInfoHits.stream().map(searchAlbumInfoHit -> {

            AlbumInfoIndexVo albumInfoIndexVo = new AlbumInfoIndexVo();
            AlbumInfoIndex source = searchAlbumInfoHit.source();
//            把source的信息拷贝到albumInfoIndexVo中,使用工具类
            BeanUtils.copyProperties(source, albumInfoIndexVo);

//          设置高亮
            List<String> albumTitle = searchAlbumInfoHit.highlight().get("albumTitle");
            if (!CollectionUtils.isEmpty(albumTitle)) {
                albumInfoIndexVo.setAlbumTitle(albumTitle.get(0));
            }
            return albumInfoIndexVo;
        }).collect(Collectors.toList());
//        把专辑信息放进去
        responseVo.setList(albumInfoIndexList);
        return responseVo;
    }

    private SearchRequest buildQueryDsl(AlbumIndexQuery albumIndexQuery) {
//        构造一个bool
        BoolQuery.Builder builderQuery = new BoolQuery.Builder();
//        通过传递的参数拿到关键字
        String keyword = albumIndexQuery.getKeyword();
//        如果关键字不为空,则进行查询
        if (!StringUtils.isEmpty(keyword)) {
//            构建一个should关键字查询
            builderQuery.should(s -> s.match(m -> m.field("albumTitle").query(keyword)));
            builderQuery.should(s -> s.match(m -> m.field("albumIntro").query(keyword)));
            builderQuery.should(s -> s.match(m -> m.field("announcerName").query(keyword)));
        }
//        构造一级分类查询
        Long category1Id = albumIndexQuery.getCategory1Id();
        if (category1Id != null) {
            builderQuery.filter(f -> f.term(t -> t.field("category1Id").value(category1Id)));
        }
//        构造二级分类查询
        Long category2Id = albumIndexQuery.getCategory2Id();
        if (category2Id != null) {
            builderQuery.filter(f -> f.term(t -> t.field("category2Id").value(category2Id)));
        }
//        构造三级分类查询
        Long category3Id = albumIndexQuery.getCategory3Id();
        if (category3Id != null) {
            builderQuery.filter(f -> f.term(t -> t.field("category3Id").value(category3Id)));
        }

//        根据分类属性进行嵌套过滤
        List<String> attributeList = albumIndexQuery.getAttributeList();
//      判断是否为空
        if (!CollectionUtils.isEmpty(attributeList)) {
//            对它进行循环
            for (String attribute : attributeList) {
//                这里的attribute是属性id:属性值id的形式,所以需要进行分割
                String[] attributeSplit = attribute.split(":");
//                判单是否分割成功
                if (attributeSplit.length == 2 && attributeSplit != null) {
//                    构造一个嵌套过滤
                    Query nestQuery = NestedQuery.of(f -> f
                            .path("attributeValueIndexList")
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m.term(t -> t.field("attributeValueIndexList.attributeId").value(attributeSplit[0])))
                                            .must(m -> m.term(t -> t.field("attributeValueIndexList.attributeValueId").value(attributeSplit[1])))
                                    )))._toQuery();
                    builderQuery.filter(nestQuery);
                }
            }
        }
//        构造最外层的query
        Query query = builderQuery.build()._toQuery();
//        构造分页和高亮查询
        SearchRequest.Builder searchRequest = new SearchRequest.Builder()
                .query(query)
                .from((albumIndexQuery.getPageNo() - 1) * albumIndexQuery.getPageSize())
                .size(albumIndexQuery.getPageSize())
                .highlight(h -> h.fields("albumTitle", f -> f.preTags("<em>").postTags("</em>")));
//        构建排序语句
        String order = albumIndexQuery.getOrder();
        String orderField = null;
        if (!StringUtils.isEmpty(order)) {
            String[] orderSplit = order.split(":");
            if (orderSplit.length == 2 && orderSplit != null) {
//                判断它的第一个的数字是多少，来得到对应的结果
                switch (orderSplit[0]) {
                    case "1":
                        orderField = "hotScore";
                        break;
                    case "2":
                        orderField = "playStatNum";
                        break;
                    case "3":
                        orderField = "createTime";
                        break;
                }
            }
            String finalOrderField = orderField;
            searchRequest.sort(s -> s.field(xs -> xs.field(finalOrderField).order("asc".equals(orderSplit[1]) ? SortOrder.Asc : SortOrder.Desc)));
        }
//        SearchRequest request = searchRequest.build();
//        System.out.println(request.toString());
//        //        最终的DSl语句
//        return request;
//        System.out.println(searchRequest.build().toString());
        return searchRequest.build();
    }


    //    自动补全结果的代码的方法实现
    private Set<String> analysisResponse(SearchResponse<SuggestIndex> suggestResponse) {

        Set<String> suggestSet = new HashSet<>();
        Map<String, List<Suggestion<SuggestIndex>>> suggestMap = suggestResponse.suggest();
//        对suggestMap进行遍历
        suggestMap.entrySet().stream().forEach(suggestEntry -> {
//            这里的suggestEntry对应表中的第一层的结构,所以需要对它再次进行遍历
            suggestEntry.getValue().stream().forEach(suggestion -> {
//                这里的suggestion对应表中的第二层的结构,所以需要对它再次进行遍历,并且从里面拿到数据，并收集成集合
                List<String> suggestTitleList = suggestion.completion().options().stream().map(m -> m
                        .source().getTitle()).collect(Collectors.toList());
                suggestSet.addAll(suggestTitleList);
            });
        });
        return suggestSet;
    }
}
