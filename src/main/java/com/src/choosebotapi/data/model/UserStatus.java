package com.src.choosebotapi.data.model;

public enum UserStatus {
    NotRegistered, EnterFullName, EnterPhone, EnterLocation, WantToEat, EnterDishOrGetRecommendations,
    EnterDishName, SelectDishCategory, SelectDishKitchenDirection, SelectAverageCheck, GetResultTopDishesByCategory,
    GetResultRestaurantFromDB;
//    VerifyEmail, MainPage,
//    WeatherMainPage, WeatherCommonSettings, WeatherSettings, LocationList, AddCity, RemoveCity, QueryWeatherInLocationCity, WeatherWatch,
//    NewsNotificationSettings, TwitterNotificationSettings, WeatherNotificationSettings,
//    NewsMainPage, NewsCommonSettings, NewsSettings, CategoriesList, SourcesList, AddCategory, RemoveCategory, AddSource, RemoveSource, NewsWatch,
//    TwitterMainPage, TwitterCommonSettings, TwitterSettings, HashtagsList, PeoplesList, AddPeople, RemovePeople, AddHashtag, RemoveHashtag, TwitterWatch;

    private static UserStatus[] userStatuses;

    public static UserStatus getInitialStatus() {
        return byId(0);
    }

    public static UserStatus byId(int id) {
        if (userStatuses == null) {
            userStatuses = UserStatus.values();
        }

        return userStatuses[id];
    }
}
