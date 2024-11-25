package com.ssg.wannavapibackend.repository;


import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssg.wannavapibackend.common.BusinessStatus;
import com.ssg.wannavapibackend.domain.Restaurant;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class RestaurantRepository {

  private final EntityManager em;
  private final JPAQueryFactory query;


  @Autowired
  public RestaurantRepository(EntityManager em){
    this.em=em;
    this.query = new JPAQueryFactory(em);
  }

  public Long save(Restaurant restaurant){
    em.persist(restaurant);
    return restaurant.getId();
  }

  public Optional<Restaurant> findById(Long id){
    return Optional.ofNullable(em.find(Restaurant.class , id));
  }





  public List<Restaurant> findAll(RestaurantSearchCond restaurantSearchCond) {
    /**
     * where 동적 조건
     */
    Boolean canPark = restaurantSearchCond.getCanPark();
    Boolean isOpen = restaurantSearchCond.getIsOpen();
    Integer startPrice = restaurantSearchCond.getStartPrice();
    Integer endPrice = restaurantSearchCond.getEndPrice();
    String roadAddress = restaurantSearchCond.getRoadAddress();
    List<Integer> rates = restaurantSearchCond.getRates();
    List<String> restaurantTypes = restaurantSearchCond.getRestaurantTypes();
    List<String> containFoodTypes = restaurantSearchCond.getContainFoodTypes();
    List<String> provideServiceTypes = restaurantSearchCond.getProvideServiceTypes();
    List<String> moodTypes = restaurantSearchCond.getMoodTypes();


    JPAQuery<Restaurant> dynamicQuery = query.selectFrom(restaurant)
        .join(restaurant.reviews, review)
        .join(restaurant.foods, food)
        .join(restaurant.businessDays, businessDay)
        .join(review.reviewTags, reviewTag)
        .where(eqContainFoodTypes(containFoodTypes), goeRate(rates),
            eqRestaurantTypes(restaurantTypes),
            eqProvideServiceTypes(provideServiceTypes), eqMoodTypes(moodTypes)
            , loeGoePrice(startPrice, endPrice),
            eqCanPark(canPark), eqIsOpen(isOpen), eqRoadAddress(roadAddress));
    //최신 순(등록일 기준 정렬) , 별점 높은 순(평균 별점에 따른 정렬) , 좋아요 높은 순
    if (restaurantSearchCond.getIsCreatedAtChecked()){
      dynamicQuery.orderBy(restaurant.rateAverage.desc().nullsLast());
    }
    if (restaurantSearchCond.getIsLikesChecked()){
      dynamicQuery.orderBy(restaurant.likesCount.desc().nullsLast());
    }
    if (restaurantSearchCond.getIsCreatedAtChecked()){
      dynamicQuery.orderBy(restaurant.createdAt.desc().nullsLast());
    }

    return dynamicQuery.fetch();



    //코드가 굉장히 간결 , return 문만 봐도 동적 쿼리 짤 수 있음
  }


  private BooleanExpression eqContainFoodTypes(List<String> containFoodTypes){
    BooleanExpression booleanExpression = null;
    for (String containFoodType : containFoodTypes) {
      booleanExpression =  containFoodType != null ? restaurant.containFoodTypes.any().eq(containFoodType) : null;
    }
    return booleanExpression;
  }

  private BooleanExpression eqRestaurantTypes(List<String> restaurantTypes){
    BooleanExpression booleanExpression = null;
    for (String restaurantType : restaurantTypes) {
      booleanExpression =  restaurantType != null ? restaurant.restaurantTypes.any().eq(restaurantType) : null;
    }
    return booleanExpression;
  }

  private BooleanExpression eqProvideServiceTypes(List<String> provideServiceTypes){
    BooleanExpression booleanExpression = null;
    for (String provideServiceType : provideServiceTypes) {
      booleanExpression =  provideServiceType != null ? restaurant.provideServiceTypes.any().eq(provideServiceType) : null;
    }
    return booleanExpression;
  }

  private BooleanExpression eqMoodTypes(List<String> moodTypes){
    BooleanExpression booleanExpression = null;
    for (String moodType : moodTypes) {
      booleanExpression =  moodType != null ? restaurant.moodTypes.any().eq(moodType) : null;
    }
    return booleanExpression;
  }

  private BooleanExpression goeRate(List<Integer> rates) {
    BooleanExpression booleanExpression = null;
    for (Integer rate : rates) {
      booleanExpression = rate != null ? restaurant.averageRate.goe(rate).and(restaurant.averageRate.lt(rate+1)) : null;
    }
    return booleanExpression;

  }

  private BooleanExpression eqRoadAddress(String roadAddress){
    return StringUtils.hasText(roadAddress) ? restaurant.address.roadAddress.eq(roadAddress) : null;
  }

  private BooleanExpression eqIsOpen(Boolean isOpen) {
    return isOpen != null ? restaurant.businessStatus.eq(BusinessStatus.OPEN) : null; //영업 중인지 판별 , 누군가 체크박스에 영업 중 여부를 체크했을 경우 영업 중만 뜨게끔 조건 추가하는 것!
  }

  private BooleanExpression eqCanPark(Boolean canPark) {
    return canPark != null ? restaurant.canPark.eq(canPark) : null;
  }

  private BooleanExpression loeGoePrice(Integer startPrice, Integer endPrice) {
    return startPrice != null && endPrice != null ? food.price.loe(endPrice).and(food.price.goe(startPrice)) : null;
  }




    Restaurant findByNameContaining(String name);

    Restaurant findByNameContainingAndNameContaining(String name1, String name2);
}
