package org.smartregister.view.controller;

import android.webkit.WebView;

import org.smartregister.CoreLibrary;

import java.util.ArrayList;
import java.util.List;

public class UpdateController {
    private WebView webView;
    private boolean pageHasFinishedLoading = false;
    private List<String> urlsToLoad;

    public UpdateController(WebView webView) {
        this.webView = webView;
        urlsToLoad = new ArrayList<String>();
    }

    public void pageHasFinishedLoading() {
        pageHasFinishedLoading = true;
        flushUrlLoads();
    }

    private void flushUrlLoads() {
        if (pageHasFinishedLoading) {
            for (String url : urlsToLoad) {
                if (webView != null) {
                    webView.loadUrl(url);
                }
            }
        }
    }

    public void destroy() {
        webView = null;
    }

}
