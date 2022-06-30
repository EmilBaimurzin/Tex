package com.example.tex.others

object PermanentStorage {

    var token = ""
    var connection = true
    var name = ""
    object WebUrl {
        const val AUTH_URL = "https://www.reddit.com/api/v1/authorize.compact?client_id=%s" +
                "&response_type=code&state=%s&redirect_uri=%s&" +
                "duration=permanent&scope=identity read account mysubreddits subscribe history vote save submit privatemessages"
        const val CLIENT_ID = "5Yabyj2QYgW_jarQzTNRVw"
        const val REDIRECT_URI = "http://www.example.com/my_redirect"
        const val STATE = "MY_RANDOM_STRING_1"
        const val ACCESS_TOKEN_URL = "https://www.reddit.com/api/v1/access_token"

        val url = String.format(AUTH_URL, CLIENT_ID, STATE, REDIRECT_URI)
    }

    object NoContextStrings {
        var nowString = ""
        var postedString = ""
        var minutesAgoString = ""
        var hoursAgoString = ""
    }

    object SharedPrefs {
        const val AUTH_PREFS = "auth_prefs"
    }
}