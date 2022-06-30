package com.example.tex.auth.WebFragment

import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.findNavController
import com.example.tex.databinding.WebFragmentBinding
import com.example.tex.others.PermanentStorage
import com.example.tex.others.viewBinding.ViewBindingFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class FragmentWeb : ViewBindingFragment<WebFragmentBinding>(WebFragmentBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled
        binding.webView.webViewClient = CallBack(binding)
        binding.webView.loadUrl(PermanentStorage.WebUrl.url)
    }
}

private class CallBack(private val binding: WebFragmentBinding) :
    WebViewClient() {
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        val uri = Uri.parse(url)
        val code = uri.getQueryParameter("code") ?: ""

        if (code != "") {
            binding.webView.visibility = View.GONE
            getAccessToken(code)
        }
    }

    private fun getAccessToken(code: String) {
        val client = OkHttpClient()
        val authString = "5Yabyj2QYgW_jarQzTNRVw" + ":"
        val encodedAuthString = Base64.encodeToString(authString.toByteArray(),
            Base64.NO_WRAP)
        val request = Request.Builder()
            .addHeader("User-Agent", "Sample App")
            .addHeader("Authorization", "Basic $encodedAuthString")
            .url(PermanentStorage.WebUrl.ACCESS_TOKEN_URL)
            .post(RequestBody.create("application/x-www-form-urlencoded".toMediaTypeOrNull(),
                "grant_type=authorization_code&code=" + code +
                        "&redirect_uri=" + PermanentStorage.WebUrl.REDIRECT_URI))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error", "ERROR: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()!!
                val data: JSONObject?
                try {
                    data = JSONObject(json)
                    val accessToken = data.getString("access_token")
                    PermanentStorage.token = accessToken

                    Log.e("scope", data.getString("scope"))

                    val refreshToken = data.getString("refresh_token")

                    CoroutineScope(Dispatchers.Main).launch {
                        binding.root.findNavController().popBackStack()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.root.findNavController().popBackStack()
                    }
                }
            }
        })
    }
}
