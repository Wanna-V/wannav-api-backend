package com.ssg.wannavapibackend.repository.repositoryImpl;


import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssg.wannavapibackend.common.BusinessStatus;
import com.ssg.wannavapibackend.domain.Restaurant;
import com.ssg.wannavapibackend.dto.request.RestaurantSearchCond;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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



  public List<Restaurant> findAll(RestaurantSearchCond restaurantSearchCond){

    Boolean canPark = restaurantSearchCond.getCanPark();
    Boolean isOpen = restaurantSearchCond.getIsOpen();
    Integer startPrice = restaurantSearchCond.getStartPrice();
    Integer endPrice = restaurantSearchCond.getEndPrice();
    String roadAddress = restaurantSearchCond.getRoadAddress();
    List<Integer> rates = restaurantSearchCond.getRates();
    Set<String> restaurantTypes = restaurantSearchCond.getRestaurantTypes();
    Set<String> containFoodTypes = restaurantSearchCond.getContainFoodTypes();
    Set<String> provideServiceTypes = restaurantSearchCond.getProvideServiceTypes();
    Set<String> moodTypes = restaurantSearchCond.getMoodTypes();
    List<String> sortConditions = restaurantSearchCond.getSortConditions();

    JPAQuery<Restaurant> dynamicQuery = query.selectFrom(restaurant)
        .join(restaurant.reviews, review)
        .join(restaurant.foods, food)
        .join(restaurant.likes , likes)
        .where(eqContainFoodTypes(containFoodTypes), eqRestaurantTypes(restaurantTypes),
            eqProvideServiceTypes(provideServiceTypes), eqMoodTypes(moodTypes),
            eqCanPark(canPark), eqIsOpen(isOpen), likeRoadAddress(roadAddress))
        .groupBy(restaurant) //restaurant.id로 해도 되고 restaurant로 해도 되는듯 ㅇㅇ 그냥 restaurant로 그루핑이 됨 ㅇㅇ
        .having(goeRate(rates), loeGoePrice(startPrice, endPrice));
    addOrderBy(sortConditions, dynamicQuery);

    return dynamicQuery.fetch();
  }

  private void addOrderBy(List<String> sortConditions, JPAQuery<Restaurant> dynamicQuery) {
    //생각해보면 굳이 Boolean으로 판정할 필요가 아예 없었네 ㅇㅇ 그냥 라디오 버튼으로 String 값 넘어오면 이 String 값 있냐고 확인해서 판정하면 끝나는 일을 ;; ㅋㅋ
    for (String sortCondition : sortConditions) {
      switch (sortCondition){
        case "NEW":
          dynamicQuery.orderBy(restaurant.createdAt.desc().nullsLast()); //최신 순
          break;
        case "RATE":
          dynamicQuery.orderBy(review.rating.avg().desc().nullsLast()); //별점 높은 순
          break;
        case "LIKE":
          dynamicQuery.orderBy(likes.count().desc().nullsLast()); //좋아요 많은 순
          break;
        case "REVIEW":
          dynamicQuery.orderBy(review.count().desc().nullsLast()); //리뷰 많은 순
          break;
      }
    }
    sortConditions.clear();
  }

  //모달 보안성 우수 => 제3자가 접근하기 어려움, 단순 팝업창 느낌이므로 , 이런 2가지 방법을 고려했을 때 ~~가 더 괜찮아서 이거를 선정하였다. 이렇게 면접이든 포폴이든 정의하자!


  private BooleanExpression eqContainFoodTypes(Set<String> containFoodTypes) {
    if (containFoodTypes == null || containFoodTypes.isEmpty()) {
      return null; // 조건이 없으면 null 반환
    }
    BooleanExpression booleanExpression = null;
    for (String containFoodType : containFoodTypes) {
      if (containFoodType != null) {
        BooleanExpression condition = restaurant.containFoodTypes.any().eq(containFoodType);
        booleanExpression = (booleanExpression == null) ? condition : booleanExpression.or(condition); //or 조건을 꼭 후미에 붙여줘야함
      }
    }
    return booleanExpression;
  }




  private BooleanExpression eqRestaurantTypes(Set<String> restaurantTypes) {
    if (restaurantTypes == null || restaurantTypes.isEmpty()) {
      return null; // 조건이 없으면 null 반환
    }
    BooleanExpression booleanExpression = null;
    for (String restaurantType : restaurantTypes) {
      if (restaurantType != null) {
        BooleanExpression condition = restaurant.restaurantTypes.any().eq(restaurantType);
        booleanExpression = (booleanExpression == null) ? condition : booleanExpression.or(condition); //or 조건을 꼭 후미에 붙여줘야함
      }
    }
    return booleanExpression;
  }



  private BooleanExpression eqProvideServiceTypes(Set<String> provideServiceTypes) {
    if (provideServiceTypes == null || provideServiceTypes.isEmpty()) {
      return null; // 조건이 없으면 null 반환
    }

    BooleanExpression booleanExpression = null;
    for (String provideServiceType : provideServiceTypes) {
      if (provideServiceType != null) {
        BooleanExpression condition = restaurant.provideServiceTypes.any().eq(provideServiceType);
        booleanExpression =
            (booleanExpression == null) ? condition : booleanExpression.or(condition); //or 조건을 꼭 후미에 붙여줘야함
      }
    }
    return booleanExpression;
  }

  private BooleanExpression eqMoodTypes(Set<String> moodTypes){
    if (moodTypes == null || moodTypes.isEmpty()) {
      return null; // 조건이 없으면 null 반환
    }

    BooleanExpression booleanExpression = null;
    for (String moodType : moodTypes) {
      if (moodType != null) {
        BooleanExpression condition = restaurant.moodTypes.any().eq(moodType);
        booleanExpression = (booleanExpression == null) ? condition : booleanExpression.or(condition);
      }
    }

    return booleanExpression;
  }


  //goeRate : 별표 1,2,3,4,5 체크박스 중 여러 개를 누를 수 있어서 List를 받아올 수 있는 것이고,  이때 별점은 평균별점임 그래서 avg로 계산했음
  private BooleanExpression goeRate(List<Integer> rates) {
    if (rates == null || rates.isEmpty()) {
      return null; // 조건이 없으면 null 반환
    }

    BooleanExpression booleanExpression = null;
    for (Integer rate : rates) {
      if (rate != null) {
        BooleanExpression condition = review.rating.avg().goe(rate).and(review.rating.avg().lt(rate + 1));
        booleanExpression = (booleanExpression == null) ? condition : booleanExpression.or(condition);
      }
    }

    return booleanExpression;
  }

  private BooleanExpression loeGoePrice(Integer startPrice, Integer endPrice) {
    return startPrice != null && endPrice != null ? food.price.avg().goe(startPrice).and(food.price.avg().loe(endPrice)) : null;
  }


  private BooleanExpression likeRoadAddress(String roadAddress){
    return StringUtils.hasText(roadAddress) ? restaurant.address.roadAddress.like(roadAddress) : null;
  }

  private BooleanExpression eqIsOpen(Boolean isOpen) {
    return Boolean.TRUE.equals(isOpen) ? restaurant.businessStatus.eq(BusinessStatus.OPEN) : null; //영업 중인지 판별 , 누군가 체크박스에 영업 중 여부를 체크했을 경우 영업 중만 뜨게끔 조건 추가하는 것!
  }

  private BooleanExpression eqCanPark(Boolean canPark) {
    return Boolean.TRUE.equals(canPark) ? restaurant.canPark.eq(true) : null;
  }





}
