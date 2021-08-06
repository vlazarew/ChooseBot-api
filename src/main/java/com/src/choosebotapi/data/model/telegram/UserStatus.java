package com.src.choosebotapi.data.model.telegram;

public enum UserStatus {
    NotRegistered, EnterFullName, EnterPhone, WantToEat, EnterLocation, EnterDishOrGetRecommendations,
    EnterDishName, SelectDishCategory, SelectDishKitchenDirection, SelectAverageCheck, GetResultTopDishesByCategory,
    SelectBookOrRoute;

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
