package com.example.tex.others

import android.util.Log
import com.example.tex.subreddit.subredditRecyclerView.RedditPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URI
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object PostParsing {
    private val scope = CoroutineScope(Dispatchers.Default)

    suspend fun getListOfParsedPosts(responseBodyString: String): List<RedditPost> {
        return suspendCoroutine { continuation ->
            scope.launch {
                withContext(Dispatchers.Default) {
                    try {
                        val listOfPosts = mutableListOf<RedditPost>()
                        val jsonObject = JSONObject(responseBodyString)
                        val dataObject = jsonObject.getJSONObject("data")
                        val childrenArray = dataObject.getJSONArray("children")

                        (0 until childrenArray.length()).map { index ->
                            childrenArray.getJSONObject(index)
                        }
                            .map { childJsonObject ->
                                val postData = childJsonObject.getJSONObject("data")
                                val nickname = postData.getString("subreddit")
                                val time = postData.getString("created_utc")
                                val title = postData.getString("title") ?: ""
                                val description = postData.getString("selftext") ?: ""
                                val numberOfComments = postData.getString("num_comments")
                                val url = postData.getString("url")
                                val postLink = postData.getString("permalink")
                                val videoLink = getVideoLink(postData)
                                val videoAudio = getVideoAudio(videoLink)
                                val icon = getIconUrl(postData)
                                val postId = postData.getString("name")
                                val listOfImages = getListOfImages(postData)
                                val thumbnail = getThumbnail(postData)
                                val isFollowed = isUserFollowed(postData)
                                val prefix = postData.getString("subreddit_name_prefixed")
                                listOfPosts.add(listConfiguration(
                                    nickname,
                                    time,
                                    title,
                                    description,
                                    numberOfComments,
                                    url,
                                    postLink,
                                    videoLink,
                                    icon,
                                    listOfImages,
                                    videoAudio,
                                    postId,
                                    thumbnail,
                                    isFollowed,
                                    prefix,
                                )!!)
                            }
                        continuation.resume(listOfPosts)
                    } catch (t: Throwable) {
                        Log.e("error", "redditPostsListError", t)
                        continuation.resume(emptyList())
                    }
                }
            }
        }
    }

    private fun isUserFollowed(json: JSONObject): Boolean {
        return try {
            val details = json.getJSONObject("sr_detail")
            details.getBoolean("user_is_subscriber")
        } catch (t: Throwable) {
            true
        }
    }

    private fun getThumbnail(jsonObject: JSONObject): String? {
        return try {
            val thumbnail = jsonObject.getString("thumbnail")
            if (thumbnail != "self" && thumbnail != "nsfw") {
                thumbnail
            } else {
                throw Exception()
            }
        } catch (t: Throwable) {
            null
        }
    }

    private fun getIconUrl(json: JSONObject): String {
        return try {
            val details = json.getJSONObject("sr_detail")
            val icon = details.getString("community_icon")
            icon.substringBefore("?")
        } catch (t: Throwable) {
            ""
        }
    }

    private fun getVideoLink(json: JSONObject): String {
        return try {
            val media = json.getJSONObject("media")
            val redditVideoJSON = media.getJSONObject("reddit_video")
            redditVideoJSON.getString("fallback_url")
        } catch (t: Throwable) {
            ""
        }
    }

    private fun getVideoAudio(url: String): String {
        return if (url != "") {
            val uri = URI(url)
            val id = uri.path.replaceAfterLast("/", "")
            "https://v.redd.it$id" + "DASHPlaylist.mpd"
        } else {
            ""
        }
    }

    private fun getListOfImages(jsonOBJ: JSONObject): List<Pair<String, String>> {
        val listOfIds = mutableListOf<String>()
        val listToReturn = mutableListOf<Pair<String, String>>()
        return try {
            val gallery = jsonOBJ.getJSONObject("gallery_data")
            val imagesArray = gallery.getJSONArray("items")

            (0 until imagesArray.length()).map { index -> imagesArray.getJSONObject(index) }
                .map { itemJson ->
                    val imageID = itemJson.getString("media_id")
                    listOfIds.add(imageID)
                }

            val metadata = jsonOBJ.getJSONObject("media_metadata")
            listOfIds.forEach {
                val imageJson = metadata.getJSONObject(it)
                val imageArray = imageJson.getJSONArray("p")
                val thumbnail = imageArray.getJSONObject(0).getString("u").replace("amp;", "")
                val image =
                    imageArray.getJSONObject(imageArray.length() - 1).getString("u").replace("amp;", "")
                listToReturn.add(thumbnail to image)
            }
            listToReturn
        } catch (t: Throwable) {
            emptyList()
        }
    }

    private suspend fun listConfiguration(
        nickName: String,
        time: String,
        title: String,
        description: String,
        numberOfComments: String,
        url: String,
        postLink: String,
        videoLink: String,
        icon: String,
        imageList: List<Pair<String, String>>,
        videoAudio: String,
        postId: String,
        thumbnail: String?,
        isFollowed: Boolean,
        prefix: String,
    ): RedditPost? {
        return suspendCoroutine { continuation ->
            scope.launch {
                withContext(Dispatchers.Default) {
                    if (imageList.isEmpty() && !url.contains("jpg") && videoLink == "" && !url.contains(
                            "gif") && !url.contains(
                            "png") && !url.contains("jpeg")
                    ) {
                        continuation.resume(RedditPost.SimplePost(
                            avatar = icon,
                            nickName = nickName,
                            time = millisecondsToTime(time),
                            title = title,
                            comments = numberOfComments,
                            url = postLink,
                            description = description,
                            postId = postId,
                            isFollowed = isFollowed,
                            prefix = prefix
                        ))
                    } else {
                        if (imageList.isNotEmpty() || url.contains("jpg") || url.contains("gif") || url.contains(
                                "png") || url.contains("jpeg")
                        ) {
                            continuation.resume(RedditPost.ImagePost(
                                avatar = icon,
                                nickName = nickName,
                                time = millisecondsToTime(time),
                                title = title,
                                comments = numberOfComments,
                                url = postLink,
                                media = imageList.ifEmpty { listOf("" to url) },
                                postId = postId,
                                isFollowed = isFollowed,
                                thumbnail = thumbnail,
                                prefix = prefix
                            ))
                        } else {
                            if (videoLink != "") {
                                continuation.resume(RedditPost.VideoPost(
                                    avatar = icon,
                                    nickName = nickName,
                                    time = millisecondsToTime(time),
                                    title = title,
                                    comments = numberOfComments,
                                    url = postLink,
                                    video = videoLink,
                                    videoAudio = videoAudio,
                                    postId = postId,
                                    isFollowed = isFollowed,
                                    prefix = prefix
                                ))
                            } else {
                                continuation.resume(null)
                            }
                        }
                    }
                }
            }
        }
    }
}