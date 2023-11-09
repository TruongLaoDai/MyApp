package com.smile.watchmovie.utils

class Constant {
    companion object SharedPreferences {
        const val FULL_SCREEN = "full_screen"
        const val AUTO_PLAY = "auto_play"
        const val ID_USER = "idUser"
        const val NAME_USER = "name"
        const val IS_VIP = "isVip"
        const val NAME_DATABASE_SHARED_PREFERENCES = "MyPrefs"
    }

    object Api {
        const val BASE_URL_WEATHER = "https://api.openweathermap.org/"
        const val BASE_URL_FILM = "http://cinema.tl/"
        const val APP_ID = "c3dd215f4ed3239d0fc98ce38ea3fcd0"
        const val WS_TOKEN = "7da353b8a3246f851e0ee436d898a26d"
    }

    object FirebaseFiretore {
        const val NAME_DATABASE = "WatchFilm"
        const val TABLE_HISTORY_WATCHED = "tblhistorywatchfilm"
        const val ID_FILM = "id_film"
        const val TABLE_HISTORY_BUY_PREMIUM = "tblhistoryupvip"
    }
}